package dk.itu.moapd.x9.s25137.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.x9.s25137.data.repositories.AuthRepository
import dk.itu.moapd.x9.s25137.data.repositories.ReportRepository
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/* Code adapted from the MOAPD 2026 subject repository, found at https://github.com/fabricionarcizo/moapd2026/.
 * Its original license is attached below.

 * MIT License
 *
 * Copyright (c) 2026 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

private const val TAG = "MainViewModel"
private const val ADD_FAKE_REPORTS = false

class MainViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val authRepository = AuthRepository()
    private val reportRepository = ReportRepository()
    private val _uiState = MutableStateFlow(MainUiState(userId = authRepository.currentUser?.uid))
    val uiState: StateFlow<MainUiState> = _uiState
    private var _reports = MutableStateFlow<List<Report>>(emptyList())
    val currentUser: User?
        get() = authRepository.currentUser

    private var reportsListener: ValueEventListener? = null

    init {
        if (ADD_FAKE_REPORTS) addFakeReports()
        observeReports()
    }

    fun addFakeReports(n: Int = 100) {
        _reports.value = Report.generateRandomReports(n)
    }

    private fun observeReports() {
        val currentUserId = currentUser?.uid ?: return
        _uiState.update { it.copy(userId = currentUserId) }

        val query = getAllReportsQuery(currentUserId)

        val valueListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    val key = child.key ?: return@mapNotNull null
                    val report = child.getValue(Report::class.java) ?: return@mapNotNull null
                    // Assign Firebase's generated key to the Report object
                    report.copy(key = key)
                }.sortedBy { it.timestamp }
                _uiState.update { it.copy(reports = items) }
            }

            override fun onCancelled(error: DatabaseError) {
                // Keep previous state; errors will be handled by Firebase SDK logs.
            }
        }

        // Update the listener and add it to the query.
        reportsListener = valueListener
        query.addValueEventListener(valueListener)
    }

    fun insertReport(report: Report) {
        val userId = currentUser?.uid ?: return
        reportRepository.insert(userId = userId, report = report)
    }

    fun updateReport(report: Report) {
        val userId = currentUser?.uid ?: return
        reportRepository.update(userId = userId, report = report)
    }

    fun deleteReport(key: String) {
        val userId = currentUser?.uid ?: return
        reportRepository.delete(userId = userId, key = key)
    }

    fun getAllReportsQuery(userId: String) =
        reportRepository.getAllQuery(userId = userId)

    fun signOut() = authRepository.signOut()

    override fun onCleared() {
        super.onCleared()
        val currentUserId = currentUser?.uid
        val l = reportsListener
        if (currentUserId != null && l != null) {
            getAllReportsQuery(currentUserId).removeEventListener(l)
        }
    }

}
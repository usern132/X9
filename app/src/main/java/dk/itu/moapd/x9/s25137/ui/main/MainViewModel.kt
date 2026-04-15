package dk.itu.moapd.x9.s25137.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.itu.moapd.x9.s25137.data.repositories.AuthRepository
import dk.itu.moapd.x9.s25137.data.repositories.ReportRepository
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState(currentUser = authRepository.currentUser))
    val uiState: StateFlow<MainUiState> = _uiState
    val currentUser: User?
        get() = authRepository.currentUser

    private var reportsListener: ValueEventListener? = null

    init {
        observeReports()
    }

    private fun observeReports() {
        _uiState.update { it.copy(currentUser = currentUser) }

        val query = getAllReportsQuery()

        val valueListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    val key = child.key ?: return@mapNotNull null
                    val report = child.getValue(Report::class.java) ?: return@mapNotNull null
                    // Assign Firebase's generated key to the Report object
                    report.copy(key = key)
                }.sortedByDescending { it.timestamp }

                _uiState.update { it.copy(reports = items) }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error listening to Realtime Database changes: $error")
                _uiState.update { it.copy(errorMessage = error.message) }
            }
        }

        reportsListener = valueListener
        query.addValueEventListener(valueListener)
    }

    fun insertReport(report: Report) {
        val user = currentUser ?: return
        val reportWithUser = report.copy(
            userId = user.uid,
            userName = user.name ?: "",
            userImageUri = user.photoUri?.toString()
        )
        reportRepository.insert(report = reportWithUser) { error ->
            if (error != null) {
                _uiState.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    fun updateReport(report: Report) {
        currentUser?.uid ?: return
        reportRepository.update(report = report) { error ->
            if (error != null) {
                _uiState.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    fun deleteReport(key: String) {
        currentUser?.uid ?: return
        reportRepository.delete(key = key) { error ->
            if (error != null) {
                _uiState.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    fun isReportEditable(report: Report): Boolean =
        report.userId == currentUser?.uid

    fun isReportDeletable(report: Report): Boolean =
        // Same criterion applied to both editing and deleting
        isReportEditable(report)

    fun errorConsumed() =
        _uiState.update { it.copy(errorMessage = null) }

    fun getAllReportsQuery() =
        reportRepository.getAllQuery()

    fun logOut() {
        authRepository.logOut()
        _uiState.update { it.copy(currentUser = null) }
    }

    override fun onCleared() {
        super.onCleared()
        val l = reportsListener
        if (l != null) {
            getAllReportsQuery().removeEventListener(l)
        }
    }

    fun showLoginAlertDialog() =
        _uiState.update { it.copy(showLoginAlertDialog = true) }

    fun hideLoginAlertDialog() =
        _uiState.update { it.copy(showLoginAlertDialog = false) }
}

package dk.itu.moapd.x9.s25137

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainActivityViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var savedReportString: String? = null
}
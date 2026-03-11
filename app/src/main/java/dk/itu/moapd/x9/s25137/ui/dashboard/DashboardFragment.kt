package dk.itu.moapd.x9.s25137.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.databinding.FragmentDashboardBinding
import dk.itu.moapd.x9.s25137.ui.reports.ReportViewModel
import dk.itu.moapd.x9.s25137.ui.reports.list.ReportList

private const val TAG = "DashboardFragment"

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val reportViewModel: ReportViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView() called")
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.reportsList.apply {
            // Dispose the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ReportList(
                    reports = reportViewModel.reports,
                    onItemClick = { index ->
                        val action = DashboardFragmentDirections.showReportDetails(index)
                        findNavController().navigate(action)
                    }
                )
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")
        binding.createReportButton.setOnClickListener {
            findNavController().navigate(R.id.show_create_report_form)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
        _binding = null
    }
}
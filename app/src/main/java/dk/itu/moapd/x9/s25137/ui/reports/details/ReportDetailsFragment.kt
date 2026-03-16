package dk.itu.moapd.x9.s25137.ui.reports.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import dk.itu.moapd.x9.s25137.databinding.FragmentReportDetailsBinding
import dk.itu.moapd.x9.s25137.ui.reports.ReportViewModel
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme

class ReportDetailsFragment : Fragment() {
    private var _binding: FragmentReportDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val reportViewModel: ReportViewModel by activityViewModels()
    private val args: ReportDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportDetailsBinding.inflate(inflater, container, false)

        binding.reportDetailsComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val reports by reportViewModel.reports.collectAsState()
                val report = reports[args.reportIndex]
                AppTheme {
                    ReportDetailsPage(report = report)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

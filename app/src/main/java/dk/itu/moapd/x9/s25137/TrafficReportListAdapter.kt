package dk.itu.moapd.x9.s25137

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import dk.itu.moapd.x9.s25137.databinding.ListItemReportBinding

class TrafficReportHolder(val binding: ListItemReportBinding) :
    RecyclerView.ViewHolder(binding.root)

class TrafficReportListAdapter(
    private val trafficReports: List<Report>
) : Adapter<TrafficReportHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrafficReportHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemReportBinding.inflate(inflater, parent, false)
        return TrafficReportHolder(binding)
    }

    override fun onBindViewHolder(holder: TrafficReportHolder, position: Int) {
        val report = trafficReports[position]
        holder.apply {
            binding.reportTitle.text = report.title
            val dateText = DateFormat.format("dd/MM/yyyy", report.date)
            binding.reportSubtitle.text = "$dateText · ${report.location}"
        }
    }

    override fun getItemCount(): Int = trafficReports.size
}
package com.pixview.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixview.app.display.BrightnessInfo

@Composable
fun BrightnessScreen(info: BrightnessInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Brightness", style = MaterialTheme.typography.titleLarge)

        MetricCard(
            title = "Current",
            value = info.currentPercent?.let { "$it%" } ?: "Not available"
        )

        DetailsCard(title = "Settings") {
            DetailRow(
                "Automatic brightness",
                when (info.isAutomatic) {
                    true -> "On"
                    false -> "Off"
                    null -> "Not available"
                }
            )
            DetailRow(
                "High Brightness Mode",
                when (info.supportsHighBrightnessMode) {
                    true -> "Supported"
                    false -> "Not supported"
                    null -> "Not available"
                }
            )
            DetailRow(
                "Peak brightness",
                info.peakBrightnessNits?.let { "${it.toInt()} nits" } ?: "Not available"
            )
        }

        NoteText(
            "Peak brightness in nits is only shown when a reliable, manufacturer-exposed value " +
                "exists. PixView never estimates nits from the brightness percentage."
        )
    }
}

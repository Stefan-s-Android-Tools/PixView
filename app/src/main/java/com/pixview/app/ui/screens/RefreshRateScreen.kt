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
import com.pixview.app.display.AdaptiveStatus
import com.pixview.app.display.RefreshRateInfo

@Composable
fun RefreshRateScreen(info: RefreshRateInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Refresh Rate", style = MaterialTheme.typography.titleLarge)

        MetricCard(
            title = "Current",
            value = "${roundedHz(info.currentHz)} Hz",
            caption = info.currentModeDescription
        )

        DetailsCard(title = "Supported rates") {
            DetailRow(
                "Supported",
                if (info.supportedHz.isEmpty()) "Not available"
                else info.supportedHz.joinToString(" / ") { "${roundedHz(it)}" } + " Hz"
            )
            DetailRow("Minimum", info.minHz?.let { "${roundedHz(it)} Hz" } ?: "Not available")
            DetailRow("Maximum", info.maxHz?.let { "${roundedHz(it)} Hz" } ?: "Not available")
        }

        DetailsCard(title = "Adaptive refresh rate") {
            DetailRow("Status", adaptiveStatusLabel(info.adaptiveStatus))
        }

        NoteText(
            "PixView reports only what the current display mode list and platform APIs expose. " +
                "It does not assume adaptive refresh support based on the device model."
        )
    }
}

private fun adaptiveStatusLabel(status: AdaptiveStatus): String = when (status) {
    AdaptiveStatus.AVAILABLE -> "Available"
    AdaptiveStatus.NOT_AVAILABLE -> "Not available"
    AdaptiveStatus.POSSIBLY_AVAILABLE -> "Possibly available (multiple refresh rates detected)"
    AdaptiveStatus.UNKNOWN -> "Unknown"
}

private fun roundedHz(hz: Float): Int = hz.toInt().let { if (hz - it >= 0.5f) it + 1 else it }

package com.pixview.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixview.app.display.PixViewDisplayInfo

@Composable
fun DashboardScreen(info: PixViewDisplayInfo, modifier: Modifier = Modifier) {
    val resolution = info.resolution
    val density = info.density
    val refreshRate = info.refreshRate
    val brightness = info.brightness
    val displaySize = info.displaySize

    val cards = buildList {
        add(
            Triple(
                "Resolution",
                "${resolution.physicalWidthPx} × ${resolution.physicalHeightPx} px",
                resolution.classification
            )
        )
        add(Triple("Pixel Density", "${density.densityDpi} DPI", null))
        add(
            Triple(
                "Refresh Rate",
                "${roundedHz(refreshRate.currentHz)} Hz",
                availableRatesCaption(refreshRate.supportedHz)
            )
        )
        add(
            Triple(
                "Brightness",
                brightness.currentPercent?.let { "$it%" } ?: "Not available",
                null
            )
        )
        add(
            Triple(
                "Display Size",
                displaySize.diagonalInches?.let { "${formatInches(it)}\"" } ?: "Not available",
                if (displaySize.diagonalInches != null) "Estimated" else null
            )
        )
    }

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "Display Information",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cards) { (title, value, caption) ->
                MetricCard(title = title, value = value, caption = caption)
            }
            item {
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = "PixView reads system-reported values only — it never modifies display settings.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

internal fun roundedHz(hz: Float): Int = hz.toInt().let { if (hz - it >= 0.5f) it + 1 else it }

internal fun availableRatesCaption(rates: List<Float>): String? {
    if (rates.size <= 1) return null
    return rates.joinToString(" / ") { roundedHz(it).toString() } + " Hz available"
}

internal fun formatInches(value: Float): String {
    return String.format("%.1f", value)
}

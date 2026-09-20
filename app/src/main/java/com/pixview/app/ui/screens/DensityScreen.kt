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
import com.pixview.app.display.DensityInfo
import kotlin.math.roundToInt

@Composable
fun DensityScreen(info: DensityInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Density", style = MaterialTheme.typography.titleLarge)

        MetricCard(
            title = "Pixel Density",
            value = "${info.densityDpi} DPI",
            caption = "Android bucket: ${info.densityBucketName}"
        )

        DetailsCard(title = "Density values") {
            DetailRow("densityDpi", "${info.densityDpi}")
            DetailRow("Logical density factor", "${roundTo1(info.logicalDensity)}x")
            DetailRow(
                "Physical DPI (x / y)",
                "${roundTo1(info.physicalDpiX)} / ${roundTo1(info.physicalDpiY)}"
            )
        }

        DetailsCard(title = "Scaling") {
            DetailRow(
                "Display scaling",
                info.displayScalingPercent?.let { "$it%" } ?: "Not available"
            )
            DetailRow("Font scaling", "${info.fontScalePercent}%")
        }

        NoteText(
            "densityDpi is Android's logical density bucket, used for dp/sp conversion. " +
                "Physical DPI (x/y) is the panel's reported pixels-per-inch and is a separate, " +
                "unrelated measurement — the two should not be confused."
        )
    }
}

private fun roundTo1(value: Float): Float = (value * 10).roundToInt() / 10f

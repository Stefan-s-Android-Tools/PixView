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
import com.pixview.app.display.DisplaySizeInfo

@Composable
fun DisplaySizeScreen(info: DisplaySizeInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Display Size", style = MaterialTheme.typography.titleLarge)

        MetricCard(
            title = "Diagonal size",
            value = info.diagonalInches?.let { "${format1(it)}\"" } ?: "Not available",
            caption = if (info.diagonalInches != null) "Estimated" else null
        )

        DetailsCard(title = "Dimensions") {
            DetailRow("Width", info.widthInches?.let { "${format1(it)}\"" } ?: "Not available")
            DetailRow("Height", info.heightInches?.let { "${format1(it)}\"" } ?: "Not available")
        }

        NoteText(
            "Physical size is calculated from the panel's reported pixel dimensions and DPI. " +
                "Manufacturer-reported DPI can be inaccurate on some devices, so this figure is " +
                "always an estimate rather than a verified spec."
        )
    }
}

private fun format1(value: Float): String = String.format("%.1f", value)

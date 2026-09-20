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
import com.pixview.app.display.ResolutionInfo

@Composable
fun ResolutionScreen(info: ResolutionInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Resolution", style = MaterialTheme.typography.titleLarge)

        MetricCard(
            title = "Physical Resolution",
            value = "${info.physicalWidthPx} × ${info.physicalHeightPx} px",
            caption = info.classification
        )

        DetailsCard(title = "Details") {
            DetailRow("Logical resolution", "${info.logicalWidthPx} × ${info.logicalHeightPx} px")
            DetailRow("Width", "${info.physicalWidthPx} px")
            DetailRow("Height", "${info.physicalHeightPx} px")
            DetailRow("Aspect ratio", info.aspectRatio)
            DetailRow("Classification", info.classification ?: "Not determined")
        }

        NoteText(
            "Logical resolution reflects the current window's usable area and can " +
                "differ from the physical panel resolution when system bars or insets are present."
        )
    }
}

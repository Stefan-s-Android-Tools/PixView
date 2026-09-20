package com.pixview.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixview.app.display.DisplayInfoProvider
import com.pixview.app.display.PixViewDisplayInfo
import com.pixview.app.ui.screens.BrightnessScreen
import com.pixview.app.ui.screens.DashboardScreen
import com.pixview.app.ui.screens.DensityScreen
import com.pixview.app.ui.screens.DisplaySizeScreen
import com.pixview.app.ui.screens.RefreshRateScreen
import com.pixview.app.ui.screens.ResolutionScreen
import com.pixview.app.ui.theme.PixViewTheme

private enum class PixViewTab(val label: String) {
    DASHBOARD("Dashboard"),
    RESOLUTION("Resolution"),
    DENSITY("Density"),
    REFRESH_RATE("Refresh Rate"),
    BRIGHTNESS("Brightness"),
    DISPLAY_SIZE("Display Size")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val displayInfo = DisplayInfoProvider(this).collect()

        setContent {
            PixViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PixViewApp(displayInfo)
                }
            }
        }
    }
}

@Composable
private fun PixViewApp(info: PixViewDisplayInfo) {
    var selectedTab by remember { mutableStateOf(PixViewTab.DASHBOARD) }

    Scaffold(
        topBar = {
            Column {
                Text(
                    text = "PixView",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
                ScrollableTabRow(selectedTabIndex = selectedTab.ordinal) {
                    PixViewTab.entries.forEach { tab ->
                        Tab(
                            selected = tab == selectedTab,
                            onClick = { selectedTab = tab },
                            text = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (selectedTab) {
                PixViewTab.DASHBOARD -> DashboardScreen(info)
                PixViewTab.RESOLUTION -> ResolutionScreen(info.resolution)
                PixViewTab.DENSITY -> DensityScreen(info.density)
                PixViewTab.REFRESH_RATE -> RefreshRateScreen(info.refreshRate)
                PixViewTab.BRIGHTNESS -> BrightnessScreen(info.brightness)
                PixViewTab.DISPLAY_SIZE -> DisplaySizeScreen(info.displaySize)
            }
        }
    }
}

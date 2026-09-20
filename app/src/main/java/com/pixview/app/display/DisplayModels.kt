package com.pixview.app.display

/**
 * Plain data holders for every piece of display information PixView surfaces.
 * Every field here is either read directly from an Android API or derived
 * with a simple, documented calculation. Nothing is invented: when a value
 * cannot be obtained reliably, the corresponding field is null and the UI
 * shows "Not available" instead of guessing.
 */

data class ResolutionInfo(
    val physicalWidthPx: Int,
    val physicalHeightPx: Int,
    val logicalWidthPx: Int,
    val logicalHeightPx: Int,
    val aspectRatio: String,
    val classification: String?
)

data class DensityInfo(
    val densityDpi: Int,
    val physicalDpiX: Float,
    val physicalDpiY: Float,
    val logicalDensity: Float,
    val densityBucketName: String,
    val displayScalingPercent: Int?,
    val fontScalePercent: Int
)

data class RefreshRateInfo(
    val currentHz: Float,
    val supportedHz: List<Float>,
    val minHz: Float?,
    val maxHz: Float?,
    val adaptiveStatus: AdaptiveStatus,
    val currentModeDescription: String
)

enum class AdaptiveStatus {
    AVAILABLE,
    NOT_AVAILABLE,
    POSSIBLY_AVAILABLE,
    UNKNOWN
}

data class BrightnessInfo(
    val currentPercent: Int?,
    val isAutomatic: Boolean?,
    val supportsHighBrightnessMode: Boolean?,
    val peakBrightnessNits: Float?
)

data class DisplaySizeInfo(
    val diagonalInches: Float?,
    val widthInches: Float?,
    val heightInches: Float?,
    val isEstimated: Boolean
)

data class PixViewDisplayInfo(
    val resolution: ResolutionInfo,
    val density: DensityInfo,
    val refreshRate: RefreshRateInfo,
    val brightness: BrightnessInfo,
    val displaySize: DisplaySizeInfo
)

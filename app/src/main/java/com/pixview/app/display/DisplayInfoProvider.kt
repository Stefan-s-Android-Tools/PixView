package com.pixview.app.display

import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Reads real, on-device display information using Android's public APIs.
 *
 * PixView never modifies display settings and never fabricates a value: if
 * the platform does not expose something reliably, the corresponding field
 * is left null and the UI is responsible for showing "Not available".
 */
class DisplayInfoProvider(private val activity: Activity) {

    private val context: Context = activity.applicationContext

    private val display: Display by lazy { resolveDisplay() }

    private fun resolveDisplay(): Display {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.display ?: legacyDisplay()
        } else {
            legacyDisplay()
        }
    }

    @Suppress("DEPRECATION")
    private fun legacyDisplay(): Display {
        val wm = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay
    }

    fun collect(): PixViewDisplayInfo {
        return PixViewDisplayInfo(
            resolution = collectResolution(),
            density = collectDensity(),
            refreshRate = collectRefreshRate(),
            brightness = collectBrightness(),
            displaySize = collectDisplaySize()
        )
    }

    // ---------------------------------------------------------------------
    // Resolution
    // ---------------------------------------------------------------------

    private fun collectResolution(): ResolutionInfo {
        val physical = DisplayMetrics()
        @Suppress("DEPRECATION")
        display.getRealMetrics(physical)

        val logical = DisplayMetrics()
        @Suppress("DEPRECATION")
        display.getMetrics(logical)

        val w = physical.widthPixels
        val h = physical.heightPixels

        return ResolutionInfo(
            physicalWidthPx = w,
            physicalHeightPx = h,
            logicalWidthPx = logical.widthPixels,
            logicalHeightPx = logical.heightPixels,
            aspectRatio = reducedAspectRatio(w, h),
            classification = classifyResolution(w, h)
        )
    }

    private fun reducedAspectRatio(width: Int, height: Int): String {
        if (width <= 0 || height <= 0) return "Unknown"
        val long = maxOf(width, height)
        val short = minOf(width, height)
        val divisor = gcd(long, short)
        return "${long / divisor}:${short / divisor}"
    }

    private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

    /**
     * Approximate marketing-style classification based on the short side
     * (portrait width) of the panel, refined by the long side for the "+"
     * variants. Ranges include tolerance for panels that are not exactly on
     * a standard figure (e.g. 1080 vs 1084 due to rounding/cutouts).
     * When nothing matches confidently, returns null and the UI shows only
     * the raw resolution, per spec.
     */
    private fun classifyResolution(width: Int, height: Int): String? {
        val shortSide = minOf(width, height)
        val longSide = maxOf(width, height)

        fun within(value: Int, target: Int, tolerance: Int) = value in (target - tolerance)..(target + tolerance)

        return when {
            shortSide < 480 -> "SD"
            within(shortSide, 540, 40) -> "qHD"
            within(shortSide, 720, 40) -> if (longSide > 1300) "HD+" else "HD"
            within(shortSide, 1080, 40) -> if (longSide > 2000) "FHD+" else "FHD"
            within(shortSide, 1440, 60) -> if (longSide > 2700) "QHD+" else "QHD"
            shortSide >= 2100 -> "UHD"
            else -> null
        }
    }

    // ---------------------------------------------------------------------
    // Density
    // ---------------------------------------------------------------------

    private fun collectDensity(): DensityInfo {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        display.getRealMetrics(metrics)

        val fontScale = context.resources.configuration.fontScale

        val displayScaling: Int? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val stable = DisplayMetrics.DENSITY_DEVICE_STABLE
            if (stable > 0) ((metrics.densityDpi.toFloat() / stable) * 100f).roundToInt() else null
        } else {
            null
        }

        return DensityInfo(
            densityDpi = metrics.densityDpi,
            physicalDpiX = metrics.xdpi,
            physicalDpiY = metrics.ydpi,
            logicalDensity = metrics.density,
            densityBucketName = densityBucketName(metrics.densityDpi),
            displayScalingPercent = displayScaling,
            fontScalePercent = (fontScale * 100).roundToInt()
        )
    }

    private fun densityBucketName(densityDpi: Int): String = when {
        densityDpi <= DisplayMetrics.DENSITY_LOW -> "ldpi"
        densityDpi <= DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
        densityDpi <= DisplayMetrics.DENSITY_HIGH -> "hdpi"
        densityDpi <= DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
        densityDpi <= DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
        else -> "xxxhdpi"
    }

    // ---------------------------------------------------------------------
    // Refresh rate
    // ---------------------------------------------------------------------

    private fun collectRefreshRate(): RefreshRateInfo {
        val modes = display.supportedModes
        val distinctRates = modes.map { it.refreshRate }.distinct().sorted()

        val current = display.refreshRate
        val min = distinctRates.minOrNull()
        val max = distinctRates.maxOrNull()

        val currentMode = display.mode
        val modeDescription = "${currentMode.physicalWidth}x${currentMode.physicalHeight} @ " +
            "${roundHz(currentMode.refreshRate)} Hz"

        val adaptive = detectAdaptiveStatus(distinctRates)

        return RefreshRateInfo(
            currentHz = current,
            supportedHz = distinctRates,
            minHz = min,
            maxHz = max,
            adaptiveStatus = adaptive,
            currentModeDescription = modeDescription
        )
    }

    private fun roundHz(hz: Float): Int = hz.roundToInt()

    /**
     * Adaptive / variable refresh rate has no simple universal public API
     * before Android 15's Display#hasArrSupport(). PixView checks for it via
     * reflection so it still builds against older SDKs, and otherwise only
     * reports what can be honestly inferred: never claims a device supports
     * adaptive refresh just because a rate is common on that model.
     */
    private fun detectAdaptiveStatus(distinctRates: List<Float>): AdaptiveStatus {
        try {
            val method = Display::class.java.getMethod("hasArrSupport")
            val result = method.invoke(display) as? Boolean
            if (result != null) {
                return if (result) AdaptiveStatus.AVAILABLE else AdaptiveStatus.NOT_AVAILABLE
            }
        } catch (_: NoSuchMethodException) {
            // Not available on this API level, fall through.
        } catch (_: Exception) {
            // Any reflection failure: fall through to the heuristic below.
        }

        return if (distinctRates.size > 1) AdaptiveStatus.POSSIBLY_AVAILABLE else AdaptiveStatus.UNKNOWN
    }

    // ---------------------------------------------------------------------
    // Brightness
    // ---------------------------------------------------------------------

    private fun collectBrightness(): BrightnessInfo {
        var currentPercent: Int? = null
        var supportsHbm: Boolean? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val method = display.javaClass.getMethod("getBrightnessInfo")
                val info = method.invoke(display)
                if (info != null) {
                    val brightnessField = info.javaClass.getField("brightness")
                    val maxField = info.javaClass.getField("brightnessMaximum")
                    val hbmField = info.javaClass.getField("highBrightnessModeMaxBrightness")

                    val b = (brightnessField.get(info) as? Number)?.toFloat() ?: 0f
                    val maxB = (maxField.get(info) as? Number)?.toFloat() ?: 1f
                    val hbmMax = (hbmField.get(info) as? Number)?.toFloat() ?: 0f

                    if (maxB > 0f) {
                        currentPercent = ((b / maxB) * 100f)
                            .roundToInt()
                            .coerceIn(0, 100)
                        supportsHbm = hbmMax > maxB
                    }
                }
            } catch (_: Exception) {
                // Fall back to the Settings-based approach below.
            }
        }

        if (currentPercent == null) {
            currentPercent = try {
                val raw = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                ((raw / 255f) * 100f).roundToInt().coerceIn(0, 100)
            } catch (_: Settings.SettingNotFoundException) {
                null
            }
        }

        val isAutomatic: Boolean? = try {
            val mode = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE)
            mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } catch (_: Settings.SettingNotFoundException) {
            null
        }

        // PixView deliberately never estimates nits: no general, reliable
        // public Android API exposes panel peak luminance across devices.
        return BrightnessInfo(
            currentPercent = currentPercent,
            isAutomatic = isAutomatic,
            supportsHighBrightnessMode = supportsHbm,
            peakBrightnessNits = null
        )
    }

    // ---------------------------------------------------------------------
    // Physical display size
    // ---------------------------------------------------------------------

    private fun collectDisplaySize(): DisplaySizeInfo {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        display.getRealMetrics(metrics)

        if (metrics.xdpi <= 0f || metrics.ydpi <= 0f) {
            return DisplaySizeInfo(null, null, null, isEstimated = true)
        }

        val widthInches = metrics.widthPixels / metrics.xdpi
        val heightInches = metrics.heightPixels / metrics.ydpi
        val diagonal = sqrt(widthInches * widthInches + heightInches * heightInches)

        return DisplaySizeInfo(
            diagonalInches = diagonal,
            widthInches = widthInches,
            heightInches = heightInches,
            // System-reported xdpi/ydpi are manufacturer-supplied and can be
            // inaccurate, so this figure is always presented as an estimate.
            isEstimated = true
        )
    }
}

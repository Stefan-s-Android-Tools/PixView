# PixView

**PixView** is a lightweight, open-source Android tool that displays real and technical information about the device's display: resolution, density, DPI, refresh rate, brightness, physical size, and related capabilities.

PixView **only reads and presents information**. It does not modify, "optimize," or adjust any display parameters.

## Features

* **Dashboard**: overview of the display's most important metrics.
* **Resolution**: physical and logical resolution, aspect ratio, and approximate classification (SD / HD / HD+ / FHD / FHD+ / QHD / QHD+ / UHD).
* **Density**: DPI, `densityDpi`, logical density, display and font scaling — clearly indicating what each value represents.
* **Refresh Rate**: current, minimum, and maximum refresh rate, supported modes, and adaptive refresh rate status (when detectable).
* **Brightness**: current level, automatic brightness, and High Brightness Mode support. Peak brightness in nits is only displayed when a reliable source is available; PixView never calculates or invents these values.
* **Display Size**: estimated physical size (width, height, and diagonal in inches), calculated from the DPI reported by the system.

## Design Principles

* Read-only: PixView never writes to system settings.
* No values are invented or estimated without explicitly indicating it in the interface (`Not available` / `Estimated` when applicable).
* No unnecessary libraries: only AndroidX Core, Activity Compose, and Compose Material 3.

## Technical Stack

* Kotlin 2.0
* Jetpack Compose + Material 3
* Gradle Kotlin DSL
* `minSdk` 26 (Android 8.0), `targetSdk` / `compileSdk` 35 (Android 15)
* No unnecessarily complex architecture: a `DisplayInfoProvider` that reads Android APIs once per session, and a Compose screen layer that only presents the collected data.

## Project Structure

```text
app/src/main/java/com/pixview/app/
├── MainActivity.kt              # Entry point and tab navigation
├── display/
│   ├── DisplayModels.kt         # Data classes for display information
│   └── DisplayInfoProvider.kt   # Reads data from Android APIs
└── ui/
    ├── theme/                   # Material 3 theme (colors, typography)
    └── screens/                 # Dashboard, Resolution, Density, etc.
```

## Building the Project

1. Clone or download the repository.
2. Open it with Android Studio (Ladybug or newer recommended).
3. If the Gradle wrapper does not include the binary (`gradle-wrapper.jar`), Android Studio will offer to generate it automatically when opening the project; it can also be generated manually with `gradle wrapper` if Gradle is installed locally.
4. Run the project on a device or emulator running Android 8.0 (API 26) or higher.

## Technical Accuracy Notes

* Resolution classification (HD, HD+, FHD, etc.) is approximate and based on common industry ranges; if it cannot be determined with confidence, only the raw resolution is displayed.
* Physical display size is calculated from the `xdpi`/`ydpi` values reported by the system, which may be inaccurate on some devices due to manufacturing variations; therefore, the result is always marked as estimated.
* Adaptive refresh rate is detected using the `Display.hasArrSupport()` API (Android 15+) when available; in other cases, PixView only indicates whether multiple supported refresh rates exist, without assuming that this implies true adaptive refresh.
* Brightness in nits is only displayed when the device exposes that information reliably; otherwise, `Not available` is shown.

## License

MIT — see [LICENSE](LICENSE).

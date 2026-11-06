package com.example.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class TextAlignment(val label: String) {
    LEFT("Left"),
    CENTER("Center"),
    RIGHT("Right")
}

enum class WallpaperTheme(
    val label: String,
    val description: String,
    val bgColorHex: Long,
    val cardBgColorHex: Long,
    val textPrimaryHex: Long,
    val textSecondaryHex: Long,
    val accentHex: Long
) {
    PURE_BLACK(
        label = "Pure OLED Black",
        description = "Deepest black for battery efficiency and maximum contrast",
        bgColorHex = 0xFF000000,
        cardBgColorHex = 0xFF111111,
        textPrimaryHex = 0xFFFFFFFF,
        textSecondaryHex = 0xFF8E8E93,
        accentHex = 0xFFFFFFFF
    ),
    CHARCOAL_SLATE(
        label = "Charcoal Slate",
        description = "Refined dark gray matte finish with architectural elegance",
        bgColorHex = 0xFF131316,
        cardBgColorHex = 0xFF1C1C21,
        textPrimaryHex = 0xFFF0F0F2,
        textSecondaryHex = 0xFF96969E,
        accentHex = 0xFFE0E0E6
    ),
    WARM_OFF_WHITE(
        label = "Warm Off-White",
        description = "Editorial gallery off-white with crisp dark ink typography",
        bgColorHex = 0xFFF5F3EC,
        cardBgColorHex = 0xFFEAE7DF,
        textPrimaryHex = 0xFF18181B,
        textSecondaryHex = 0xFF71717A,
        accentHex = 0xFF000000
    ),
    MINIMAL_TITANIUM(
        label = "Titanium Smoke",
        description = "Industrial steel dark tint with clean monochromatic tone",
        bgColorHex = 0xFF1A1C1E,
        cardBgColorHex = 0xFF23262A,
        textPrimaryHex = 0xFFE3E8EC,
        textSecondaryHex = 0xFF8A939B,
        accentHex = 0xFF90CAF9
    )
}

enum class LayoutPosition(val label: String, val description: String) {
    CENTER(
        label = "Balanced Center",
        description = "Harmoniously centered countdown visible on both lock and home screens"
    ),
    LOWER_THIRD(
        label = "Pixel Lock Safe",
        description = "Positioned below the large Pixel 9 lock screen clock & At a Glance widget"
    ),
    ELEVATED(
        label = "Upper Focus",
        description = "Positioned above home screen icon grid and dock"
    )
}

enum class WallpaperTarget(val label: String) {
    BOTH("Home & Lock Screen"),
    HOME_SCREEN("Home Screen Only"),
    LOCK_SCREEN("Lock Screen Only")
}

data class Goal(
    val title: String = "Achieve the Vision",
    val targetDate: LocalDate = LocalDate.now().plusDays(30),
    val motivationalMessage: String = "Freedom. Focus on today.",
    val showTopHeader: Boolean = false,
    val topHeaderCustomText: String = "",
    val customUnitLabel: String = "Days Remaining",
    val topHeaderAlignment: TextAlignment = TextAlignment.CENTER,
    val titleAlignment: TextAlignment = TextAlignment.CENTER,
    val dateAlignment: TextAlignment = TextAlignment.CENTER,
    val numberAlignment: TextAlignment = TextAlignment.CENTER,
    val messageAlignment: TextAlignment = TextAlignment.CENTER,
    val theme: WallpaperTheme = WallpaperTheme.PURE_BLACK,
    val layoutPosition: LayoutPosition = LayoutPosition.CENTER,
    val targetScreen: WallpaperTarget = WallpaperTarget.BOTH,
    val autoDailyUpdate: Boolean = true,
    val lastAppliedTimestamp: Long = 0L,
    val lastAppliedDays: Long = -1L
) {
    val formattedTargetDate: String
        get() = targetDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))

    val shortTargetDate: String
        get() = targetDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
}

data class CountdownResult(
    val daysRemaining: Long,
    val isToday: Boolean,
    val isPast: Boolean,
    val displayDays: String,
    val defaultStatusLabel: String,
    val detailedStatus: String,
    val referenceDate: LocalDate
) {
    val statusLabel: String
        get() = defaultStatusLabel
}

object CountdownCalculator {
    fun calculate(targetDate: LocalDate, currentDate: LocalDate = LocalDate.now()): CountdownResult {
        val days = ChronoUnit.DAYS.between(currentDate, targetDate)
        return when {
            days > 0 -> {
                CountdownResult(
                    daysRemaining = days,
                    isToday = false,
                    isPast = false,
                    displayDays = days.toString(),
                    defaultStatusLabel = if (days == 1L) "Day Remaining" else "Days Remaining",
                    detailedStatus = "Target in $days ${if (days == 1L) "calendar day" else "calendar days"}",
                    referenceDate = currentDate
                )
            }
            days == 0L -> {
                CountdownResult(
                    daysRemaining = 0,
                    isToday = true,
                    isPast = false,
                    displayDays = "0",
                    defaultStatusLabel = "DAY ZERO // TODAY",
                    detailedStatus = "Day Zero is here. Today is the target day.",
                    referenceDate = currentDate
                )
            }
            else -> {
                val pastDays = -days
                CountdownResult(
                    daysRemaining = 0,
                    isToday = false,
                    isPast = true,
                    displayDays = "0",
                    defaultStatusLabel = "TARGET REACHED",
                    detailedStatus = "Target date passed ($pastDays ${if (pastDays == 1L) "day" else "days"} ago). Goal completed.",
                    referenceDate = currentDate
                )
            }
        }
    }
}

package com.example

import com.example.model.CountdownCalculator
import com.example.model.Goal
import com.example.model.TextAlignment
import com.example.model.WallpaperTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {

    @Test
    fun `test countdown calculation for future date`() {
        val today = LocalDate.of(2026, 11, 6)
        val target = LocalDate.of(2026, 12, 16) // 40 days later

        val result = CountdownCalculator.calculate(targetDate = target, currentDate = today)
        assertEquals(40L, result.daysRemaining)
        assertEquals("40", result.displayDays)
        assertFalse(result.isToday)
        assertFalse(result.isPast)
        assertEquals("Days Remaining", result.defaultStatusLabel)
    }

    @Test
    fun `test countdown calculation for today is Day Zero`() {
        val today = LocalDate.of(2026, 11, 6)
        val target = LocalDate.of(2026, 11, 6)

        val result = CountdownCalculator.calculate(targetDate = target, currentDate = today)
        assertEquals(0L, result.daysRemaining)
        assertEquals("0", result.displayDays)
        assertTrue(result.isToday)
        assertFalse(result.isPast)
        assertEquals("DAY ZERO // TODAY", result.defaultStatusLabel)
    }

    @Test
    fun `test countdown calculation for past date never returns negative`() {
        val today = LocalDate.of(2026, 11, 6)
        val target = LocalDate.of(2026, 11, 1) // 5 days in past

        val result = CountdownCalculator.calculate(targetDate = target, currentDate = today)
        assertEquals(0L, result.daysRemaining)
        assertEquals("0", result.displayDays)
        assertFalse(result.isToday)
        assertTrue(result.isPast)
        assertEquals("TARGET REACHED", result.defaultStatusLabel)
    }

    @Test
    fun `test goal formatting, customizable labels, and alignment defaults`() {
        val goal = Goal(
            title = "Pixel 9 Launch",
            targetDate = LocalDate.of(2026, 12, 31),
            motivationalMessage = "Execute every single day.",
            showTopHeader = false,
            topHeaderCustomText = "",
            customUnitLabel = "Days to Go",
            topHeaderAlignment = TextAlignment.LEFT,
            titleAlignment = TextAlignment.LEFT,
            numberAlignment = TextAlignment.CENTER,
            messageAlignment = TextAlignment.RIGHT,
            theme = WallpaperTheme.PURE_BLACK
        )
        assertEquals("Pixel 9 Launch", goal.title)
        assertEquals("Dec 31, 2026", goal.shortTargetDate)
        assertFalse(goal.showTopHeader)
        assertEquals("Days to Go", goal.customUnitLabel)
        assertEquals(TextAlignment.LEFT, goal.titleAlignment)
        assertEquals(TextAlignment.CENTER, goal.numberAlignment)
        assertEquals(TextAlignment.RIGHT, goal.messageAlignment)
    }
}

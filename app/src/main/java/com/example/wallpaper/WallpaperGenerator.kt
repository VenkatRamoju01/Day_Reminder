package com.example.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.model.CountdownResult
import com.example.model.Goal
import com.example.model.LayoutPosition
import com.example.model.TextAlignment
import kotlin.math.max

object WallpaperGenerator {

    // Default Pixel 9 Actua Display Specifications
    const val PIXEL_9_WIDTH = 1080
    const val PIXEL_9_HEIGHT = 2424

    fun getOptimalDimensions(context: Context?): Pair<Int, Int> {
        val displayMetrics = context?.resources?.displayMetrics
        val width = if (displayMetrics != null && displayMetrics.widthPixels > 0) {
            max(PIXEL_9_WIDTH, displayMetrics.widthPixels)
        } else {
            PIXEL_9_WIDTH
        }
        val height = if (displayMetrics != null && displayMetrics.heightPixels > 0) {
            max(PIXEL_9_HEIGHT, displayMetrics.heightPixels)
        } else {
            PIXEL_9_HEIGHT
        }
        return Pair(width, height)
    }

    private fun getX(alignment: TextAlignment, margin: Float, centerX: Float, rightX: Float): Float {
        return when (alignment) {
            TextAlignment.LEFT -> margin
            TextAlignment.CENTER -> centerX
            TextAlignment.RIGHT -> rightX
        }
    }

    private fun getPaintAlign(alignment: TextAlignment): Paint.Align {
        return when (alignment) {
            TextAlignment.LEFT -> Paint.Align.LEFT
            TextAlignment.CENTER -> Paint.Align.CENTER
            TextAlignment.RIGHT -> Paint.Align.RIGHT
        }
    }

    fun generateWallpaper(
        goal: Goal,
        countdown: CountdownResult,
        width: Int = PIXEL_9_WIDTH,
        height: Int = PIXEL_9_HEIGHT
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val theme = goal.theme
        val bgColor = theme.bgColorHex.toInt()
        val textPrimaryColor = theme.textPrimaryHex.toInt()
        val textSecondaryColor = theme.textSecondaryHex.toInt()
        val accentColor = theme.accentHex.toInt()

        // 1. Draw Background
        canvas.drawColor(bgColor)

        val scale = width / 1080f
        val margin = 96f * scale
        val rightX = width - margin
        val centerX = width / 2f

        // 2. Setup Base Paints
        val hairlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textSecondaryColor
            alpha = 60
            strokeWidth = 2f * scale
            style = Paint.Style.STROKE
        }

        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
            style = Paint.Style.FILL
        }

        val topHeaderPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = textSecondaryColor
            textSize = 28f * scale
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            letterSpacing = 0.28f
            textAlign = getPaintAlign(goal.topHeaderAlignment)
        }

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = textPrimaryColor
            textSize = 42f * scale
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            letterSpacing = 0.12f
            textAlign = getPaintAlign(goal.titleAlignment)
        }

        val numberPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = textPrimaryColor
            val numChars = countdown.displayDays.length
            textSize = when {
                numChars <= 2 -> 260f * scale
                numChars == 3 -> 220f * scale
                else -> 180f * scale
            }
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            letterSpacing = -0.04f
            textAlign = getPaintAlign(goal.numberAlignment)
        }

        val unitLabelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = accentColor
            textSize = 30f * scale
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            letterSpacing = 0.35f
            textAlign = getPaintAlign(goal.numberAlignment)
        }

        val messagePaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = textSecondaryColor
            textSize = 32f * scale
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            letterSpacing = 0.04f
        }

        // Layout anchoring based on LayoutPosition
        val centerAnchorY = when (goal.layoutPosition) {
            LayoutPosition.CENTER -> height * 0.48f
            LayoutPosition.LOWER_THIRD -> height * 0.58f
            LayoutPosition.ELEVATED -> height * 0.40f
        }

        // Draw Minimalist Framing Marks (Swiss Grid Lines)
        val frameMargin = 72f * scale
        val crossSize = 14f * scale
        // Top Left Cross
        canvas.drawLine(frameMargin, frameMargin - crossSize, frameMargin, frameMargin + crossSize, hairlinePaint)
        canvas.drawLine(frameMargin - crossSize, frameMargin, frameMargin + crossSize, frameMargin, hairlinePaint)
        // Top Right Cross
        val frameRight = width - frameMargin
        canvas.drawLine(frameRight, frameMargin - crossSize, frameRight, frameMargin + crossSize, hairlinePaint)
        canvas.drawLine(frameRight - crossSize, frameMargin, frameRight + crossSize, frameMargin, hairlinePaint)

        // 1. Top Subtle Info Bar (Above center anchor) - Only drawn if enabled or custom text provided
        val customHeader = goal.topHeaderCustomText.trim()
        val headerText = when {
            customHeader.isNotEmpty() -> customHeader.uppercase()
            goal.showTopHeader -> "// TARGET: ${goal.shortTargetDate.uppercase()}"
            else -> null
        }

        val topInfoY = centerAnchorY - 320f * scale
        if (headerText != null) {
            val headerX = getX(goal.topHeaderAlignment, margin, centerX, rightX)
            canvas.drawText(headerText, headerX, topInfoY, topHeaderPaint)
            // Accent dot positioned relative to header alignment
            val dotX = when (goal.topHeaderAlignment) {
                TextAlignment.LEFT -> margin + 6f * scale
                TextAlignment.CENTER -> centerX
                TextAlignment.RIGHT -> rightX - 6f * scale
            }
            canvas.drawCircle(dotX, topInfoY + 28f * scale, 3.5f * scale, dotPaint)
        }

        // 2. Goal Title (Clean & prominent)
        val titleY = if (headerText != null) topInfoY + 90f * scale else centerAnchorY - 240f * scale
        val formattedTitle = goal.title.trim().uppercase()
        if (formattedTitle.isNotEmpty()) {
            val titleX = getX(goal.titleAlignment, margin, centerX, rightX)
            canvas.drawText(formattedTitle, titleX, titleY, titlePaint)

            // Divider hairline under title
            val dividerHalfWidth = 140f * scale
            val dividerY = titleY + 36f * scale
            when (goal.titleAlignment) {
                TextAlignment.LEFT -> canvas.drawLine(margin, dividerY, margin + (dividerHalfWidth * 2f), dividerY, hairlinePaint)
                TextAlignment.CENTER -> canvas.drawLine(centerX - dividerHalfWidth, dividerY, centerX + dividerHalfWidth, dividerY, hairlinePaint)
                TextAlignment.RIGHT -> canvas.drawLine(rightX - (dividerHalfWidth * 2f), dividerY, rightX, dividerY, hairlinePaint)
            }
        }

        // 3. Giant Countdown Number
        val numberBounds = Rect()
        numberPaint.getTextBounds(countdown.displayDays, 0, countdown.displayDays.length, numberBounds)
        val numberY = if (formattedTitle.isNotEmpty()) {
            (titleY + 36f * scale) + 220f * scale
        } else {
            centerAnchorY + 60f * scale
        }
        val numberX = getX(goal.numberAlignment, margin, centerX, rightX)
        canvas.drawText(countdown.displayDays, numberX, numberY, numberPaint)

        // 4. Unit Label below number (e.g. "Days to Go", "Days", "Days Remaining", or completely removed)
        val customUnit = goal.customUnitLabel.trim()
        val effectiveUnitLabel: String? = when {
            customUnit.isEmpty() -> null
            countdown.isToday && customUnit.equals("Days Remaining", ignoreCase = true) -> "DAY ZERO // TODAY"
            countdown.isPast && customUnit.equals("Days Remaining", ignoreCase = true) -> "TARGET REACHED"
            else -> customUnit.uppercase()
        }

        var messageStartY = numberY + 64f * scale
        if (effectiveUnitLabel != null) {
            val labelY = numberY + 68f * scale
            val labelX = getX(goal.numberAlignment, margin, centerX, rightX)
            canvas.drawText(effectiveUnitLabel, labelX, labelY, unitLabelPaint)
            messageStartY = labelY + 60f * scale
        }

        // 5. Motivational Message (Small, breathable, multi-line)
        val messageText = goal.motivationalMessage.trim()
        if (messageText.isNotEmpty()) {
            val maxTextWidth = (width - (margin * 2)).toInt()
            val staticLayoutAlignment = when (goal.messageAlignment) {
                TextAlignment.LEFT -> Layout.Alignment.ALIGN_NORMAL
                TextAlignment.CENTER -> Layout.Alignment.ALIGN_CENTER
                TextAlignment.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
            }

            val staticLayout = StaticLayout.Builder.obtain(
                messageText,
                0,
                messageText.length,
                messagePaint,
                maxTextWidth
            )
                .setAlignment(staticLayoutAlignment)
                .setLineSpacing(8f * scale, 1.15f)
                .setIncludePad(false)
                .build()

            val layoutTranslateX = when (goal.messageAlignment) {
                TextAlignment.LEFT -> margin
                TextAlignment.CENTER -> centerX - (maxTextWidth / 2f)
                TextAlignment.RIGHT -> rightX - maxTextWidth
            }

            canvas.save()
            canvas.translate(layoutTranslateX, messageStartY)
            staticLayout.draw(canvas)
            canvas.restore()
        }

        return bitmap
    }
}

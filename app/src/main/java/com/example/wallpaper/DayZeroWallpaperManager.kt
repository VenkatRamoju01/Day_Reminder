package com.example.wallpaper

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.model.CountdownCalculator
import com.example.model.Goal
import com.example.model.WallpaperTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDate

sealed class WallpaperApplyResult {
    data class Success(val which: Int, val message: String) : WallpaperApplyResult()
    data class Failure(val error: String, val throwable: Throwable? = null) : WallpaperApplyResult()
}

class DayZeroWallpaperManager(private val context: Context) {

    private val wallpaperManager: WallpaperManager = WallpaperManager.getInstance(context)

    /**
     * Checks if the device permits wallpaper changes.
     */
    fun isWallpaperAllowed(): Boolean {
        return try {
            wallpaperManager.isSetWallpaperAllowed
        } catch (e: Exception) {
            true
        }
    }

    /**
     * Applies the generated wallpaper bitmap to the specified target screen(s)
     * using Android's WallpaperManager with FLAG_SYSTEM and/or FLAG_LOCK.
     */
    suspend fun applyGoalWallpaper(
        goal: Goal,
        targetScreen: WallpaperTarget = goal.targetScreen,
        referenceDate: LocalDate = LocalDate.now()
    ): WallpaperApplyResult = withContext(Dispatchers.IO) {
        if (!isWallpaperAllowed()) {
            return@withContext WallpaperApplyResult.Failure("Device policy does not permit changing wallpapers.")
        }

        try {
            val countdown = CountdownCalculator.calculate(goal.targetDate, referenceDate)
            val (width, height) = WallpaperGenerator.getOptimalDimensions(context)
            val bitmap = WallpaperGenerator.generateWallpaper(goal, countdown, width, height)

            val flag = when (targetScreen) {
                WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                WallpaperTarget.HOME_SCREEN -> WallpaperManager.FLAG_SYSTEM
                WallpaperTarget.LOCK_SCREEN -> WallpaperManager.FLAG_LOCK
            }

            // Android N+ (API 24+) supports setBitmap with which flag
            val resultId = wallpaperManager.setBitmap(bitmap, null, true, flag)

            val screenLabel = when (targetScreen) {
                WallpaperTarget.BOTH -> "both Home and Lock screens"
                WallpaperTarget.HOME_SCREEN -> "Home screen"
                WallpaperTarget.LOCK_SCREEN -> "Lock screen"
            }

            WallpaperApplyResult.Success(
                which = flag,
                message = "Wallpaper successfully applied to $screenLabel (Pixel 9 • ${width}x${height}px)."
            )
        } catch (e: SecurityException) {
            WallpaperApplyResult.Failure("Permission denied while applying wallpaper.", e)
        } catch (e: Exception) {
            WallpaperApplyResult.Failure(e.localizedMessage ?: "Failed to set wallpaper bitmap.", e)
        }
    }

    /**
     * Exports the wallpaper bitmap directly to the device's Pictures folder as a PNG.
     */
    suspend fun exportWallpaperToGallery(
        goal: Goal,
        referenceDate: LocalDate = LocalDate.now()
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val countdown = CountdownCalculator.calculate(goal.targetDate, referenceDate)
            val (width, height) = WallpaperGenerator.getOptimalDimensions(context)
            val bitmap = WallpaperGenerator.generateWallpaper(goal, countdown, width, height)

            val filename = "DAYZERO_${goal.title.replace("\\s+".toRegex(), "_")}_${countdown.displayDays}D.png"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DayZero")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@withContext Result.failure(Exception("Failed to create MediaStore entry"))

                resolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

                Result.success(uri)
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val dayZeroDir = File(picturesDir, "DayZero").apply { mkdirs() }
                val file = File(dayZeroDir, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                Result.success(Uri.fromFile(file))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

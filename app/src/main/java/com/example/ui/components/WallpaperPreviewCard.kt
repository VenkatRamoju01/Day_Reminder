package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CountdownResult
import com.example.model.WallpaperTarget
import com.example.ui.theme.DayZeroAccentWhite
import com.example.ui.theme.DayZeroBlack
import com.example.ui.theme.DayZeroBorder
import com.example.ui.theme.DayZeroDarkCharcoal
import com.example.ui.theme.DayZeroSurface
import com.example.ui.theme.DayZeroTextMuted
import com.example.ui.theme.DayZeroTextPrimary
import com.example.ui.theme.DayZeroTextSecondary

enum class PreviewOverlay {
    CLEAN,
    LOCK_SCREEN,
    HOME_SCREEN
}

@Composable
fun WallpaperPreviewCard(
    bitmap: Bitmap?,
    countdown: CountdownResult,
    isApplying: Boolean,
    targetScreen: WallpaperTarget,
    onApplyClick: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var overlayMode by remember { mutableStateOf(PreviewOverlay.CLEAN) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DayZeroDarkCharcoal)
            .border(1.dp, DayZeroBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Centered Overlay mode toggles
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(DayZeroSurface)
                .border(0.5.dp, DayZeroBorder, RoundedCornerShape(12.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OverlayChip(
                label = "Pure",
                icon = Icons.Outlined.Visibility,
                selected = overlayMode == PreviewOverlay.CLEAN,
                onClick = { overlayMode = PreviewOverlay.CLEAN }
            )
            OverlayChip(
                label = "Lock",
                icon = Icons.Outlined.Lock,
                selected = overlayMode == PreviewOverlay.LOCK_SCREEN,
                onClick = { overlayMode = PreviewOverlay.LOCK_SCREEN }
            )
            OverlayChip(
                label = "Home",
                icon = Icons.Outlined.Home,
                selected = overlayMode == PreviewOverlay.HOME_SCREEN,
                onClick = { overlayMode = PreviewOverlay.HOME_SCREEN }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Phone Frame with 1080x2424 Aspect Ratio (~9:20.2)
        Box(
            modifier = Modifier
                .width(220.dp)
                .aspectRatio(1080f / 2424f)
                .clip(RoundedCornerShape(32.dp))
                .border(2.5.dp, DayZeroBorder, RoundedCornerShape(32.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Wallpaper Live Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = DayZeroTextSecondary,
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            // Punch hole camera simulation for Pixel 9
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF101010))
                    .align(Alignment.TopCenter)
            )

            // Dynamic Overlays
            when (overlayMode) {
                PreviewOverlay.LOCK_SCREEN -> Pixel9LockScreenOverlay()
                PreviewOverlay.HOME_SCREEN -> Pixel9HomeScreenOverlay()
                PreviewOverlay.CLEAN -> Unit
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onApplyClick,
                enabled = !isApplying,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DayZeroAccentWhite,
                    contentColor = DayZeroBlack
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("apply_wallpaper_button")
            ) {
                if (isApplying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = DayZeroBlack,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "APPLYING...",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Wallpaper,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "APPLY",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            FilledTonalButton(
                onClick = onExportClick,
                enabled = !isApplying,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = DayZeroSurface,
                    contentColor = DayZeroTextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("export_wallpaper_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Save high-res PNG to gallery",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SAVE PNG",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun OverlayChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) DayZeroAccentWhite else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) DayZeroBlack else DayZeroTextMuted,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) DayZeroBlack else DayZeroTextMuted
        )
    }
}

@Composable
private fun Pixel9LockScreenOverlay() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Status bar simulation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("09:41", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("5G", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
                Text("100%", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
            }
        }

        // Pixel 9 Iconic Lock Screen Clock at top
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            Text(
                text = "09:41",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "Fri, Nov 6 • 18°C",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 9.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Pixel 9 Fingerprint & Bottom Shortcuts
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Bottom home bar
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
private fun Pixel9HomeScreenOverlay() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Status bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("09:41", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("5G", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
                Text("100%", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
            }
        }

        // At a Glance widget top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Friday, Nov 6 • 18°C",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Home screen bottom dock & search bar simulation
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            // App icon dock row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Google Search pill bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("G", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            // Navigation bar pill
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
            )
        }
    }
}

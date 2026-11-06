package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.GoalEditorSection
import com.example.ui.components.WallpaperPreviewCard
import com.example.ui.theme.DayZeroBlack

@Composable
fun DayZeroScreen(
    viewModel: DayZeroViewModel,
    modifier: Modifier = Modifier
) {
    val goal by viewModel.goal.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val previewBitmap by viewModel.previewBitmap.collectAsState()
    val isApplying by viewModel.isApplying.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DayZeroBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Hero Preview Card (Centered overlays, 'Apply' button, clean display)
            item {
                WallpaperPreviewCard(
                    bitmap = previewBitmap,
                    countdown = countdown,
                    isApplying = isApplying,
                    targetScreen = goal.targetScreen,
                    onApplyClick = { viewModel.applyWallpaper() },
                    onExportClick = { viewModel.exportWallpaperToGallery() }
                )
            }

            // 2. Goal Editor Section (Reordered hierarchy with Left/Center/Right alignment)
            item {
                GoalEditorSection(
                    goal = goal,
                    countdown = countdown,
                    onTitleChange = { viewModel.updateTitle(it) },
                    onTargetDateChange = { viewModel.updateTargetDate(it) },
                    onMotivationalMessageChange = { viewModel.updateMotivationalMessage(it) },
                    onShowTopHeaderChange = { viewModel.updateShowTopHeader(it) },
                    onTopHeaderCustomTextChange = { viewModel.updateTopHeaderCustomText(it) },
                    onCustomUnitLabelChange = { viewModel.updateCustomUnitLabel(it) },
                    onTopHeaderAlignmentChange = { viewModel.updateTopHeaderAlignment(it) },
                    onTitleAlignmentChange = { viewModel.updateTitleAlignment(it) },
                    onDateAlignmentChange = { viewModel.updateDateAlignment(it) },
                    onNumberAlignmentChange = { viewModel.updateNumberAlignment(it) },
                    onMessageAlignmentChange = { viewModel.updateMessageAlignment(it) },
                    onThemeChange = { viewModel.updateTheme(it) },
                    onLayoutPositionChange = { viewModel.updateLayoutPosition(it) },
                    onTargetScreenChange = { viewModel.updateTargetScreen(it) },
                    onAutoDailyChange = { viewModel.updateAutoDaily(it) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

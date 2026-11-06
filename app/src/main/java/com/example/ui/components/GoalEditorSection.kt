package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CountdownResult
import com.example.model.Goal
import com.example.model.LayoutPosition
import com.example.model.TextAlignment
import com.example.model.WallpaperTarget
import com.example.model.WallpaperTheme
import com.example.ui.theme.DayZeroAccentWhite
import com.example.ui.theme.DayZeroBlack
import com.example.ui.theme.DayZeroBorder
import com.example.ui.theme.DayZeroDarkCharcoal
import com.example.ui.theme.DayZeroSurface
import com.example.ui.theme.DayZeroSurfaceVariant
import com.example.ui.theme.DayZeroTextMuted
import com.example.ui.theme.DayZeroTextPrimary
import com.example.ui.theme.DayZeroTextSecondary
import java.time.LocalDate

@Composable
fun GoalEditorSection(
    goal: Goal,
    countdown: CountdownResult,
    onTitleChange: (String) -> Unit,
    onTargetDateChange: (LocalDate) -> Unit,
    onMotivationalMessageChange: (String) -> Unit,
    onShowTopHeaderChange: (Boolean) -> Unit,
    onTopHeaderCustomTextChange: (String) -> Unit,
    onCustomUnitLabelChange: (String) -> Unit,
    onTopHeaderAlignmentChange: (TextAlignment) -> Unit,
    onTitleAlignmentChange: (TextAlignment) -> Unit,
    onDateAlignmentChange: (TextAlignment) -> Unit,
    onNumberAlignmentChange: (TextAlignment) -> Unit,
    onMessageAlignmentChange: (TextAlignment) -> Unit,
    onThemeChange: (WallpaperTheme) -> Unit,
    onLayoutPositionChange: (LayoutPosition) -> Unit,
    onTargetScreenChange: (WallpaperTarget) -> Unit,
    onAutoDailyChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerModal(
            initialDate = goal.targetDate,
            onDateSelected = { newDate -> onTargetDateChange(newDate) },
            onDismiss = { showDatePicker = false }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WALLPAPER ELEMENTS & ALIGNMENT",
                style = MaterialTheme.typography.labelSmall,
                color = DayZeroTextSecondary,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "1 ACTIVE GOAL",
                style = MaterialTheme.typography.labelSmall,
                color = DayZeroAccentWhite,
                fontWeight = FontWeight.Bold
            )
        }

        // ==========================================
        // 1. TOP TARGET HEADER (Placed first per hierarchy)
        // ==========================================
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DayZeroDarkCharcoal),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DayZeroBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "1. TOP TARGET HEADER",
                            style = MaterialTheme.typography.labelSmall,
                            color = DayZeroTextSecondary
                        )
                        Text(
                            text = if (goal.showTopHeader || goal.topHeaderCustomText.isNotBlank()) {
                                "Header text is displayed above title"
                            } else {
                                "Disabled (Clean minimalist layout)"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = DayZeroTextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = goal.showTopHeader || goal.topHeaderCustomText.isNotBlank(),
                        onCheckedChange = { isChecked ->
                            onShowTopHeaderChange(isChecked)
                            if (!isChecked) {
                                onTopHeaderCustomTextChange("")
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DayZeroBlack,
                            checkedTrackColor = DayZeroAccentWhite,
                            uncheckedThumbColor = DayZeroTextMuted,
                            uncheckedTrackColor = DayZeroSurface
                        ),
                        modifier = Modifier.testTag("top_header_switch")
                    )
                }

                if (goal.showTopHeader || goal.topHeaderCustomText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = goal.topHeaderCustomText,
                        onValueChange = onTopHeaderCustomTextChange,
                        label = { Text("Top Header Text", color = DayZeroTextSecondary) },
                        placeholder = { Text("e.g. // TARGET: ${goal.shortTargetDate.uppercase()}", color = DayZeroTextMuted) },
                        singleLine = true,
                        trailingIcon = {
                            if (goal.topHeaderCustomText.isNotEmpty()) {
                                IconButton(onClick = { onTopHeaderCustomTextChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = DayZeroTextMuted
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("top_header_custom_text_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DayZeroTextPrimary,
                            unfocusedTextColor = DayZeroTextPrimary,
                            focusedBorderColor = DayZeroAccentWhite,
                            unfocusedBorderColor = DayZeroBorder,
                            focusedContainerColor = DayZeroDarkCharcoal,
                            unfocusedContainerColor = DayZeroDarkCharcoal
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Header Alignment",
                        fontSize = 11.sp,
                        color = DayZeroTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AlignmentSelector(
                        selected = goal.topHeaderAlignment,
                        onSelect = onTopHeaderAlignmentChange
                    )
                }
            }
        }

        // ==========================================
        // 2. GOAL TITLE
        // ==========================================
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DayZeroDarkCharcoal),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DayZeroBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "2. GOAL TITLE",
                    style = MaterialTheme.typography.labelSmall,
                    color = DayZeroTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = goal.title,
                    onValueChange = onTitleChange,
                    label = { Text("Goal Title", color = DayZeroTextSecondary) },
                    placeholder = { Text("e.g. Marathon 2026, Bar Exam, Launch App", color = DayZeroTextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DayZeroTextPrimary,
                        unfocusedTextColor = DayZeroTextPrimary,
                        focusedBorderColor = DayZeroAccentWhite,
                        unfocusedBorderColor = DayZeroBorder,
                        focusedContainerColor = DayZeroDarkCharcoal,
                        unfocusedContainerColor = DayZeroDarkCharcoal
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Title Alignment",
                    fontSize = 11.sp,
                    color = DayZeroTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                AlignmentSelector(
                    selected = goal.titleAlignment,
                    onSelect = onTitleAlignmentChange
                )
            }
        }

        // ==========================================
        // 3. TARGET DATE
        // ==========================================
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DayZeroDarkCharcoal),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DayZeroBorder, RoundedCornerShape(16.dp))
                .testTag("target_date_picker_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = DayZeroAccentWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. TARGET DATE",
                            style = MaterialTheme.typography.labelSmall,
                            color = DayZeroTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DayZeroSurface)
                            .border(0.5.dp, DayZeroBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${countdown.displayDays} DAYS",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = DayZeroAccentWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = goal.formattedTargetDate,
                    style = MaterialTheme.typography.headlineMedium,
                    color = DayZeroTextPrimary,
                    fontSize = 20.sp
                )

                Text(
                    text = countdown.detailedStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DayZeroTextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Prominent Change Target Date Button
                Button(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DayZeroSurfaceVariant,
                        contentColor = DayZeroAccentWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(1.dp, DayZeroBorder, RoundedCornerShape(10.dp))
                        .testTag("change_target_date_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = "Pick Target Date",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CHANGE TARGET DATE (CALENDAR)",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Date Shortcuts
                val today = LocalDate.now()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DateShortcutChip("+7 Days") { onTargetDateChange(today.plusDays(7)) }
                    DateShortcutChip("+30 Days") { onTargetDateChange(today.plusDays(30)) }
                    DateShortcutChip("+90 Days") { onTargetDateChange(today.plusDays(90)) }
                    DateShortcutChip("+180 Days") { onTargetDateChange(today.plusDays(180)) }
                    DateShortcutChip("New Year") {
                        val nextYear = today.year + 1
                        onTargetDateChange(LocalDate.of(nextYear, 1, 1))
                    }
                }
            }
        }

        // ==========================================
        // 4. COUNTDOWN NUMBER & LABEL BELOW NUMBER
        // ==========================================
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DayZeroDarkCharcoal),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DayZeroBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Label,
                        contentDescription = null,
                        tint = DayZeroAccentWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4. COUNTDOWN NUMBER & LABEL",
                        style = MaterialTheme.typography.labelSmall,
                        color = DayZeroTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = goal.customUnitLabel,
                    onValueChange = onCustomUnitLabelChange,
                    label = { Text("Label Below Countdown Number", color = DayZeroTextSecondary) },
                    placeholder = { Text("e.g. Days to Go, Days, or leave blank to remove", color = DayZeroTextMuted) },
                    trailingIcon = {
                        if (goal.customUnitLabel.isNotEmpty()) {
                            IconButton(onClick = { onCustomUnitLabelChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Remove text",
                                    tint = DayZeroTextMuted
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_unit_label_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DayZeroTextPrimary,
                        unfocusedTextColor = DayZeroTextPrimary,
                        focusedBorderColor = DayZeroAccentWhite,
                        unfocusedBorderColor = DayZeroBorder,
                        focusedContainerColor = DayZeroDarkCharcoal,
                        unfocusedContainerColor = DayZeroDarkCharcoal
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (goal.customUnitLabel.isBlank()) {
                        "Label removed. Only the large number is displayed."
                    } else {
                        "Currently displays '${goal.customUnitLabel}' underneath the number."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = DayZeroTextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetChip("Days to Go") { onCustomUnitLabelChange("Days to Go") }
                    PresetChip("Days") { onCustomUnitLabelChange("Days") }
                    PresetChip("Days Remaining") { onCustomUnitLabelChange("Days Remaining") }
                    PresetChip("Remove / None") { onCustomUnitLabelChange("") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Countdown Alignment (Number & Label)",
                    fontSize = 11.sp,
                    color = DayZeroTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                AlignmentSelector(
                    selected = goal.numberAlignment,
                    onSelect = onNumberAlignmentChange
                )
            }
        }

        // ==========================================
        // 5. MOTIVATIONAL MESSAGE
        // ==========================================
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DayZeroDarkCharcoal),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DayZeroBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "5. MOTIVATIONAL MESSAGE",
                    style = MaterialTheme.typography.labelSmall,
                    color = DayZeroTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = goal.motivationalMessage,
                    onValueChange = onMotivationalMessageChange,
                    label = { Text("Motivational Message", color = DayZeroTextSecondary) },
                    placeholder = { Text("Short, powerful directive", color = DayZeroTextMuted) },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("motivational_message_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DayZeroTextPrimary,
                        unfocusedTextColor = DayZeroTextPrimary,
                        focusedBorderColor = DayZeroAccentWhite,
                        unfocusedBorderColor = DayZeroBorder,
                        focusedContainerColor = DayZeroDarkCharcoal,
                        unfocusedContainerColor = DayZeroDarkCharcoal
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuoteSuggestionChip("Freedom. Focus on today.") {
                        onMotivationalMessageChange("Freedom. Focus on today.")
                    }
                    QuoteSuggestionChip("Discipline > Motivation") {
                        onMotivationalMessageChange("Discipline equals freedom. Focus entirely on today.")
                    }
                    QuoteSuggestionChip("Every rep counts") {
                        onMotivationalMessageChange("Every single rep counts. No zero days.")
                    }
                    QuoteSuggestionChip("Stay relentless") {
                        onMotivationalMessageChange("The resistance is real. Push through.")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Message Alignment",
                    fontSize = 11.sp,
                    color = DayZeroTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                AlignmentSelector(
                    selected = goal.messageAlignment,
                    onSelect = onMessageAlignmentChange
                )
            }
        }

        // ==========================================
        // 6. WALLPAPER THEME
        // ==========================================
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "MINIMALIST THEME",
                style = MaterialTheme.typography.labelSmall,
                color = DayZeroTextSecondary,
                letterSpacing = 1.2.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WallpaperTheme.values().forEach { theme ->
                    val isSelected = goal.theme == theme
                    ThemeCard(
                        theme = theme,
                        isSelected = isSelected,
                        onClick = { onThemeChange(theme) }
                    )
                }
            }
        }

        // ==========================================
        // 7. COUNTDOWN POSITIONING (SAFE ZONES)
        // ==========================================
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "VERTICAL POSITIONING",
                style = MaterialTheme.typography.labelSmall,
                color = DayZeroTextSecondary,
                letterSpacing = 1.2.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LayoutPosition.values().forEach { pos ->
                    val isSelected = goal.layoutPosition == pos
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) DayZeroSurfaceVariant else DayZeroDarkCharcoal)
                            .border(
                                1.dp,
                                if (isSelected) DayZeroAccentWhite else DayZeroBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onLayoutPositionChange(pos) }
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pos.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) DayZeroAccentWhite else DayZeroTextSecondary,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }
        }

        // ==========================================
        // 8. TARGET SCREEN
        // ==========================================
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "APPLY TARGET",
                style = MaterialTheme.typography.labelSmall,
                color = DayZeroTextSecondary,
                letterSpacing = 1.2.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WallpaperTarget.values().forEach { target ->
                    val isSelected = goal.targetScreen == target
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) DayZeroSurfaceVariant else DayZeroDarkCharcoal)
                            .border(
                                1.dp,
                                if (isSelected) DayZeroAccentWhite else DayZeroBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onTargetScreenChange(target) }
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = target.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) DayZeroAccentWhite else DayZeroTextSecondary
                        )
                    }
                }
            }
        }

        // ==========================================
        // 9. DAILY MIDNIGHT REFRESH SWITCH
        // ==========================================
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DayZeroDarkCharcoal),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DayZeroBorder, RoundedCornerShape(14.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = DayZeroAccentWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Daily Midnight Refresh",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = DayZeroTextPrimary
                        )
                        Text(
                            text = "WorkManager automatically updates Pixel 9 wallpaper at 00:01 AM",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DayZeroTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = goal.autoDailyUpdate,
                    onCheckedChange = onAutoDailyChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DayZeroBlack,
                        checkedTrackColor = DayZeroAccentWhite,
                        uncheckedThumbColor = DayZeroTextMuted,
                        uncheckedTrackColor = DayZeroSurface
                    ),
                    modifier = Modifier.testTag("auto_daily_switch")
                )
            }
        }
    }
}

@Composable
fun AlignmentSelector(
    selected: TextAlignment,
    onSelect: (TextAlignment) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DayZeroSurface)
            .border(0.5.dp, DayZeroBorder, RoundedCornerShape(8.dp))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        TextAlignment.values().forEach { align ->
            val isSelected = selected == align
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) DayZeroAccentWhite else Color.Transparent)
                    .clickable { onSelect(align) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = align.label,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) DayZeroBlack else DayZeroTextMuted
                )
            }
        }
    }
}

@Composable
private fun ThemeCard(
    theme: WallpaperTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(theme.cardBgColorHex))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) DayZeroAccentWhite else DayZeroBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(theme.bgColorHex))
                        .border(1.dp, Color(theme.textPrimaryHex), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = theme.label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = Color(theme.textPrimaryHex),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = theme.description,
                fontSize = 9.sp,
                color = Color(theme.textSecondaryHex),
                maxLines = 2,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun DateShortcutChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DayZeroSurface)
            .border(0.5.dp, DayZeroBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = DayZeroTextSecondary
        )
    }
}

@Composable
private fun PresetChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DayZeroSurface)
            .border(0.5.dp, DayZeroBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.SansSerif,
            color = DayZeroTextPrimary
        )
    }
}

@Composable
private fun QuoteSuggestionChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DayZeroDarkCharcoal)
            .border(0.5.dp, DayZeroBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = DayZeroTextMuted,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = DayZeroTextSecondary
            )
        }
    }
}

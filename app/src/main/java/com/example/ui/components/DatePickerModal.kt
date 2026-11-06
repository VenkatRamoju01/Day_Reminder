package com.example.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.example.ui.theme.DayZeroAccentWhite
import com.example.ui.theme.DayZeroBlack
import com.example.ui.theme.DayZeroBorder
import com.example.ui.theme.DayZeroDarkCharcoal
import com.example.ui.theme.DayZeroTextPrimary
import com.example.ui.theme.DayZeroTextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = initialDate
        .atStartOfDay(ZoneId.of("UTC"))
        .toInstant()
        .toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val selectedDate = Instant.ofEpochMilli(selectedMillis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                    onDismiss()
                }
            ) {
                Text("CONFIRM", color = DayZeroAccentWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = DayZeroTextSecondary)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = DayZeroDarkCharcoal
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = DayZeroDarkCharcoal,
                titleContentColor = DayZeroTextPrimary,
                headlineContentColor = DayZeroTextPrimary,
                weekdayContentColor = DayZeroTextSecondary,
                subheadContentColor = DayZeroTextSecondary,
                yearContentColor = DayZeroTextSecondary,
                currentYearContentColor = DayZeroAccentWhite,
                selectedYearContentColor = DayZeroBlack,
                selectedYearContainerColor = DayZeroAccentWhite,
                dayContentColor = DayZeroTextPrimary,
                selectedDayContentColor = DayZeroBlack,
                selectedDayContainerColor = DayZeroAccentWhite,
                todayContentColor = DayZeroAccentWhite,
                todayDateBorderColor = DayZeroBorder
            )
        )
    }
}

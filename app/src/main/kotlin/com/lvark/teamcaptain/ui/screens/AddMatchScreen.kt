package com.lvark.teamcaptain.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lvark.teamcaptain.R
import com.lvark.teamcaptain.viewmodel.AddMatchViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("FunctionName")
fun AddMatchScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddMatchViewModel = hiltViewModel(),
) {
    var opponent by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isHome by remember { mutableStateOf(true) }
    var totalMatchDuration by remember { mutableStateOf("40") }
    var numberOfBlocks by remember { mutableStateOf("4") }
    var blockDuration by remember { mutableStateOf("10") }

    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedHour by remember { mutableStateOf(14) }
    var selectedMinute by remember { mutableStateOf(0) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var opponentError by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    val formattedDate by remember(selectedDateMillis) {
        derivedStateOf { dateFormat.format(Date(selectedDateMillis)) }
    }
    val formattedTime by remember(selectedHour, selectedMinute) {
        derivedStateOf { String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute) }
    }

    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = selectedDateMillis,
            )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDateMillis = it
                        }
                        showDatePicker = false
                    },
                ) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState =
            rememberTimePickerState(
                initialHour = selectedHour,
                initialMinute = selectedMinute,
            )
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute
                        showTimePicker = false
                    },
                ) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
        ) {
            TimePicker(state = timePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.matches_add_match)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = opponent,
                onValueChange = {
                    opponent = it
                    opponentError = false
                },
                label = { Text(stringResource(R.string.matches_opponent)) },
                modifier = Modifier.fillMaxWidth(),
                isError = opponentError,
                supportingText =
                    if (opponentError) {
                        { Text(stringResource(R.string.matches_opponent_required)) }
                    } else {
                        null
                    },
                singleLine = true,
            )

            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                label = { Text(stringResource(R.string.matches_date)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.matches_select_date),
                        )
                    }
                },
            )

            OutlinedTextField(
                value = formattedTime,
                onValueChange = {},
                label = { Text(stringResource(R.string.matches_time)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.matches_select_time),
                        )
                    }
                },
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(R.string.matches_location)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.matches_home_match),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = isHome,
                    onCheckedChange = { isHome = it },
                )
            }

            Text(
                text = stringResource(R.string.matches_match_format_section),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp),
            )

            OutlinedTextField(
                value = totalMatchDuration,
                onValueChange = { totalMatchDuration = it.filter { char -> char.isDigit() } },
                label = { Text(stringResource(R.string.matches_total_duration)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                supportingText = { Text(stringResource(R.string.matches_total_duration_hint)) },
            )

            OutlinedTextField(
                value = numberOfBlocks,
                onValueChange = { numberOfBlocks = it.filter { char -> char.isDigit() } },
                label = { Text(stringResource(R.string.matches_number_of_blocks)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                supportingText = { Text(stringResource(R.string.matches_blocks_hint)) },
            )

            OutlinedTextField(
                value = blockDuration,
                onValueChange = { blockDuration = it.filter { char -> char.isDigit() } },
                label = { Text(stringResource(R.string.matches_block_duration)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                supportingText = { Text(stringResource(R.string.matches_block_duration_hint)) },
            )

            Button(
                onClick = {
                    if (opponent.isBlank()) {
                        opponentError = true
                    } else {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = selectedDateMillis
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        calendar.set(Calendar.MINUTE, selectedMinute)
                        calendar.set(Calendar.SECOND, 0)

                        viewModel.addMatch(
                            opponent = opponent.trim(),
                            dateTime = calendar.timeInMillis,
                            location = location.trim().takeIf { it.isNotBlank() },
                            isHome = isHome,
                            totalMatchDurationMinutes = totalMatchDuration.toIntOrNull() ?: 40,
                            numberOfBlocks = numberOfBlocks.toIntOrNull() ?: 4,
                            blockDurationMinutes = blockDuration.toIntOrNull() ?: 10,
                            onSuccess = onNavigateBack,
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.common_save))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("FunctionName")
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = content,
    )
}

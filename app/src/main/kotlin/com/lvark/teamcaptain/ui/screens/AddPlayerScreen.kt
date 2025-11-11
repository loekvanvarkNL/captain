package com.lvark.teamcaptain.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.lvark.teamcaptain.model.entity.Position
import com.lvark.teamcaptain.model.entity.PreferredFoot
import com.lvark.teamcaptain.viewmodel.AddPlayerViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
@Suppress("FunctionName")
fun AddPlayerScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddPlayerViewModel = hiltViewModel(),
) {
    var firstName by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var numberText by remember { mutableStateOf("") }
    var selectedFoot by remember { mutableStateOf(PreferredFoot.RIGHT) }
    var selectedPositions by remember { mutableStateOf(setOf<Position>()) }
    var firstNameError by remember { mutableStateOf(false) }
    var surnameError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.team_add_player)) },
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
                value = firstName,
                onValueChange = {
                    firstName = it
                    firstNameError = false
                },
                label = { Text(stringResource(R.string.team_player_first_name)) },
                modifier = Modifier.fillMaxWidth(),
                isError = firstNameError,
                supportingText =
                    if (firstNameError) {
                        { Text(stringResource(R.string.team_first_name_required)) }
                    } else {
                        null
                    },
                singleLine = true,
            )

            OutlinedTextField(
                value = surname,
                onValueChange = {
                    surname = it
                    surnameError = false
                },
                label = { Text(stringResource(R.string.team_player_surname)) },
                modifier = Modifier.fillMaxWidth(),
                isError = surnameError,
                supportingText =
                    if (surnameError) {
                        { Text(stringResource(R.string.team_surname_required)) }
                    } else {
                        null
                    },
                singleLine = true,
            )

            OutlinedTextField(
                value = numberText,
                onValueChange = { numberText = it.filter { char -> char.isDigit() } },
                label = { Text(stringResource(R.string.team_player_number)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.team_preferred_foot),
                    style = MaterialTheme.typography.titleMedium,
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PreferredFoot.entries.forEach { foot ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedFoot = foot },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selectedFoot == foot,
                                onClick = { selectedFoot = foot },
                            )
                            Text(
                                text =
                                    when (foot) {
                                        PreferredFoot.LEFT -> stringResource(R.string.team_preferred_foot_left)
                                        PreferredFoot.RIGHT -> stringResource(R.string.team_preferred_foot_right)
                                        PreferredFoot.BOTH -> stringResource(R.string.team_preferred_foot_both)
                                    },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.team_preferred_positions),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.team_positions_limit),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Position.entries.forEach { position ->
                        val isSelected = position in selectedPositions
                        val canSelect = isSelected || selectedPositions.size < 2
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (canSelect) {
                                    selectedPositions =
                                        if (isSelected) {
                                            selectedPositions - position
                                        } else {
                                            selectedPositions + position
                                        }
                                }
                            },
                            label = { Text("${position.abbreviation} - ${position.fullName}") },
                            enabled = canSelect,
                        )
                    }
                }
            }

            Button(
                onClick = {
                    var hasError = false
                    if (firstName.isBlank()) {
                        firstNameError = true
                        hasError = true
                    }
                    if (surname.isBlank()) {
                        surnameError = true
                        hasError = true
                    }
                    if (!hasError) {
                        viewModel.addPlayer(
                            firstName = firstName.trim(),
                            surname = surname.trim(),
                            number = numberText.toIntOrNull(),
                            preferredFoot = selectedFoot,
                            preferredPositions = selectedPositions.toList(),
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

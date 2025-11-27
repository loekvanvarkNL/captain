package com.lvark.teamcaptain.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lvark.teamcaptain.R
import com.lvark.teamcaptain.model.entity.Player
import com.lvark.teamcaptain.model.entity.Position
import com.lvark.teamcaptain.viewmodel.LineupBuilderViewModel
import com.lvark.teamcaptain.viewmodel.PlayerPosition
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
@Suppress("FunctionName")
fun LineupBuilderScreen(
    matchId: Long?,
    onNavigateBack: () -> Unit,
    onNavigateToBlockView: (Long) -> Unit = {},
    viewModel: LineupBuilderViewModel = hiltViewModel(),
) {
    val match by viewModel.match.collectAsStateWithLifecycle()
    val currentBlockNumber by viewModel.currentBlockNumber.collectAsStateWithLifecycle()
    val fieldPlayers by viewModel.fieldPlayers.collectAsStateWithLifecycle()
    val benchPlayers by viewModel.benchPlayers.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            R.string.lineup_builder_title,
                            match?.opponent ?: "",
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { matchId?.let { onNavigateToBlockView(it) } },
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = stringResource(R.string.lineup_view_block_overview),
                        )
                    }
                    IconButton(
                        onClick = { viewModel.saveAllBlocks(onNavigateBack) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.common_save),
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Block navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = { viewModel.goToPreviousBlock() },
                    enabled = currentBlockNumber > 1,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.lineup_previous_block),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.lineup_previous))
                }

                Text(
                    text = stringResource(R.string.lineup_block_number, currentBlockNumber, match?.numberOfBlocks ?: 0),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                OutlinedButton(
                    onClick = { viewModel.goToNextBlock() },
                    enabled = currentBlockNumber < (match?.numberOfBlocks ?: 0),
                ) {
                    Text(stringResource(R.string.lineup_next))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(R.string.lineup_next_block),
                    )
                }
            }

            // Auto-rotation button
            FilledTonalButton(
                onClick = { viewModel.generateAutoRotation() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.lineup_auto_rotate))
            }

            // Football pitch
            FootballPitch(
                fieldPlayers = fieldPlayers,
                onPlayerMoved = { player, position ->
                    viewModel.movePlayerToField(player, position)
                },
                onPositionCleared = { position ->
                    viewModel.removePlayerFromPosition(position)
                },
            )

            // Bench
            BenchSection(
                benchPlayers = benchPlayers,
                onPlayerDropped = { player ->
                    viewModel.movePlayerToBench(player)
                },
            )

            // Save button
            Button(
                onClick = { viewModel.saveCurrentBlock() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.lineup_save_block))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Suppress("FunctionName")
private fun FootballPitch(
    fieldPlayers: List<PlayerPosition>,
    onPlayerMoved: (Player, Position) -> Unit,
    onPositionCleared: (Position) -> Unit,
) {
    // Football pitch green color
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFF2E7D32),
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Title
            Text(
                text = stringResource(R.string.lineup_field),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            // Forward positions (2 players)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                PositionSlot(
                    position = Position.FW,
                    playerPosition = fieldPlayers.find { it.position == Position.FW },
                    onPlayerDropped = onPlayerMoved,
                    onClearPosition = onPositionCleared,
                    label = "FW",
                )
                PositionSlot(
                    position = Position.FW,
                    playerPosition = fieldPlayers.filter { it.position == Position.FW }.getOrNull(1),
                    onPlayerDropped = onPlayerMoved,
                    onClearPosition = onPositionCleared,
                    label = "FW",
                )
            }

            // Midfielder position (1 player)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                PositionSlot(
                    position = Position.MF,
                    playerPosition = fieldPlayers.find { it.position == Position.MF },
                    onPlayerDropped = onPlayerMoved,
                    onClearPosition = onPositionCleared,
                    label = "MF",
                )
            }

            // Defender positions (2 players)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                PositionSlot(
                    position = Position.DF,
                    playerPosition = fieldPlayers.find { it.position == Position.DF },
                    onPlayerDropped = onPlayerMoved,
                    onClearPosition = onPositionCleared,
                    label = "DF",
                )
                PositionSlot(
                    position = Position.DF,
                    playerPosition = fieldPlayers.filter { it.position == Position.DF }.getOrNull(1),
                    onPlayerDropped = onPlayerMoved,
                    onClearPosition = onPositionCleared,
                    label = "DF",
                )
            }

            // Goalkeeper position (1 player)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                PositionSlot(
                    position = Position.GK,
                    playerPosition = fieldPlayers.find { it.position == Position.GK },
                    onPlayerDropped = onPlayerMoved,
                    onClearPosition = onPositionCleared,
                    label = "GK",
                )
            }
        }
    }
}

@Composable
@Suppress("FunctionName")
private fun PositionSlot(
    position: Position,
    playerPosition: PlayerPosition?,
    onPlayerDropped: (Player, Position) -> Unit,
    onClearPosition: (Position) -> Unit,
    label: String,
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .size(80.dp)
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.5f),
                    shape = CircleShape,
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (playerPosition != null) {
            DraggablePlayerChip(
                player = playerPosition.player,
                onDragEnd = {
                    // Player is being dragged away, clear position
                    onClearPosition(position)
                },
            )
        } else {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
@Suppress("FunctionName")
private fun DraggablePlayerChip(
    player: Player,
    onDragEnd: () -> Unit = {},
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .offset {
                    if (isDragging) {
                        IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt())
                    } else {
                        IntOffset.Zero
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                            dragOffset = Offset.Zero
                            onDragEnd()
                        },
                        onDragCancel = {
                            isDragging = false
                            dragOffset = Offset.Zero
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragOffset += dragAmount
                        },
                    )
                },
    ) {
        PlayerChip(player = player, isDragging = isDragging)
    }
}

@Composable
@Suppress("FunctionName")
private fun PlayerChip(
    player: Player,
    isDragging: Boolean = false,
) {
    Box(
        modifier =
            Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(
                    if (isDragging) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (player.number != null) {
                Text(
                    text = "#${player.number}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(
                text = player.firstName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Suppress("FunctionName")
private fun BenchSection(
    benchPlayers: List<Player>,
    onPlayerDropped: (Player) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.lineup_bench, benchPlayers.size),
                style = MaterialTheme.typography.titleMedium,
            )

            if (benchPlayers.isEmpty()) {
                Text(
                    text = stringResource(R.string.lineup_all_players_on_field),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    benchPlayers.forEach { player ->
                        PlayerChip(player = player)
                    }
                }
            }
        }
    }
}

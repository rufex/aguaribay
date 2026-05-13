package dev.rufex.aguaribay.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) { Text("← Back") }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        HorizontalDivider()

        StepperRow(
            label = "Confirmation delay",
            value = state.confirmationDelaySecs,
            unit = "sec",
            min = SettingsViewModel.DELAY_MIN,
            max = SettingsViewModel.DELAY_MAX,
            onDecrement = { viewModel.setConfirmationDelay(state.confirmationDelaySecs - 1) },
            onIncrement = { viewModel.setConfirmationDelay(state.confirmationDelaySecs + 1) },
        )

        HorizontalDivider()

        StepperRow(
            label = "Re-check interval",
            value = state.sessionRecheckMinutes,
            unit = "min",
            min = SettingsViewModel.RECHECK_MIN,
            max = SettingsViewModel.RECHECK_MAX,
            onDecrement = { viewModel.setSessionRecheck(state.sessionRecheckMinutes - 1) },
            onIncrement = { viewModel.setSessionRecheck(state.sessionRecheckMinutes + 1) },
        )

        HorizontalDivider()
    }
}

@Composable
private fun StepperRow(
    label: String,
    value: Int,
    unit: String,
    min: Int,
    max: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text("$value $unit", style = MaterialTheme.typography.headlineSmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement, enabled = value > min) {
                Text("−", style = MaterialTheme.typography.headlineMedium)
            }
            IconButton(onClick = onIncrement, enabled = value < max) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

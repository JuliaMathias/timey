package com.juliamathias.timey.ui.prototype

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juliamathias.timey.domain.playback.*

/** Render-only diagnostic UI, with explicit actions supplied by the playback owner or test fakes. */
@Composable
internal fun PrototypeScreen(
    state: PrototypeUiState,
    configure: (RepFixture, String) -> Unit,
    start: (Boolean) -> Unit,
    pause: () -> Unit,
    resume: () -> Unit,
    restart: () -> Unit,
    next: () -> Unit,
    stop: () -> Unit,
    home: () -> Unit,
) {
    val active = state.playback.status == RepStatus.RUNNING || state.playback.status == RepStatus.PAUSED
    Scaffold { insets ->
        Column(Modifier.fillMaxSize().padding(insets).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Phased-rep diagnostic", style = MaterialTheme.typography.headlineMedium)
            Text("${state.playback.status} · Rep ${state.playback.rep} · ${state.fixture.phases[state.playback.phase].name}",
                style = MaterialTheme.typography.headlineSmall)
            Text("Active elapsed: ${state.playback.elapsedMs}ms")
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Foreground prototype: leaving the app pauses. Screen-off playback follows in issue #12.")
                Text(state.fixture.name)
                Text("Count-up · Every rep · ${state.language}" + if (state.preview) " · Two-rep preview" else "")
                Text(state.voice)
                Text("Fixture", style = MaterialTheme.typography.titleMedium)
                Column(Modifier.selectableGroup()) {
                    DiagnosticOption("Fixture A", state.fixture == PrototypeFixtures.a, !active) {
                        configure(PrototypeFixtures.a, state.language)
                    }
                    DiagnosticOption("Fixture B", state.fixture == PrototypeFixtures.b, !active) {
                        configure(PrototypeFixtures.b, state.language)
                    }
                    DiagnosticOption("Short phases", state.fixture == PrototypeFixtures.short, !active) {
                        configure(PrototypeFixtures.short, state.language)
                    }
                }
                Text("Voice language", style = MaterialTheme.typography.titleMedium)
                Column(Modifier.selectableGroup()) {
                    DiagnosticOption("English", state.language == "en-US", !active) {
                        configure(state.fixture, "en-US")
                    }
                    DiagnosticOption("Português (Brasil)", state.language == "pt-BR", !active) {
                        configure(state.fixture, "pt-BR")
                    }
                }
                Text("Preview warning uses an estimate, not measured speech duration.")
                previewWarnings(state.fixture).forEach { Text(it) }
                Button(onClick = { start(true) }, enabled = !active) { Text("Preview 2 reps") }
                Button(onClick = { start(false) }, enabled = !active) { Text("Start 10 reps") }
                Button(onClick = pause, enabled = state.playback.status == RepStatus.RUNNING) { Text("Pause") }
                Button(onClick = resume, enabled = state.playback.status == RepStatus.PAUSED) { Text("Resume") }
                Button(onClick = restart, enabled = active || state.playback.status == RepStatus.FINISHED) { Text("Restart step") }
                Button(onClick = next, enabled = active) { Text("Next (finish fixture)") }
                Button(onClick = stop, enabled = active) { Text("Stop") }
                Button(onClick = home) { Text("Back to foundation") }
                Text("Diagnostics: dispatch lateness and engine callbacks. Use an external recording for audible latency.")
                state.events.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

/**
 * Shows one choice from an owner-controlled group with a radio indicator and explicit selected text.
 * The entire row is a single accessible target; disabling edits preserves its selection semantics.
 */
@Composable
private fun DiagnosticOption(label: String, selected: Boolean, enabled: Boolean, onSelect: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 48.dp)
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onSelect)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RadioButton(selected = selected, enabled = enabled, onClick = null)
        Text(label, modifier = Modifier.weight(1f))
        if (selected) Text("Selected", style = MaterialTheme.typography.labelMedium)
    }
}

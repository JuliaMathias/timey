package com.juliamathias.timey

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.Button
import com.juliamathias.timey.ui.prototype.PrototypeViewModel
import com.juliamathias.timey.ui.prototype.PrototypeScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Android entry point. Hosts Compose; it will never own the background workout clock. */
class MainActivity : ComponentActivity() {
    private lateinit var prototype: PrototypeViewModel
    /** Creates the offline foundation and foreground diagnostic routes without an account gate. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        prototype = ViewModelProvider(this)[PrototypeViewModel::class.java]
        setContent {
            val state by prototype.state.collectAsState()
            TimeyTheme {
                if (state.open) {
                    BackHandler { prototype.home() }
                    PrototypeScreen(state, { fixture, language -> prototype.configure(fixture, language) },
                        prototype::start, prototype::pause, prototype::resume, prototype::restart,
                        prototype::next, prototype::stop, prototype::home)
                } else FoundationScreen(prototype::open)
            }
        }
    }
    /** Pause foreground-only diagnostics on Home/screen-off; retain playback across rotation. */
    override fun onStop() {
        if (!isChangingConfigurations) prototype.background()
        super.onStop()
    }
}

/** Follows the device's light/dark setting until portable theme preferences are implemented. */
@Composable
internal fun TimeyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
        content = content,
    )
}

/** Shows the honest P0 feature status, using scrollable content for small screens and large text. */
@Composable
internal fun FoundationScreen(openPrototype: () -> Unit = {}) {
    Scaffold { insets ->
        Column(
            modifier = Modifier.fillMaxSize().padding(insets)
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(R.string.foundation_heading),
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(stringResource(R.string.foundation_message), style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.foundation_status), style = MaterialTheme.typography.bodyMedium)
            Button(onClick = openPrototype) { Text("Open rep diagnostic") }
        }
    }
}

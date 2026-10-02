package com.juliamathias.timey

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.juliamathias.timey.domain.playback.*
import com.juliamathias.timey.ui.prototype.PrototypeScreen
import com.juliamathias.timey.ui.prototype.PrototypeUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Render/action tests use supplied snapshots and callbacks, never real TTS or timer sleeps. */
@RunWith(AndroidJUnit4::class)
class PrototypeScreenTest {
    /** Provides a Compose host without constructing the Android speech owner. */
    @get:Rule val compose = createComposeRule()

    /** Configuration cannot mutate running fixture snapshots; pause is the available action. */
    @Test fun runningDisablesConfigurationAndStart() {
        render(PrototypeUiState(playback = RepState(RepStatus.RUNNING, 0, 1, 0)))
        compose.onNodeWithText("Fixture B").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Start 10 reps").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Pause").performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Resume").performScrollTo().assertIsNotEnabled()
    }

    /** Short-phase preview discloses its estimate and permits configured pacing. */
    @Test fun shortPhasesWarnAndRemainPlayable() {
        render(PrototypeUiState(fixture = PrototypeFixtures.short))
        compose.onNodeWithText(previewWarnings(PrototypeFixtures.short).first()).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Preview 2 reps").performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Start 10 reps").performScrollTo().assertIsEnabled()
    }

    /** Language, preview and navigation buttons dispatch the intended owner commands. */
    @Test fun actionsReachTheOwner() {
        var language = ""
        var preview: Boolean? = null
        var stops = 0
        compose.setContent {
            TimeyTheme {
                PrototypeScreen(PrototypeUiState(), { _, tag -> language = tag },
                    { preview = it }, {}, {}, {}, {}, {}, { stops++ })
            }
        }
        compose.onNodeWithText("Português (Brasil)").performScrollTo().performClick()
        assertEquals("pt-BR", language)
        compose.onNodeWithText("Preview 2 reps").performScrollTo().performClick()
        assertEquals(true, preview)
        compose.onNodeWithText("Back to foundation").performScrollTo().performClick()
        assertEquals(1, stops)
    }

    /** Renders state with inert adapters so tests validate UI behavior in isolation. */
    private fun render(state: PrototypeUiState) {
        compose.setContent { TimeyTheme { PrototypeScreen(state, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}) } }
    }
}

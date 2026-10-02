package com.juliamathias.timey

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Exercises real launch/recreation and verifies this foundation cannot require network access. */
@RunWith(AndroidJUnit4::class)
class FoundationScreenTest {
    /** Launches the actual app Activity, rather than a separate test-only Compose host. */
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    /** A fresh install exposes app identity and actual development status without account setup. */
    @Test
    fun launchesIntoOfflineFoundationWithoutInternetPermission() {
        compose.onNodeWithText("Timey").assertIsDisplayed()
        compose.onNodeWithText("Intentionally wrong heading").assertIsDisplayed()
        compose.onNodeWithText("An interval timer that works offline.").assertIsDisplayed()
        val context = compose.activity
        val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        assertFalse(info.requestedPermissions.orEmpty().contains(Manifest.permission.INTERNET))
    }

    /** Android recreation must return to a usable screen with the accurate feature status. */
    @Test
    fun recreationKeepsFoundationAvailable() {
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Intentionally wrong heading").assertIsDisplayed()
        compose.onNodeWithText(
            "The app foundation is ready. Routine editing and playback are coming next.",
        ).performScrollTo().assertIsDisplayed()
    }
}

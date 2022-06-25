/* $Id$ */
package spoiler.blocker.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

/**
 * Created by Muthuraj on 26/06/22.
 *
 * Jambav, Zoho Corporation
 */
@Composable
fun SpoilerBlockerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors() else lightColors(),
        content = content
    )
}
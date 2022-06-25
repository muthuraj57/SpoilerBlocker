package spoiler.blocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import spoiler.blocker.ui.SpoilerBlockerTheme
import spoiler.blocker.util.SystemBarColors


class MainActivity : AppCompatActivity() {

    private var isAccessibilityPermissionEnabled = mutableStateOf(SpoilerBlockerService.isEnabled)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpoilerBlockerTheme {
                SystemBarColors()
                Scaffold {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "This app blocks spoilers on YouTube, Relay for Reddit, Whatsapp and Facebook Android apps.",
                                style = MaterialTheme.typography.h6,
                                lineHeight = 26.sp
                            )
                            Text(
                                modifier = Modifier.padding(top = 16.dp),
                                text = "This is done using Accessibility service where the app listens for any UI change in the above mentioned apps.",
                                style = MaterialTheme.typography.body1,
                                lineHeight = 23.sp
                            )
                            Text(
                                modifier = Modifier.padding(top = 16.dp),
                                text = "If the keywords you entered is detected in the UI, that specific UI component will be blocked by drawing a red overlay.",
                                style = MaterialTheme.typography.body1,
                                lineHeight = 23.sp
                            )
                            if (isAccessibilityPermissionEnabled.value) {
                                KeywordButton()
                            } else {
                                AccessibilityConsentView()
                            }
                            ClickableText(
                                modifier = Modifier.padding(20.dp),
                                style = MaterialTheme.typography.subtitle2,
                                text = buildAnnotatedString {
                                    pushStyle(
                                        SpanStyle(color = MaterialTheme.colors.onSurface)
                                    )
                                    append("Note: The source code for this app is open-source and you can view them from ")
                                    pop()
                                    pushStyle(
                                        SpanStyle(
                                            color = MaterialTheme.colors.primary,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    )
                                    append("github.com/muthuraj57/SpoilerBlocker")
                                    pop()
                                }, onClick = {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            "https://github.com/muthuraj57/SpoilerBlocker".toUri()
                                        )
                                    )
                                })
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isAccessibilityPermissionEnabled.value = SpoilerBlockerService.isEnabled
    }

    @Composable
    private fun ColumnScope.AccessibilityConsentView() {
        val accessibilityPermissionRequest =
            rememberLauncherForActivityResult(contract = AccessibilityPermissionContract(),
                onResult = {
                    isAccessibilityPermissionEnabled.value = it
                })
        Divider(Modifier.padding(vertical = 16.dp))
        Text(
            text = "To do this, we need you to enable Accessibility permission for this app. This app don't save or share any data and this app don't even require internet connection to work.",
            style = MaterialTheme.typography.body1,
            lineHeight = 22.sp
        )
        Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = { accessibilityPermissionRequest.launch(Unit) }) {
            Text(text = "Enable Accessibility permission")
        }
    }

    @Composable
    private fun ColumnScope.KeywordButton() {
        Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                startActivity(
                    Intent(
                        this@MainActivity, KeywordActivity::class.java
                    )
                )
            }) {
            Text(text = "View/Edit Keywords")
        }
    }
}
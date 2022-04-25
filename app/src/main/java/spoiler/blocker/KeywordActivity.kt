/* $Id$ */
@file:OptIn(ExperimentalFoundationApi::class)

package spoiler.blocker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import spoiler.blocker.util.SystemBarColors

/**
 * Created by Muthuraj on 25/03/22.
 */
class KeywordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val dataStore = keywordsDataStore
        val keywordsFlow = dataStore.data.map { it.keywords }
        setContent {
            SystemBarColors()

            Scaffold {
                val keywords = keywordsFlow.collectAsState(initial = emptyList()).value
                val scope = rememberCoroutineScope()
                Column(
                    modifier = Modifier
                        .padding(it)
                        .systemBarsPadding()
                        .padding(16.dp)
                ) {
                    val scrollState = rememberLazyListState()

                    KeywordInputBox(keywords, addKeyword = { keyword ->
                        dataStore.updateData { keywords ->
                            keywords.copy(keywords.keywords + keyword)
                        }
                        scrollState.animateScrollToItem(keywords.size - 1)
                    })

                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .imePadding()
                            .fillMaxWidth()
                    ) {
                        itemsIndexed(keywords) { index, text ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "${index + 1}) $text", fontSize = 16.sp)
                                IconButton(onClick = {
                                    scope.launch {
                                        dataStore.updateData { keywords ->
                                            val newList = keywords.keywords.toMutableList()
                                                .apply { removeAt(index) }
                                                .toList()
                                            keywords.copy(newList)
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ColumnScope.KeywordInputBox(
        keywords: List<String>, addKeyword: suspend (keyword: String) -> Unit
    ) {
        var errorMessage: String? by remember {
            mutableStateOf(null)
        }
        Column(Modifier.padding(bottom = 16.dp)) {
            Row {
                var text by remember { mutableStateOf("") }
                var showProgress by remember {
                    mutableStateOf(false)
                }
                val scope = rememberCoroutineScope()

                OutlinedTextField(value = text, onValueChange = {
                    errorMessage = null
                    text = it
                })
                Button(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .wrapContentWidth(unbounded = true),
                    onClick = {
                        val trimmedText = text.trim()
                        if (trimmedText.isEmpty()) {
                            errorMessage = "Please enter a keyword"
                        } else if (keywords.any {
                                it.equals(trimmedText, ignoreCase = true)
                            }) {
                            errorMessage = "Keyword already present"
                        } else {
                            showProgress = true
                            scope.launch {
                                addKeyword(trimmedText)

                                //Reset text
                                text = ""
                                showProgress = false
                            }
                        }
                    }, enabled = showProgress.not()
                ) {
                    Text(text = "Enter")
                    if (showProgress) {
                        CircularProgressIndicator()
                    }
                }
            }
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red)
            }
        }
    }
}
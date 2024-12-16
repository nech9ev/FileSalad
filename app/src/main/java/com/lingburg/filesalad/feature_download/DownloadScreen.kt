@file:OptIn(ExperimentalMaterial3Api::class)

package com.lingburg.filesalad.feature_download

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingburg.filesalad.FileSaladAppState
import com.lingburg.filesalad.feature_download.component.WordItem
import com.lingburg.filesalad.ui.theme.AppTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers

@Composable
fun DownloadScreen(
    appState: FileSaladAppState,
    modifier: Modifier = Modifier,
    viewModel: DownloadScreenViewModel = hiltViewModel(),
) {
    val ui =
        viewModel.uiState.collectAsStateWithLifecycle(context = Dispatchers.Main.immediate).value

    DownloadScreenView(
        ui = ui,
        onWordTextChanged = viewModel::onWordTextChanged,
        onDownloadClick = viewModel::onDownloadClick,
        onBackClick = viewModel::onBackClick,
        modifier = modifier,
    )
}

@Composable
private fun DownloadScreenView(
    ui: DownloadScreenUi,
    onWordTextChanged: (index: Int, text: String) -> Unit,
    onDownloadClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val focusManager = LocalFocusManager.current
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier,
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = "Download",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = {
                            focusManager.clearFocus()
                        }
                    )
                    .padding(paddings),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = "Enter 3 words",
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "These words identify your file",
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                        )
                    }
                )
                ui.words.forEachIndexed { index, wordUi ->
                    WordItem(
                        ui = wordUi,
                        focusManager = focusManager,
                        onTextChanged = { text ->
                            onWordTextChanged(index, text)
                        },
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .widthIn(max = 460.dp)
                        .fillMaxWidth(),
                    onClick = onDownloadClick,
                ) {
                    Text(
                        text = "Download",
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun DownloadViewPreview() = AppTheme {
    DownloadScreenView(
        ui = DownloadScreenUi(
            words = persistentListOf(
                WordUi(
                    label = "First word",
                    text = "apple"
                ),
                WordUi(
                    label = "Second word",
                    text = "car"
                ),
                WordUi(
                    label = "Third word",
                    text = "rain"
                ),
            ),
            inProgress = false,
        ),
        onWordTextChanged = { _, _ -> },
        onDownloadClick = {},
        onBackClick = {},
    )
}

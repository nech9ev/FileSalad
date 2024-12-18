@file:OptIn(ExperimentalMaterial3Api::class)

package com.lingburg.filesalad.feature_files

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingburg.filesalad.FileSaladAppState
import com.lingburg.filesalad.feature_files.component.WordItem
import com.lingburg.filesalad.ui.theme.AppTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers

@Composable
fun FilesScreen(
    appState: FileSaladAppState,
    modifier: Modifier = Modifier,
    viewModel: DownloadScreenViewModel = hiltViewModel(),
) {
    val ui = viewModel.uiState.collectAsStateWithLifecycle(
        context = Dispatchers.Main.immediate,
    ).value
    val navigationEvent = viewModel.navigationEvent.collectAsStateWithLifecycle(
        initialValue = NavigationEvent.Empty,
        context = Dispatchers.Main.immediate
    ).value
    val snackbarState = viewModel.snackbarState.collectAsStateWithLifecycle(
        initialValue = SnackbarState.Empty,
        context = Dispatchers.Main.immediate
    ).value


    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            NavigationEvent.Empty -> {}
            is NavigationEvent.ShareWords -> {
                val textForShare =
                    "Download file with FileSalad using these words: ${navigationEvent.text}"
                val shareIntent = Intent.createChooser(
                    Intent().setType("text/plain")
                        .setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, textForShare)
                        .putExtra(Intent.EXTRA_SUBJECT, "FileSalad file secret words"),
                    "Share your file words"
                )
                try {
                    context.startActivity(shareIntent)
                } catch (_: ActivityNotFoundException) {
                }
            }
        }
    }
    LaunchedEffect(snackbarState) {
        when (snackbarState) {
            SnackbarState.Empty -> {}
            is SnackbarState.Success -> {
                snackbarHostState.showSnackbar(
                    message = snackbarState.text,
                )
            }
            is SnackbarState.Error -> {
                snackbarHostState.showSnackbar(
                    message = snackbarState.text,
                )
            }
        }
    }
    FilesScreenView(
        ui = ui,
        snackbarHostState = snackbarHostState,
        onWordTextChanged = viewModel::onWordTextChanged,
        onDownloadClick = viewModel::onDownloadClick,
        onDocumentSelected = viewModel::onDocumentSelected,
        onBaseUrlChange = viewModel::onBaseUrlChange,
        modifier = modifier,
    )
}

@Composable
private fun FilesScreenView(
    ui: FilesScreenUi,
    snackbarHostState: SnackbarHostState,
    onWordTextChanged: (index: Int, text: String) -> Unit,
    onDownloadClick: () -> Unit,
    onDocumentSelected: (Uri) -> Unit,
    onBaseUrlChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    val filePickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        onDocumentSelected(uri)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            ) { data ->
                Snackbar(
                    snackbarData = data,
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier,
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = "Anonymous file sharing",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
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
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddings),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = "Download file",
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "Enter 3 words, these words identify your file",
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 2,
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
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .widthIn(max = 460.dp)
                        .fillMaxWidth(),
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onDownloadClick()
                    },
                    enabled = !ui.inProgress,
                ) {
                    when (ui.downloadProgress) {
                        false -> Text(
                            text = "Download",
                        )

                        true -> LinearProgressIndicator()
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                ListItem(
                    headlineContent = {
                        Text(
                            text = "Share file",
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "Get 3 words for your file",
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                        )
                    }
                )
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .widthIn(max = 460.dp)
                        .fillMaxWidth(),
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        filePickLauncher.launch(arrayOf("*/*"))
                    },
                    enabled = !ui.inProgress,
                    colors = ButtonColors(
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                            alpha = 0.3f
                        ),
                        disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                            alpha = 0.3f
                        ),
                    )
                ) {
                    when (ui.uploadProgress) {
                        false -> Text(
                            text = "Upload file",
                        )

                        true -> LinearProgressIndicator()
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun FilesViewPreview() = AppTheme {
    FilesScreenView(
        ui = FilesScreenUi(
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
            downloadProgress = false,
            uploadProgress = true,
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onWordTextChanged = { _, _ -> },
        onDownloadClick = {},
        onDocumentSelected = {},
        onBaseUrlChange = {},
    )
}

package com.lingburg.filesalad.feature_files.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.lingburg.filesalad.feature_files.WordUi
import com.lingburg.filesalad.ui.theme.AppTheme

private val keyboardOptions = KeyboardOptions(
    capitalization = KeyboardCapitalization.None,
    autoCorrectEnabled = false,
    keyboardType = KeyboardType.Text,
    imeAction = ImeAction.Done
)

@Composable
fun WordItem(
    ui: WordUi,
    focusManager: FocusManager,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier,
        label = {
            Text(
                text = ui.label,
            )
        },
        value = ui.text,
        onValueChange = onTextChanged,
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        keyboardOptions = keyboardOptions,
    )
}

@Preview
@Composable
private fun WordItemPreview() = AppTheme {
    WordItem(
        ui = WordUi(
            label = "Second word",
            text = "apple",
        ),
        focusManager = LocalFocusManager.current,
        onTextChanged = {},
    )
}

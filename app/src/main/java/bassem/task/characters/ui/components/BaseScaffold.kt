package bassem.task.characters.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScaffold(
    modifier: Modifier = Modifier,
    title: String,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    if (showBackButton && onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = snackbarHost
    ) { paddingValues ->
        content(paddingValues)
    }
}

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*

@Composable @Preview fun App()
{
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    //val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
    val windowState = rememberWindowState(placement = WindowPlacement.Maximized, size = WindowSize(1920.dp, 1080.dp)) // Set this to match screen resolution

    Window(onCloseRequest = ::exitApplication, state = windowState) {
        App()
    }
}

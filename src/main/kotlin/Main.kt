import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*

@Composable @Preview fun App()
{
    var inputText by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }

    MaterialTheme {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Input") }
            )
            Button(onClick = { outputText = inputText }) {
                Text("Copy Text")
            }
            Text("Output\n$outputText")
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

package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun Menu(selection: MutableState<String>) {
    var showDialog by remember { mutableStateOf(true) }
    var joinCode by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = true }) {
                Surface(
                    color = Color(0xFF113540),
                    shape = MaterialTheme.shapes.medium,
                    elevation = 8.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    selection.value = "START"
                                    showDialog = false
                                },
                                modifier = Modifier.weight(1f).pointerHoverIcon(PointerIcon.Hand),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF0173BD), contentColor = Color.White
                                )
                            ) {
                                Text("Start Game")
                            }
                            Button(
                                onClick = {
                                    if (joinCode.isNotEmpty()) {
                                        selection.value = "JOIN $joinCode"
                                        showDialog = false
                                    }
                                },
                                modifier = Modifier.weight(1f).pointerHoverIcon(PointerIcon.Hand),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFFF1222), contentColor = Color.White
                                )
                            ) {
                                Text("Join Game")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = joinCode,
                            onValueChange = { joinCode = it },
                            placeholder = { Text("Enter game code") },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0x33FF1222),
                                focusedIndicatorColor = Color(0xFFFF1222),
                                unfocusedIndicatorColor = Color(0xFFFF1222),
                                textColor = Color.White,
                                placeholderColor = Color.LightGray,
                                cursorColor = Color(0xFFFF1222)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

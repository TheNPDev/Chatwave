package com.example.chatwave.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatwave.CWViewModel
import com.example.chatwave.CommonProgressBar
import com.example.chatwave.TitleText


@Composable
fun ChatListScreen(navController: NavController, viewModel: CWViewModel) {
    val inProgress = viewModel.inProcessChat
    if (inProgress.value) {
        CommonProgressBar()
    } else {
        val chats = viewModel.chats.value
        val userData = viewModel.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFABClick: () -> Unit = {
            showDialog.value = true

        }
        val onDismiss: () -> Unit = { showDialog.value = false }
        val onAddChat: (String) -> Unit = {
            viewModel.onAddChat(it)
            showDialog.value = false
        }

        Scaffold(
            floatingActionButton = {
                FAB(
                    showDialog = showDialog.value,
                    onFABClick = onFABClick ,
                    onDismiss =  onDismiss,
                    onAddChat = onAddChat
                )
            },
            content = {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)

                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        TitleText(txt = "Chats")
                        if (chats.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize().align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                Text(text = "No chats available")
                            }

                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        BottomNavigationMenu(
                            selectedItem = BottomNavigationItem.CHATLIST,
                            navController = navController
                        )
                    }
                }
            }
        )


    }


}



@Composable
fun FAB(
    showDialog: Boolean,
    onFABClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit,
) {
    val addChatNumber = remember {
        mutableStateOf("")
    }



    if (showDialog)
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
            addChatNumber.value = ""
        },
            confirmButton = {
                Button(onClick = { onAddChat(addChatNumber.value) }) {
                    Text(text = "Add Chat")
                }
            },
            title = { Text(text = "Add Chat") },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value,
                    onValueChange = { addChatNumber.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        )

    FloatingActionButton(
        onClick = onFABClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp),

        ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
    }


}
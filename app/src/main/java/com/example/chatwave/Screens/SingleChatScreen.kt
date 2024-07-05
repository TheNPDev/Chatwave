package com.example.chatwave.Screens

import android.media.Image
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.chatwave.CWViewModel
import com.example.chatwave.CommonDivider
import com.example.chatwave.CommonImage
import com.example.chatwave.R
import com.example.chatwave.data.Message
import kotlinx.coroutines.launch


@Composable
fun SingleChatScreen(navController: NavController, viewModel: CWViewModel, chatId: String) {

    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val onSendReply = {
        viewModel.onSendReply(chatId, reply)
        reply = ""
    }
    val myUser = viewModel.userData.value
    val currentChat = viewModel.chats.value.first { it.chatId == chatId }
    val chatUser =
        if (currentChat.user1.userId == myUser?.userId) currentChat.user2 else currentChat.user1
    val chatMessages = viewModel.chatMessages

    LaunchedEffect(key1 = Unit) {
        viewModel.populateMessages(chatId)
    }
    BackHandler {
        viewModel.depopulateMessages()
        navController.popBackStack()
    }

    Column {

        ChatHeader(name = chatUser.name ?: "", imageUrl = chatUser.imageUrl ?: "") {
            navController.popBackStack()
            viewModel.depopulateMessages()
        }
        CommonDivider()
        MessageBox(
            modifier = Modifier
                .weight(1f),
            chatMessages = chatMessages,
            currentUserId = myUser?.userId ?: ""
        )


        replyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)
    }


}

@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(Icons.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
            .clickable {
                onBackClick.invoke()
            }
            .padding(8.dp))

        CommonImage(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun replyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {

    Column(modifier = Modifier.fillMaxWidth()) {

        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = reply,
                onValueChange = onReplyChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(48.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color.LightGray)
                    .padding(horizontal = 16.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                maxLines = 5
            )
            Button(
                onClick = onSendReply,
                modifier = Modifier
                    .height(48.dp)
                    .padding(start = 8.dp)
                    .clip(shape = RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(Color.Green),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(text = "Send", color = Color.White)
            }
        }

    }
}

@Composable
fun MessageBox(modifier: Modifier, chatMessages: List<Message>, currentUserId: String) {


    LazyColumn(modifier = modifier) {
        items(chatMessages) { msg ->

            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendBy == currentUserId) Color(0xFF00BFFF) else Color(0xFF5CB3FF)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {
                Text(
                    text = msg.message ?: "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(12.dp),
                    color = Color.White,
                    fontWeight = FontWeight.W400
                )
            }
        }


    }
}
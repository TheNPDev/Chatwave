package com.example.chatwave.Screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatwave.CWViewModel
import com.example.chatwave.CommonDivider
import com.example.chatwave.CommonProgressBar
import com.example.chatwave.CommonRow
import com.example.chatwave.DestinationScreen
import com.example.chatwave.TitleText
import com.example.chatwave.data.Status
import com.example.chatwave.navigateTo


@Composable
fun StatusScreen(navController: NavController, viewModel: CWViewModel) {

    val inProcess = viewModel.inProgressStatus.value
    if (inProcess) {
        CommonProgressBar()
    } else {
        val statuses = viewModel.status.value
        val userData = viewModel.userData.value
        val myStatuses = statuses.filter {
            it.user.userId == userData?.userId
        }
        val otherStatuses = statuses.filter {
            it.user.userId != userData?.userId
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ){
            uri ->

            uri?.let {
                viewModel.uploadStatus(uri)
            }
        }

        Scaffold(
            floatingActionButton = {
                FAB {
                    launcher.launch("image/*")
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {

                    TitleText(txt = "Status")
                    if (statuses.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "No status available")
                        }

                    } else {
                        if (myStatuses.isNotEmpty()) {
                            CommonRow(
                                imageUrl = myStatuses[0].user.imageUrl,
                                name = myStatuses[0].user.name
                            ) {
                                navigateTo(
                                    navController,
                                    DestinationScreen.SingleStatus.createRoute(myStatuses[0].user.userId!!)
                                )
                            }
                            CommonDivider()
                            val uniqueUsers = otherStatuses.map { it.user }.toSet().toList()
                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(uniqueUsers) { user ->
                                    CommonRow(imageUrl = user.imageUrl, name = user.name) {
                                        navigateTo(
                                            navController,
                                            DestinationScreen.SingleStatus.createRoute(user.userId!!)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        BottomNavigationMenu(
                            selectedItem = BottomNavigationItem.STATUSLIST,
                            navController = navController
                        )
                    }
                }
            }
        )

    }

}

@Composable
fun FAB(onFABClick: () -> Unit) {
    FloatingActionButton(
        onClick = onFABClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
    ) {
        Icon(imageVector = Icons.Rounded.Edit, contentDescription = null, tint = Color.White)
    }
}
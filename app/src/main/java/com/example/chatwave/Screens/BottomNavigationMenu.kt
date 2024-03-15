package com.example.chatwave.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.chatwave.DestinationScreen
import com.example.chatwave.R
import com.example.chatwave.navigateTo


enum class BottomNavigationItem(val Icon: Int, val navDestination: DestinationScreen) {
    CHATLIST(R.drawable.chatlogo, DestinationScreen.ChatList),
    STATUSLIST(R.drawable.status, DestinationScreen.StatusList),
    PROFILE(R.drawable.profile, DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(Color.White)
    ) {

        for (item in BottomNavigationItem.entries){
            Image(painter = painterResource(id = item.Icon), contentDescription = null,
                modifier = Modifier.fillMaxSize().aspectRatio(2.9f).padding(4.dp).weight(1f).clickable {

                    navigateTo(navController,item.navDestination.route)
                },
                colorFilter = if(item == selectedItem)
            ColorFilter.tint(color = Color.Black)
            else
            ColorFilter.tint(color = Color.Gray))
        }
    }
}
package com.example.chatwave.Screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.chatwave.CWViewModel


@Composable
fun StatusScreen(navController:NavController,viewModel: CWViewModel) {

    BottomNavigationMenu(selectedItem = BottomNavigationItem.STATUSLIST, navController = navController)
}
package com.example.chatwave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatwave.Screens.ChatListScreen
import com.example.chatwave.Screens.LoginScreen
import com.example.chatwave.Screens.ProfileScreen
import com.example.chatwave.Screens.SignupScreen
import com.example.chatwave.Screens.SingleChatScreen
import com.example.chatwave.Screens.SingleStatusScreen
import com.example.chatwave.Screens.StatusScreen
import com.example.chatwave.ui.theme.ChatwaveTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(var route: String){
    object SignUp : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")
    object ChatList : DestinationScreen("chatList")
    object SingleChat : DestinationScreen("singleChat/{chatId}"){
        fun createRoute(id: String) = "singleChat/$id"
    }

    object StatusList : DestinationScreen("statusList")
    object SingleStatus : DestinationScreen("singleStatus/{userId}"){
        fun createRoute(userId: String) = "singleStatus/$userId"
    }

}



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatwaveTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CWNavigation()
                }
            }
        }
    }

    @Composable
    fun CWNavigation(){

        val navController = rememberNavController()
        val viewModel = hiltViewModel<CWViewModel>()
        NavHost(navController = navController, startDestination = DestinationScreen.SignUp.route){

            composable(DestinationScreen.SignUp.route){
                SignupScreen(navController,viewModel)
            }
            composable(DestinationScreen.Login.route){
                LoginScreen(navController,viewModel)
            }
            composable(DestinationScreen.ChatList.route){
                ChatListScreen(navController,viewModel)
            }
            composable(DestinationScreen.SingleChat.route){
                val chatId = it.arguments?.getString("chatId")
                chatId?.let {
                    SingleChatScreen(navController,viewModel,chatId)
                }
            }
            composable(DestinationScreen.StatusList.route){
                StatusScreen(navController,viewModel)
            }
            composable(DestinationScreen.Profile.route){
                ProfileScreen(navController,viewModel)
            }
            composable(DestinationScreen.SingleStatus.route){
                val userId = it.arguments?.getString("userId")
                userId?.let {
                    SingleStatusScreen(navController,viewModel,userId)
                }
            }
        }

    }
}


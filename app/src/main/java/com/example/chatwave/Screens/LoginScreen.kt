package com.example.chatwave.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatwave.CWViewModel
import com.example.chatwave.CheckedSignedIn
import com.example.chatwave.CommonProgressBar
import com.example.chatwave.DestinationScreen
import com.example.chatwave.R
import com.example.chatwave.navigateTo
import com.example.stencilmolecules.LoginMolecule


@Composable
fun LoginScreen( navController : NavController,viewModel : CWViewModel) {



        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp

        CheckedSignedIn(viewModel = viewModel, navController = navController)

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
                    .verticalScroll(
                        rememberScrollState()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val emailState = remember {
                    mutableStateOf(TextFieldValue())
                }
                val passwordState = remember {
                    mutableStateOf(TextFieldValue())
                }
                val focus = LocalFocusManager.current
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(screenWidth * 0.3f)
                        .padding(top = 16.dp)
                        .padding(8.dp)
                )
                Text(
                    text = "Sign In",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)

                )
                OutlinedTextField(value = emailState.value,
                    onValueChange = {
                        emailState.value = it
                    },
                    label = { Text(text = "Email")},
                    modifier = Modifier.padding(8.dp)
                )
                OutlinedTextField(value = passwordState.value,
                    onValueChange = {
                        passwordState.value = it
                    },
                    label = { Text(text = "Password")},
                    modifier = Modifier.padding(8.dp)
                )

                Button(onClick = {
                                 viewModel.Login(emailState.value.text,passwordState.value.text)
                },
                    modifier = Modifier.padding(8.dp)) {
                    Text(text = "SIGN IN")
                }
                Text(text = "Already a user ? Go to Sign Up",
                    color = Color.DarkGray,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigateTo(navController, DestinationScreen.SignUp.route)
                        })
            }
//            LoginMolecule(
//                onLoginClicked = { username, password ->
//                    // Handle login logic here
////                    println("Username: $username, Password: $password")
//                },
//                onForgotPasswordClicked = {
//                    // Handle forgot password logic here
////                    println("Forgot Password Clicked")
//                },
//                modifier = Modifier
//                    .fillMaxSize().padding(16.dp),
//                textFieldModifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                buttonModifier = Modifier
//                    .width(200.dp)
//                    .height(48.dp),
//                textModifier = Modifier
//                    .padding(top = 16.dp)
//            )
        }

        if(viewModel.inProcess.value){
            CommonProgressBar()
        }

}
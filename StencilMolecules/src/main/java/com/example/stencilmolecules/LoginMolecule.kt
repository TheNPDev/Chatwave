package com.example.stencilmolecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp


@Composable
fun LoginMolecule(
    onLoginClicked: (username: String, password: String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleAndTextField(
            title = "Username",
            text = username,
            onTextChanged = { username = it },
            hint = "Enter Username",
            modifier = textFieldModifier
        )
        Spacer(modifier = Modifier.height(16.dp))
        TitleAndTextField(
            title = "Password",
            text = password,
            onTextChanged = { password = it },
            hint = "Enter Password",
            isPassword = true,
            modifier = textFieldModifier
        )
        Spacer(modifier = Modifier.height(16.dp))
        SubmitButton(onClick = { onLoginClicked(username, password) }, modifier = buttonModifier)
        Spacer(modifier = Modifier.height(16.dp))
        ForgotPasswordText(onClick = onForgotPasswordClicked, modifier = textModifier)
    }
}

@Composable
fun TitleAndTextField(
    title: String,
    text: String,
    onTextChanged: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            label = { Text(hint) },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SubmitButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text("Submit")
    }
}

@Composable
fun ForgotPasswordText(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = "Forgot Password",
        style = TextStyle(
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colors.primary
        ),
        modifier = modifier.clickable(onClick = onClick)
    )
}




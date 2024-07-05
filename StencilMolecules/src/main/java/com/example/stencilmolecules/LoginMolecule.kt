package com.example.stencilmolecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginMolecule(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onOtpClick: () -> Unit,
    otpButtonText: String,
    infoText: String,
    infoPhoneNumber: String
) {
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("जैसे: 9876543210") }
        )
        Button(onClick = onOtpClick, modifier = Modifier.padding(top = 16.dp)) {
            Text(text = otpButtonText)
        }
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(text = infoText)
            Text(text = infoPhoneNumber)
        }
    }
}
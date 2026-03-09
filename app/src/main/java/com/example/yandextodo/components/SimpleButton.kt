package com.example.yandextodo.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SimpleButton(
    text:String,
    onClick:()-> Unit
) {
    Button(onClick = onClick,
           modifier = Modifier
               .height(36.dp)
               .width(108.dp),
           shape = RoundedCornerShape(40.dp),
           colors = ButtonDefaults.buttonColors(
               containerColor = MaterialTheme.colorScheme.secondary,
               contentColor = MaterialTheme.colorScheme.onSecondary)) {
        Text(text = text)
    }
}
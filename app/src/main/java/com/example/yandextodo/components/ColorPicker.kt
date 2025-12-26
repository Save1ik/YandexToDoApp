package com.example.yandextodo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val colorList = listOf(
    Color.White,
    Color.Red,
    Color.Blue,
    Color.Green,
    Color.Cyan
)

@Composable
fun ColorPicker(
    currentColor: Color,
    onColorSelected: (Color) -> Unit
    ){
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Цвет дела",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                colorList.forEach { color ->
                    ColorBox(
                        color = color,
                        isChecked = color == currentColor,
                        onColorSelected = { onColorSelected(color) }
                    )
                }
            }
        }
    }
}

@Composable
fun ColorBox(color: Color,
             isChecked: Boolean,
             onColorSelected: () -> Unit){
    Box(
        modifier = Modifier.size(48.dp)
                           .background(color)
                           .border(width = 2.dp,
                               color = if (isChecked) Color.Black else Color.Transparent)
                           .clickable{onColorSelected()},
        contentAlignment = Alignment.Center
    )
    {
        if(isChecked){
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Текущий цвет",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
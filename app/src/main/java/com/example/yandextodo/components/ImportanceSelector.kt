package com.example.yandextodo.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yandextodo.Importance

@Composable
fun ImportanceSelector(
    selectedValue: Importance,
    onSelected: (Importance) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        ImportanceItem(
            value = Importance.low,
            isSelected = selectedValue == Importance.low,
            onSelected = onSelected
        )
        ImportanceItem(
            value = Importance.medium,
            isSelected = selectedValue == Importance.medium,
            onSelected = onSelected
        )
        ImportanceItem(
            value = Importance.hight,
            isSelected = selectedValue == Importance.hight,
            onSelected = onSelected
        )
    }

}

@Composable
fun ImportanceItem(
    value:Importance,
    isSelected:Boolean,
    onSelected: (Importance) -> Unit
)
{
    val backgroundColor = when (value) {
        Importance.low -> Color(0xFF8FEB8F)
        Importance.medium -> Color(0xFFF5EB96)
        Importance.hight -> Color(0xFFF895A3)
    }

    val selectedColor = when (value) {
        Importance.low -> Color(0xFF4CAF50)
        Importance.medium -> Color(0xFFFFC107)
        Importance.hight -> Color(0xFFF44336)
    }

    val borderColor = if (isSelected) selectedColor else Color.Transparent

    Card(
        onClick = { onSelected(value) },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(width = 2.dp, color = borderColor)
    )
    {
        Text(text = if(value == Importance.medium){"Обычная"}
        else if (value == Importance.hight){"Высокая"}
        else {"Низкая"},
             modifier = Modifier.padding(15.dp))
    }
}

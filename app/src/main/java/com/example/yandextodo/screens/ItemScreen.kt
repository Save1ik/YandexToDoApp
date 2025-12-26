package com.example.yandextodo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yandextodo.Importance
import com.example.yandextodo.components.ColorPicker
import com.example.yandextodo.components.DescriptionField
import com.example.yandextodo.components.ImportanceSelector
import com.example.yandextodo.components.SimpleButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen() {
    var text by remember {mutableStateOf("")}
    var state by remember {mutableStateOf(false)}
    var dateFlag by remember { mutableStateOf(false)}
    val datePickerState = rememberDatePickerState()
    var date by remember { mutableStateOf<Long?>(null)}
    var currentColor by remember { mutableStateOf(Color.White)}
    var importance by remember { mutableStateOf(Importance.medium)}

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)){
    item {
        DescriptionField(text = text,
            onTextChanged = {t -> text = t})
        Spacer(modifier = Modifier.height(36.dp))
    }
    item {
        SimpleButton(text = "Выбрать дату", onClick = {dateFlag=true})

        if(dateFlag){
            DatePickerDialog(
                confirmButton ={
                    TextButton(onClick = {
                        dateFlag = false
                        date = datePickerState.selectedDateMillis}) {
                        Text(text = "Сохранить")
                    }
                },
                onDismissRequest = {dateFlag = false}
            ){
                DatePicker(state = datePickerState)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        val currentDate = date
        Text(
            text = if (currentDate != null) {
                "Дедлайн: ${mlsToDate(currentDate)}"
            } else {
                "Дедлайн не установлен"
            })
        Spacer(modifier = Modifier.height(24.dp))
    }
    item {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){
            Checkbox(checked = state,
                onCheckedChange = {t -> state = t},
                enabled = true)
            Text(text = "Дело сделано")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
    item {
        ColorPicker(
            currentColor = currentColor,
            onColorSelected = { color -> currentColor = color }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
    item {
        Text(text = "Выбор важности")
        Spacer(modifier = Modifier.height(16.dp))
        ImportanceSelector(selectedValue = importance, onSelected = {i -> importance = i})
    }
    }
}

private fun mlsToDate(mills: Long):String {
    val date = Date(mills)
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
    return formatter.format(date)
}
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.example.yandextodo.ToDoItem
import com.example.yandextodo.components.ColorPicker
import com.example.yandextodo.components.DescriptionField
import com.example.yandextodo.components.ImportanceSelector
import com.example.yandextodo.components.SimpleButton
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    item: ToDoItem? = null,
    onSave: (ToDoItem) -> Unit,
    onBack: () -> Unit
) {
    var text by remember { mutableStateOf(item?.text ?: "") }
    var isDone by remember { mutableStateOf(item?.isDone ?: false) }
    var importance by remember { mutableStateOf(item?.importance ?: Importance.medium) }
    var currentColor by remember { mutableStateOf(item?.color ?: Color.White) }


    var date by remember {
        mutableStateOf<Long?>(
            item?.deadline?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
        )
    }

    var dateFlag by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)


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
                Checkbox(checked = isDone,
                    onCheckedChange = {t -> isDone = t},
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
            Spacer(modifier = Modifier.height(24.dp))
        }
        item{
            Button(onClick = {onSave(ToDoItem(
                uid = item?.uid ?: UUID.randomUUID().toString(),
                text = text,
                color = currentColor,
                deadline = date?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC) },
                isDone = isDone,
                importance = importance
            ))},
                modifier = Modifier
                    .height(38.dp)
                    .width(128.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary)) {
                Text(text = "Сохранить")
            }
        }
    }

}

private fun mlsToDate(mills: Long):String {
    val date = Date(mills)
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
    return formatter.format(date)
}
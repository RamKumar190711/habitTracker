package com.toqsoft.habittracker.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toqsoft.habittracker.ui.theme.MeronLight
import com.toqsoft.habittracker.ui.theme.MeronUltraLight
import com.toqsoft.habittracker.ui.theme.MeronWarm
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import java.text.SimpleDateFormat

import java.time.DayOfWeek
import java.util.Date
import java.util.Locale
import kotlin.time.ExperimentalTime


data class DateItem(
    val date: LocalDate,
    val dayName: String,
    val dayNumber: Int
)


fun generateDates(startYear: Int, endYear: Int): List<DateItem> {
    val list = mutableListOf<DateItem>()
    var current = kotlinx.datetime.LocalDate(startYear, 1, 1)
    val end = kotlinx.datetime.LocalDate(endYear, 12, 31)

    val dayNames = mapOf(
        DayOfWeek.MONDAY to "Mon",
        DayOfWeek.TUESDAY to "Tue",
        DayOfWeek.WEDNESDAY to "Wed",
        DayOfWeek.THURSDAY to "Thu",
        DayOfWeek.FRIDAY to "Fri",
        DayOfWeek.SATURDAY to "Sat",
        DayOfWeek.SUNDAY to "Sun"
    )

    while (current <= end) {
        list.add(
            DateItem(
                date = current,
                dayName = dayNames[current.dayOfWeek] ?: "",
                dayNumber = current.dayOfMonth
            )
        )
        current = current.plus(DatePeriod(days = 1))
    }
    return list
}

@Composable
fun Calendar(
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val dates = remember { generateDates(2025, 2100) }
    val listState = rememberLazyListState()
    val selectedDate = remember { mutableStateOf(today) }

    LaunchedEffect(Unit) {
        val todayIndex = dates.indexOfFirst { it.date == today }
        if (todayIndex >= 0) {
            val startOfWeekIndex = todayIndex - today.dayOfWeek.ordinal
            listState.scrollToItem(startOfWeekIndex.coerceAtLeast(0))
        }
    }

    LazyRow(
        modifier = modifier.padding(8.dp), // use passed modifier
        state = listState
    ) {
        items(dates) { item ->
            val isSelected = item.date == selectedDate.value
            Card(
                modifier = Modifier
                    .padding(3.dp)
                    .height(65.dp)
                    .width(50.dp)
                    .clickable {
                        selectedDate.value = item.date
                        onDateSelected(item.date)
                    },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MeronWarm else MeronLight
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = item.dayName,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MeronLight else MeronUltraLight
                        )
                    ) {
                        Text(
                            text = item.dayNumber.toString(),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}
fun formatDate1(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}



fun formatDate(date: kotlinx.datetime.LocalDate): String {
    val months = listOf(
        "Jan","Feb","Mar","Apr","May","Jun",
        "Jul","Aug","Sep","Oct","Nov","Dec"
    )
    val monthName = months[date.monthNumber - 1]
    return "${date.dayOfMonth} $monthName ${date.year}"
}

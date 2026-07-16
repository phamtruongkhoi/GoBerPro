package com.example.goberpro.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goberpro.viewmodel.BarberViewModel

import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: BarberViewModel = viewModel(),
    onBack: (() -> Unit)? = null
) {

    val totalRevenue by viewModel.totalRevenue.collectAsState()

    val todayRevenue by viewModel.todayRevenue.collectAsState()

    val monthRevenue by viewModel.monthRevenue.collectAsState()

    val yearRevenue by viewModel.yearRevenue.collectAsState()

    val totalOrders by viewModel.totalOrders.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        if (onBack != null) {

            Button(
                onClick = { onBack() }
            ) {
                Text("← Quay lại")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "THỐNG KÊ DOANH THU",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.loadStatistics()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Làm mới thống kê")
        }

        Spacer(modifier = Modifier.height(20.dp))


        StatisticCard(
            title = "Tổng doanh thu",
            value = formatMoney(totalRevenue)
        )
        Spacer(modifier = Modifier.height(12.dp))

        StatisticCard(
            title = "Doanh thu hôm nay",
            value = formatMoney(todayRevenue)
        )

        StatisticCard(
            title = "Doanh thu tháng",
            value = formatMoney(monthRevenue)
        )

        StatisticCard(
            title = "Doanh thu năm",
            value = formatMoney(yearRevenue)
        )
    }

}
@Composable
fun StatisticCard(
    title: String,
    value: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        )

        {

            Text(
                text = title,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 24.sp
            )

        }

    }
}
    fun formatMoney(value: Long): String {
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(value)} VNĐ"
    }

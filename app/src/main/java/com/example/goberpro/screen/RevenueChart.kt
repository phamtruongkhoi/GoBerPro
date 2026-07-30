package com.example.goberpro.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun RevenueChart() {

    val values = listOf(180f, 250f, 320f, 210f, 430f, 280f, 165f)
    val days = listOf("T2","T3","T4","T5","T6","T7","CN")

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "📈 Báo cáo doanh thu 7 ngày",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {

                val max = values.maxOrNull() ?: 1f
                val stepX = size.width / (values.size - 1)

                val path = Path()

                values.forEachIndexed { index, value ->

                    val x = index * stepX
                    val y = size.height - (value / max) * size.height

                    if (index == 0)
                        path.moveTo(x, y)
                    else
                        path.lineTo(x, y)

                    drawCircle(
                        color = Color(0xFF7E57C2),
                        radius = 8f,
                        center = Offset(x, y)
                    )
                }

                drawPath(
                    path = path,
                    color = Color(0xFF7E57C2)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach {
                    Text(it)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Doanh thu TB/ngày: 262.000 VNĐ")
            Text("Ngày cao nhất: Thứ 6")
        }
    }
}
package com.example.goberpro.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goberpro.model.InsertProduct
import com.example.goberpro.model.InsertService
import com.example.goberpro.supabase
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Dịch Vụ") } // "Dịch Vụ" hoặc "Phụ Liệu"
    var isSubmitting by remember { mutableStateOf(false) }

    val categories = listOf("Dịch Vụ", "Phụ Liệu")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        // Thanh tiêu đề
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text("Thêm Mới Dữ Liệu", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Chọn Danh Mục (Dịch vụ / Phụ liệu)
        Text("Chọn danh mục:", color = TextPrimary, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            categories.forEach { category ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        colors = RadioButtonDefaults.colors(selectedColor = AccentGold, unselectedColor = TextSecondary)
                    )
                    Text(text = category, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Nhập Liệu
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên (Ví dụ: Cắt tóc nam)", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = AccentGold)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Giá tiền (Ví dụ: 150000)", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = AccentGold)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Link Ảnh (Dán URL vào đây)", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = AccentGold)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Nút Thêm Mới
        Button(
            onClick = {
                if (name.isEmpty() || price.isEmpty() || imageUrl.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isSubmitting = true
                coroutineScope.launch {
                    try {
                        val parsedPrice = price.toLongOrNull() ?: 0L

                        if (selectedCategory == "Dịch Vụ") {
                            val newService = InsertService(name = name, price = parsedPrice, imageUrl = imageUrl)
                            supabase.postgrest["services"].insert(newService)
                        } else {
                            val newProduct = InsertProduct(name = name, price = parsedPrice, imageUrl = imageUrl)
                            supabase.postgrest["products"].insert(newProduct)
                        }

                        Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                        name = ""
                        price = ""
                        imageUrl = ""
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        isSubmitting = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
            shape = RoundedCornerShape(8.dp),
            enabled = !isSubmitting
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("THÊM VÀO CỬA HÀNG", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
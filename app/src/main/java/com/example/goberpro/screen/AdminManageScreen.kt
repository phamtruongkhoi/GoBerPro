package com.example.goberpro.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.goberpro.model.AppProduct
import com.example.goberpro.model.BarberService
import com.example.goberpro.model.InsertProduct
import com.example.goberpro.model.InsertService
import com.example.goberpro.supabase
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Dịch Vụ, 1: Phụ Liệu
    var services by remember { mutableStateOf<List<BarberService>>(emptyList()) }
    var products by remember { mutableStateOf<List<AppProduct>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Trạng thái cho hộp thoại (Dialog) Sửa dữ liệu
    var showEditDialog by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf(0L) }
    var editName by remember { mutableStateOf("") }
    var editPrice by remember { mutableStateOf("") }
    var editImageUrl by remember { mutableStateOf("") }

    // Hàm tải lại dữ liệu
    suspend fun loadData() {
        isLoading = true
        try {
            services = supabase.postgrest["services"].select().decodeList()
            products = supabase.postgrest["products"].select().decodeList()
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(16.dp)) {
        // Thanh tiêu đề
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary) }
            Text("Quản Lý Dữ Liệu", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab chọn Dịch vụ / Phụ liệu
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = AccentGold
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("Dịch Vụ", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("Phụ Liệu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentGold) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (selectedTab == 0) {
                    items(services) { service ->
                        ManageItemCard(
                            name = service.name,
                            price = service.price,
                            imageUrl = service.imageUrl ?: "",
                            onEdit = {
                                editId = service.id
                                editName = service.name
                                editPrice = service.price.toString()
                                editImageUrl = service.imageUrl ?: ""
                                showEditDialog = true
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    supabase.postgrest["services"].delete { filter { eq("id", service.id) } }
                                    loadData()
                                    Toast.makeText(context, "Đã xóa ${service.name}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                } else {
                    items(products) { product ->
                        ManageItemCard(
                            name = product.name,
                            price = product.price,
                            imageUrl = product.imageUrl ?: "",
                            onEdit = {
                                editId = product.id
                                editName = product.name
                                editPrice = product.price.toString()
                                editImageUrl = product.imageUrl ?: ""
                                showEditDialog = true
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    supabase.postgrest["products"].delete { filter { eq("id", product.id) } }
                                    loadData()
                                    Toast.makeText(context, "Đã xóa ${product.name}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // --- DIALOG CHỈNH SỬA DỮ LIỆU ---
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = SurfaceColor,
            title = { Text("Chỉnh Sửa Dữ Liệu", color = AccentGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName, onValueChange = { editName = it },
                        label = { Text("Tên", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editPrice, onValueChange = { editPrice = it },
                        label = { Text("Giá", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editImageUrl, onValueChange = { editImageUrl = it },
                        label = { Text("Link Ảnh", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                    onClick = {
                        coroutineScope.launch {
                            val parsedPrice = editPrice.toLongOrNull() ?: 0L
                            if (selectedTab == 0) {
                                supabase.postgrest["services"].update(
                                    InsertService(name = editName, price = parsedPrice, imageUrl = editImageUrl)
                                ) { filter { eq("id", editId) } }
                            } else {
                                supabase.postgrest["products"].update(
                                    InsertProduct(name = editName, price = parsedPrice, imageUrl = editImageUrl)
                                ) { filter { eq("id", editId) } }
                            }
                            showEditDialog = false
                            loadData()
                            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) { Text("Lưu", color = Color.Black, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Hủy", color = TextSecondary) }
            }
        )
    }
}

@Composable
fun ManageItemCard(name: String, price: Long, imageUrl: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(SurfaceColor, RoundedCornerShape(8.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl, contentDescription = name,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, color = TextPrimary, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = "${String.format("%,d", price)} Đ", color = AccentGold, fontSize = 14.sp)
        }
        IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color(0xFF4CAF50)) }
        IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFF44336)) }
    }
}
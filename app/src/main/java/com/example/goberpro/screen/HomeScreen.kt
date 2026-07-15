package com.example.goberpro.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.goberpro.model.AppBarber
import com.example.goberpro.model.AppCombo
import com.example.goberpro.model.AppProduct
import com.example.goberpro.model.BarberService
import com.example.goberpro.supabase
import io.github.jan.supabase.postgrest.postgrest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // 1. Quản lý trạng thái dữ liệu
    var serviceList by remember { mutableStateOf<List<BarberService>>(emptyList()) }
    var productList by remember { mutableStateOf<List<AppProduct>>(emptyList()) }
    var comboList by remember { mutableStateOf<List<AppCombo>>(emptyList()) }
    var barberList by remember { mutableStateOf<List<AppBarber>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 2. Tải tất cả dữ liệu cùng lúc khi mở màn hình
    LaunchedEffect(Unit) {
        try {
            serviceList = supabase.postgrest["services"].select().decodeList()
            productList = supabase.postgrest["products"].select().decodeList()
            comboList = supabase.postgrest["combos"].select().decodeList()
            barberList = supabase.postgrest["barbers"].select().decodeList()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Thanh tìm kiếm
        item {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Tìm Dịch Vụ Bạn Muốn Làm", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceColor,
                    unfocusedContainerColor = SurfaceColor,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = AccentGold
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentGold)
                }
            }
        } else {
            // === MỤC 1: COMBO ƯU ĐÃI THẬT ===
            if (comboList.isNotEmpty()) {
                item {
                    Text("ComBo Ưu Đãi", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(comboList.size) { index ->
                            val combo = comboList[index]
                            Card(
                                modifier = Modifier.size(width = 240.dp, height = 140.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                AsyncImage(
                                    model = combo.imageUrl,
                                    contentDescription = combo.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // === MỤC 2: GOBER THẬT ===
            if (barberList.isNotEmpty()) {
                item {
                    Text("Gober", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(barberList.size) { index ->
                            val barber = barberList[index]
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(
                                    model = barber.imageUrl,
                                    contentDescription = barber.name,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = barber.name, color = TextPrimary, fontSize = 14.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // === MỤC 3: DỊCH VỤ NỔI BẬT ===
            if (serviceList.isNotEmpty()) {
                item {
                    Text("Dịch Vụ Nổi Bật", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                items(serviceList.size) { index ->
                    val service = serviceList[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .background(SurfaceColor, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = service.imageUrl,
                            contentDescription = service.name,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = service.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "${String.format("%,d", service.price)} Đ", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            // === MỤC 4: CÁC PHỤ LIỆU NỔI BẬT ===
            if (productList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Các Phụ Liệu Nổi Bật", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                items(productList.size) { index ->
                    val product = productList[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .background(SurfaceColor, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = product.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "${String.format("%,d", product.price)} Đ", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) } // Spacer cuối cùng để tránh bị thanh Navi che khuất
        }
    }
}
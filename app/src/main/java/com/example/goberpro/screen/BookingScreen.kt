package com.example.goberpro.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goberpro.model.BarberService
import com.example.goberpro.viewmodel.BarberViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale
import java.util.Calendar
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: BarberViewModel = viewModel(), 
    onBookingConfirmed: (String, String, String, String) -> Unit
) {
    val availableServices by viewModel.availableServices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedServices by viewModel.selectedServices.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var customerName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)) }
    
    val timeSlots = listOf(
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
        "11:00", "11:30", "13:00", "13:30", "14:00", "14:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
        "18:00", "18:30", "19:00", "19:30", "20:00"
    )
    var selectedTime by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.fetchServices()
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.resetBookingSelection() },
            confirmButton = {
                TextButton(onClick = { viewModel.resetBookingSelection() }) {
                    Text("Thử lại", color = AccentGold)
                }
            },
            title = { Text("Lỗi Đặt Lịch", color = Color.Red) },
            text = { Text(errorMessage ?: "Đã xảy ra lỗi không xác định.", color = Color.White) },
            containerColor = SurfaceColor
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Calendar.getInstance().apply { timeInMillis = it }
                        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.time)
                    }
                    showDatePicker = false
                }) { Text("Chọn") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ĐẶT LỊCH HẸN", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundColor)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))
            Text("1. Thông tin khách hàng", color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("Tên của bạn", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = AccentGold) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AccentGold,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Số điện thoại", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = AccentGold) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AccentGold,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("2. Chọn ngày: ", color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(
                    selectedDate,
                    color = AccentGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { showDatePicker = true }
                        .padding(8.dp)
                        .border(1.dp, AccentGold, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            Text("3. Chọn giờ hẹn", color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            val chunkedTimeSlots = timeSlots.chunked(4)
            chunkedTimeSlots.forEach { rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowSlots.forEach { time ->
                        val isTimeSelected = selectedTime == time
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTime = time },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isTimeSelected) AccentGold else SurfaceColor,
                            border = BorderStroke(1.dp, if (isTimeSelected) AccentGold else Color.DarkGray)
                        ) {
                            Text(
                                text = time,
                                color = if (isTimeSelected) Color.Black else TextPrimary,
                                modifier = Modifier.padding(vertical = 12.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontWeight = if (isTimeSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    if (rowSlots.size < 4) {
                        repeat(4 - rowSlots.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))
            Text("4. Chọn dịch vụ", color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            if (isLoading && availableServices.isEmpty()) {
                Box(modifier = Modifier.height(150.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentGold)
                }
            } else {
                availableServices.forEach { service ->
                    val isSelected = selectedServices.any { it.id == service.id }
                    ServiceItem(service, isSelected) { viewModel.toggleService(service) }
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tổng cộng:", color = TextSecondary)
                        Text(
                            "${String.format(Locale.getDefault(), "%,d", totalPrice)} Đ",
                            color = AccentGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { onBookingConfirmed(customerName, phoneNumber, selectedDate, selectedTime) },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        enabled = selectedServices.isNotEmpty() && customerName.isNotBlank() && phoneNumber.isNotBlank() && selectedTime.isNotBlank() && !isLoading
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("TIẾP TỤC", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceItem(service: BarberService, isSelected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceColor, RoundedCornerShape(8.dp))
            .border(1.dp, if (isSelected) AccentGold else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(service.name, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(service.description ?: "", color = TextSecondary, fontSize = 12.sp)
            Text("${String.format(Locale.getDefault(), "%,d", service.price)} Đ", color = AccentGold, fontWeight = FontWeight.Bold)
        }
        if (isSelected) Icon(Icons.Default.Check, contentDescription = "Selected", tint = AccentGold)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BookingScreenPreview() {
    MaterialTheme { BookingScreen(onBookingConfirmed = { _, _, _, _ -> }) }
}

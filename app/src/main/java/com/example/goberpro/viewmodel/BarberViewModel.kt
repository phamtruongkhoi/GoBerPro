package com.example.goberpro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goberpro.model.BarberService
import com.example.goberpro.model.Booking
import com.example.goberpro.model.BookingItem
import com.example.goberpro.model.Discount
import com.example.goberpro.supabase
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BarberViewModel : ViewModel() {
    private val _availableServices = MutableStateFlow<List<BarberService>>(emptyList())
    val availableServices: StateFlow<List<BarberService>> = _availableServices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedServices = MutableStateFlow<List<BarberService>>(emptyList())
    val selectedServices: StateFlow<List<BarberService>> = _selectedServices.asStateFlow()

    private val _totalPrice = MutableStateFlow(0L)
    val totalPrice: StateFlow<Long> = _totalPrice.asStateFlow()
    
    private val _appliedDiscount = MutableStateFlow<Discount?>(null)
    val appliedDiscount: StateFlow<Discount?> = _appliedDiscount.asStateFlow()

    private val _discountAmount = MutableStateFlow(0L)
    val discountAmount: StateFlow<Long> = _discountAmount.asStateFlow()

    private val _finalPrice = MutableStateFlow(0L)
    val finalPrice: StateFlow<Long> = _finalPrice.asStateFlow()

    private val _bookingSuccess = MutableStateFlow(false)
    val bookingSuccess: StateFlow<Boolean> = _bookingSuccess.asStateFlow()

    private val _bookingHistory = MutableStateFlow<List<Booking>>(emptyList())
    val bookingHistory: StateFlow<List<Booking>> = _bookingHistory.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _activeBooking = MutableStateFlow<Booking?>(null)
    val activeBooking: StateFlow<Booking?> = _activeBooking.asStateFlow()

    private val _isUpdateMode = MutableStateFlow(false)
    val isUpdateMode: StateFlow<Boolean> = _isUpdateMode.asStateFlow()

    // Trạng thái cho BookingScreen để dễ dàng pre-fill
    val customerName = MutableStateFlow("")
    val phoneNumber = MutableStateFlow("")
    val selectedDate = MutableStateFlow("")
    val selectedTime = MutableStateFlow("")
    
    // Lưu danh sách ID dịch vụ gốc để thực hiện logic GỘP (không ghi đè)
    private var originalServiceIds = listOf<Long>()

    fun fetchServices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = supabase.postgrest["services"].select().decodeList<BarberService>()
                _availableServices.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchBookingHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Lấy lịch sử booking, sắp xếp theo thời gian mới nhất
                val data = supabase.postgrest["bookings"]
                    .select()
                    .decodeList<Booking>()
                    .sortedByDescending { it.id } // Hoặc dùng created_at nếu có trong model
                _bookingHistory.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleService(service: BarberService) {
        val current = _selectedServices.value.toMutableList()
        if (current.any { it.id == service.id }) {
            current.removeAll { it.id == service.id }
        } else {
            current.add(service)
        }
        _selectedServices.value = current
        calculateTotal()
    }

    private fun calculateTotal() {
        val total = _selectedServices.value.sumOf { it.price }
        _totalPrice.value = total
        updateFinalPrice()
    }

    fun applyDiscountCode(code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val discount = supabase.postgrest["discounts"]
                    .select { filter { Discount::code eq code } }
                    .decodeSingleOrNull<Discount>()
                
                if (discount != null && discount.is_active) {
                    _appliedDiscount.value = discount
                    updateFinalPrice()
                } else {
                    _errorMessage.value = "Mã giảm giá không hợp lệ hoặc đã hết hạn"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi khi kiểm tra mã giảm giá"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateFinalPrice() {
        val total = _totalPrice.value
        val discountPercent = _appliedDiscount.value?.discount_percent ?: 0
        val discountAmt = (total * discountPercent) / 100
        _discountAmount.value = discountAmt
        _finalPrice.value = total - discountAmt
    }

    fun resetBookingSelection() {
        _selectedServices.value = emptyList()
        _totalPrice.value = 0L
        _appliedDiscount.value = null
        _discountAmount.value = 0L
        _finalPrice.value = 0L
        _bookingSuccess.value = false
        _activeBooking.value = null
        _errorMessage.value = null
        _isUpdateMode.value = false
        customerName.value = ""
        phoneNumber.value = ""
        selectedDate.value = ""
        selectedTime.value = ""
    }

    // Hàm để nạp lại đơn hàng cũ khi khách đến tiệm muốn thêm dịch vụ
    fun loadBookingForUpdate(booking: Booking) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Đảm bảo đã có danh sách dịch vụ để map ID -> Object
                if (_availableServices.value.isEmpty()) {
                    val data = supabase.postgrest["services"].select().decodeList<BarberService>()
                    _availableServices.value = data
                }

                // 1. Lấy danh sách ID các dịch vụ đã đặt từ bảng booking_items
                val items = supabase.postgrest["booking_items"]
                    .select { filter { BookingItem::booking_id eq (booking.id ?: 0) } }
                    .decodeList<BookingItem>()
                
                // 2. Chuyển ID thành danh sách BarberService object
                val currentServices = _availableServices.value.filter { service ->
                    items.any { it.service_id == service.id }
                }
                
                // Lưu lại danh sách ID gốc để phục vụ logic gộp sau này
                originalServiceIds = items.map { it.service_id }
                
                // 3. Đưa vào trạng thái chỉnh sửa và pre-fill thông tin khách
                _selectedServices.value = currentServices
                _activeBooking.value = booking
                _isUpdateMode.value = true
                
                customerName.value = booking.customer_name
                phoneNumber.value = booking.phone ?: ""
                selectedDate.value = booking.booking_date
                selectedTime.value = booking.booking_time

                calculateTotal()
                
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi khi nạp thông tin đơn hàng"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm cập nhật đơn hàng sau khi thêm dịch vụ mới
    fun updateExistingBooking() {
        val currentBooking = _activeBooking.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Cập nhật tổng tiền mới cho bảng bookings (Cái này là update thông tin chung)
                supabase.postgrest["bookings"].update(
                    {
                        set("total_price", _finalPrice.value)
                    }
                ) { filter { Booking::id eq currentBooking.id } }

                // 2. Xử lý GỘP dịch vụ (Append/addAll): Chỉ chèn thêm những món mới chọn
                // Tuyệt đối không xóa/ghi đè các món cũ trong DB
                val newSelectedServices = _selectedServices.value.filter { it.id !in originalServiceIds }
                
                if (newSelectedServices.isNotEmpty()) {
                    val newItems = newSelectedServices.map { service ->
                        BookingItem(
                            booking_id = currentBooking.id ?: 0,
                            service_id = service.id,
                            price = service.price,
                            quantity = 1
                        )
                    }
                    supabase.postgrest["booking_items"].insert(newItems)
                }

                // Cập nhật lại đối tượng local với toàn bộ danh sách dịch vụ (Cả cũ và mới)
                // để InvoiceScreen và PdfGenerator in ra đầy đủ các dòng
                _activeBooking.value = currentBooking.copy(total_price = _finalPrice.value)
                _bookingSuccess.value = true
                
                // Cập nhật lại danh sách lịch sử để HistoryScreen đồng bộ
                fetchBookingHistory()
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi khi cập nhật đơn hàng"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm cập nhật trạng thái đơn hàng thành 'Success'
    fun markBookingAsSuccess(bookingId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Cập nhật trạng thái trên Supabase
                supabase.postgrest["bookings"].update(
                    {
                        set("status", "Success")
                    }
                ) { filter { Booking::id eq bookingId } }
                
                // 2. Cập nhật lại đối tượng đang hoạt động nếu khớp ID
                if (_activeBooking.value?.id == bookingId) {
                    _activeBooking.value = _activeBooking.value?.copy(status = "Success")
                }

                // 3. Tải lại danh sách lịch sử để UI đồng bộ ngay lập tức
                fetchBookingHistory()
                
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi khi cập nhật trạng thái đơn hàng"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm thanh toán cuối cùng tại quầy
    fun markAsPaid() {
        val currentBooking = _activeBooking.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                supabase.postgrest["bookings"].update(
                    {
                        set("status", "Paid")
                    }
                ) { filter { Booking::id eq currentBooking.id } }
                
                // Cập nhật lại đối tượng local để UI hiển thị đúng
                _activeBooking.value = currentBooking.copy(status = "Paid")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun confirmBooking(customerName: String, phone: String, date: String, time: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Insert into 'bookings' table
                val newBooking = Booking(
                    customer_name = customerName,
                    phone = phone,
                    booking_date = date,
                    booking_time = time,
                    total_price = _finalPrice.value,
                    status = "Pending"
                )
                
                // We need the ID of the inserted booking
                val response = supabase.postgrest["bookings"].insert(newBooking) {
                    select()
                }.decodeSingle<Booking>()
                
                val bookingId = response.id ?: return@launch
                _activeBooking.value = response

                // 2. Insert into 'booking_items' table
                val items = _selectedServices.value.map { service ->
                    BookingItem(
                        booking_id = bookingId,
                        service_id = service.id.toLong(),
                        price = service.price,
                        quantity = 1
                    )
                }
                
                supabase.postgrest["booking_items"].insert(items)
                
                _bookingSuccess.value = true
                _errorMessage.value = null
                // Note: Don't reset selection here so InvoiceScreen can show details
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = e.localizedMessage ?: "Lỗi kết nối cơ sở dữ liệu"
                _bookingSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}
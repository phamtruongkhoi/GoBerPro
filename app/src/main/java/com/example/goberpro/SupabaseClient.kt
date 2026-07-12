package com.example.goberpro
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://svrlbkzfovxhedibrctn.supabase.co", // Thay bằng URL của cậu
    supabaseKey = "sb_publishable_FajUwq8rt8Z9R-FBeHpoCw_eGUmrXIM" // Dán Khóa API công khai mặc định từ ảnh 3 vào đây
) {
    install(Postgrest)
}
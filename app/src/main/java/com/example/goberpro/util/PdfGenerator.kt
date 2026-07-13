package com.example.goberpro.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.goberpro.model.BarberService
import com.example.goberpro.model.Booking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

object PdfGenerator {
    fun generateInvoicePdf(context: Context, booking: Booking, services: List<BarberService>) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(300, 500, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 16f
        canvas.drawText("GOBER BARBER SHOP", 150f, 40f, titlePaint)

        paint.textSize = 12f
        canvas.drawText("Khách hàng: ${booking.customer_name}", 20f, 80f, paint)
        canvas.drawText("Số điện thoại: ${booking.phone ?: "N/A"}", 20f, 100f, paint)
        canvas.drawText("Ngày: ${booking.booking_date}", 20f, 120f, paint)
        canvas.drawText("Giờ: ${booking.booking_time}", 20f, 140f, paint)

        canvas.drawLine(20f, 160f, 280f, 160f, paint)
        canvas.drawText("Dịch vụ", 20f, 180f, paint)
        canvas.drawText("Giá", 240f, 180f, paint)

        var y = 200f
        
        services.forEach { service ->
            canvas.drawText(service.name, 20f, y, paint)
            canvas.drawText("${service.price} Đ", 240f, y, paint)
            y += 20f
        }

        canvas.drawLine(20f, y, 280f, y, paint)
        y += 30f
        titlePaint.textSize = 14f
        canvas.drawText("TỔNG CỘNG: ${booking.total_price} Đ", 150f, y, titlePaint)

        pdfDocument.finishPage(page)

        val fileName = "Invoice_${booking.id ?: System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "Đã lưu PDF tại: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Lỗi xuất PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }
}
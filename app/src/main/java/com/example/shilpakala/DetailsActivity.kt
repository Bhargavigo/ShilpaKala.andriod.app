package com.example.shilpakala

import android.content.Intent
import android.graphics.*
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class DetailsActivity : AppCompatActivity() {

    private lateinit var savedFile: File
    private lateinit var imagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        imagePath = intent.getStringExtra("imagePath")!!

        // ✅ SAVE BUTTON
        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            processImage()
        }

        // ✅ SHARE BUTTON
        findViewById<Button>(R.id.shareBtn).setOnClickListener {
            if (::savedFile.isInitialized) {
                shareImage(savedFile)
            } else {
                Toast.makeText(this, "Please save image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processImage() {

        val file = File(imagePath)

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val canvas = Canvas(newBitmap)
        val paint = Paint()

        // 🔥 BRAND NAME (TOP)
        paint.color = Color.WHITE
        paint.textSize = 50f
        paint.isFakeBoldText = true
        paint.setShadowLayer(8f, 4f, 4f, Color.BLACK)
        canvas.drawText("Shilpa Kala", 50f, 100f, paint)

        val product = findViewById<EditText>(R.id.productName).text.toString()
        val wood = findViewById<EditText>(R.id.woodType).text.toString()
        val price = findViewById<EditText>(R.id.price).text.toString()

        // 🔲 BACKGROUND BOX
        // 🔲 PREMIUM CARD BACKGROUND

// ================= PREMIUM PRODUCT CARD =================

// Dark transparent card
        val cardPaint = Paint()
        cardPaint.color = Color.parseColor("#CC000000")

        val cardTop = newBitmap.height - 700f

        canvas.drawRoundRect(
            40f,
            cardTop,
            newBitmap.width - 40f,
            newBitmap.height - 40f,
            45f,
            45f,
            cardPaint
        )

// ================= BRAND BOX =================

        val brandPaint = Paint()
        brandPaint.color = Color.parseColor("#6A1B9A")

        canvas.drawRoundRect(
            50f,
            50f,
            350f,
            170f,
            25f,
            25f,
            brandPaint
        )

        paint.color = Color.WHITE
        paint.textSize = 42f
        paint.isFakeBoldText = true

        canvas.drawText("Shilpa Kala", 80f, 105f, paint)

        paint.textSize = 28f
        paint.color = Color.LTGRAY

        canvas.drawText("Handcrafted with Pride", 80f, 145f, paint)

// ================= PRODUCT NAME =================

        paint.color = Color.WHITE
        paint.textSize = 85f
        paint.isFakeBoldText = true

        canvas.drawText(
            product,
            70f,
            cardTop + 120f,
            paint
        )

// Decorative line
        val linePaint = Paint()
        linePaint.color = Color.parseColor("#D4AF37")
        linePaint.strokeWidth = 4f

        canvas.drawLine(
            70f,
            cardTop + 160f,
            newBitmap.width - 100f,
            cardTop + 160f,
            linePaint
        )

// ================= DETAILS =================

        paint.color = Color.parseColor("#F5F5F5")
        paint.textSize = 52f
        paint.isFakeBoldText = false

        canvas.drawText(
            "Wood Type",
            90f,
            cardTop + 250f,
            paint
        )

        paint.color = Color.WHITE
        paint.textSize = 60f

        canvas.drawText(
            wood,
            90f,
            cardTop + 320f,
            paint
        )

// ================= HANDMADE TEXT =================

        paint.color = Color.parseColor("#E0E0E0")
        paint.textSize = 45f

        canvas.drawText(
            "Handcrafted with Pride",
            90f,
            cardTop + 410f,
            paint
        )

        canvas.drawText(
            "Perfect for Home & Office",
            90f,
            cardTop + 490f,
            paint
        )

// ================= PRICE TAG =================

        val pricePaint = Paint()
        pricePaint.color = Color.parseColor("#6A1B9A")

        canvas.drawRoundRect(
            newBitmap.width - 370f,
            cardTop + 500f,
            newBitmap.width - 70f,
            cardTop + 640f,
            30f,
            30f,
            pricePaint
        )

// Price border
        val borderPaint = Paint()
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = Color.parseColor("#FFD54F")
        borderPaint.strokeWidth = 5f

        canvas.drawRoundRect(
            newBitmap.width - 370f,
            cardTop + 500f,
            newBitmap.width - 70f,
            cardTop + 640f,
            30f,
            30f,
            borderPaint
        )

// Price text
        paint.color = Color.WHITE
        paint.textSize = 80f
        paint.isFakeBoldText = true

        canvas.drawText(
            "₹$price",
            newBitmap.width - 320f,
            cardTop + 595f,
            paint
        )


// 🌳
// 💰

        // 📁 SAVE IMAGE
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "ShilpaKala"
        )

        if (!folder.exists()) folder.mkdirs()

        savedFile = File(folder, "FINAL_${System.currentTimeMillis()}.jpg")
        Toast.makeText(this, "Saved: ${savedFile.absolutePath}", Toast.LENGTH_LONG).show()

        val out = savedFile.outputStream()
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.close()

        // 🔄 REFRESH GALLERY
        MediaScannerConnection.scanFile(
            this,
            arrayOf(savedFile.absolutePath),
            null,
            null
        )

        Toast.makeText(this, "Image Saved in Gallery", Toast.LENGTH_SHORT).show()
    }

    private fun shareImage(file: File) {

        val uri = FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(Intent.createChooser(intent, "Share Image"))
    }
}
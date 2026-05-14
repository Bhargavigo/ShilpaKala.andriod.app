package com.example.shilpakala

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class FullImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        val imagePath = intent.getStringExtra("imagePath")!!
        val file = File(imagePath)

        val imageView = findViewById<ImageView>(R.id.fullImage)
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        imageView.setImageBitmap(bitmap)

        findViewById<Button>(R.id.shareBtn).setOnClickListener {

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
}
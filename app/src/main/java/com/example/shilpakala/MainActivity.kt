package com.example.shilpakala

import android.Manifest
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            1
        )

        startCamera()

        findViewById<Button>(R.id.captureBtn).setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            val previewView = findViewById<PreviewView>(R.id.previewView)
            preview.setSurfaceProvider(previewView.surfaceProvider)

            imageCapture = ImageCapture.Builder().build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    // ✅ IMPORTANT
                    processImage(file)

                }

                override fun onError(exception: ImageCaptureException) {}
            }
        )
    }

    // ✅ CORRECT PLACE (inside class, not inside callback)
    private fun processImage(file: File) {

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val canvas = Canvas(newBitmap)
        val paint = Paint()

        paint.color = Color.WHITE
        paint.textSize = 50f
        paint.isAntiAlias = true

        val product = findViewById<EditText>(R.id.productName).text.toString()
        val wood = findViewById<EditText>(R.id.woodType).text.toString()
        val price = findViewById<EditText>(R.id.price).text.toString()

        canvas.drawText("Handmade in Karnataka", 50f, 100f, paint)
        canvas.drawText("Product: $product", 50f, 200f, paint)
        canvas.drawText("Wood: $wood", 50f, 260f, paint)
        canvas.drawText("Price: ₹$price", 50f, 320f, paint)

        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "ShilpaKala"
        )

        if (!folder.exists()) {
            folder.mkdirs()
        }

        val outFile = File(folder, "BRANDED_${System.currentTimeMillis()}.jpg")


        val out = outFile.outputStream()
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.close()

        shareImage(outFile)
    }

    // ✅ Share feature
    private fun shareImage(file: File) {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }
}
package com.example.shilpakala

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val gridView = findViewById<GridView>(R.id.galleryGrid)

        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "ShilpaKala"
        )

        if (!folder.exists()) folder.mkdirs()

        val imageList = folder.listFiles()?.filter {
            it.name.endsWith(".jpg")
        }?.sortedByDescending {
            it.lastModified()
        } ?: listOf()

        if (imageList.isEmpty()) {
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show()
        }

        gridView.adapter = ImageAdapter(this, imageList)
    }
}

// Gallery Adapter
class ImageAdapter(
    private val context: Context,
    private val images: List<File>
) : BaseAdapter() {

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Any = images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val imageView = if (convertView == null) {

            ImageView(context).apply {

                // Bigger professional size
                layoutParams = ViewGroup.LayoutParams(500, 500)

                // Crop image nicely
                scaleType = ImageView.ScaleType.CENTER_CROP

                // Space around image
                setPadding(10, 10, 10, 10)

                // Frame effect
                background = context.getDrawable(android.R.drawable.picture_frame)
            }

        } else {
            convertView as ImageView
        }

        val bitmap = BitmapFactory.decodeFile(images[position].absolutePath)
        imageView.setImageBitmap(bitmap)

        // Open full image
        imageView.setOnClickListener {
            imageView.setOnLongClickListener {

                val deleted = images[position].delete()

                if (deleted) {
                    Toast.makeText(context, "Image Deleted", Toast.LENGTH_SHORT).show()

                    val intent = Intent(context, GalleryActivity::class.java)
                    context.startActivity(intent)
                }

                true
            }
            val intent = Intent(context, FullImageActivity::class.java)

            intent.putExtra(
                "imagePath",
                images[position].absolutePath
            )

            context.startActivity(intent)
        }

        return imageView
    }
}
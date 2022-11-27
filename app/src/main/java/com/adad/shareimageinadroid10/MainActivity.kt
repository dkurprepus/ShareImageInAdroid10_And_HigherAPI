package com.adad.shareimageinadroid10

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var ivImage: ImageView
    lateinit var btnLoadImage: Button
    lateinit var ibShare: ImageButton

    var bitmap: Bitmap? = null

    var imageUrl = "https://fujifilm-x.com/wp-content/uploads/2021/01/gfx100s_sample_01_thum.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        btnLoadImage.setOnClickListener {
            loadImage(imageUrl)
        }

        // now we will write code for share image in android 10+ api

        ibShare.setOnClickListener {
            if (bitmap != null) {
                shareImage()
            }
        }

    }

    private fun shareImage() {
        try {
            val cachePath = File(cacheDir, "images")
            cachePath.mkdir()
            val stream = FileOutputStream("$cachePath/sharable_image.png")
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val imagePath = File(cacheDir, "images")
        val newFile = File(imagePath, "sharable_image.png")
        val contentUri=FileProvider.getUriForFile(this,"$packageName.fileprovider",newFile)

        if (contentUri!=null){
            // we will share image from here
            val shareIntent=Intent()
            shareIntent.action=Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.setDataAndType(contentUri,contentResolver.getType(contentUri))
            shareIntent.putExtra(Intent.EXTRA_STREAM,contentUri)
            shareIntent.putExtra(Intent.EXTRA_TEXT,"This image is shared by android 10/11/12/13 without downloading and without asking for WRITE_EXTERNAL_STORAGE permission")
            startActivity(Intent.createChooser(shareIntent,"Choose  an app"))

            //lets run the app

        }

    }

    private fun loadImage(imageUrl: String) {
        // load image from glide
        Glide.with(this).asBitmap().load(imageUrl).into(
            object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    bitmap = resource
                    ivImage.setImageBitmap(bitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            }
        )
    }

    private fun initView() {
        ivImage = findViewById(R.id.ivImage)
        btnLoadImage = findViewById(R.id.btnLoadImage)
        ibShare = findViewById(R.id.ibShare)
    }


}

package com.gap.hoodies_network.mockwebserver

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream
import java.io.InputStream


class ImageReturn(private val context: Context) : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        get {
                // access file from assets folder
                val inputStream: InputStream = context.assets.open("drawables/orangeimage.png")
                // create drawable
                val drawable = Drawable.createFromStream(inputStream, null)
                // convert drawable to bitmap
                val bitmap = (drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                // compress bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                // convert bitmap to byte array
                val byteArray: ByteArray = stream.toByteArray()
                // pass byte array to call's respond method
                call.respond(200, byteArray)
        }
    }
}


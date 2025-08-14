package dev.mforcen.marshgallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import at.bitfire.dav4jvm.BasicDigestAuthHandler
import at.bitfire.dav4jvm.DavCollection
import at.bitfire.dav4jvm.Response
import at.bitfire.dav4jvm.property.GetContentType
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.random.Random
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Gallery (private val host: String, private val user: String, private val pass: String){
    var photos: List<String> = listOf()
    val random = Random(System.currentTimeMillis())
    fun getList() {
        val authHandler = BasicDigestAuthHandler(
            domain = null,
            username= user,
            password = pass
        )
        val okHttpClient = OkHttpClient.Builder()
            .followRedirects(false)
            .authenticator(authHandler)
            .addNetworkInterceptor(authHandler)
            .build()

        val photos = mutableListOf<String>()
        val collection = DavCollection(okHttpClient, ("$host/remote.php/dav/files/$user/Photos").toHttpUrl())
        collection.propfind(-1) {a: Response, b: Response.HrefRelation ->
            if(b == Response.HrefRelation.SELF) {
                return@propfind
            }
            for (p in a.properties ) {
                if(p is GetContentType) {
                    if(p.type == null || p.type!!.type != "image") return@propfind
                    val href = a.href.toString()
                    photos.add(href)
                }
            }
            return@propfind
        }
        this.photos = photos

        Log.d("Gallery", "Found ${photos.size} photos")
    }

    fun getLocalPath(inval: String): String {
        var counter = 7 // Nextcloud paths have 7 / before account url
        var curr = inval
        while(counter > 0) {
            val currIdx = curr.indexOf("/")
            if(currIdx < 0) return ""
            curr = curr.substring(currIdx)
            counter -= 1
        }
        return curr
    }

    fun decodeForRender(stream: InputStream): Bitmap {
        var bodyContent = ByteArrayOutputStream()
        var chunk = ByteArray(1024)
        while(true) {
            val readBytes = stream.read(chunk)
            if(readBytes == -1) {
                break
            }
            bodyContent.write(chunk, 0, readBytes)
        }
        var exif = ExifInterface(ByteArrayInputStream(bodyContent.toByteArray()))
        Log.d("imagedata", "rotation: ${exif.rotationDegrees}")
        var bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(bodyContent.toByteArray()))
        if(bitmap.width > 1920 || bitmap.height > 1080) {
            val aspectRatio = bitmap.width / bitmap.height.toFloat()
            val width = 1920
            val height = Math.round(width/aspectRatio)
            bitmap = bitmap.scale(width, height, false)
        }
        if(exif.rotationDegrees != 0) {
            bitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                Matrix().apply { preRotate(exif.rotationDegrees.toFloat()) },
                true
            )
        }
        return bitmap
    }

    fun getRandomPhoto(): Pair<String, Bitmap>? {
        if(photos.isEmpty()) return null
        val photoIndex = random.nextInt(0, photos.size)
        val authHandler = BasicDigestAuthHandler(
            domain = null,
            username= user,
            password = pass
        )
        val okHttpClient = OkHttpClient.Builder()
            .followRedirects(false)
            .authenticator(authHandler)
            .addNetworkInterceptor(authHandler)
            .build()

        val path = photos[photoIndex]
        Log.d("Gallery", "Getting $path")
        val request = Request.Builder().url(path).build()
        val call = okHttpClient.newCall(request)
        val response = call.execute()
        Log.d("Get", response.code.toString())
        val body = response.body
        if(body != null) {
            return Pair(getLocalPath(path), decodeForRender(body.byteStream()))
        }

        return null
    }
}
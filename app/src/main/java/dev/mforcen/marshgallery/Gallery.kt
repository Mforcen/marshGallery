package dev.mforcen.marshgallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import at.bitfire.dav4jvm.BasicDigestAuthHandler
import at.bitfire.dav4jvm.DavCollection
import at.bitfire.dav4jvm.Response
import at.bitfire.dav4jvm.property.GetContentType
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.time.LocalDateTime
import kotlin.random.Random
import androidx.core.graphics.scale

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
                    Log.d("Property", href + " " + p.type)
                    photos.add(href)
                }
            }
            return@propfind
        }
        this.photos = photos

        Log.d("Gallery", "Found ${photos.size} photos")
    }

    fun getRandomPhoto(): Bitmap? {
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

        Log.d("Gallery", "Getting " + photos[photoIndex])
        val request = Request.Builder().url(photos[photoIndex]).build()
        val call = okHttpClient.newCall(request)
        val response = call.execute()
        Log.d("Get", response.code.toString())
        val body = response.body
        if(body != null) {
            var bitmap = BitmapFactory.decodeStream(body.byteStream())
            if(bitmap.width > 1920 || bitmap.height > 1080) {
                val aspectRatio = bitmap.width / bitmap.height.toFloat()
                val width = 1920
                val height = Math.round(width/aspectRatio)
                bitmap = bitmap.scale(width, height, false)
            }
            return bitmap
        }

        return null
    }
}
package ruiz.marisol.lookiest.data

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import ruiz.marisol.lookiest.BuildConfig
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object CloudinaryManager {

    fun init(context: Context) {
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key"    to BuildConfig.CLOUDINARY_API_KEY
        )
        try {
            MediaManager.init(context, config)
        } catch (e: Exception) {
            // ya estaba inicializado
        }
    }

    suspend fun subirImagen(context: Context, uri: Uri): String =
        suspendCancellableCoroutine { cont ->
            MediaManager.get()
                .upload(uri)
                .unsigned("lookiest_preset")
                .option("folder", "prendas")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) cont.resume(url)
                        else cont.resumeWithException(Exception("URL nula"))
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        cont.resumeWithException(Exception(error.description))
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        cont.resumeWithException(Exception(error.description))
                    }
                })
                .dispatch(context)
        }
}
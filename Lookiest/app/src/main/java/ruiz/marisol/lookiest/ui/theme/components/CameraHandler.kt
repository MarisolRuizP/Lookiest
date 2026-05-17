package ruiz.marisol.lookiest.ui.theme.components

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

class CameraState(
    val launcher: ManagedActivityResultLauncher<Uri, Boolean>,
    val obtenerUri: () -> Uri?
)

@Composable
fun rememberCameraHandler(onFotoTomada: (Uri) -> Unit): CameraState {
    val context = LocalContext.current

    var fotoPathState by rememberSaveable { androidx.compose.runtime.mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            fotoPathState?.let { path ->
                val file = File(path)
                if (file.exists() && file.length() > 0.toLong()) {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    onFotoTomada(uri)
                }
            }
        }
    }

    return remember(launcher) {
        CameraState(
            launcher = launcher,
            obtenerUri = {
                val directory = File(context.cacheDir, "camera_photos").apply { mkdirs() }
                val file = File(directory, "img_${System.currentTimeMillis()}.jpg")
                fotoPathState = file.absolutePath
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            }
        )
    }
}
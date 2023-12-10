package com.immanlv.trem.presentation.screens.timetableBuilder.util

import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun FilePicker(
    mimeType: Array<String>?,
    openPicker:Boolean,
    onFileSelected: (ByteArray?) -> Unit
) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val v = context.applicationContext.contentResolver.openInputStream(uri)
                .use { it?.readBytes() }
            onFileSelected(v)
        }
    }
    if(openPicker)
        LaunchedEffect(key1 = Unit)
        {
            filePickerLauncher.launch(mimeType)
        }
}

fun saveFileToDownloads(
    fileName: String,
    content: ByteArray?
): String? {

    val collection = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    val f = File(collection, fileName)

    return try {
        FileOutputStream(f).use {
            it.write(content)
        }
        f.absolutePath
    } catch (_: Exception) {
        null
    }

}
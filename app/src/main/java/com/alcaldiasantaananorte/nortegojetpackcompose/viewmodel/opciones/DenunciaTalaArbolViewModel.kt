package com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.Event
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloBasico
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class DenunciaTalaArbolViewModel : ViewModel() {

    private val _resultado = MutableLiveData<Event<ModeloBasico>>()
    val resultado: LiveData<Event<ModeloBasico>> = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var disposable: Disposable? = null
    private var isRequestInProgress = false

    fun registrarDenunciaTalaArbolRX(token: String,
                                    context: Context,
                                    imageUri: Uri,
                                    _idUsuario: String,
                                    _latitud: String?,
                                    _longitud: String?,
                                     nota: String?
    ) {

        // Verificar si ya hay una solicitud en progreso
        if (isRequestInProgress) return

        isRequestInProgress = true

        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Obtener el ContentResolver
                val compressedFile = compressImage(context, imageUri)
                val requestFile = compressedFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestFile)

                // Crear RequestBody
                val notaRequestBody = nota?.toRequestBody("text/plain".toMediaTypeOrNull())
                    ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

                // Opciones
                val latitud = _latitud?.toRequestBody("text/plain".toMediaTypeOrNull())
                    ?: "".toRequestBody("text/plain".toMediaTypeOrNull())
                val longitud = _longitud?.toRequestBody("text/plain".toMediaTypeOrNull())
                    ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

                disposable =
                    RetrofitBuilder.getAuthenticatedApiService(token).registrarDenunciaTalaArbol(
                        imagePart,
                        _idUsuario,
                        notaRequestBody,
                        latitud,
                        longitud
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry(3)
                        .subscribe(
                            { response ->
                                _isLoading.value = false
                                _resultado.value = Event(response)
                                isRequestInProgress = false
                            },
                            { error ->
                                _isLoading.value = false
                                isRequestInProgress = false
                            }
                        )
            } catch (e: Exception) {
                _isLoading.value = false
                isRequestInProgress = false
                // Manejar el error de IO
            }
        }
    }

    // COMPRIMIR IMAGEN
    private suspend fun compressImage(context: Context, uri: Uri): File {
        return withContext(Dispatchers.IO) {
            val file = uriToFile(context, uri)
            Compressor.compress(context, file) {
                resolution(1280, 720)
                quality(80)
                format(Bitmap.CompressFormat.JPEG)
            }
        }
    }

    // RECONVERTIR
    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image.jpg")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose() // Limpiar la suscripción
    }
}
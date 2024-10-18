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
import java.lang.String.format

class RegistrarDenunciaBasicaViewModel : ViewModel() {

    private val _nota = MutableLiveData<String>()
    val nota: LiveData<String> = _nota

    private val _resultado = MutableLiveData<Event<ModeloBasico>>()
    val resultado: LiveData<Event<ModeloBasico>> = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var disposable: Disposable? = null
    private var isRequestInProgress = false

    fun setNota(nota: String) {
        _nota.value = nota
    }

    suspend fun registrarDenunciaBasicaRetrofit(token: String,
                                                idservicio: Int,
                                                context: Context,
                                                imageUri: Uri,
                                                _latitud: String?,
                                                _longitud: String?
        ) {

        // Verificar si ya hay una solicitud en progreso
        if (isRequestInProgress) return

        isRequestInProgress = true

        val currentNota = _nota.value ?: ""
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val compressedFile = compressImage(context, imageUri)
                val requestFile = compressedFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestFile)

                // Crear RequestBody para la nota
                val notaRequestBody = currentNota.toRequestBody("text/plain".toMediaTypeOrNull())
                val idServicioRequestBody =
                    idservicio.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                // Opciones
                val latitud = _latitud?.toRequestBody("text/plain".toMediaTypeOrNull())
                    ?: "".toRequestBody("text/plain".toMediaTypeOrNull())
                val longitud = _longitud?.toRequestBody("text/plain".toMediaTypeOrNull())
                    ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

                disposable =
                    RetrofitBuilder.getAuthenticatedApiService(token).registrarDenunciaBasica(
                        imagePart,
                        notaRequestBody,
                        idServicioRequestBody,
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




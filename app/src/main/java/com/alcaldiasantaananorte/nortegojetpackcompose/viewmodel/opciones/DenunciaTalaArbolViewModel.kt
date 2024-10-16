package com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.Event
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloBasico
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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

        try {
            // Obtener el ContentResolver
            val contentResolver = context.contentResolver

            // Crear RequestBody para la imagen
            val inputStream = contentResolver.openInputStream(imageUri)
            val byteArray = inputStream?.readBytes()
            inputStream?.close()

            if (byteArray != null) {
                val requestFile = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("imagen", "image.jpg", requestFile)

                // Crear RequestBody

                val notaRequestBody = nota?.toRequestBody("text/plain".toMediaTypeOrNull()) ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

                // Opciones
                val latitud = _latitud?.toRequestBody("text/plain".toMediaTypeOrNull()) ?: "".toRequestBody("text/plain".toMediaTypeOrNull())
                val longitud = _longitud?.toRequestBody("text/plain".toMediaTypeOrNull()) ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

                disposable = RetrofitBuilder.getAuthenticatedApiService(token).registrarDenunciaTalaArbol(imagePart,
                    _idUsuario,
                    notaRequestBody,
                    latitud,
                    longitud)

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
            } else {
                _isLoading.value = false
                // Manejar el error de imagen nula
            }
        } catch (e: IOException) {
            _isLoading.value = false
            // Manejar el error de IO
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose() // Limpiar la suscripción
    }
}
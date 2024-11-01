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
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Part
import java.io.IOException

class SolicitudTalaArbolViewModel : ViewModel() {

    private val _nombre = MutableLiveData<String>()
    val nombre: LiveData<String> = _nombre

    private val _telefono = MutableLiveData<String>()
    val telefono: LiveData<String> = _telefono

    private val _direccion = MutableLiveData<String>()
    val direccion: LiveData<String> = _direccion

    private val _nota = MutableLiveData<String>()
    val nota: LiveData<String> = _nota

    private val _resultado = MutableLiveData<Event<ModeloBasico>>()
    val resultado: LiveData<Event<ModeloBasico>> = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var disposable: Disposable? = null
    private var isRequestInProgress = false


    fun setNombre(nombre: String) {
        _nombre.value = nombre
    }

    fun setTelefono(telefono: String) {
        _telefono.value = telefono
    }

    fun setDireccion(direccion: String) {
        _direccion.value = direccion
    }

    fun setNota(nota: String) {
        _nota.value = nota
    }

    fun registrarSolicitudTalaArbolRX(token: String,
                                      context: Context,
                                      imageUri: Uri,
                                      _escritura: String,
                                      _latitud: String?,
                                      _longitud: String?
    ) {

        // Verificar si ya hay una solicitud en progreso
        if (isRequestInProgress) return

        isRequestInProgress = true

        val currentNombre = _nombre.value ?: ""
        val currentTelefono = _telefono.value ?: ""
        val currentDireccion = _direccion.value ?: ""
        val currentEscritura = _escritura
        val currentNota = _nota.value ?: ""

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

                val nombreRequestBody = currentNombre.toRequestBody("text/plain".toMediaTypeOrNull())
                val telefonoRequestBody = currentTelefono.toRequestBody("text/plain".toMediaTypeOrNull())
                val direccionRequestBody = currentDireccion.toRequestBody("text/plain".toMediaTypeOrNull())
                val escrituraRequestBody = currentEscritura.toRequestBody("text/plain".toMediaTypeOrNull())
                val notaRequestBody = currentNota.toRequestBody("text/plain".toMediaTypeOrNull())

                // Opciones
                val latitud = _latitud?.toRequestBody("text/plain".toMediaTypeOrNull()) ?: "".toRequestBody("text/plain".toMediaTypeOrNull())
                val longitud = _longitud?.toRequestBody("text/plain".toMediaTypeOrNull()) ?: "".toRequestBody("text/plain".toMediaTypeOrNull())

                disposable = RetrofitBuilder.getAuthenticatedApiService(token).registrarSolicitudTalaArbol(imagePart,
                    nombreRequestBody,
                    telefonoRequestBody,
                    direccionRequestBody,
                    escrituraRequestBody,
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
                isRequestInProgress = false
                // Manejar el error de imagen nula
            }
        } catch (e: IOException) {
            _isLoading.value = false
            isRequestInProgress = false
            // Manejar el error de IO
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose() // Limpiar la suscripción
    }
}
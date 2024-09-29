package com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.Event
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloBasico
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloVerificarCodigo
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class RegistrarDenunciaBasicaViewModel : ViewModel() {


    private val _nota = MutableLiveData<String>()
    val nota: LiveData<String> = _nota

    private val _resultado = MutableLiveData<Event<ModeloBasico>>()
    val resultado: LiveData<Event<ModeloBasico>> = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var disposable: Disposable? = null

    fun setNota(nota: String) {
        _nota.value = nota
    }


    fun registrarDenunciaBasicaRetrofit(token: String, idservicio: Int, context: Context, imageUri: Uri) {
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

                // Crear RequestBody para la nota
                val notaRequestBody = currentNota.toRequestBody("text/plain".toMediaTypeOrNull())
                val idServicioRequestBody = idservicio.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                disposable = RetrofitBuilder.getAuthenticatedApiService(token).registrarDenunciaBasica(imagePart, notaRequestBody, idServicioRequestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .retry(3)
                    .subscribe(
                        { response ->
                            _isLoading.value = false
                            _resultado.value = Event(response)
                        },
                        { error ->
                            _isLoading.value = false
                            // Manejar el error aquí
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
package com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.Event
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloBasico
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloListaServicios
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SolvenciaViewModel() : ViewModel() {

    private val _nombre = MutableLiveData<String>()
    val nombre: LiveData<String> = _nombre

    private val _dui = MutableLiveData<String>()
    val dui: LiveData<String> = _dui

    private val _resultado = MutableLiveData<Event<ModeloBasico>>()
    val resultado: LiveData<Event<ModeloBasico>> get() = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var disposable: Disposable? = null
    private var isRequestInProgress = false


    fun setNombre(nombre: String) {
        _nombre.value = nombre
    }

    fun setDui(dui: String) {
        _dui.value = dui
    }

    fun solvenciasRetrofit(token: String, tipoSolvencia: Int, nombre: String, dui: String, _latitud: String?,
                           _longitud: String?) {
        if (isRequestInProgress) return

        isRequestInProgress = true

        _isLoading.value = true
        disposable = RetrofitBuilder.getAuthenticatedApiService(token).registrarSolvencias(tipoSolvencia,
            nombre, dui, _latitud, _longitud)

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry()
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
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose() // Limpiar la suscripción
    }
}
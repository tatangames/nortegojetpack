package com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.solicitudes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.Event
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloSolicitudes
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SolicitudesViewModel() : ViewModel() {

    private val _resultado = MutableLiveData<Event<ModeloSolicitudes>>()
    val resultado: LiveData<Event<ModeloSolicitudes>> get() = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var disposable: Disposable? = null
    private var isRequestInProgress = false

    fun solicitudesRetrofit(token: String, idusuario: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true

        _isLoading.value = true
        disposable = RetrofitBuilder.getAuthenticatedApiService(token).listadoSolicitudes(idusuario)
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


class SolicitudesOcultarViewModel() : ViewModel() {

    private val _resultado = MutableLiveData<Event<ModeloSolicitudes>>()
    val resultado: LiveData<Event<ModeloSolicitudes>> get() = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var disposable: Disposable? = null
    private var isRequestInProgress = false

    fun solicitudesOcultarRetrofit(token: String, id: Int, tipo: Int) {
        if (isRequestInProgress) return

        isRequestInProgress = true

        _isLoading.value = true
        disposable = RetrofitBuilder.getAuthenticatedApiService(token).ocultarSolicitudes(id, tipo)
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
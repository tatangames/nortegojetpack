package com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.opciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.Event
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloListaServicios
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ServiciosViewModel() : ViewModel() {

    private val _resultado = MutableLiveData<Event<ModeloListaServicios>>()
    val resultado: LiveData<Event<ModeloListaServicios>> get() = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var disposable: Disposable? = null
    private var isRequestInProgress = false

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }


    fun serviciosRetrofit(token: String, onesignal: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        _isLoading.value = true
        disposable = RetrofitBuilder.getAuthenticatedApiService(token).listadoServicios(onesignal)
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
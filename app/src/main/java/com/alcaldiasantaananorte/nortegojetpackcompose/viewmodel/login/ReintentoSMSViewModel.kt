package com.alcaldiasantaananorte.nortegojetpackcompose.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.extras.Event
import com.alcaldiasantaananorte.nortegojetpackcompose.model.datos.ModeloReintentoSMS
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ReintentoSMSViewModel : ViewModel() {
    private val _telefono = MutableLiveData<String>()

    private val _resultado = MutableLiveData<Event<ModeloReintentoSMS>>()
    val resultado: LiveData<Event<ModeloReintentoSMS>> get() = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var disposable: Disposable? = null
    private var isRequestInProgress = false

    fun setTelefono(telefono: String) {
        _telefono.value = telefono
    }

    fun reitentoSMSRetrofit() {
        if (isRequestInProgress) return

        isRequestInProgress = true

        _isLoading.value = true
        disposable = RetrofitBuilder.getApiService().reintentoSMS(_telefono.value!!)
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
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose() // Limpiar la suscripción
    }
}
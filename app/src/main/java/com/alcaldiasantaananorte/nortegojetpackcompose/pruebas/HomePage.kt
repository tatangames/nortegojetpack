package com.alcaldiasantaananorte.nortegojetpackcompose.pruebas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomePage(modifier: Modifier = Modifier, viewModel: HomeViewModel){


    val userData = viewModel.userData.observeAsState()
    var isLoading = viewModel.isLoading.observeAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            viewModel.getUserData()
        }) {
            Text(text = "Get Data")
        }

        if(isLoading.value == true){
            CircularProgressIndicator()
        }else {
            userData.value?.name?.let {
                Text(text = "Nombre: $it")
            }

            userData.value?.age?.let {
                Text(text = "Edad: $it")
            }
        }
    }
}
package com.alcaldiasantaananorte.nortegojetpackcompose.pruebas

import kotlinx.coroutines.delay

class UserRepository {

    suspend fun fetchUserData() : UserData{
        delay(2000)
        return UserData("Jonathan", 30)
    }
}




















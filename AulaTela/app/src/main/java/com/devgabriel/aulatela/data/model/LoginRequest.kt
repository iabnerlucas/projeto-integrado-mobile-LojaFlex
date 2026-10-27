
package com.devgabriel.aulatela.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("login")
    val login: String,

    @SerializedName("senha")
    val senha: String
)

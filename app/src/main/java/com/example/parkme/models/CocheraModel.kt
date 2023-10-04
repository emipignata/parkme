package com.example.parkme.models

import com.google.gson.annotations.SerializedName

data class CocheraModel (
    @SerializedName("status") var status: String,
    @SerializedName("message") var images: List<String>
)
// aca estamos usando el modelo de esta API pero hay que establecer los datos de la cochera
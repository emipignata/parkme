package com.example.parkme.viewmodels

import androidx.lifecycle.ViewModel
import com.example.parkme.entities.Cochera

class CocheraViewModel : ViewModel() {
    var cocheras : MutableList<Cochera> = ArrayList<Cochera>()
    fun initTestList ()
    {
        cocheras.add(Cochera("Pedro1", "Libertador 123455", -33.1301719, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg"))
        cocheras.add(Cochera("Pedro2", "Libertador 123456", -33.1301719, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg"))
        cocheras.add(Cochera("Pedro3", "Libertador 123457", -33.1301719, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg"))
        cocheras.add(Cochera("Pedro4", "Libertador 123458", -33.1301719, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg"))
        cocheras.add(Cochera("Pedro5", "Libertador 123459", -33.1301719, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg"))
        cocheras.add(Cochera("Pedro6", "Libertador 123450", -33.1301719, -64.34902, 3.0f, "https://raicesdeperaleda.com/recursos/cache/cochera-1555889699-250x250.jpg"))
    }

}
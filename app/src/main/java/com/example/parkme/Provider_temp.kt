package com.example.parkme
import com.example.parkme.models.CocheraModel
import com.example.parkme.models.PaginatedResponse
import com.example.parkme.models.ParkSlot
import com.example.parkme.services.CocheraService
import retrofit2.Call
import retrofit2.http.GET

class Provider_temp {
    companion object {

        val cardCocheraList = listOf<ParkSlot>(
            ParkSlot(
                "Impecalbe Cochera en Recoleta",
                "Juncal 2200",
                "$450",
                "https://static.wikia.nocookie.net/death-battle-fanon-wiki-en-espanol/images/5/5d/Wolverine.png/revision/latest/thumbnail/width/360/height/360?cb=20190217160514&path-prefix=es"

            ),
            ParkSlot(
                "Cochera super espaciosa",
                "Larrea 1234",
                "$450",
                "https://static.wikia.nocookie.net/ficcion-sin-limites/images/6/6b/Spidey.png/revision/latest/scale-to-width-down/375?cb=20210226124716&path-prefix=es"
            ),
            ParkSlot(
                "Cochera Caballito",
                "9 de Julio 3215",
                "$450",
                "https://static.wikia.nocookie.net/watchmen/images/d/d6/Doctor_Manhattan.jpg/revision/latest?cb=20211012133425"
            ),
            ParkSlot(
                "Estacionamiento en Edificio",
                "Av Libertador 1230",
                "$450",
                "https://www.fayerwayer.com/resizer/9hSv8JSTiR3a59ijpdVTdD0qUFA=/1024x1024/filters:format(jpg):quality(70)/cloudfront-us-east-1.images.arcpublishing.com/metroworldnews/OIDT5BK4ARHWDAO736DJD5CNMU.jpeg"
            ),
            ParkSlot(
                "Cochera",
                "Alvear 3456",
                "$450",
                "https://static.wikia.nocookie.net/marveldatabase/images/2/23/Benjamin_Grimm_%28Earth-616%29_from_Heroic_Age_Advertisement.jpg/revision/latest/scale-to-width-down/527?cb=20110622061953"
            ),
            ParkSlot(
                "Cool Parking at Buenos Aires",
                "Av Santa Fe 9875",
                "$450",
                "https://static.wikia.nocookie.net/marvelall/images/e/e2/Galactus.jpg/revision/latest?cb=20130828192211&path-prefix=es"
            ),
            ParkSlot(
                "Parking",
                "Corrientes 2000",
                "$450",
                "https://www.eltiempo.com/files/image_1200_680/uploads/2019/04/04/5ca667c6a0964.jpeg"
            ),
            ParkSlot(
                "Estacionamiento privado",
                "Gutierrexz 3754",
                "$450",
                "https://pbs.twimg.com/media/CVpMW0xXIAABwR9.jpg"
            ),
        )
    }


}
package routes

import dev.kona.core.Kona
import dev.kona.crema.CremaEngine
import dev.kona.http.Response
import dev.kona.http.Router

object WebRoutes {
    fun register(router: Router) {
        router.get("/") {
            val crema = Kona.app().resolve(CremaEngine::class.java)
            Response.html(crema.render("welcome", mapOf("title" to "Brew sample")))
        }.name("welcome")
    }
}

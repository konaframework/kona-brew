package routes

import io.konaframework.core.Kona
import io.konaframework.crema.CremaEngine
import io.konaframework.http.Response
import io.konaframework.http.Router

object WebRoutes {
    fun register(router: Router) {
        router.get("/") {
            val crema = Kona.app().resolve(CremaEngine::class.java)
            Response.html(crema.render("welcome", mapOf("title" to "Brew sample")))
        }.name("welcome")
    }
}

package bootstrap

import dev.kona.brew.BrewServiceProvider
import dev.kona.core.Application
import dev.kona.core.Env
import dev.kona.core.Kona
import dev.kona.crema.CremaServiceProvider
import dev.kona.http.HttpKernel
import dev.kona.http.HttpMiddleware
import dev.kona.http.HttpServiceProvider
import dev.kona.http.KonaHttpServer
import dev.kona.http.Router
import dev.kona.session.SessionManager
import dev.kona.session.SessionServiceProvider
import dev.kona.session.StartSession
import dev.kona.session.VerifyCsrfToken
import routes.WebRoutes

object App {
    @JvmStatic
    fun create(args: Array<String>): Application {
        val app = Kona.create(args)
            .configure {
                it.set("app.key", Env.getOrDefault("APP_KEY", "brew-sample-kotlin-key"))
                it.set("app.debug", true)
                it.set("session.driver", "cookie")
            }
            .providers(HttpServiceProvider(), SessionServiceProvider(), CremaServiceProvider(), BrewServiceProvider())
            .afterBoot(::middleware)
            .boot()
        WebRoutes.register(app.resolve(Router::class.java))
        return app
    }

    private fun middleware(app: Application) {
        val sessions = app.resolve(SessionManager::class.java)
        HttpMiddleware()
            .alias("start.session") { StartSession(sessions) }
            .alias("csrf") { VerifyCsrfToken() }
            .web(listOf("start.session", "csrf"))
            .apply(app)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val app = create(args)
        val host = Env.getOrDefault("HOST", "127.0.0.1")
        val port = Env.getOrDefault("PORT", "4000").toInt()
        KonaHttpServer(app.resolve(HttpKernel::class.java), host, port).start()
    }
}

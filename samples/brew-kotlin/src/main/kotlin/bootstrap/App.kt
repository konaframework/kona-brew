package bootstrap

import io.konaframework.brew.BrewServiceProvider
import io.konaframework.core.Application
import io.konaframework.core.Env
import io.konaframework.core.Kona
import io.konaframework.crema.CremaServiceProvider
import io.konaframework.http.HttpKernel
import io.konaframework.http.HttpMiddleware
import io.konaframework.http.HttpServiceProvider
import io.konaframework.http.KonaHttpServer
import io.konaframework.http.Router
import io.konaframework.session.SessionManager
import io.konaframework.session.SessionServiceProvider
import io.konaframework.session.StartSession
import io.konaframework.session.VerifyCsrfToken
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

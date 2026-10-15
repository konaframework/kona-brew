package dev.kona.brew

import dev.kona.brew.view.InjectAssets
import dev.kona.core.Application
import dev.kona.core.ServiceProvider
import dev.kona.crema.CremaEngine
import dev.kona.http.HttpKernel
import dev.kona.http.Response
import dev.kona.http.Router

/**
 * Binds [BrewManager], installs the Crema surface, and registers
 * `POST {brew.path}/update`, `GET {brew.path}/brew.js|brew.min.js|brew.min.js.map`, plus asset injection.
 */
class BrewServiceProvider : ServiceProvider {
    override fun register(app: Application) {
        app.container.singleton(BrewManager::class.java) {
            val views = if (app.container.bound(CremaEngine::class.java)) app.resolve(CremaEngine::class.java) else null
            BrewManager(BrewConfig.from(app.config), views, app.container)
        }
    }

    override fun boot(app: Application) {
        val brew = app.resolve(BrewManager::class.java)
        Brew.setManager(brew)
        brew.config.classNamespaces.forEach { brew.registry.addLocation(it) }
        if (app.container.bound(Router::class.java)) routes(brew, app.resolve(Router::class.java))
        if (app.container.bound(HttpKernel::class.java)) app.resolve(HttpKernel::class.java).pushMiddleware(InjectAssets(brew))
    }

    companion object {
        @JvmStatic
        fun routes(brew: BrewManager, router: Router) {
            router.post(brew.config.updateUri) {
                if (brew.updateUri != brew.config.updateUri) Response.text("Not Found", 404) else brew.handleUpdate(it)
            }
            for (name in listOf("brew.js", "brew.min.js", "brew.min.js.map")) {
                router.get("${brew.config.path}/$name") {
                    if (brew.scriptUri != null) Response.text("Not Found", 404) else brew.assets.response(name)
                }
            }
        }
    }
}

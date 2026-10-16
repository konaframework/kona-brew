package io.konaframework.brew

import io.konaframework.brew.view.InjectAssets
import io.konaframework.core.Application
import io.konaframework.core.ServiceProvider
import io.konaframework.crema.CremaEngine
import io.konaframework.http.HttpKernel
import io.konaframework.http.Response
import io.konaframework.http.Router

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
        // Capture the app classloader (set by AppLoader / the creating thread) before request VTs spawn.
        Thread.currentThread().contextClassLoader?.let { brew.registry.classLoader = it }
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
            for (name in listOf("brew.js", "brew.min.js", "brew.min.js.map", "brew.csp.min.js", "brew.esm.js")) {
                router.get("${brew.config.path}/$name") {
                    if (brew.scriptUri != null) Response.text("Not Found", 404) else brew.assets.response(name)
                }
            }
            router.post("${brew.config.path}/upload") { brew.handleUpload(it) }
            router.get("${brew.config.path}/preview/{path}") { brew.handlePreview(it) }
        }
    }
}


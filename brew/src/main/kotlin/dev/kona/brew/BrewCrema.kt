package dev.kona.brew

import dev.kona.brew.hydration.Coercion
import dev.kona.crema.CremaEngine
import dev.kona.crema.CremaTag

/**
 * Crema surface: `<brew:name … />`, `@brew('name', $params, $key)`, `@brewStyles`, `@brewScripts`,
 * `@brewScriptConfig`, `@js($value)`.
 */
internal object BrewCrema {
    fun install(engine: CremaEngine, manager: BrewManager) {
        engine.tag("brew") { tag -> renderTag(manager, tag) }
        engine.directive("brew") { call ->
            val args = call.arguments()
            require(args.isNotEmpty()) { "@brew requires a component name" }
            @Suppress("UNCHECKED_CAST")
            val params = (args.getOrNull(1) as? Map<String, Any?>) ?: emptyMap()
            manager.mount(args[0].toString(), params, args.getOrNull(2)?.toString())
        }
        engine.directive("brewStyles") { manager.assets.styles() }
        engine.directive("brewScripts") { manager.assets.scripts() }
        engine.directive("brewScriptConfig") { manager.assets.scriptConfig() }
        engine.directive("js") { call -> js(call.evaluate(call.expression)) }
    }

    private fun renderTag(manager: BrewManager, tag: CremaTag): String {
        val params = LinkedHashMap<String, Any?>()
        var key: String? = null
        for ((name, value) in tag.attributes) {
            when (name) {
                "key", "wire:key" -> key = value?.toString()
                else -> params[if (name.contains('-') && !name.contains(':')) camel(name) else name] = value
            }
        }
        return manager.mount(tag.name, params, key)
    }

    private val KEBAB = Regex("-+([a-zA-Z0-9])")

    private fun camel(value: String): String = KEBAB.replace(value) { it.groupValues[1].uppercase() }

    /** `Js::from` — safe JavaScript expression for embedding in HTML attributes / scripts. */
    fun js(value: Any?): String {
        val json = Coercion.mapper.writeValueAsString(value)
            .replace("<", "\\u003C").replace(">", "\\u003E").replace("&", "\\u0026").replace("'", "\\u0027")
        return when {
            json.startsWith("{") || json.startsWith("[") -> "JSON.parse('${json.replace("\\", "\\\\").replace("\"", "\\u0022")}')"
            json.startsWith("\"") -> "'" + json.substring(1, json.length - 1).replace("\\\"", "\\u0022") + "'"
            else -> json
        }
    }
}

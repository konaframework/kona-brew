package dev.kona.brew.features

import dev.kona.brew.lifecycle.ComponentContext
import dev.kona.brew.lifecycle.ComponentHook
import dev.kona.brew.lifecycle.ReturnEarly
import dev.kona.brew.meta.ComponentMeta

/** `$refresh`, `$commit`, `$set(path, value)`, `$toggle(path)` (`SupportMagicActions`). */
class SupportMagicActions : ComponentHook() {
    override fun call(
        method: String,
        params: List<Any?>,
        returnEarly: ReturnEarly,
        metadata: Map<String, Any?>,
        context: ComponentContext,
    ): ((Any?) -> Any?)? {
        when (method) {
            "\$refresh", "\$commit" -> returnEarly()
            "\$set" -> {
                manager.updateProperty(component, params[0].toString(), params.getOrNull(1), context)()
                returnEarly()
            }
            "\$toggle" -> {
                val path = params[0].toString()
                val segments = path.split('.')
                val root = ComponentMeta.of(component).properties[segments.first()]?.get(component)
                val current = if (segments.size == 1) root else manager.synths.getPath(root, segments.drop(1))
                manager.updateProperty(component, path, current != true, context)()
                returnEarly()
            }
        }
        return null
    }
}

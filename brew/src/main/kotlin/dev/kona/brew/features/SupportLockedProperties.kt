package dev.kona.brew.features

import dev.kona.brew.CannotUpdateLockedPropertyException
import dev.kona.brew.lifecycle.ComponentHook
import dev.kona.brew.meta.ComponentMeta

/** `@Locked` (and read-only `val`) properties reject browser updates. */
class SupportLockedProperties : ComponentHook() {
    override fun update(property: String, fullPath: String, value: Any?): (() -> Unit)? {
        if (ComponentMeta.of(component).properties[property]?.locked == true) {
            throw CannotUpdateLockedPropertyException(property)
        }
        return null
    }
}

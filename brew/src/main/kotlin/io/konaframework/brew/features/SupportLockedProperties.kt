package io.konaframework.brew.features

import io.konaframework.brew.CannotUpdateLockedPropertyException
import io.konaframework.brew.lifecycle.ComponentHook
import io.konaframework.brew.meta.ComponentMeta

/** `@Locked` (and read-only `val`) properties reject browser updates. */
class SupportLockedProperties : ComponentHook() {
    override fun update(property: String, fullPath: String, value: Any?): (() -> Unit)? {
        if (ComponentMeta.of(component).properties[property]?.locked == true) {
            throw CannotUpdateLockedPropertyException(property)
        }
        return null
    }
}

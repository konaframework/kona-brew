package io.konaframework.brew.features

import io.konaframework.brew.lifecycle.ComponentContext
import io.konaframework.brew.lifecycle.ComponentHook

/** Queues `$this->js(...)` expressions into `effects.xjs`. */
class SupportJsEvaluation : ComponentHook() {
    override fun dehydrate(context: ComponentContext) {
        @Suppress("UNCHECKED_CAST")
        val scripts = component.state().store["xjs"] as? List<String>
        if (!scripts.isNullOrEmpty()) context.addEffect("xjs", scripts)
    }
}

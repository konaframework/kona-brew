package dev.kona.brew.features

import dev.kona.brew.lifecycle.ComponentContext
import dev.kona.brew.lifecycle.ComponentHook

/** Queues `$this->js(...)` expressions into `effects.xjs`. */
class SupportJsEvaluation : ComponentHook() {
    override fun dehydrate(context: ComponentContext) {
        @Suppress("UNCHECKED_CAST")
        val scripts = component.state().store["xjs"] as? List<String>
        if (!scripts.isNullOrEmpty()) context.addEffect("xjs", scripts)
    }
}

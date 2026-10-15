package dev.kona.brew.features

import dev.kona.brew.lifecycle.ComponentContext
import dev.kona.brew.lifecycle.ComponentHook

/** Emits `effects.streams` from `$this->stream(...)`. */
class SupportStreaming : ComponentHook() {
    override fun dehydrate(context: ComponentContext) {
        @Suppress("UNCHECKED_CAST")
        val streams = component.state().store["streams"] as? List<Map<String, Any?>>
        if (!streams.isNullOrEmpty()) context.addEffect("streams", streams)
    }
}

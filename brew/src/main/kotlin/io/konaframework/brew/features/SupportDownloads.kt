package io.konaframework.brew.features

import io.konaframework.brew.lifecycle.ComponentContext
import io.konaframework.brew.lifecycle.ComponentHook
import java.util.Base64

/** Emits `effects.download` from `$this->download(...)` / `$this->streamDownload(...)`. */
class SupportDownloads : ComponentHook() {
    override fun dehydrate(context: ComponentContext) {
        @Suppress("UNCHECKED_CAST")
        val download = component.state().store["download"] as? Map<String, Any?> ?: return
        context.addEffect("download", download)
    }

    companion object {
        fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)
    }
}

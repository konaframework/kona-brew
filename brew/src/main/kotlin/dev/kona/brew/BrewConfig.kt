package dev.kona.brew

import dev.kona.core.Config

/**
 * Brew settings (`config/brew` — Livewire's `config/livewire.php`). Read from Kona [Config] keys under `brew.*`.
 */
class BrewConfig @JvmOverloads constructor(
    /** HMAC key for snapshot checksums (`app.key`); a random per-process key is used when blank. */
    val key: String = "",
    /** Base path for Brew endpoints. */
    val path: String = "/brew",
    /** Package roots scanned by discovery (`brew.class_namespace`). */
    val classNamespaces: List<String> = emptyList(),
    /** View prefix for convention views (`brew.view_path` → `brew.<name>`). */
    val viewPrefix: String = "brew",
    /** Inject `@brewStyles` / `@brewScripts` into full HTML responses that lack them. */
    val injectAssets: Boolean = true,
    /** Wrap conditionals/loops in morph markers. */
    val injectMorphMarkers: Boolean = true,
    /** Require `_token` / `X-CSRF-TOKEN` on updates when a session is present. */
    val csrf: Boolean = true,
    /** Max request body size in bytes (`payload.max_size`); null (or `0` in config) disables. */
    val maxPayloadSize: Int? = 1024 * 1024,
    /** Max components per request (`payload.max_components`). */
    val maxComponents: Int? = 20,
    /** Max method calls per component (`payload.max_calls`). */
    val maxCalls: Int? = 50,
    /** Max dot-path depth for updates (`payload.max_nesting_depth`). */
    val maxNestingDepth: Int? = 10,
    /** Show detailed errors (debug modal on the client). */
    val debug: Boolean = false,
    /** Asset URL used in `@brewScripts` (`asset_url`). */
    val assetUrl: String? = null,
    /** CSP nonce added to Brew's `<script>` / `<style>` tags (`brew.nonce`). */
    val scriptNonce: String? = null,
) {
    val updateUri: String get() = "$path/update"

    companion object {
        @JvmStatic
        fun from(config: Config): BrewConfig {
            val namespaces = when (val ns = config.get<Any>("brew.class_namespace")) {
                is Collection<*> -> ns.map { it.toString() }
                null -> emptyList()
                else -> ns.toString().split(',').map { it.trim() }.filter { it.isNotEmpty() }
            }
            return BrewConfig(
                key = config.getString("app.key", ""),
                path = config.getString("brew.path", "/brew"),
                classNamespaces = namespaces,
                viewPrefix = config.getString("brew.view_prefix", "brew"),
                injectAssets = config.getBoolean("brew.inject_assets", true),
                injectMorphMarkers = config.getBoolean("brew.inject_morph_markers", true),
                csrf = config.getBoolean("brew.csrf", true),
                maxPayloadSize = optionalInt(config, "brew.payload.max_size", 1024 * 1024),
                maxComponents = optionalInt(config, "brew.payload.max_components", 20),
                maxCalls = optionalInt(config, "brew.payload.max_calls", 50),
                maxNestingDepth = optionalInt(config, "brew.payload.max_nesting_depth", 10),
                debug = config.getBoolean("app.debug", false),
                assetUrl = config.getString("brew.asset_url"),
                scriptNonce = config.getString("brew.nonce"),
            )
        }

        private fun optionalInt(config: Config, key: String, default: Int): Int? {
            if (!config.has(key)) return default
            return config.getInt(key)?.takeIf { it > 0 }
        }
    }
}

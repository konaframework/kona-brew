package dev.kona.brew

/**
 * What a component renders: a named Crema view, inline Crema source, or raw HTML.
 */
class BrewView private constructor(
    /** Crema view name (null for inline / raw). */
    val name: String?,
    /** Inline Crema source (null for named / raw). */
    val source: String?,
    /** Pre-rendered HTML used verbatim. */
    val html: String?,
    data: Map<String, Any?>,
) {
    val data: MutableMap<String, Any?> = LinkedHashMap(data)

    /** Add view data (`->with('key', $value)`). */
    fun with(key: String, value: Any?): BrewView {
        data[key] = value
        return this
    }

    fun with(values: Map<String, Any?>): BrewView {
        data.putAll(values)
        return this
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun named(name: String, data: Map<String, Any?> = emptyMap()): BrewView = BrewView(name, null, null, data)

        @JvmStatic
        @JvmOverloads
        fun inline(source: String, data: Map<String, Any?> = emptyMap()): BrewView = BrewView(null, source, null, data)

        @JvmStatic
        fun raw(html: String): BrewView = BrewView(null, null, html, emptyMap())
    }
}

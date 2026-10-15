package dev.kona.brew.testing

import dev.kona.brew.BrewManager
import dev.kona.brew.Component
import dev.kona.brew.ComponentResult
import dev.kona.brew.hydration.Coercion
import dev.kona.brew.meta.ComponentMeta
import dev.kona.brew.view.HtmlRoot
import dev.kona.http.HttpException
import java.math.BigDecimal
import java.util.function.Predicate

/**
 * Fluent component test driver (`Livewire::test`). Every interaction performs a real snapshot round-trip
 * (JSON-encoded, checksum-verified) through [BrewManager.updateComponent].
 */
class Testable private constructor(
    private val manager: BrewManager,
    private var result: ComponentResult,
) {
    private var lastHtml: String = result.html!!
    private var status: Int = 200

    companion object {
        @JvmStatic
        fun create(manager: BrewManager, name: String, params: Map<String, Any?>): Testable =
            Testable(manager, manager.mountComponent(name, params, null))

        private val INITIAL_DATA = Regex(""" wire:(snapshot|effects)="[^"]*"""")
    }

    // ---- interactions -------------------------------------------------------------------------------------------

    fun set(name: String, value: Any?): Testable = roundTrip(mapOf(name to value), emptyList())

    fun set(values: Map<String, Any?>): Testable = roundTrip(values, emptyList())

    fun toggle(name: String): Testable = set(name, get(name) != true)

    fun call(method: String, vararg params: Any?): Testable =
        roundTrip(emptyMap(), listOf(mapOf("path" to "", "method" to method, "params" to params.toList(), "metadata" to emptyMap<String, Any?>())))

    fun refresh(): Testable = call("\$refresh")

    fun commit(): Testable = call("\$commit")

    /** Remount with query-string params (`->withQueryParams([...])`). */
    fun withQueryParams(params: Map<String, Any?>): Testable {
        result = manager.mountComponent(result.component.name(), params, null)
        lastHtml = result.html!!
        status = 200
        return this
    }

    /** Assert the page HTML contains a mounted Brew component by name (`assertSeeLivewire`). */
    fun assertSeeBrew(name: String): Testable {
        check(html().contains("wire:name=\"$name\"") || html().contains("wire:name='$name'")) {
            "Failed asserting that Brew component [$name] is visible"
        }
        return this
    }

    fun assertDontSeeBrew(name: String): Testable {
        val seen = html().contains("wire:name=\"$name\"") || html().contains("wire:name='$name'")
        check(!seen) { "Failed asserting that Brew component [$name] is not visible" }
        return this
    }

    private fun roundTrip(updates: Map<String, Any?>, calls: List<Map<String, Any?>>): Testable {
        @Suppress("UNCHECKED_CAST")
        val wire = Coercion.mapper.readValue(manager.json(result.snapshot), Map::class.java) as Map<String, Any?>
        try {
            result = manager.updateComponent(wire, updates, calls)
            status = 200
            result.html?.let { lastHtml = it }
        } catch (e: HttpException) {
            status = e.status
        }
        return this
    }

    // ---- accessors ----------------------------------------------------------------------------------------------

    fun instance(): Component = result.component

    fun id(): String = result.component.id()

    fun snapshot(): Map<String, Any?> = result.snapshot

    fun effects(): Map<String, Any?> = result.effects

    @JvmOverloads
    fun html(stripInitialData: Boolean = false): String = if (stripInitialData) INITIAL_DATA.replace(lastHtml, "") else lastHtml

    /** Property, nested path (`todos.0.title`) or `@Computed` value. */
    fun get(name: String): Any? {
        val component = result.component
        val segments = name.split('.')
        val meta = ComponentMeta.of(component)
        val root = when {
            meta.properties.containsKey(segments[0]) -> meta.properties.getValue(segments[0]).get(component)
            meta.computed.containsKey(segments[0]) -> manager.computedValue(component, segments[0])
            else -> throw AssertionError("Property [${segments[0]}] not found on component [${component.name()}]")
        }
        return if (segments.size == 1) root else manager.synths.getPath(root, segments.drop(1))
    }

    fun viewData(key: String): Any? = result.viewData[key].let { if (it is dev.kona.crema.CremaLazy) it.get() else it }

    // ---- assertions ---------------------------------------------------------------------------------------------

    @JvmOverloads
    fun assertSet(name: String, value: Any?, strict: Boolean = false): Testable {
        val actual = get(name)
        check(if (strict) actual == value else looseEquals(actual, value)) {
            "Failed asserting that [$name] is set to [$value]; actual [$actual]"
        }
        return this
    }

    @JvmOverloads
    fun assertNotSet(name: String, value: Any?, strict: Boolean = false): Testable {
        val actual = get(name)
        check(!(if (strict) actual == value else looseEquals(actual, value))) {
            "Failed asserting that [$name] is not set to [$value]"
        }
        return this
    }

    fun assertCount(name: String, count: Int): Testable {
        val size = when (val value = get(name)) {
            is Collection<*> -> value.size
            is Map<*, *> -> value.size
            is Array<*> -> value.size
            is CharSequence -> value.length
            else -> throw AssertionError("[$name] is not countable")
        }
        check(size == count) { "Failed asserting that [$name] has $count items; actual $size" }
        return this
    }

    @JvmOverloads
    fun assertSee(value: Any, escape: Boolean = true): Testable {
        for (v in values(value)) {
            val needle = if (escape) HtmlRoot.escape(v) else v
            check(html(true).contains(needle)) { "Failed asserting that [$v] is visible in:\n${html(true)}" }
        }
        return this
    }

    @JvmOverloads
    fun assertDontSee(value: Any, escape: Boolean = true): Testable {
        for (v in values(value)) {
            val needle = if (escape) HtmlRoot.escape(v) else v
            check(!html(true).contains(needle)) { "Failed asserting that [$v] is not visible" }
        }
        return this
    }

    fun assertSeeHtml(value: Any): Testable = assertSee(value, escape = false)

    fun assertDontSeeHtml(value: Any): Testable = assertDontSee(value, escape = false)

    @JvmOverloads
    fun assertSeeInOrder(values: List<String>, escape: Boolean = true): Testable {
        val haystack = html(true)
        var from = 0
        for (v in values) {
            val needle = if (escape) HtmlRoot.escape(v) else v
            val at = haystack.indexOf(needle, from)
            check(at >= 0) { "Failed asserting that [$values] appear in order" }
            from = at + needle.length
        }
        return this
    }

    fun assertSeeHtmlInOrder(values: List<String>): Testable = assertSeeInOrder(values, escape = false)

    fun assertViewHas(key: String): Testable {
        check(result.viewData.containsKey(key)) { "Failed asserting that the view has [$key]" }
        return this
    }

    fun assertViewHas(key: String, value: Any?): Testable {
        assertViewHas(key)
        val actual = viewData(key)
        @Suppress("UNCHECKED_CAST")
        val ok = when (value) {
            is Predicate<*> -> (value as Predicate<Any?>).test(actual)
            is Function1<*, *> -> (value as (Any?) -> Any?)(actual) == true
            else -> looseEquals(actual, value)
        }
        check(ok) { "Failed asserting that view data [$key] matches [$value]; actual [$actual]" }
        return this
    }

    fun assertViewHas(key: String, predicate: Predicate<Any?>): Testable = assertViewHas(key, predicate as Any)

    fun assertViewIs(name: String): Testable {
        val actual = result.view?.name
        check(actual == name) { "Failed asserting that view [$actual] is [$name]" }
        return this
    }

    fun assertReturned(value: Any?): Testable {
        val returns = result.effects["returns"] as? List<*> ?: emptyList<Any?>()
        check(returns.isNotEmpty() && looseEquals(returns.last(), value)) { "Failed asserting that [$value] was returned; got $returns" }
        return this
    }

    fun assertRenderSkipped(): Testable {
        check(!result.effects.containsKey("html")) { "Failed asserting that rendering was skipped" }
        return this
    }

    fun assertRenderNotSkipped(): Testable {
        check(result.effects.containsKey("html")) { "Failed asserting that the component re-rendered" }
        return this
    }

    fun assertStatus(code: Int): Testable {
        check(status == code) { "Expected status [$code] but received [$status]" }
        return this
    }

    fun assertOk(): Testable = assertStatus(200)

    fun assertUnauthorized(): Testable = assertStatus(401)

    fun assertForbidden(): Testable = assertStatus(403)

    fun assertNotFound(): Testable = assertStatus(404)

    fun assertHasErrors(vararg keys: String): Testable {
        val bag = result.component.errorBag()
        if (keys.isEmpty()) {
            check(bag.isNotEmpty()) { "Failed asserting that the component has errors" }
        } else {
            for (key in keys) check(bag.has(key)) { "Failed asserting that [$key] has errors" }
        }
        return this
    }

    fun assertHasNoErrors(vararg keys: String): Testable {
        val bag = result.component.errorBag()
        if (keys.isEmpty()) {
            check(bag.isEmpty()) { "Failed asserting that the component has no errors; got ${bag.all()}" }
        } else {
            for (key in keys) check(!bag.has(key)) { "Failed asserting that [$key] has no errors" }
        }
        return this
    }

    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun assertDispatched(name: String, params: Map<String, Any?>? = null): Testable {
        val dispatches = dispatchRows(result.effects["dispatches"])
        val match = dispatches.firstOrNull { it["name"] == name }
            ?: throw AssertionError("Failed asserting that [$name] was dispatched; got $dispatches")
        if (params != null) {
            val rawParams = match["params"]
            val actual: Map<Any?, Any?> = if (rawParams is Map<*, *>) {
                rawParams.entries.associate { (k, v) -> k to v }
            } else {
                emptyMap()
            }
            for ((k, v) in params) {
                check(looseEquals(actual[k], v)) { "Failed asserting dispatch [$name] param [$k]=[$v]; got $actual" }
            }
        }
        return this
    }

    fun assertNotDispatched(name: String): Testable {
        val dispatches = dispatchRows(result.effects["dispatches"])
        check(dispatches.none { it["name"] == name }) { "Failed asserting that [$name] was not dispatched" }
        return this
    }

    /** Assert a download effect was queued (`assertFileDownloaded`). */
    @JvmOverloads
    fun assertFileDownloaded(name: String? = null, content: String? = null): Testable {
        @Suppress("UNCHECKED_CAST")
        val download = result.effects["download"] as? Map<*, *>
            ?: throw AssertionError("Failed asserting that a file was downloaded; no download effect")
        if (name != null) {
            check(download["name"] == name) { "Failed asserting download name [$name]; got [${download["name"]}]" }
        }
        if (content != null) {
            val encoded = download["content"].toString()
            val decoded = String(java.util.Base64.getDecoder().decode(encoded), Charsets.UTF_8)
            check(decoded == content) { "Failed asserting download content; got [$decoded]" }
        }
        return this
    }

    private fun dispatchRows(raw: Any?): List<Map<*, *>> {
        if (raw !is List<*>) return emptyList()
        return raw.mapNotNull { row -> if (row is Map<*, *>) row else null }
    }

    private fun values(value: Any): List<String> = when (value) {
        is Collection<*> -> value.map { it.toString() }
        is Array<*> -> value.map { it.toString() }
        else -> listOf(value.toString())
    }

    private fun check(condition: Boolean, message: () -> String) {
        if (!condition) throw AssertionError(message())
    }

    private fun looseEquals(a: Any?, b: Any?): Boolean {
        if (a == b) return true
        if (a is Enum<*> || b is Enum<*>) return a.toString() == b.toString()
        val x = numeric(a)
        val y = numeric(b)
        return x != null && y != null && x.compareTo(y) == 0
    }

    private fun numeric(v: Any?): BigDecimal? = when (v) {
        is Number -> v.toString().toBigDecimalOrNull()
        is String -> v.trim().toBigDecimalOrNull()
        else -> null
    }
}

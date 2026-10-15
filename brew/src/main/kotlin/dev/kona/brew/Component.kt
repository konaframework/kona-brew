package dev.kona.brew

import dev.kona.brew.hydration.Coercion
import dev.kona.brew.lifecycle.HookSet
import dev.kona.brew.meta.ComponentMeta
import dev.kona.http.ValidationException
import dev.kona.http.Validator

/**
 * A Brew component (`Livewire\Component`).
 *
 * Public state is every public Kotlin `var`/`val` with a backing field (or Java public field) declared on your subclass.
 * Public methods are actions callable from the browser; lifecycle hooks (`mount`, `boot`, `updatedFoo`, …) are found
 * by name. Render with [render] (return a [BrewView], raw HTML, or `null`), or override [template] with inline Crema.
 */
abstract class Component {
    private var state: ComponentState? = null

    @JvmSynthetic
    internal fun attach(state: ComponentState) {
        this.state = state
    }

    @JvmSynthetic
    internal fun state(): ComponentState = state ?: throw BrewException(
        "Component [${javaClass.name}] is not attached to Brew; create it through Brew.mount / Brew.test",
    )

    /** Unique component id (`$this->getId()`). */
    fun id(): String = state().id

    /** Registered component name (`$this->getName()`). */
    fun name(): String = state().name

    /** Rendering hook: return a [BrewView] / HTML string, or `null` to use [template] or the convention view. */
    open fun render(): Any? = null

    /** Inline Crema source for single-file components. Kotlin raw strings: write `{{ count }}` (the `$` is optional). */
    open fun template(): String? = null

    /** Crema view `name` with optional [data] (`view('livewire.x', [...])`). */
    @JvmOverloads
    protected fun view(name: String, data: Map<String, Any?> = emptyMap()): BrewView = BrewView.named(name, data)

    /** Inline Crema [source] with optional [data]. */
    @JvmOverloads
    protected fun inline(source: String, data: Map<String, Any?> = emptyMap()): BrewView = BrewView.inline(source, data)

    /** Skip rendering for this request (`$this->skipRender()`); optional replacement [html]. Ignored after [forceRender]. */
    @JvmOverloads
    fun skipRender(html: String? = null) {
        val state = state()
        if (state.forceRender) return
        state.skipRender = true
        state.skipRenderHtml = html
    }

    /** Alias of [skipRender] (`$this->renderless()`). */
    fun renderless() = skipRender()

    /** Make later [skipRender] calls (including renderless actions) no-ops for this request (`$this->forceRender()`). */
    fun forceRender() {
        state().forceRender = true
    }

    /** Assign several properties (`fill`). Unknown keys are ignored. */
    fun fill(values: Map<String, Any?>) {
        val meta = ComponentMeta.of(this)
        for ((key, value) in values) {
            val prop = meta.properties[key] ?: continue
            prop.set(this, Coercion.toType(value, prop.type, prop.genericType))
        }
    }

    /** Reset [properties] (all when empty) to their initial values (`reset`). */
    fun reset(vararg properties: String) {
        val fresh = freshInstance()
        val meta = ComponentMeta.of(this)
        val names = if (properties.isEmpty()) meta.properties.keys else properties.toList()
        for (name in names) {
            val prop = meta.properties[name] ?: throw PublicPropertyNotFoundException(name, nameOrClass())
            prop.set(this, prop.get(fresh))
        }
    }

    /** Reset every property except [properties] (`resetExcept`). */
    fun resetExcept(vararg properties: String) {
        val keep = properties.toSet()
        val names = ComponentMeta.of(this).properties.keys.filter { it !in keep }
        if (names.isNotEmpty()) reset(*names.toTypedArray())
    }

    /** Return a property's value and reset it (`pull('title')`). */
    fun pull(property: String): Any? {
        val value = all()[property]
        reset(property)
        return value
    }

    /** Return several properties and reset them (`pull(['a', 'b'])`; all when empty). */
    fun pull(vararg properties: String): Map<String, Any?> {
        val values = if (properties.isEmpty()) all() else only(*properties)
        reset(*properties)
        return values
    }

    /** Public state for [properties] (`only`). */
    fun only(vararg properties: String): Map<String, Any?> {
        val all = all()
        return properties.filter { all.containsKey(it) }.associateWithTo(LinkedHashMap()) { all[it] }
    }

    /** All public state (`all()`). */
    fun all(): Map<String, Any?> {
        val out = LinkedHashMap<String, Any?>()
        for ((name, prop) in ComponentMeta.of(this).properties) out[name] = prop.get(this)
        return out
    }

    /** Memoized value of the `@Computed` method [name] for this request. */
    @Suppress("UNCHECKED_CAST")
    fun <T> computed(name: String): T = state().manager.computedValue(this, name) as T

    /** Forget a memoized computed value (`unset($this->posts)`). */
    fun forgetComputed(name: String) {
        state().manager.forgetComputed(this, name)
    }

    /** Validation error bag (`getErrorBag()`). */
    fun errorBag(): ErrorBag = state().errorBag

    fun getErrorBag(): ErrorBag = errorBag()

    fun setErrorBag(bag: ErrorBag) {
        state().errorBag = bag
    }

    fun addError(key: String, message: String) {
        errorBag().add(key, message)
    }

    fun resetErrorBag() {
        errorBag().clear()
    }

    fun resetValidation(vararg fields: String) {
        if (fields.isEmpty()) errorBag().clear() else fields.forEach { errorBag().clear(it) }
    }

    /** Validate public properties (`$this->validate()`). */
    fun validate(): Map<String, Any?> = validate(null)

    /**
     * Validate public properties (`$this->validate([...])`).
     * [rules] overrides annotation / `rules()` maps when non-null.
     */
    fun validate(rules: Map<String, List<String>>?): Map<String, Any?> {
        val resolved = rules ?: ValidationRules.forComponent(this)
        val data = all()
        val validator = Validator(data, resolved)
        try {
            val validated = validator.validate()
            errorBag().clear()
            return validated
        } catch (e: ValidationException) {
            errorBag().clear().merge(e.errors)
            throw e
        }
    }

    fun validateOnly(field: String): Map<String, Any?> {
        val rules = ValidationRules.forComponent(this).filterKeys { it == field || it.startsWith("$field.") }
        return validate(rules)
    }

    /** Optional component-level rules map (`rules()`). */
    open fun rules(): Map<String, List<String>> = emptyMap()

    open fun messages(): Map<String, String> = emptyMap()

    open fun validationAttributes(): Map<String, String> = emptyMap()

    private fun nameOrClass(): String {
        val s = state ?: return javaClass.simpleName
        return s.name
    }

    private fun freshInstance(): Any {
        state?.let { return it.manager.freshInstance(this) }
        val ctor = javaClass.getDeclaredConstructor()
        ctor.isAccessible = true
        return ctor.newInstance()
    }
}

/** Runtime bookkeeping kept off the public surface. */
internal class ComponentState(
    val id: String,
    val name: String,
    val manager: BrewManager,
) {
    lateinit var hooks: HookSet
    var skipRender: Boolean = false
    var forceRender: Boolean = false
    var skipRenderHtml: String? = null
    val computedMemo: MutableMap<String, Any?> = HashMap()
    val store: MutableMap<String, Any?> = HashMap()
    var errorBag: ErrorBag = ErrorBag()
    var renderedView: BrewView? = null
    var renderedData: Map<String, Any?>? = null
}

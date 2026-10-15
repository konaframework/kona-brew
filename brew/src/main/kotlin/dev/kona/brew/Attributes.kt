package dev.kona.brew

/**
 * Property cannot be changed by the browser (`#[Locked]`). Kotlin: `@Locked var id = 0`; Java: annotate the public field.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Locked

/**
 * Memoized derived value (`#[Computed]`). Put on a zero-argument public method; templates read it as `$name` or `$this.name`.
 *
 * - [persist]: cache across requests for this component instance (in the Brew cache store).
 * - [cache]: cache across **all** instances of the component class.
 * - [seconds]: TTL for [persist] / [cache] (default 3600).
 * - [key]: explicit cache key for [cache].
 * - [tags]: cache tags (informational for tag-aware stores).
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Computed(
    val persist: Boolean = false,
    val cache: Boolean = false,
    val seconds: Int = 3600,
    val key: String = "",
    val tags: Array<String> = [],
)

/** The action never re-renders the component (`#[Renderless]`). */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Renderless

/**
 * Registers a component class under an explicit name for discovery (`Brew.discover`).
 * Without it the name is derived from the class name (`ShowPosts` → `show-posts`).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class BrewComponent(val value: String)

/**
 * Validation rules for a property (`#[Validate]`). Pipe-joined or array [value] uses Kona [dev.kona.http.Validator] syntax.
 *
 * - [as]: attribute display name
 * - [message]: single custom message (applies to the first rule)
 * - [attribute]: alternate attribute key
 * - [onUpdate]: when false, skip real-time validation on `updated`
 * - [translate]: reserved for locales (M6); currently informational
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Validate(
    vararg val value: String = [],
    val `as`: String = "",
    val message: String = "",
    val attribute: String = "",
    val onUpdate: Boolean = true,
    val translate: Boolean = true,
)

/** Alias of [Validate] (`#[Rule]`). */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Rule(
    vararg val value: String = [],
    val `as`: String = "",
    val message: String = "",
    val attribute: String = "",
    val onUpdate: Boolean = true,
    val translate: Boolean = true,
)

/**
 * CSS transition hints for morph updates (`#[Transition]`).
 * Client `wire:transition` reads [type]; [skip] opts out.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Transition(
    val type: String = "",
    val skip: Boolean = false,
)

/** Listen for a browser / server event (`#[On('post-created')]`). Dynamic `{prop}` placeholders are resolved from state. */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@MustBeDocumented
annotation class On(vararg val value: String)

/** Run the action in parallel without blocking other requests (`#[Async]` / `.async`). */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Async

/** Expose a client-side JS function on `$wire.$js.name` (`#[Js]`). */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Js(val name: String = "")

/**
 * Action returns JSON to the caller and skips HTML render (`#[Json]`).
 * Validation failures reject the promise with the error bag.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Json

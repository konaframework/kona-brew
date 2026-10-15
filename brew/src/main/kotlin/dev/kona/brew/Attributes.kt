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

package dev.kona.brew.console

import dev.kona.brew.ComponentRegistry
import dev.kona.grind.ConsoleCommand
import java.nio.file.Files
import java.nio.file.Path

/**
 * `./grind make:brew posts.create-post` — scaffolds a component (`make:livewire`).
 *
 * Default: single-file component (class with inline `template()`). `--class` also writes
 * `views/brew/<name>.crema.html` and a `render()` that points at it. `--test` adds a component test.
 */
class MakeBrewCommand : ConsoleCommand() {
    override val signature: String =
        "make:brew {name : Component name (posts.create-post, CreatePost, admin::users)} " +
            "{--class : Separate Crema view file} {--test : Generate a test} {--lang= : kotlin or java} " +
            "{--base= : Project root} {--force : Overwrite existing files}"

    override val description: String = "Create a new Brew component"

    override fun handle(): Int {
        val raw = argumentOrFail("name")
        if (!NAME.matches(raw)) {
            error("Invalid component name [$raw]: use names like counter, posts.create-post or CreatePost")
            return FAILURE
        }
        val name = normalize(raw)
        val base = Path.of(option("base", ".")).toAbsolutePath().normalize()
        val lang = option("lang") ?: detectLang(base)
        if (lang != "kotlin" && lang != "java") {
            error("Unknown --lang [$lang]; expected kotlin or java")
            return FAILURE
        }
        val segments = name.substringAfter("::").split('.')
        val namespace = name.substringBefore("::", "").replace('-', '_')
        val packageParts = listOf("app", "brew") + listOfNotNull(namespace.ifEmpty { null }) +
            segments.dropLast(1).map { it.replace('-', '_') }
        val pkg = packageParts.joinToString(".")
        val className = ComponentRegistry.studly(segments.last())
        val separate = optionBool("class")
        val viewName = "brew." + name.replace("::", ".")
        val ext = if (lang == "java") "java" else "kt"
        val root = if (lang == "java") "src/main/java" else "src/main/kotlin"
        val files = linkedMapOf(
            "$root/${pkg.replace('.', '/')}/$className.$ext" to componentSource(lang, pkg, className, separate, viewName),
        )
        if (separate) {
            files["src/main/resources/views/${viewName.replace('.', '/')}.crema.html"] = VIEW
        }
        if (optionBool("test")) {
            val testRoot = if (lang == "java") "src/test/java" else "src/test/kotlin"
            files["$testRoot/${pkg.replace('.', '/')}/${className}Test.$ext"] = testSource(lang, pkg, className, name)
        }
        val force = optionBool("force")
        for ((relative, _) in files) {
            if (!force && Files.exists(base.resolve(relative))) {
                error("File already exists: $relative (use --force to overwrite)")
                return FAILURE
            }
        }
        for ((relative, content) in files) {
            val dest = base.resolve(relative)
            Files.createDirectories(dest.parent)
            Files.writeString(dest, content)
            success("Created $relative")
        }
        info("Render it with <brew:$name /> or @brew('$name')")
        return SUCCESS
    }

    private fun normalize(raw: String): String {
        if (raw.contains("::") || raw.contains('.') || raw.contains('-') || raw.first().isLowerCase()) return raw
        return ComponentRegistry.kebab(raw)
    }

    private fun detectLang(base: Path): String {
        val java = base.resolve("src/main/java")
        if (!Files.isDirectory(java)) return "kotlin"
        return Files.walk(java).use { s -> if (s.anyMatch { it.toString().endsWith(".java") }) "java" else "kotlin" }
    }

    private fun componentSource(lang: String, pkg: String, name: String, separate: Boolean, view: String): String =
        if (lang == "java") {
            if (separate) {
                """
                package $pkg;

                import dev.kona.brew.Component;

                public class $name extends Component {
                    public int count = 0;

                    public void increment() {
                        count++;
                    }

                    @Override
                    public Object render() {
                        return view("$view");
                    }
                }
                """.trimIndent() + "\n"
            } else {
                """
                package $pkg;

                import dev.kona.brew.Component;

                public class $name extends Component {
                    public int count = 0;

                    public void increment() {
                        count++;
                    }

                    @Override
                    public String template() {
                        return ""${'"'}
                            <div>
                                <span>{{ count }}</span>
                                <button wire:click="increment">+</button>
                            </div>
                            ""${'"'};
                    }
                }
                """.trimIndent() + "\n"
            }
        } else if (separate) {
            """
            package $pkg

            import dev.kona.brew.Component

            class $name : Component() {
                var count = 0

                fun increment() {
                    count++
                }

                override fun render() = view("$view")
            }
            """.trimIndent() + "\n"
        } else {
            """
            package $pkg

            import dev.kona.brew.Component

            class $name : Component() {
                var count = 0

                fun increment() {
                    count++
                }

                override fun template() = ""${'"'}
                    <div>
                        <span>{{ count }}</span>
                        <button wire:click="increment">+</button>
                    </div>
                ""${'"'}
            }
            """.trimIndent() + "\n"
        }

    private fun testSource(lang: String, pkg: String, name: String, component: String): String =
        if (lang == "java") {
            """
            package $pkg;

            import dev.kona.brew.Brew;
            import org.junit.jupiter.api.Test;

            class ${name}Test {
                @Test
                void renders() {
                    Brew.test($name.class).assertSet("count", 0).call("increment").assertSet("count", 1);
                }
            }
            """.trimIndent() + "\n"
        } else {
            """
            package $pkg

            import dev.kona.brew.Brew
            import org.junit.jupiter.api.Test

            class ${name}Test {
                @Test
                fun renders() {
                    Brew.test("$component").assertSet("count", 0).call("increment").assertSet("count", 1)
                }
            }
            """.trimIndent() + "\n"
        }
}

private val NAME = Regex("^([a-z][a-z0-9-]*::)?[A-Za-z][A-Za-z0-9-]*(\\.[A-Za-z][A-Za-z0-9-]*)*$")

private val VIEW = """
    <div>
        <span>{{ count }}</span>
        <button wire:click="increment">+</button>
    </div>
""".trimIndent() + "\n"

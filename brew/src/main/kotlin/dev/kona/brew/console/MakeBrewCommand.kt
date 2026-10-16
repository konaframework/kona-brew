package dev.kona.brew.console

import dev.kona.brew.Brew
import dev.kona.brew.ComponentRegistry
import dev.kona.grind.ConsoleCommand
import java.nio.file.Files
import java.nio.file.Path

/**
 * `./grind make:brew posts.create-post` — scaffolds a component (`make:livewire`).
 *
 * Default type comes from `brew.make.type` (`sfc` | `class` | `mfc`). Flags: `--class`, `--mfc`,
 * `--test`, `--emoji`, `--lang`, namespaces (`pages::`, `admin::`).
 */
class MakeBrewCommand : ConsoleCommand() {
    override val signature: String =
        "make:brew {name : Component name (posts.create-post, CreatePost, pages::dashboard)} " +
            "{--class : Separate Crema view file} {--mfc : Multi-file component assets} " +
            "{--test : Generate a test} {--emoji : Prefix the view name with ⚡} " +
            "{--lang= : kotlin or java} {--base= : Project root} {--force : Overwrite existing files}"

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
        val type = resolveType()
        val separate = type == "class" || type == "mfc"
        val emoji = optionBool("emoji")
        val viewLeaf = name.replace("::", ".")
        val viewName = if (emoji) {
            val parts = viewLeaf.split('.')
            "brew." + (parts.dropLast(1) + ("⚡" + parts.last())).joinToString(".")
        } else {
            "brew.$viewLeaf"
        }
        val ext = if (lang == "java") "java" else "kt"
        val root = if (lang == "java") "src/main/java" else "src/main/kotlin"
        val classRel = "$root/${pkg.replace('.', '/')}/$className.$ext"
        val files = linkedMapOf(
            classRel to componentSource(base, lang, pkg, className, separate, viewName),
        )
        if (separate) {
            files["src/main/resources/views/${viewName.replace('.', '/')}.crema.html"] =
                BrewStubsCommand.load(base, "view.crema.html.stub", VIEW)
        }
        if (optionBool("test")) {
            val testRoot = if (lang == "java") "src/test/java" else "src/test/kotlin"
            val stub = if (lang == "java") "test.java.stub" else "test.kt.stub"
            val fallback = testSource(lang, pkg, className, name)
            files["$testRoot/${pkg.replace('.', '/')}/${className}Test.$ext"] =
                BrewStubsCommand.load(base, stub, fallback)
                    .replace("{{package}}", pkg)
                    .replace("{{class}}", className)
                    .replace("{{name}}", name)
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
        if (type == "mfc") {
            val mfcDir = base.resolve(classRel).parent.resolve(className.lowercase())
            Files.createDirectories(mfcDir)
            Files.writeString(mfcDir.resolve("$className.js"), "export default function (\$wire) {}\n")
            Files.writeString(mfcDir.resolve("$className.css"), "/* $className */\n")
            success("Created MFC assets under $mfcDir")
        }
        info("Render it with <brew:$name /> or @brew('$name')")
        return SUCCESS
    }

    private fun resolveType(): String {
        if (optionBool("mfc")) return "mfc"
        if (optionBool("class")) return "class"
        val configured = try {
            Brew.manager().config.makeType.lowercase()
        } catch (_: Exception) {
            "sfc"
        }
        return if (configured in setOf("sfc", "class", "mfc")) configured else "sfc"
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

    private fun componentSource(
        base: Path,
        lang: String,
        pkg: String,
        name: String,
        separate: Boolean,
        view: String,
    ): String {
        val stubName = if (lang == "java") "component.java.stub" else "component.kt.stub"
        val fallback = defaultComponent(lang, pkg, name, separate, view)
        val loaded = BrewStubsCommand.load(base, stubName, fallback)
            .replace("{{package}}", pkg)
            .replace("{{class}}", name)
            .replace("{{view}}", view)
        // Prefer the stub when its shape matches the requested type; otherwise use the generated default.
        val stubOk = if (separate) "render()" in loaded else "template()" in loaded
        return if (stubOk) loaded else fallback
    }

    private fun defaultComponent(lang: String, pkg: String, name: String, separate: Boolean, view: String): String =
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

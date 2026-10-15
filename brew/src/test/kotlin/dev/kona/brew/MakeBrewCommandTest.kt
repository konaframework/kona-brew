package dev.kona.brew

import dev.kona.brew.console.MakeBrewCommand
import dev.kona.grind.CommandExecutor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class MakeBrewCommandTest {
    private fun run(dir: Path, vararg args: String): Int =
        CommandExecutor(dir).run(MakeBrewCommand(), args.toList() + "--base=$dir")

    @Test
    fun `kotlin single-file component`(@TempDir dir: Path) {
        assertEquals(0, run(dir, "counter"))
        val file = dir.resolve("src/main/kotlin/app/brew/Counter.kt")
        val source = Files.readString(file)
        assertTrue(source.contains("package app.brew"))
        assertTrue(source.contains("override fun template() = \"\"\""))
        assertEquals(1, run(dir, "counter"))
        assertEquals(0, run(dir, "counter", "--force"))
    }

    @Test
    fun `kotlin class component with view and test`(@TempDir dir: Path) {
        assertEquals(0, run(dir, "posts.create-post", "--class", "--test"))
        val source = Files.readString(dir.resolve("src/main/kotlin/app/brew/posts/CreatePost.kt"))
        assertTrue(source.contains("override fun render() = view(\"brew.posts.create-post\")"), source)
        assertTrue(Files.exists(dir.resolve("src/main/resources/views/brew/posts/create-post.crema.html")))
        val test = Files.readString(dir.resolve("src/test/kotlin/app/brew/posts/CreatePostTest.kt"))
        assertTrue(test.contains("Brew.test(\"posts.create-post\")"))
    }

    @Test
    fun `java components and namespaces`(@TempDir dir: Path) {
        assertEquals(0, run(dir, "admin-area::UserList", "--lang=java", "--test"))
        val source = Files.readString(dir.resolve("src/main/java/app/brew/admin_area/UserList.java"))
        assertTrue(source.contains("public String template()"))
        assertTrue(source.contains("\"\"\""))
        assertTrue(Files.exists(dir.resolve("src/test/java/app/brew/admin_area/UserListTest.java")))
        assertEquals(0, run(dir, "Dashboard", "--lang=java", "--class"))
        val dash = Files.readString(dir.resolve("src/main/java/app/brew/Dashboard.java"))
        assertTrue(dash.contains("return view(\"brew.dashboard\");"), dash)
    }

    @Test
    fun `language detection and validation`(@TempDir dir: Path) {
        Files.createDirectories(dir.resolve("src/main/java/app"))
        assertEquals(0, run(dir, "empty-java"))
        assertTrue(Files.exists(dir.resolve("src/main/kotlin/app/brew/EmptyJava.kt")))
        Files.writeString(dir.resolve("src/main/java/app/App.java"), "class App {}")
        assertEquals(0, run(dir, "ShowPosts"))
        assertTrue(Files.exists(dir.resolve("src/main/java/app/brew/ShowPosts.java")))
        assertEquals(1, run(dir, "bad name!"))
        assertEquals(1, run(dir, "ok", "--lang=scala"))
        assertFalse(Files.exists(dir.resolve("src/main/scala")))
    }
}

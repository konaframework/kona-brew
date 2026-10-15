package dev.kona.brew.fixtures

import dev.kona.brew.Component
import dev.kona.brew.Form
import dev.kona.brew.Rule
import dev.kona.brew.Validate

class ProfileForm : Form() {
    @JvmField
    @field:Validate("required", "min:2")
    var name: String = ""

    @JvmField
    @field:Validate("max:40")
    var nickname: String = ""

    @JvmField
    @field:Validate("max:5", onUpdate = false)
    var code: String = ""

    // Non-public field forces FormMeta's visibility continue branch.
    @Suppress("unused")
    private var hidden: Int = 0
}

class EmptyRulesForm : Form() {
    @JvmField
    var value: String = ""
}

class ValidatedCounter : Component() {
    @field:Validate("required", "email", onUpdate = true)
    var email: String = ""

    @JvmField
    @field:Validate("max:10", onUpdate = false)
    var note: String = ""

    var profile = ProfileForm()

    override fun rules(): Map<String, List<String>> = mapOf("email" to listOf("required", "email"))

    fun save() {
        validate()
    }

    override fun template() = """
        <div>
            <span>{{ email }}</span>
            @if(errors['email'])
                <em>{{ errors['email'][0] }}</em>
            @endif
        </div>
    """.trimIndent()
}

class RuleOnlyComponent : Component() {
    @JvmField
    @field:Rule("required", "min:2")
    var title: String = ""

    fun save() {
        validate()
    }

    override fun template() = "<div>{{ title }}</div>"
}

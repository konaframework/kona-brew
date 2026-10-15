package dev.kona.brew.fixtures.admin_area

import dev.kona.brew.BrewComponent
import dev.kona.brew.Component

class UserList : Component() {
    var users = listOf("ana", "kai")

    override fun template() = "<ul>@foreach(users as user)<li>{{ user }}</li>@endforeach</ul>"
}

@BrewComponent("admin.custom")
class Named : Component() {
    override fun template() = "<b>named</b>"
}

abstract class BaseWidget : Component()

class NotAComponent

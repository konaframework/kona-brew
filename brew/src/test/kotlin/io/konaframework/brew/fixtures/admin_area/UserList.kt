package io.konaframework.brew.fixtures.admin_area

import io.konaframework.brew.BrewComponent
import io.konaframework.brew.Component

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

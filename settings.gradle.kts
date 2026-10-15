rootProject.name = "kona-brew"

include("kona-brew")
include("brew-sample-kotlin")
include("brew-sample-java")

project(":kona-brew").projectDir = file("brew")
project(":brew-sample-kotlin").projectDir = file("samples/brew-kotlin")
project(":brew-sample-java").projectDir = file("samples/brew-java")

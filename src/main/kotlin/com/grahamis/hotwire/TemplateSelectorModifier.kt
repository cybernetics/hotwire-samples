package com.grahamis.hotwire

interface TemplateSelectorModifier {
    fun modifyName(name: String): String = name
}

class DefaultTemplateSelectorModifier {
    companion object : TemplateSelectorModifier {
        override fun modifyName(name: String) = name
    }
}

class TurboStreamTemplateSelectorModifier {
    companion object : TemplateSelectorModifier {
        override fun modifyName(name: String) = "$name.turbo-stream"
    }
}

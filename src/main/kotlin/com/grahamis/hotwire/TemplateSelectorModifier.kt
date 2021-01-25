package com.grahamis.hotwire

interface TemplateSelectorModifier {
    fun modifyName(name: String): String = name

    object Default : TemplateSelectorModifier {
        override fun modifyName(name: String) = name
    }

    object TurboStream : TemplateSelectorModifier {
        override fun modifyName(name: String) = "$name.turbo-stream"
    }
}

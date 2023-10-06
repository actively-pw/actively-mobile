package com.actively.field

fun interface Validator {

    operator fun invoke(value: String): Boolean

    companion object {
        private val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        val None = Validator { true }
        val NonEmptyString = Validator { it.isNotEmpty() }
        val Email = Validator { NonEmptyString(it) && emailRegex.matches(it) }
        fun lengthInRange(range: IntRange) = Validator { it.length in range }
    }
}








package util

import androidx.compose.runtime.MutableState

fun bindState(get: () -> String, set: (String) -> Unit): MutableState<String> = object : MutableState<String> {
    override var value: String
        get() = get()
        set(value) = set(value)

    override fun component1() = value
    override fun component2() = { newValue: String -> value = newValue }
}

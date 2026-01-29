package com.watabou.noosa.ui
open class CheckBox : Button() {
    protected var checked: Boolean = false
    fun checked(): Boolean {
        return checked
    }
    fun checked(value: Boolean) {
        if (checked != value) {
            checked = value
            updateState()
        }
    }
    protected open fun updateState() {
    }
    override fun onClick() {
        checked(!checked)
        onChange()
    }
    protected open fun onChange() {
    }
}

package com.watabou.pixeldungeon.windows
import com.watabou.pixeldungeon.ui.Icons
open class WndError(message: String) : WndTitledMessage(Icons.WARNING.get(), TXT_TITLE, message) {
    companion object {
        private const val TXT_TITLE = "ERROR"
    }
}

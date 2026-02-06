package com.watabou.pixeldungeon.utils
import android.util.Log
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.utils.Signal
object GLog {
    const val TAG = "GAME"
    const val POSITIVE = "++ "
    const val NEGATIVE = "-- "
    const val WARNING = "** "
    const val HIGHLIGHT = "@@ "
    var update = Signal<String>()
    fun i(text: String, vararg args: Any) {
        var msg = text
        if (args.isNotEmpty()) {
            msg = Utils.format(text, *args)
        }
        LlmTextEnhancer.enhanceCombatMessage(msg)?.let { msg = it }
        Log.i(TAG, msg)
        update.dispatch(msg)
    }
    fun p(text: String, vararg args: Any) {
        i(POSITIVE + text, *args)
    }
    fun n(text: String, vararg args: Any) {
        i(NEGATIVE + text, *args)
    }
    fun w(text: String, vararg args: Any) {
        i(WARNING + text, *args)
    }
    fun h(text: String, vararg args: Any) {
        i(HIGHLIGHT + text, *args)
    }
}

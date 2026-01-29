package com.watabou.glscripts
import com.watabou.glwrap.Program
import com.watabou.glwrap.Shader
import java.util.HashMap
open class Script : Program() {
    fun compile(src: String) {
        val srcShaders = src.split("//\n".toRegex()).toTypedArray()
        attach(Shader.createCompiled(Shader.VERTEX, srcShaders[0]))
        attach(Shader.createCompiled(Shader.FRAGMENT, srcShaders[1]))
        link()
    }
    open fun unuse() {}
    companion object {
        private val all = HashMap<Class<out Script>, Script>()
        private var curScript: Script? = null
        private var curScriptClass: Class<out Script>? = null
        fun <T : Script> use(c: Class<T>): T {
            if (c != curScriptClass) {
                var script = all[c]
                if (script == null) {
                    try {
                        script = c.getDeclaredConstructor().newInstance()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    all[c] = script!!
                }
                if (curScript != null) {
                    curScript!!.unuse()
                }
                curScript = script
                curScriptClass = c
                curScript!!.use()
            }
            @Suppress("UNCHECKED_CAST")
            return curScript as T
        }
        fun reset() {
            for (script in all.values) {
                script.delete()
            }
            all.clear()
            curScript = null
            curScriptClass = null
        }
    }
}

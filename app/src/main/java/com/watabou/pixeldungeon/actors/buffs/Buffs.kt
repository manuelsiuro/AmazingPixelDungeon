package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.actors.Char
object Buffs {
    fun <T : Buff> append(target: Char, buffClass: Class<T>): T? {
        return try {
            val buff = buffClass.getDeclaredConstructor().newInstance()
            if (buff.attachTo(target)) {
                buff
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    fun <T : FlavourBuff> append(target: Char, buffClass: Class<T>, duration: Float): T? {
        val buff = append(target, buffClass)
        buff?.spend(duration)
        return buff
    }
    fun <T : Buff> affect(target: Char, buffClass: Class<T>): T? {
        return target.buff(buffClass) ?: append(target, buffClass)
    }
    fun <T : FlavourBuff> affect(target: Char, buffClass: Class<T>, duration: Float): T? {
        val buff = affect(target, buffClass)
        buff?.spend(duration)
        return buff
    }
    fun <T : FlavourBuff> prolong(target: Char, buffClass: Class<T>, duration: Float): T? {
        val buff = affect(target, buffClass)
        buff?.postpone(duration)
        return buff
    }
    fun detach(buff: Buff?) {
        buff?.detach()
    }
    fun detach(target: Char, cl: Class<out Buff>) {
        detach(target.buff(cl))
    }
}

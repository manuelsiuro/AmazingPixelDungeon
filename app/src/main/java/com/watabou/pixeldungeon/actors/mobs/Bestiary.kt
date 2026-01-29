package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.Yog.BurningFist
import com.watabou.pixeldungeon.actors.mobs.Yog.RottingFist
import com.watabou.utils.Random
object Bestiary {
    @Suppress("UNCHECKED_CAST")
    fun mob(depth: Int): Mob? {
        val cl = mobClass(depth) as Class<out Mob>
        return try {
            cl.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            null
        }
    }
    @Suppress("UNCHECKED_CAST")
    fun mutable(depth: Int): Mob? {
        var cl = mobClass(depth) as Class<out Mob>
        if (Random.Int(30) == 0) {
            cl = when (cl) {
                Rat::class.java -> Albino::class.java
                Thief::class.java -> Bandit::class.java
                Brute::class.java -> Shielded::class.java
                Monk::class.java -> Senior::class.java
                Scorpio::class.java -> Acidic::class.java
                else -> cl
            }
        }
        return try {
            cl.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            null
        }
    }
    private fun mobClass(depth: Int): Class<*> {
        val chances: FloatArray
        val classes: Array<Class<*>>
        when (depth) {
            1 -> {
                chances = floatArrayOf(1f)
                classes = arrayOf(Rat::class.java)
            }
            2 -> {
                chances = floatArrayOf(1f, 1f)
                classes = arrayOf(Rat::class.java, Gnoll::class.java)
            }
            3 -> {
                chances = floatArrayOf(1f, 2f, 1f, 0.02f)
                classes = arrayOf(Rat::class.java, Gnoll::class.java, Crab::class.java, Swarm::class.java)
            }
            4 -> {
                chances = floatArrayOf(1f, 2f, 3f, 0.02f, 0.01f, 0.01f)
                classes = arrayOf(Rat::class.java, Gnoll::class.java, Crab::class.java, Swarm::class.java, Skeleton::class.java, Thief::class.java)
            }
            5 -> {
                chances = floatArrayOf(1f)
                classes = arrayOf(Goo::class.java)
            }
            6 -> {
                chances = floatArrayOf(4f, 2f, 1f, 0.2f)
                classes = arrayOf(Skeleton::class.java, Thief::class.java, Swarm::class.java, Shaman::class.java)
            }
            7 -> {
                chances = floatArrayOf(3f, 1f, 1f, 1f)
                classes = arrayOf(Skeleton::class.java, Shaman::class.java, Thief::class.java, Swarm::class.java)
            }
            8 -> {
                chances = floatArrayOf(3f, 2f, 1f, 1f, 1f, 0.02f)
                classes = arrayOf(Skeleton::class.java, Shaman::class.java, Gnoll::class.java, Thief::class.java, Swarm::class.java, Bat::class.java)
            }
            9 -> {
                chances = floatArrayOf(3f, 3f, 1f, 1f, 0.02f, 0.01f)
                classes = arrayOf(Skeleton::class.java, Shaman::class.java, Thief::class.java, Swarm::class.java, Bat::class.java, Brute::class.java)
            }
            10 -> {
                chances = floatArrayOf(1f)
                classes = arrayOf(Tengu::class.java)
            }
            11 -> {
                chances = floatArrayOf(1f, 0.2f)
                classes = arrayOf(Bat::class.java, Brute::class.java)
            }
            12 -> {
                chances = floatArrayOf(1f, 1f, 0.2f)
                classes = arrayOf(Bat::class.java, Brute::class.java, Spinner::class.java)
            }
            13 -> {
                chances = floatArrayOf(1f, 3f, 1f, 1f, 0.02f)
                classes = arrayOf(Bat::class.java, Brute::class.java, Shaman::class.java, Spinner::class.java, Elemental::class.java)
            }
            14 -> {
                chances = floatArrayOf(1f, 3f, 1f, 4f, 0.02f, 0.01f)
                classes = arrayOf(Bat::class.java, Brute::class.java, Shaman::class.java, Spinner::class.java, Elemental::class.java, Monk::class.java)
            }
            15 -> {
                chances = floatArrayOf(1f)
                classes = arrayOf(DM300::class.java)
            }
            16 -> {
                chances = floatArrayOf(1f, 1f, 0.2f)
                classes = arrayOf(Elemental::class.java, Warlock::class.java, Monk::class.java)
            }
            17 -> {
                chances = floatArrayOf(1f, 1f, 1f)
                classes = arrayOf(Elemental::class.java, Monk::class.java, Warlock::class.java)
            }
            18 -> {
                chances = floatArrayOf(1f, 2f, 1f, 1f)
                classes = arrayOf(Elemental::class.java, Monk::class.java, Golem::class.java, Warlock::class.java)
            }
            19 -> {
                chances = floatArrayOf(1f, 2f, 3f, 1f, 0.02f)
                classes = arrayOf(Elemental::class.java, Monk::class.java, Golem::class.java, Warlock::class.java, Succubus::class.java)
            }
            20 -> {
                chances = floatArrayOf(1f)
                classes = arrayOf(King::class.java)
            }
            22 -> {
                chances = floatArrayOf(1f, 1f)
                classes = arrayOf(Succubus::class.java, Eye::class.java)
            }
            23 -> {
                chances = floatArrayOf(1f, 2f, 1f)
                classes = arrayOf(Succubus::class.java, Eye::class.java, Scorpio::class.java)
            }
            24 -> {
                chances = floatArrayOf(1f, 2f, 3f)
                classes = arrayOf(Succubus::class.java, Eye::class.java, Scorpio::class.java)
            }
            25 -> {
                chances = floatArrayOf(1f)
                classes = arrayOf(Yog::class.java)
            }
            else -> {
                chances = floatArrayOf(1f)
                classes = arrayOf(Eye::class.java)
            }
        }
        return classes[Random.chances(chances)]
    }
    fun isBoss(mob: Char): Boolean {
        return mob is Goo ||
                mob is Tengu ||
                mob is DM300 ||
                mob is King ||
                mob is Yog || mob is BurningFist || mob is RottingFist
    }
}

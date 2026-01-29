package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ParalyticGas
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.items.quest.RatSkull
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.FetidRatSprite
import com.watabou.utils.Random
import java.util.*
class FetidRat : Mob() {
    init {
        name = "fetid rat"
        spriteClass = FetidRatSprite::class.java
        HT = 15
        HP = HT
        defenseSkill = 5
        EXP = 3
        maxLvl = 5
        state = WANDERING
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(2, 6)
    }
    override fun attackSkill(target: Char?): Int {
        return 12
    }
    override fun dr(): Int {
        return 2
    }
    override fun defenseVerb(): String {
        return "evaded"
    }
    override fun defenseProc(enemy: Char, damage: Int): Int {
        GameScene.add(Blob.seed(pos, 20, ParalyticGas::class.java) as Blob)
        return super.defenseProc(enemy, damage)
    }
    override fun die(src: Any?) {
        super.die(src)
        Dungeon.level?.drop(RatSkull(), pos)?.sprite?.drop()
    }
    override fun description(): String {
        return "This marsupial rat is much larger than a regular one. It is surrounded by a foul cloud."
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private val IMMUNITIES = hashSetOf<Class<*>>(Paralysis::class.java)
    }
}

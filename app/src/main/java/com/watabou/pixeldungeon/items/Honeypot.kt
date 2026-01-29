package com.watabou.pixeldungeon.items
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.npcs.Bee
import com.watabou.pixeldungeon.effects.Pushing
import com.watabou.pixeldungeon.effects.Splash
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random
import java.util.ArrayList
class Honeypot : Item() {
    init {
        name = "honeypot"
        image = ItemSpriteSheet.HONEYPOT
        defaultAction = AC_THROW
        stackable = true
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_SHATTER)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_SHATTER) {
            hero.sprite?.zap(hero.pos)
            shatter(hero.pos)
            detach(hero.belongings.backpack)
            hero.spendAndNext(TIME_TO_THROW)
        } else {
            super.execute(hero, action)
        }
    }
    override fun onThrow(cell: Int) {
        if (Level.pit[cell]) {
            super.onThrow(cell)
        } else {
            shatter(cell)
        }
    }
    private fun shatter(pos: Int) {
        Sample.play(Assets.SND_SHATTER)
        if (Dungeon.visible[pos]) {
            Splash.at(pos, 0xffd500, 5)
        }
        var newPos = pos
        if (Actor.findChar(pos) != null) {
            val candidates = ArrayList<Int>()
            val passable = Level.passable
            for (n in Level.NEIGHBOURS4) {
                val c = pos + n
                if (passable[c] && Actor.findChar(c) == null) {
                    candidates.add(c)
                }
            }
            newPos = if (candidates.size > 0) Random.element(candidates) ?: -1 else -1
        }
        if (newPos != -1) {
            val bee = Bee()
            bee.spawn(Dungeon.depth)
            bee.HP = bee.HT
            bee.pos = newPos
            GameScene.add(bee)
            Actor.addDelayed(Pushing(bee, pos, newPos), -1f)
            bee.sprite?.let { sprite ->
                sprite.alpha(0f)
                sprite.parent?.add(AlphaTweener(sprite, 1f, 0.15f))
            }
            Sample.play(Assets.SND_BEE)
        }
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun price(): Int {
        return 50 * quantity
    }
    override fun info(): String {
        return "There is not much honey in this small honeypot, but there is a golden bee there and it doesn't want to leave it."
    }
    companion object {
        const val AC_SHATTER = "SHATTER"
    }
}

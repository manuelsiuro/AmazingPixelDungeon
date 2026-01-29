package com.watabou.pixeldungeon.items
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.wands.WandOfBlink
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.InterlevelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import java.util.ArrayList
class LloydsBeacon : Item() {
    private var returnDepth = -1
    private var returnPos = 0
    init {
        name = "lloyd's beacon"
        image = ItemSpriteSheet.BEACON
        unique = true
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DEPTH, returnDepth)
        if (returnDepth != -1) {
            bundle.put(POS, returnPos)
        }
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        returnDepth = bundle.getInt(DEPTH)
        returnPos = bundle.getInt(POS)
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_SET)
        if (returnDepth != -1) {
            actions.add(AC_RETURN)
        }
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_SET || action == AC_RETURN) {
            if (Dungeon.bossLevel()) {
                hero.spend(TIME_TO_USE)
                GLog.w(TXT_PREVENTING)
                return
            }
            for (i in Level.NEIGHBOURS8.indices) {
                if (Actor.findChar(hero.pos + Level.NEIGHBOURS8[i]) != null) {
                    GLog.w(TXT_CREATURES)
                    return
                }
            }
        }
        if (action == AC_SET) {
            returnDepth = Dungeon.depth
            returnPos = hero.pos
            hero.spend(TIME_TO_USE)
            hero.busy()
            hero.sprite?.operate(hero.pos)
            Sample.play(Assets.SND_BEACON)
            GLog.i(TXT_RETURN)
        } else if (action == AC_RETURN) {
            if (returnDepth == Dungeon.depth) {
                reset()
                WandOfBlink.appear(hero, returnPos)
                Dungeon.level?.press(returnPos, hero)
                Dungeon.observe()
            } else {
                InterlevelScene.mode = InterlevelScene.Mode.RETURN
                InterlevelScene.returnDepth = returnDepth
                InterlevelScene.returnPos = returnPos
                reset()
                Game.switchScene(InterlevelScene::class.java)
            }
        } else {
            super.execute(hero, action)
        }
    }
    fun reset() {
        returnDepth = -1
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun glowing(): ItemSprite.Glowing? {
        return if (returnDepth != -1) WHITE else null
    }
    override fun info(): String {
        return TXT_INFO + if (returnDepth == -1) "" else Utils.format(TXT_SET, returnDepth)
    }
    companion object {
        private const val TXT_PREVENTING = "Strong magic aura of this place prevents you from using the lloyd's beacon!"
        private const val TXT_CREATURES = "Psychic aura of neighbouring creatures doesn't allow you to use the lloyd's beacon at this moment."
        private const val TXT_RETURN = "The lloyd's beacon is successfully set at your current location, now you can return here anytime."
        private const val TXT_INFO = "Lloyd's beacon is an intricate magic device, that allows you to return to a place you have already been."
        private const val TXT_SET = "\n\nThis beacon was set somewhere on the level %d of Pixel Dungeon."
        const val TIME_TO_USE = 1f
        const val AC_SET = "SET"
        const val AC_RETURN = "RETURN"
        private const val DEPTH = "depth"
        private const val POS = "pos"
        private val WHITE = ItemSprite.Glowing(0xFFFFFF)
    }
}

package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Blindness
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.Bestiary
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.HeroSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
import java.util.ArrayList
class ScrollOfWipeOut : Item() {
    init {
        name = "Scroll of Wipe Out"
        image = ItemSpriteSheet.SCROLL_WIPE_OUT
        stackable = true
        defaultAction = AC_READ
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_READ)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_READ) {
            if (hero.buff(Blindness::class.java) != null) {
                GLog.w(TXT_BLINDED)
            } else {
                curUser = hero
                curItem = detach(hero.belongings.backpack)
                doRead()
            }
        } else {
            super.execute(hero, action)
        }
    }
    private fun doRead() {
        GameScene.flash(0xFF6644.toInt())
        Invisibility.dispel()
        val level = Dungeon.level ?: return
        for (mob in level.mobs.toList()) {
            if (!Bestiary.isBoss(mob)) {
                Sample.play(Assets.SND_CURSED, 0.3f, 0.3f, Random.Float(0.6f, 0.9f))
                mob.die(this)
            }
        }
        for (heap in level.heaps.values().toList()) {
            when (heap.type) {
                Heap.Type.FOR_SALE -> {
                    heap.type = Heap.Type.HEAP
                    if (Dungeon.visible[heap.pos]) {
                        CellEmitter.center(heap.pos).burst(Speck.factory(Speck.COIN), 2)
                    }
                }
                Heap.Type.MIMIC -> {
                    heap.type = Heap.Type.HEAP
                    heap.sprite?.link()
                    Sample.play(Assets.SND_CURSED, 0.3f, 0.3f, Random.Float(0.6f, 0.9f))
                }
                else -> {
                }
            }
        }
        curUser?.spend(TIME_TO_READ)
        curUser?.busy()
        (curUser?.sprite as? HeroSprite)?.read()
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun desc(): String {
        return "Read this scroll to unleash the wrath of the dungeon spirits, killing everything on the current level. " +
                "Well, almost everything. Some of the more powerful creatures may be not affected."
    }
    override fun price(): Int {
        return 100 * quantity
    }
    companion object {
        private const val TXT_BLINDED = "You can't read a scroll while blinded"
        const val AC_READ = "READ"
        const val TIME_TO_READ = 1f
    }
}

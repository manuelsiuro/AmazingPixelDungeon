package com.watabou.pixeldungeon.items.wands
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.pixeldungeon.effects.Swap
import com.watabou.pixeldungeon.items.Dewdrop
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.potions.Potion
import com.watabou.pixeldungeon.items.potions.PotionOfMight
import com.watabou.pixeldungeon.items.potions.PotionOfStrength
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.items.scrolls.ScrollOfEnchantment
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback
import kotlin.math.min
class WandOfReach : Wand() {
    init {
        name = "Wand of Reach"
        hitChars = false
    }
    override fun onZap(cell: Int) {
        val level = Dungeon.level ?: return
        val user = Item.curUser ?: return
        val reach = min(Ballistica.distance, power() + 4)
        var mapUpdated = false
        for (i in 1 until reach) {
            val c = Ballistica.trace[i]
            val before = level.map[c]
            val ch = Actor.findChar(c)
            if (ch != null) {
                Actor.addDelayed(Swap(user, ch), -1f)
                break
            }
            val heap = level.heaps[c]
            if (heap != null) {
                when (heap.type) {
                    Heap.Type.HEAP -> transport(heap)
                    Heap.Type.CHEST, Heap.Type.MIMIC, Heap.Type.TOMB, Heap.Type.SKELETON -> heap.open(user)
                    else -> {}
                }
                break
            }
            level.press(c, null)
            if (before == Terrain.OPEN_DOOR) {
                Level.set(c, Terrain.DOOR)
                GameScene.updateMap(c)
            } else if (Level.water[c]) {
                GameScene.ripple(c)
            }
            mapUpdated = mapUpdated || level.map[c] != before
        }
        if (mapUpdated) {
            Dungeon.observe()
        }
    }
    private fun transport(heap: Heap) {
        val item = heap.pickUp()
        val user = Item.curUser ?: return
        if (item.doPickUp(user)) {
            if (item is Dewdrop) {
                // Do nothing
            } else {
                if (((item is ScrollOfUpgrade || item is ScrollOfEnchantment) && (item as Scroll).isKnown) ||
                    ((item is PotionOfStrength || item is PotionOfMight) && (item as Potion).isKnown)
                ) {
                    GLog.p(TXT_YOU_NOW_HAVE, item.name())
                } else {
                    GLog.i(TXT_YOU_NOW_HAVE, item.name())
                }
            }
        } else {
            Dungeon.level?.drop(item, user.pos)?.sprite?.drop()
        }
    }
    override fun fx(cell: Int, callback: Callback) {
        val user = Item.curUser ?: return
        val parent = user.sprite?.parent ?: return
        MagicMissile.force(parent, user.pos, cell, callback)
        Sample.play(Assets.SND_ZAP)
    }
    override fun desc(): String {
        return "This utility wand can be used to grab objects from a distance and to switch places with enemies. " +
                "Waves of magic force radiated from it will affect all cells on their way triggering traps, " +
                "trampling high vegetation, opening closed doors and closing open ones."
    }
    companion object {
        private const val TXT_YOU_NOW_HAVE = "You have magically transported %s into your backpack"
    }
}

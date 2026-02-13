package com.watabou.pixeldungeon
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.rings.Ring
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.items.weapon.enchantments.Soulbound
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.io.IOException
object Bones {
    private const val BONES_FILE = "bones.dat"
    private const val LEVEL = "level"
    private const val ITEM = "item"
    private var depth = -1
    private var item: Item? = null
    fun leave() {
        item = null
        // Soulbound weapon takes priority over random equipment
        val weapon = Dungeon.hero!!.belongings.weapon
        if (weapon is Weapon && weapon.enchantment is Soulbound) {
            item = weapon
        }
        if (item == null) {
            when (Random.Int(4)) {
                0 -> item = Dungeon.hero!!.belongings.weapon
                1 -> item = Dungeon.hero!!.belongings.armor
                2 -> item = Dungeon.hero!!.belongings.ring1
                3 -> item = Dungeon.hero!!.belongings.ring2
            }
        }
        if (item == null) {
            if (Dungeon.gold > 0) {
                item = Gold(Random.IntRange(1, Dungeon.gold))
            } else {
                item = Gold(1)
            }
        }
        depth = Dungeon.depth
        val bundle = Bundle()
        bundle.put(LEVEL, depth)
        bundle.put(ITEM, item)
        try {
            val output = Game.instance!!.openFileOutput(BONES_FILE, android.content.Context.MODE_PRIVATE)
            Bundle.write(bundle, output)
            output.close()
        } catch (e: IOException) {
        }
    }
    fun get(): Item? {
        if (depth == -1) {
            try {
                val input = Game.instance!!.openFileInput(BONES_FILE)
                val bundle = Bundle.read(input)
                input.close()
                depth = bundle!!.getInt(LEVEL)
                item = bundle.get(ITEM) as Item?
                return get()
            } catch (e: IOException) {
                return null
            }
        } else {
            if (depth == Dungeon.depth) {
                Game.instance!!.deleteFile(BONES_FILE)
                depth = 0
                val i = item!!
                if (!i.stackable) {
                    i.cursed = true
                    i.cursedKnown = true
                    if (i.isUpgradable) {
                        val lvl = (Dungeon.depth - 1) * 3 / 5 + 1
                        if (lvl < i.level()) {
                            i.degrade(i.level() - lvl)
                        }
                        i.levelKnown = false
                    }
                }
                if (i is Ring) {
                    i.syncGem()
                }
                return i
            } else {
                return null
            }
        }
    }
}

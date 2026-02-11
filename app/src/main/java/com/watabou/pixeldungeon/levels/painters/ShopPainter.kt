package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.mobs.npcs.ImpShopkeeper
import com.watabou.pixeldungeon.actors.mobs.npcs.Shopkeeper
import com.watabou.pixeldungeon.items.Ankh
import com.watabou.pixeldungeon.items.Bomb
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.HolyWater
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.SmokeBomb
import com.watabou.pixeldungeon.items.ThrowingNet
import com.watabou.pixeldungeon.items.Torch
import com.watabou.pixeldungeon.items.Weightstone
import com.watabou.pixeldungeon.items.armor.LeatherArmor
import com.watabou.pixeldungeon.items.armor.MailArmor
import com.watabou.pixeldungeon.items.armor.PlateArmor
import com.watabou.pixeldungeon.items.armor.ScaleArmor
import com.watabou.pixeldungeon.items.bags.ScrollHolder
import com.watabou.pixeldungeon.items.bags.SeedPouch
import com.watabou.pixeldungeon.items.bags.WandHolster
import com.watabou.pixeldungeon.items.food.CheeseWedge
import com.watabou.pixeldungeon.items.food.DragonPepper
import com.watabou.pixeldungeon.items.food.FrostBerry
import com.watabou.pixeldungeon.items.food.OverpricedRation
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.watabou.pixeldungeon.items.scrolls.ScrollOfRemoveCurse
import com.watabou.pixeldungeon.items.weapon.melee.BattleAxe
import com.watabou.pixeldungeon.items.weapon.melee.Glaive
import com.watabou.pixeldungeon.items.weapon.melee.Longsword
import com.watabou.pixeldungeon.items.weapon.melee.Mace
import com.watabou.pixeldungeon.items.weapon.melee.Quarterstaff
import com.watabou.pixeldungeon.items.weapon.melee.Spear
import com.watabou.pixeldungeon.items.weapon.melee.Sword
import com.watabou.pixeldungeon.items.weapon.melee.WarHammer
import com.watabou.pixeldungeon.levels.LastShopLevel
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Point
import com.watabou.utils.Random
import java.util.ArrayList
object ShopPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY_SP)
        val pasWidth = room.width() - 2
        val pasHeight = room.height() - 2
        val per = pasWidth * 2 + pasHeight * 2
        val range = range()
        val entrance = room.entrance() ?: return
        var pos = xy2p(room, entrance, pasWidth, pasHeight) + (per - range.size) / 2
        for (i in range.indices) {
            val xy = p2xy(room, (pos + per) % per, pasWidth, pasHeight)
            var cell = xy.x + xy.y * Level.WIDTH
            if (level.heaps[cell] != null) {
                do {
                    cell = room.random()
                } while (level.heaps[cell] != null)
            }
            level.drop(range[i], cell).type = Heap.Type.FOR_SALE
            pos++
        }
        placeShopkeeper(level, room)
        for (door in room.connected.values) {
            door?.set(Room.Door.Type.REGULAR)
        }
    }
    private fun range(): Array<Item> {
        val items = ArrayList<Item>()
        when (Dungeon.depth) {
            6 -> {
                items.add((if (Random.Int(2) == 0) Quarterstaff() else Spear()).identify())
                items.add(LeatherArmor().identify())
                items.add(SeedPouch())
                items.add(Weightstone())
                items.add(ThrowingNet().apply { quantity = 2 })
                Generator.random(Generator.Category.SEED)?.let { items.add(it) }
                Generator.random(Generator.Category.SEED)?.let { items.add(it) }
            }
            11 -> {
                items.add((if (Random.Int(2) == 0) Sword() else Mace()).identify())
                items.add(MailArmor().identify())
                items.add(ScrollHolder())
                items.add(Weightstone())
                items.add(FrostBerry())
                items.add(SmokeBomb().apply { quantity = 2 })
                items.add(HolyWater().apply { quantity = 2 })
            }
            16 -> {
                items.add((if (Random.Int(2) == 0) Longsword() else BattleAxe()).identify())
                items.add(ScaleArmor().identify())
                items.add(WandHolster())
                items.add(Weightstone())
                items.add(FrostBerry())
                items.add(DragonPepper())
                for (i in 0 until 5) {
                    Generator.random(Generator.Category.WEAPON)?.let { items.add(it) }
                }
                items.add(Ankh())
            }
            21 -> {
                when (Random.Int(3)) {
                    0 -> items.add(Glaive().identify())
                    1 -> items.add(WarHammer().identify())
                    2 -> items.add(PlateArmor().identify())
                }
                items.add(Torch())
                items.add(Torch())
                items.add(DragonPepper())
                items.add(Bomb().apply { quantity = 3 })
            }
        }
        items.add(PotionOfHealing())
        for (i in 0 until 3) {
            Generator.random(Generator.Category.POTION)?.let { items.add(it) }
        }
        items.add(ScrollOfIdentify())
        items.add(ScrollOfRemoveCurse())
        items.add(ScrollOfMagicMapping())
        Generator.random(Generator.Category.SCROLL)?.let { items.add(it) }
        items.add(OverpricedRation())
        items.add(OverpricedRation())
        items.add(CheeseWedge())
        items.add(Ankh())
        val range = items.toTypedArray()
        Random.shuffle(range)
        return range
    }
    private fun placeShopkeeper(level: Level, room: Room) {
        var pos: Int
        do {
            pos = room.random()
        } while (level.heaps[pos] != null)
        val shopkeeper = if (level is LastShopLevel) ImpShopkeeper() else Shopkeeper()
        shopkeeper.pos = pos
        level.mobs.add(shopkeeper)
        if (level is LastShopLevel) {
            for (i in Level.NEIGHBOURS9.indices) {
                val p = shopkeeper.pos + Level.NEIGHBOURS9[i]
                if (level.map[p] == Terrain.EMPTY_SP) {
                    level.map[p] = Terrain.WATER
                }
            }
        }
    }
    private fun xy2p(room: Room, xy: Point, pasWidth: Int, pasHeight: Int): Int {
        if (xy.y == room.top) {
            return (xy.x - room.left - 1)
        } else if (xy.x == room.right) {
            return (xy.y - room.top - 1) + pasWidth
        } else if (xy.y == room.bottom) {
            return (room.right - xy.x - 1) + pasWidth + pasHeight
        } else /*if (xy.x == room.left)*/ {
            if (xy.y == room.top + 1) {
                return 0
            } else {
                return (room.bottom - xy.y - 1) + pasWidth * 2 + pasHeight
            }
        }
    }
    private fun p2xy(room: Room, p: Int, pasWidth: Int, pasHeight: Int): Point {
        if (p < pasWidth) {
            return Point(room.left + 1 + p, room.top + 1)
        } else if (p < pasWidth + pasHeight) {
            return Point(room.right - 1, room.top + 1 + (p - pasWidth))
        } else if (p < pasWidth * 2 + pasHeight) {
            return Point(room.right - 1 - (p - (pasWidth + pasHeight)), room.bottom - 1)
        } else {
            return Point(room.left + 1, room.bottom - 1 - (p - (pasWidth * 2 + pasHeight)))
        }
    }
}

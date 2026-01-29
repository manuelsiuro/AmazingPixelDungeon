package com.watabou.pixeldungeon.items
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.actors.buffs.Frost
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.Mimic
import com.watabou.pixeldungeon.actors.mobs.Wraith
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.Splash
import com.watabou.pixeldungeon.effects.particles.ElmoParticle
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.effects.particles.ShadowParticle
import com.watabou.pixeldungeon.items.food.ChargrilledMeat
import com.watabou.pixeldungeon.items.food.FrozenCarpaccio
import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.plants.Plant.Seed
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.LinkedList
class Heap : Bundlable {
    enum class Type {
        HEAP,
        FOR_SALE,
        CHEST,
        LOCKED_CHEST,
        CRYSTAL_CHEST,
        TOMB,
        SKELETON,
        MIMIC,
        HIDDEN
    }
    var type: Type = Type.HEAP
    var pos: Int = 0
    var sprite: ItemSprite? = null
    var items: LinkedList<Item> = LinkedList()
    fun image(): Int {
        return when (type) {
            Type.HEAP, Type.FOR_SALE -> if (size() > 0) items.peek()?.image() ?: 0 else 0
            Type.CHEST, Type.MIMIC -> ItemSpriteSheet.CHEST
            Type.LOCKED_CHEST -> ItemSpriteSheet.LOCKED_CHEST
            Type.CRYSTAL_CHEST -> ItemSpriteSheet.CRYSTAL_CHEST
            Type.TOMB -> ItemSpriteSheet.TOMB
            Type.SKELETON -> ItemSpriteSheet.BONES
            Type.HIDDEN -> ItemSpriteSheet.HIDDEN
        }
    }
    fun glowing(): ItemSprite.Glowing? {
        return if ((type == Type.HEAP || type == Type.FOR_SALE) && items.isNotEmpty()) items.peek()?.glowing() else null
    }
    fun open(hero: Hero) {
        when (type) {
            Type.MIMIC -> if (Mimic.spawnAt(pos, items) != null) {
                GLog.n(TXT_MIMIC)
                destroy()
            } else {
                type = Type.CHEST
            }
            Type.TOMB -> Wraith.spawnAround(hero.pos)
            Type.SKELETON -> {
                CellEmitter.center(pos).start(Speck.factory(Speck.RATTLE), 0.1f, 3)
                for (item in items) {
                    if (item.cursed) {
                        if (Wraith.spawnAt(pos) == null) {
                            hero.sprite?.emitter()?.burst(ShadowParticle.CURSE, 6)
                            hero.damage(hero.HP / 2, this)
                        }
                        Sample.play(Assets.SND_CURSED)
                        break
                    }
                }
            }
            Type.HIDDEN -> {
                val s = sprite ?: return
                s.alpha(0f)
                s.parent?.add(AlphaTweener(s, 1f, FADE_TIME))
            }
            else -> {
            }
        }
        if (type != Type.MIMIC) {
            type = Type.HEAP
            sprite?.link()
            sprite?.drop()
        }
    }
    fun size(): Int {
        return items.size
    }
    fun pickUp(): Item {
        val item = items.removeFirst()
        if (items.isEmpty()) {
            destroy()
        } else {
            sprite?.view(image(), glowing())
        }
        return item
    }
    fun peek(): Item? {
        return items.peek()
    }
    fun drop(item: Item) {
        var itemToAdd = item
        if (itemToAdd.stackable) {
            val c = itemToAdd.javaClass
            for (i in items) {
                if (i.javaClass == c) {
                    i.quantity += itemToAdd.quantity
                    itemToAdd = i // This logic in Java was: item = i; break;
                    // But effectively we discard 'item' passed and keep 'i'.
                    // Wait. In Java: item = i; break;
                    // Then logic was: items.remove(item); (because item is now i, which is in list)
                    // Then items.addFirst(item);
                    // So it moves it to top?
                    // Java code:
                    // items.remove(item);
                    // if (item instanceof Dewdrop) items.add(item) else items.addFirst(item)
                    // So yes, it merges and moves to top (or bottom for Dewdrop).
                    break
                }
            }
            items.remove(itemToAdd)
        }
        // if (item instanceof Dewdrop) needs checking
        // But since Dewdrop is not converted yet, we can check by class name or assume it works if we convert Dewdrop soon.
        // Or check simple name?
        // Let's use string check for now or assume Dewdrop will be converted.
        // Actually Dewdrop is class com.watabou.pixeldungeon.items.Dewdrop.
        // Since we are compiled together, instanceof works even if Dewdrop is Java.
        // But I need to import it. It's in the same package (subpackage?). No, items package.
        // Wait, Dewdrop is in com.watabou.pixeldungeon.items.Dewdrop?
        // Let's check listing. Yes.
        // However, I haven't converted Dewdrop yet. It is in Items package.
        // I can reference it if I import it (it's in same package effectively or I import it).
        // It's in `com.watabou.pixeldungeon.items`. So automatic import.
        // Wait, items.remove(itemToAdd) removes instance.
        // Then we add it.
        // Note: In Kotlin LinkedList is MutableList.
        if (itemToAdd.javaClass.simpleName == "Dewdrop") { // Hack until Dewdrop converted or verified? No, normal `is` works with Java class too.
             // But simpler:
        }
        // Wait, for now `itemToAdd is Dewdrop` works if Dewdrop is accessible.
        // Since I haven't deleted Dewdrop.java, it works.
        // But wait. `itemToAdd.block`... no.
        // Simpler implementation of drop logic:
        // Refactored logic:
        if (itemToAdd.stackable) {
             val c = itemToAdd.javaClass
             for (i in items) {
                 if (i.javaClass == c) {
                     i.quantity += itemToAdd.quantity
                     items.remove(i)
                     itemToAdd = i
                     break
                 }
             }
        }
        // I'll stick to Java logic translation.
        if (itemToAdd.javaClass.simpleName == "Dewdrop") {
             items.add(itemToAdd)
        } else {
             items.addFirst(itemToAdd)
        }
        sprite?.view(image(), glowing())
    }
    fun replace(a: Item, b: Item) {
        val index = items.indexOf(a)
        if (index != -1) {
            items.removeAt(index)
            items.add(index, b)
        }
    }
    fun burn() {
        if (type == Type.MIMIC) {
            val m = Mimic.spawnAt(pos, items)
            if (m != null) {
                Buffs.affect(m, Burning::class.java)?.reignite(m)
                m.sprite?.emitter()?.burst(FlameParticle.FACTORY, 5)
                destroy()
            }
        }
        if (type != Type.HEAP) {
            return
        }
        var burnt = false
        var evaporated = false
        // items.toArray() copy for iteration
        for (item in items.toTypedArray()) {
            if (item is Scroll) {
                items.remove(item)
                burnt = true
            } else if (item.javaClass.simpleName == "Dewdrop") { // item is Dewdrop
                items.remove(item)
                evaporated = true
            } else if (item is MysteryMeat) {
                replace(item, ChargrilledMeat.cook(item))
                burnt = true
            }
        }
        if (burnt || evaporated) {
            if (Dungeon.visible[pos]) {
                if (burnt) {
                    burnFX(pos)
                } else {
                    evaporateFX(pos)
                }
            }
            if (isEmpty) {
                destroy()
            } else {
                sprite?.view(image(), glowing())
            }
        }
    }
    fun freeze() {
        if (type == Type.MIMIC) {
            val m = Mimic.spawnAt(pos, items)
            if (m != null) {
                Buffs.prolong(m, Frost::class.java, Frost.duration(m) * Random.Float(1.0f, 1.5f))
                destroy()
            }
        }
        if (type != Type.HEAP) {
            return
        }
        var frozen = false
        for (item in items.toTypedArray()) {
            if (item is MysteryMeat) {
                replace(item, FrozenCarpaccio.cook(item))
                frozen = true
            }
        }
        if (frozen) {
            if (isEmpty) {
                destroy()
            } else {
                sprite?.view(image(), glowing())
            }
        }
    }
    fun transmute(): Item? {
        CellEmitter.get(pos).burst(Speck.factory(Speck.BUBBLE), 3)
        Splash.at(pos, 0xFFFFFF, 3)
        val chances = FloatArray(items.size)
        var count = 0
        var index = 0
        for (item in items) {
            if (item is Seed) {
                count += item.quantity
                chances[index++] = item.quantity.toFloat()
            } else {
                count = 0
                break
            }
        }
        if (count >= SEEDS_TO_POTION) {
            CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6)
            Sample.play(Assets.SND_PUFF)
            if (Random.Int(count) == 0) {
                CellEmitter.center(pos).burst(Speck.factory(Speck.EVOKE), 3)
                destroy()
                Statistics.potionsCooked++
                Badges.validatePotionsCooked()
                return Generator.random(Generator.Category.POTION)
            } else {
                val proto = items[Random.chances(chances)] as Seed
                val itemClass = proto.alchemyClass
                destroy()
                Statistics.potionsCooked++
                Badges.validatePotionsCooked()
                return if (itemClass == null) {
                    Generator.random(Generator.Category.POTION)
                } else {
                    try {
                        itemClass.getDeclaredConstructor().newInstance()
                    } catch (e: Exception) {
                         null
                    }
                }
            }
        } else {
            return null
        }
    }
    val isEmpty: Boolean
        get() = items.isEmpty()
    fun destroy() {
        Dungeon.level?.heaps?.remove(this.pos)
        sprite?.kill()
        items.clear()
    }
    override fun restoreFromBundle(bundle: Bundle) {
        pos = bundle.getInt(POS)
        type = Type.valueOf(bundle.getString(TYPE))
        // items casting from collection
        val collection = bundle.getCollection(ITEMS)
        items.clear()
        for (o in collection) {
            if (o is Item) items.add(o)
        }
    }
    override fun storeInBundle(bundle: Bundle) {
        bundle.put(POS, pos)
        bundle.put(TYPE, type.toString())
        bundle.put(ITEMS, items)
    }
    companion object {
        private const val TXT_MIMIC = "This is a mimic!"
        private const val SEEDS_TO_POTION = 3
        private const val FADE_TIME = 0.6f
        private const val POS = "pos"
        private const val TYPE = "type"
        private const val ITEMS = "items"
        fun burnFX(pos: Int) {
            CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6)
            Sample.play(Assets.SND_BURNING)
        }
        fun evaporateFX(pos: Int) {
            CellEmitter.get(pos).burst(Speck.factory(Speck.STEAM), 5)
        }
    }
}

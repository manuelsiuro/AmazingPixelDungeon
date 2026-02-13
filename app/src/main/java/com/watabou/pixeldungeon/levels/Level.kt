package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Scene
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Challenges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Alchemy
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.WellWater
import com.watabou.pixeldungeon.actors.buffs.Awareness
import com.watabou.pixeldungeon.actors.buffs.Blindness
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.MindVision
import com.watabou.pixeldungeon.actors.buffs.Shadows
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.actors.mobs.Bestiary
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.particles.FlowParticle
import com.watabou.pixeldungeon.effects.particles.WindParticle
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.quests.AiQuestBook
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.bags.ScrollHolder
import com.watabou.pixeldungeon.items.bags.SeedPouch
import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.items.potions.PotionOfStrength
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.items.scrolls.ScrollOfEnchantment
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.pixeldungeon.levels.features.Chasm
import com.watabou.pixeldungeon.levels.features.Door
import com.watabou.pixeldungeon.levels.features.HighGrass
import com.watabou.pixeldungeon.levels.painters.Painter
import com.watabou.pixeldungeon.levels.traps.*
import com.watabou.pixeldungeon.mechanics.ShadowCaster
import com.watabou.pixeldungeon.plants.Plant
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import com.watabou.utils.SparseArray
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
abstract class Level : Bundlable {
    enum class Feeling {
        NONE, CHASM, WATER, GRASS
    }
    var map = IntArray(0)
    var visited = BooleanArray(0)
    var mapped = BooleanArray(0)
    var harvestable = BooleanArray(0)
    var blockHP = android.util.SparseIntArray()
    var viewDistance = if (Dungeon.isChallenged(Challenges.DARKNESS)) 3 else 8
    var feeling = Feeling.NONE
    var entrance: Int = 0
    var exit: Int = 0
    var mobs = HashSet<Mob>()
    var heaps = SparseArray<Heap>()
    var blobs = HashMap<Class<out Blob>, Blob>()
    var plants = SparseArray<Plant>()
    protected var itemsToSpawn = ArrayList<Item>()
    var color1 = 0x004400
    var color2 = 0x88CC44
    open fun create() {
        resizingNeeded = false
        map = IntArray(LENGTH)
        visited = BooleanArray(LENGTH)
        mapped = BooleanArray(LENGTH)
        harvestable = BooleanArray(LENGTH)
        blockHP = android.util.SparseIntArray()
        mobs = HashSet()
        heaps = SparseArray()
        blobs = HashMap()
        plants = SparseArray()
        if (!Dungeon.bossLevel()) {
            addItemToSpawn(Generator.random(Generator.Category.FOOD))
            if (Dungeon.posNeeded()) {
                addItemToSpawn(PotionOfStrength())
                Dungeon.potionOfStrength++
            }
            if (Dungeon.souNeeded()) {
                addItemToSpawn(ScrollOfUpgrade())
                Dungeon.scrollsOfUpgrade++
            }
            if (Dungeon.soeNeeded()) {
                addItemToSpawn(ScrollOfEnchantment())
                Dungeon.scrollsOfEnchantment++
            }
            if (Dungeon.depth > 1) {
                when (Random.Int(10)) {
                    0 -> if (!Dungeon.bossLevel(Dungeon.depth + 1)) {
                        feeling = Feeling.CHASM
                    }
                    1 -> feeling = Feeling.WATER
                    2 -> feeling = Feeling.GRASS
                }
            }
        }
        val pitNeeded = Dungeon.depth > 1 && weakFloorCreated
        do {
            Arrays.fill(map, if (feeling == Feeling.CHASM) Terrain.CHASM else Terrain.WALL)
            pitRoomNeeded = pitNeeded
            weakFloorCreated = false
        } while (!build())
        decorate()
        buildFlagMaps()
        cleanWalls()
        markHarvestable()
        createMobs()
        createItems()
    }
    open fun reset() {
        for (mob in mobs.toTypedArray()) {
            if (!mob.reset()) {
                mobs.remove(mob)
            }
        }
        createMobs()
    }
    override fun restoreFromBundle(bundle: Bundle) {
        mobs = HashSet()
        heaps = SparseArray()
        blobs = HashMap()
        plants = SparseArray()
        map = bundle.getIntArray(MAP) ?: IntArray(0)
        visited = bundle.getBooleanArray(VISITED) ?: BooleanArray(0)
        mapped = bundle.getBooleanArray(MAPPED) ?: BooleanArray(0)
        harvestable = if (bundle.contains(HARVESTABLE)) {
            bundle.getBooleanArray(HARVESTABLE) ?: BooleanArray(0)
        } else {
            BooleanArray(0)
        }
        blockHP = android.util.SparseIntArray()
        if (bundle.contains(BLOCK_HP_KEYS)) {
            val keys = bundle.getIntArray(BLOCK_HP_KEYS)
            val vals = bundle.getIntArray(BLOCK_HP_VALS)
            if (keys != null && vals != null) {
                for (i in keys.indices) {
                    blockHP.put(keys[i], vals[i])
                }
            }
        }
        entrance = bundle.getInt(ENTRANCE)
        exit = bundle.getInt(EXIT)
        weakFloorCreated = false
        adjustMapSize()
        var collection = bundle.getCollection(HEAPS)
        for (h in collection) {
            val heap = h as Heap
            if (resizingNeeded) {
                heap.pos = adjustPos(heap.pos)
            }
            heaps.put(heap.pos, heap)
        }
        collection = bundle.getCollection(PLANTS)
        for (p in collection) {
            val plant = p as Plant
            if (resizingNeeded) {
                plant.pos = adjustPos(plant.pos)
            }
            plants.put(plant.pos, plant)
        }
        collection = bundle.getCollection(MOBS)
        for (m in collection) {
            val mob = m as? Mob
            if (mob != null) {
                if (resizingNeeded) {
                    mob.pos = adjustPos(mob.pos)
                }
                mobs.add(mob)
            }
        }
        collection = bundle.getCollection(BLOBS)
        for (b in collection) {
            val blob = b as Blob
            blobs[blob.javaClass] = blob
        }
        buildFlagMaps()
        cleanWalls()
    }
    override fun storeInBundle(bundle: Bundle) {
        bundle.put(MAP, map)
        bundle.put(VISITED, visited)
        bundle.put(MAPPED, mapped)
        bundle.put(HARVESTABLE, harvestable)
        // Serialize blockHP as parallel int arrays
        val blockSize = blockHP.size()
        val blockKeys = IntArray(blockSize)
        val blockVals = IntArray(blockSize)
        for (i in 0 until blockSize) {
            blockKeys[i] = blockHP.keyAt(i)
            blockVals[i] = blockHP.valueAt(i)
        }
        bundle.put(BLOCK_HP_KEYS, blockKeys)
        bundle.put(BLOCK_HP_VALS, blockVals)
        bundle.put(ENTRANCE, entrance)
        bundle.put(EXIT, exit)
        bundle.put(HEAPS, heaps.values())
        bundle.put(PLANTS, plants.values())
        bundle.put(MOBS, mobs)
        bundle.put(BLOBS, blobs.values)
    }
    open fun tunnelTile(): Int {
        return if (feeling == Feeling.CHASM) Terrain.EMPTY_SP else Terrain.EMPTY
    }
    private fun adjustMapSize() {
        // For levels saved before 1.6.3
        if (map.size < LENGTH) {
            resizingNeeded = true
            loadedMapSize = sqrt(map.size.toDouble()).toInt()
            val newMap = IntArray(LENGTH)
            Arrays.fill(newMap, Terrain.WALL)
            val newVisited = BooleanArray(LENGTH)
            //Arrays.fill(newVisited, false) // Default is false
            val newMapped = BooleanArray(LENGTH)
            val newHarvestable = BooleanArray(LENGTH)
            for (i in 0 until loadedMapSize) {
                System.arraycopy(this.map, i * loadedMapSize, newMap, i * WIDTH, loadedMapSize)
                System.arraycopy(this.visited, i * loadedMapSize, newVisited, i * WIDTH, loadedMapSize)
                System.arraycopy(this.mapped, i * loadedMapSize, newMapped, i * WIDTH, loadedMapSize)
                if (this.harvestable.size > i * loadedMapSize) {
                    System.arraycopy(this.harvestable, i * loadedMapSize, newHarvestable, i * WIDTH, loadedMapSize)
                }
            }
            this.map = newMap
            this.visited = newVisited
            this.mapped = newMapped
            this.harvestable = newHarvestable
            entrance = adjustPos(entrance)
            exit = adjustPos(exit)
        } else {
            resizingNeeded = false
        }
        // Ensure harvestable array is correct size for old saves
        if (harvestable.size < LENGTH) {
            harvestable = BooleanArray(LENGTH)
        }
    }
    open fun adjustPos(pos: Int): Int {
        return (pos / loadedMapSize) * WIDTH + (pos % loadedMapSize)
    }
    open fun tilesTex(): String? {
        return null
    }
    open fun waterTex(): String? {
        return null
    }
    protected abstract fun build(): Boolean
    protected abstract fun decorate()
    protected abstract fun createMobs()
    protected abstract fun createItems()

    protected open fun markHarvestable() {
        // Default: no harvestable walls. Overridden in RegularLevel.
    }

    fun damageBlock(cell: Int, dmg: Int) {
        val hp = blockHP.get(cell, 0)
        if (hp <= 0) return
        val remaining = hp - dmg
        if (remaining <= 0) {
            blockHP.delete(cell)
            set(cell, Terrain.EMBERS)
            GameScene.updateMap(cell)
            CellEmitter.get(cell).burst(Speck.factory(Speck.ROCK), 4)
            Sample.play(Assets.SND_ROCKS)
        } else {
            blockHP.put(cell, remaining)
        }
    }
    open fun addVisuals(scene: Scene) {
        for (i in 0 until LENGTH) {
            if (pit[i]) {
                scene.add(WindParticle.Wind(i))
                if (i >= WIDTH && water[i - WIDTH]) {
                    scene.add(FlowParticle.Flow(i - WIDTH))
                }
            }
        }
    }
    open fun nMobs(): Int {
        return 0
    }
    open fun respawner(): Actor? {
        return object : Actor() {
            override fun act(): Boolean {
                if (mobs.size < nMobs()) {
                    val mob = Bestiary.mutable(Dungeon.depth)
                    if (mob != null) {
                        mob.state = mob.WANDERING
                        mob.pos = randomRespawnCell()
                        if ((Dungeon.hero?.isAlive == true) && mob.pos != -1) {
                            GameScene.add(mob)
                            if (Statistics.amuletObtained) {
                                Dungeon.hero?.let { mob.beckon(it.pos) }
                            }
                        }
                    }
                }
                spend(if (Dungeon.nightMode || Statistics.amuletObtained) TIME_TO_RESPAWN / 2 else TIME_TO_RESPAWN)
                return true
            }
        }
    }
    open fun randomRespawnCell(): Int {
        var cell: Int
        do {
            cell = Random.Int(LENGTH)
        } while (!passable[cell] || Dungeon.visible[cell] || Actor.findChar(cell) != null)
        return cell
    }
    open fun randomDestination(): Int {
        var cell: Int
        do {
            cell = Random.Int(LENGTH)
        } while (!passable[cell])
        return cell
    }
    open fun addItemToSpawn(item: Item?) {
        if (item != null) {
            itemsToSpawn.add(item)
        }
    }
    open fun itemToSpanAsPrize(): Item? {
        if (Random.Int(itemsToSpawn.size + 1) > 0) {
            val item = Random.element(itemsToSpawn)
            itemsToSpawn.remove(item)
            return item
        } else {
            return null
        }
    }
    private fun buildFlagMaps() {
        for (i in 0 until LENGTH) {
            val flags = Terrain.flags[map[i]]
            passable[i] = (flags and Terrain.PASSABLE) != 0
            losBlocking[i] = (flags and Terrain.LOS_BLOCKING) != 0
            flamable[i] = (flags and Terrain.FLAMABLE) != 0
            secret[i] = (flags and Terrain.SECRET) != 0
            solid[i] = (flags and Terrain.SOLID) != 0
            avoid[i] = (flags and Terrain.AVOID) != 0
            water[i] = (flags and Terrain.LIQUID) != 0
            pit[i] = (flags and Terrain.PIT) != 0
        }
        val lastRow = LENGTH - WIDTH
        for (i in 0 until WIDTH) {
            avoid[i] = false
            passable[i] = avoid[i]
            avoid[lastRow + i] = false
            passable[lastRow + i] = avoid[lastRow + i]
        }
        var i = WIDTH
        while (i < lastRow) {
            avoid[i] = false
            passable[i] = avoid[i]
            avoid[i + WIDTH - 1] = false
            passable[i + WIDTH - 1] = avoid[i + WIDTH - 1]
            i += WIDTH
        }
        for (j in WIDTH until LENGTH - WIDTH) {
            if (water[j]) {
                map[j] = getWaterTile(j)
            }
            if (pit[j]) {
                if (!pit[j - WIDTH]) {
                    val c = map[j - WIDTH]
                    if (c == Terrain.EMPTY_SP || c == Terrain.STATUE_SP) {
                        map[j] = Terrain.CHASM_FLOOR_SP
                    } else if (water[j - WIDTH]) {
                        map[j] = Terrain.CHASM_WATER
                    } else if ((Terrain.flags[c] and Terrain.UNSTITCHABLE) != 0) {
                        map[j] = Terrain.CHASM_WALL
                    } else {
                        map[j] = Terrain.CHASM_FLOOR
                    }
                }
            }
        }
    }
    private fun getWaterTile(pos: Int): Int {
        var t = Terrain.WATER_TILES
        for (j in NEIGHBOURS4.indices) {
            if ((Terrain.flags[map[pos + NEIGHBOURS4[j]]] and Terrain.UNSTITCHABLE) != 0) {
                t += 1 shl j
            }
        }
        return t
    }
    open fun destroy(pos: Int) {
        if ((Terrain.flags[map[pos]] and Terrain.UNSTITCHABLE) == 0) {
            set(pos, Terrain.EMBERS)
        } else {
            var flood = false
            for (j in NEIGHBOURS4.indices) {
                if (water[pos + NEIGHBOURS4[j]]) {
                    flood = true
                    break
                }
            }
            if (flood) {
                set(pos, getWaterTile(pos))
            } else {
                set(pos, Terrain.EMBERS)
            }
        }
    }
    private fun cleanWalls() {
        for (i in 0 until LENGTH) {
            var d = false
            for (j in NEIGHBOURS9.indices) {
                val n = i + NEIGHBOURS9[j]
                if (n >= 0 && n < LENGTH && map[n] != Terrain.WALL && map[n] != Terrain.WALL_DECO) {
                    d = true
                    break
                }
            }
            if (d) {
                d = false
                for (j in NEIGHBOURS9.indices) {
                    val n = i + NEIGHBOURS9[j]
                    if (n >= 0 && n < LENGTH && !pit[n]) {
                        d = true
                        break
                    }
                }
            }
            discoverable[i] = d
        }
    }
    open fun drop(item: Item, cell: Int): Heap {
        var droppedItem = item
        if (Dungeon.isChallenged(Challenges.NO_FOOD) && droppedItem is Food) {
            droppedItem = Gold(droppedItem.price())
        } else if (Dungeon.isChallenged(Challenges.NO_ARMOR) && droppedItem is Armor) {
            droppedItem = Gold(droppedItem.price())
        } else if (Dungeon.isChallenged(Challenges.NO_HEALING) && droppedItem is PotionOfHealing) {
            droppedItem = Gold(droppedItem.price())
        } else if (Dungeon.isChallenged(Challenges.NO_HERBALISM) && droppedItem is SeedPouch) {
            droppedItem = Gold(droppedItem.price())
        } else if (Dungeon.isChallenged(Challenges.NO_SCROLLS) && (droppedItem is Scroll || droppedItem is ScrollHolder)) {
            if (droppedItem is ScrollOfUpgrade) {
                // These scrolls still can be found
            } else {
                droppedItem = Gold(droppedItem.price())
            }
        }
        var targetCell = cell
        if (map[targetCell] == Terrain.ALCHEMY && droppedItem !is Plant.Seed) {
            var n: Int
            do {
                n = targetCell + NEIGHBOURS8[Random.Int(8)]
            } while (map[n] != Terrain.EMPTY_SP)
            targetCell = n
        }
        var heap = heaps[targetCell]
        if (heap == null) {
            heap = Heap()
            heap.pos = targetCell
            if (map[targetCell] == Terrain.CHASM || (Dungeon.level != null && pit[targetCell])) {
                Dungeon.dropToChasm(droppedItem)
                GameScene.discard(heap)
            } else {
                heaps.put(targetCell, heap)
                GameScene.add(heap)
            }
        } else if (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST) {
            var n: Int
            do {
                n = targetCell + NEIGHBOURS8[Random.Int(8)]
            } while (!passable[n] && !avoid[n])
            return drop(droppedItem, n)
        }
        heap.drop(droppedItem)
        if (Dungeon.level != null) {
            press(targetCell, null)
        }
        return heap
    }
    open fun plant(seed: Plant.Seed, pos: Int): Plant {
        var plant = plants[pos]
        if (plant != null) {
            plant.wither()
        }
        plant = seed.couch(pos)
        plants.put(pos, plant)
        GameScene.add(plant)
        return plant
    }
    open fun uproot(pos: Int) {
        plants.delete(pos)
    }
    open fun pitCell(): Int {
        return randomRespawnCell()
    }
    open fun press(cell: Int, ch: Char?) {
        if (pit[cell] && ch === Dungeon.hero) {
            Chasm.heroFall(cell)
            return
        }
        var trap = false
        when (map[cell]) {
            Terrain.SECRET_TOXIC_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                ToxicTrap.trigger(cell, ch)
            }
            Terrain.TOXIC_TRAP -> {
                trap = true
                ToxicTrap.trigger(cell, ch)
            }
            Terrain.SECRET_FIRE_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                FireTrap.trigger(cell, ch)
            }
            Terrain.FIRE_TRAP -> {
                trap = true
                FireTrap.trigger(cell, ch)
            }
            Terrain.SECRET_PARALYTIC_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                ParalyticTrap.trigger(cell, ch)
            }
            Terrain.PARALYTIC_TRAP -> {
                trap = true
                ParalyticTrap.trigger(cell, ch)
            }
            Terrain.SECRET_POISON_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                PoisonTrap.trigger(cell, ch)
            }
            Terrain.POISON_TRAP -> {
                trap = true
                PoisonTrap.trigger(cell, ch)
            }
            Terrain.SECRET_ALARM_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                AlarmTrap.trigger(cell, ch)
            }
            Terrain.ALARM_TRAP -> {
                trap = true
                AlarmTrap.trigger(cell, ch)
            }
            Terrain.SECRET_LIGHTNING_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                LightningTrap.trigger(cell, ch)
            }
            Terrain.LIGHTNING_TRAP -> {
                trap = true
                LightningTrap.trigger(cell, ch)
            }
            Terrain.SECRET_GRIPPING_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                GrippingTrap.trigger(cell, ch)
            }
            Terrain.GRIPPING_TRAP -> {
                trap = true
                GrippingTrap.trigger(cell, ch)
            }
            Terrain.SECRET_SUMMONING_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                SummoningTrap.trigger(cell, ch)
            }
            Terrain.SUMMONING_TRAP -> {
                trap = true
                SummoningTrap.trigger(cell, ch)
            }
            Terrain.HIGH_GRASS -> HighGrass.trample(this, cell, ch)
            Terrain.WELL -> WellWater.affectCell(cell)
            Terrain.ALCHEMY -> if (ch == null) {
                Alchemy.transmute(cell)
            }
            Terrain.DOOR -> Door.enter(cell)
            // CRAFTING_TABLE and FURNACE are SOLID — interaction handled via HeroAction.UseStation
        }
        if (trap) {
            Sample.play(Assets.SND_TRAP)
            if (ch === Dungeon.hero) {
                Dungeon.hero?.interrupt()
                AiQuestBook.onTrapTriggered()
            }
            set(cell, Terrain.INACTIVE_TRAP)
            GameScene.updateMap(cell)
        }
        val plant = plants[cell]
        if (plant != null) {
            plant.activate(ch)
        }
    }
    open fun mobPress(mob: Mob) {
        val cell = mob.pos
        if (pit[cell] && !mob.flying) {
            Chasm.mobFall(mob)
            return
        }
        var trap = true
        when (map[cell]) {
            Terrain.TOXIC_TRAP -> ToxicTrap.trigger(cell, mob)
            Terrain.FIRE_TRAP -> FireTrap.trigger(cell, mob)
            Terrain.PARALYTIC_TRAP -> ParalyticTrap.trigger(cell, mob)
            Terrain.POISON_TRAP -> PoisonTrap.trigger(cell, mob)
            Terrain.ALARM_TRAP -> AlarmTrap.trigger(cell, mob)
            Terrain.LIGHTNING_TRAP -> LightningTrap.trigger(cell, mob)
            Terrain.GRIPPING_TRAP -> GrippingTrap.trigger(cell, mob)
            Terrain.SUMMONING_TRAP -> SummoningTrap.trigger(cell, mob)
            Terrain.DOOR -> {
                Door.enter(cell)
                trap = false
            }
            else -> trap = false
        }
        if (trap) {
            if (Dungeon.visible[cell]) {
                Sample.play(Assets.SND_TRAP)
            }
            set(cell, Terrain.INACTIVE_TRAP)
            GameScene.updateMap(cell)
        }
        val plant = plants[cell]
        if (plant != null) {
            plant.activate(mob)
        }
    }
    open fun updateFieldOfView(c: Char): BooleanArray {
        val cx = c.pos % WIDTH
        val cy = c.pos / WIDTH
        val sighted = c.buff(Blindness::class.java) == null && c.buff(Shadows::class.java) == null && c.isAlive
        if (sighted) {
            ShadowCaster.castShadow(cx, cy, fieldOfView, c.viewDistance)
        } else {
            Arrays.fill(fieldOfView, false)
        }
        var sense = 1
        if (c.isAlive) {
            for (b in c.buffs(MindVision::class.java)) {
                sense = max((b.distance).toDouble(), sense.toDouble()).toInt()
            }
        }
        if (sighted && sense > 1 || !sighted) {
            val ax = max(0.0, (cx - sense).toDouble()).toInt()
            val bx = min((cx + sense).toDouble(), (WIDTH - 1).toDouble()).toInt()
            val ay = max(0.0, (cy - sense).toDouble()).toInt()
            val by = min((cy + sense).toDouble(), (HEIGHT - 1).toDouble()).toInt()
            val len = bx - ax + 1
            var pos = ax + ay * WIDTH
            var y = ay
            while (y <= by) {
                Arrays.fill(fieldOfView, pos, pos + len, true)
                y++
                pos += WIDTH
            }
            for (i in 0 until LENGTH) {
                fieldOfView[i] = fieldOfView[i] and discoverable[i]
            }
        }
        if (c.isAlive) {
            if (c.buff(MindVision::class.java) != null) {
                for (mob in mobs) {
                    val p = mob.pos
                    fieldOfView[p] = true
                    fieldOfView[p + 1] = true
                    fieldOfView[p - 1] = true
                    fieldOfView[p + WIDTH + 1] = true
                    fieldOfView[p + WIDTH - 1] = true
                    fieldOfView[p - WIDTH + 1] = true
                    fieldOfView[p - WIDTH - 1] = true
                    fieldOfView[p + WIDTH] = true
                    fieldOfView[p - WIDTH] = true
                }
            } else if (c === Dungeon.hero && c.heroClass == HeroClass.HUNTRESS) {
                for (mob in mobs) {
                    val p = mob.pos
                    if (distance(c.pos, p) == 2) {
                        fieldOfView[p] = true
                        fieldOfView[p + 1] = true
                        fieldOfView[p - 1] = true
                        fieldOfView[p + WIDTH + 1] = true
                        fieldOfView[p + WIDTH - 1] = true
                        fieldOfView[p - WIDTH + 1] = true
                        fieldOfView[p - WIDTH - 1] = true
                        fieldOfView[p + WIDTH] = true
                        fieldOfView[p - WIDTH] = true
                    }
                }
            }
            if (c.buff(Awareness::class.java) != null) {
                for (heap in heaps.values()) {
                    val p = heap.pos
                    fieldOfView[p] = true
                    fieldOfView[p + 1] = true
                    fieldOfView[p - 1] = true
                    fieldOfView[p + WIDTH + 1] = true
                    fieldOfView[p + WIDTH - 1] = true
                    fieldOfView[p - WIDTH + 1] = true
                    fieldOfView[p - WIDTH - 1] = true
                    fieldOfView[p + WIDTH] = true
                    fieldOfView[p - WIDTH] = true
                }
            }
        }
        return fieldOfView
    }
    open fun tileName(tile: Int): String {
        if (tile >= Terrain.WATER_TILES) {
            return tileName(Terrain.WATER)
        }
        if (tile != Terrain.CHASM && (Terrain.flags[tile] and Terrain.PIT) != 0) {
            return tileName(Terrain.CHASM)
        }
        return when (tile) {
            Terrain.CHASM -> "Chasm"
            Terrain.EMPTY, Terrain.EMPTY_SP, Terrain.EMPTY_DECO, Terrain.SECRET_TOXIC_TRAP, Terrain.SECRET_FIRE_TRAP, Terrain.SECRET_PARALYTIC_TRAP, Terrain.SECRET_POISON_TRAP, Terrain.SECRET_ALARM_TRAP, Terrain.SECRET_LIGHTNING_TRAP -> "Floor"
            Terrain.GRASS -> "Grass"
            Terrain.WATER -> "Water"
            Terrain.WALL, Terrain.WALL_DECO, Terrain.SECRET_DOOR -> "Wall"
            Terrain.DOOR -> "Closed door"
            Terrain.OPEN_DOOR -> "Open door"
            Terrain.ENTRANCE -> "Depth entrance"
            Terrain.EXIT -> "Depth exit"
            Terrain.EMBERS -> "Embers"
            Terrain.LOCKED_DOOR -> "Locked door"
            Terrain.PEDESTAL -> "Pedestal"
            Terrain.BARRICADE -> "Barricade"
            Terrain.HIGH_GRASS -> "High grass"
            Terrain.LOCKED_EXIT -> "Locked depth exit"
            Terrain.UNLOCKED_EXIT -> "Unlocked depth exit"
            Terrain.SIGN -> "Sign"
            Terrain.WELL -> "Well"
            Terrain.EMPTY_WELL -> "Empty well"
            Terrain.STATUE, Terrain.STATUE_SP -> "Statue"
            Terrain.TOXIC_TRAP -> "Toxic gas trap"
            Terrain.FIRE_TRAP -> "Fire trap"
            Terrain.PARALYTIC_TRAP -> "Paralytic gas trap"
            Terrain.POISON_TRAP -> "Poison dart trap"
            Terrain.ALARM_TRAP -> "Alarm trap"
            Terrain.LIGHTNING_TRAP -> "Lightning trap"
            Terrain.GRIPPING_TRAP -> "Gripping trap"
            Terrain.SUMMONING_TRAP -> "Summoning trap"
            Terrain.INACTIVE_TRAP -> "Triggered trap"
            Terrain.BOOKSHELF -> "Bookshelf"
            Terrain.ALCHEMY -> "Alchemy pot"
            Terrain.CRAFTING_TABLE -> "Crafting table"
            Terrain.FURNACE -> "Furnace"
            else -> "???"
        }
    }
    open fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.CHASM -> "You can't see the bottom."
            Terrain.WATER -> "In case of burning step into the water to extinguish the fire."
            Terrain.ENTRANCE -> "Stairs lead up to the upper depth."
            Terrain.EXIT, Terrain.UNLOCKED_EXIT -> "Stairs lead down to the lower depth."
            Terrain.EMBERS -> "Embers cover the floor."
            Terrain.HIGH_GRASS -> "Dense vegetation blocks the view."
            Terrain.LOCKED_DOOR -> "This door is locked, you need a matching key to unlock it."
            Terrain.LOCKED_EXIT -> "Heavy bars block the stairs leading down."
            Terrain.BARRICADE -> "The wooden barricade is firmly set but has dried over the years. Might it burn?"
            Terrain.SIGN -> "You can't read the text from here."
            Terrain.TOXIC_TRAP, Terrain.FIRE_TRAP, Terrain.PARALYTIC_TRAP, Terrain.POISON_TRAP, Terrain.ALARM_TRAP, Terrain.LIGHTNING_TRAP, Terrain.GRIPPING_TRAP, Terrain.SUMMONING_TRAP -> "Stepping onto a hidden pressure plate will activate the trap."
            Terrain.INACTIVE_TRAP -> "The trap has been triggered before and it's not dangerous anymore."
            Terrain.STATUE, Terrain.STATUE_SP -> "Someone wanted to adorn this place, but failed, obviously."
            Terrain.ALCHEMY -> "Drop some seeds here to cook a potion."
            Terrain.EMPTY_WELL -> "The well has run dry."
            Terrain.CRAFTING_TABLE -> "A sturdy workbench for crafting items from raw materials."
            Terrain.FURNACE -> "A furnace for smelting ore into metal ingots."
            else -> {
                if (tile >= Terrain.WATER_TILES) {
                    return tileDesc(Terrain.WATER)
                }
                if ((Terrain.flags[tile] and Terrain.PIT) != 0) {
                    return tileDesc(Terrain.CHASM)
                }
                ""
            }
        }
    }
    companion object {
        const val WIDTH = 32
        const val HEIGHT = 32
        const val LENGTH = WIDTH * HEIGHT
        val NEIGHBOURS4 = intArrayOf(-WIDTH, +1, +WIDTH, -1)
        val NEIGHBOURS8 = intArrayOf(+1, -1, +WIDTH, -WIDTH, +1 + WIDTH, +1 - WIDTH, -1 + WIDTH, -1 - WIDTH)
        val NEIGHBOURS9 = intArrayOf(0, +1, -1, +WIDTH, -WIDTH, +1 + WIDTH, +1 - WIDTH, -1 + WIDTH, -1 - WIDTH)
        protected var TIME_TO_RESPAWN = 50f
        private const val TXT_HIDDEN_PLATE_CLICKS = "A hidden pressure plate clicks!"
        var resizingNeeded: Boolean = false
        var loadedMapSize: Int = 0
        var fieldOfView = BooleanArray(LENGTH)
        var passable = BooleanArray(LENGTH)
        var losBlocking = BooleanArray(LENGTH)
        var flamable = BooleanArray(LENGTH)
        var secret = BooleanArray(LENGTH)
        var solid = BooleanArray(LENGTH)
        var avoid = BooleanArray(LENGTH)
        var water = BooleanArray(LENGTH)
        var pit = BooleanArray(LENGTH)
        var discoverable = BooleanArray(LENGTH)
        var pitRoomNeeded = false
        var weakFloorCreated = false
        private const val MAP = "map"
        private const val VISITED = "visited"
        private const val MAPPED = "mapped"
        private const val HARVESTABLE = "harvestable"
        private const val BLOCK_HP_KEYS = "blockHPKeys"
        private const val BLOCK_HP_VALS = "blockHPVals"
        private const val ENTRANCE = "entrance"
        private const val EXIT = "exit"
        private const val HEAPS = "heaps"
        private const val PLANTS = "plants"
        private const val MOBS = "mobs"
        private const val BLOBS = "blobs"
        fun set(cell: Int, terrain: Int) {
            val level = Dungeon.level ?: return
            Painter.set(level, cell, terrain)
            val flags = Terrain.flags[terrain]
            passable[cell] = (flags and Terrain.PASSABLE) != 0
            losBlocking[cell] = (flags and Terrain.LOS_BLOCKING) != 0
            flamable[cell] = (flags and Terrain.FLAMABLE) != 0
            secret[cell] = (flags and Terrain.SECRET) != 0
            solid[cell] = (flags and Terrain.SOLID) != 0
            avoid[cell] = (flags and Terrain.AVOID) != 0
            pit[cell] = (flags and Terrain.PIT) != 0
            water[cell] = terrain == Terrain.WATER || terrain >= Terrain.WATER_TILES
        }
        fun distance(a: Int, b: Int): Int {
            val ax = a % WIDTH
            val ay = a / WIDTH
            val bx = b % WIDTH
            val by = b / WIDTH
            return max(abs((ax - bx).toDouble()), abs((ay - by).toDouble())).toInt()
        }
        fun adjacent(a: Int, b: Int): Boolean {
            val diff = abs((a - b).toDouble()).toInt()
            return diff == 1 || diff == WIDTH || diff == WIDTH + 1 || diff == WIDTH - 1
        }
    }
}

package com.watabou.pixeldungeon
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Amok
import com.watabou.pixeldungeon.actors.buffs.Light
import com.watabou.pixeldungeon.actors.buffs.Rage
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.actors.mobs.npcs.Blacksmith
import com.watabou.pixeldungeon.actors.mobs.npcs.Ghost
import com.watabou.pixeldungeon.actors.mobs.npcs.Imp
import com.watabou.pixeldungeon.actors.mobs.npcs.Wandmaker
import com.watabou.pixeldungeon.items.Ankh
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.potions.Potion
import com.watabou.pixeldungeon.items.rings.Ring
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.items.wands.Wand
import com.watabou.pixeldungeon.levels.*
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.StartScene
import com.watabou.pixeldungeon.ui.QuickSlot
import com.watabou.pixeldungeon.utils.BArray
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.windows.WndResurrect
import com.watabou.utils.Bundle
import com.watabou.utils.Bundlable
import com.watabou.utils.PathFinder
import com.watabou.utils.Random
import com.watabou.utils.SparseArray
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.ArrayList
import java.util.Arrays
import java.util.Date
import java.util.HashSet
object Dungeon {
    var potionOfStrength: Int = 0
    var scrollsOfUpgrade: Int = 0
    var scrollsOfEnchantment: Int = 0
    var dewVial: Boolean = false // true if the dew vial can be spawned
    var challenges: Int = 0
    var hero: Hero? = null
    var level: Level? = null
    var depth: Int = 0
    var gold: Int = 0
    // Reason of death
    var resultDescription: String? = null
    var chapters: HashSet<Int>? = null
    // Hero's field of view
    var visible: BooleanArray = BooleanArray(Level.LENGTH)
    var nightMode: Boolean = false
    var droppedItems: SparseArray<ArrayList<Item>>? = null
    fun init() {
        challenges = PixelDungeon.challenges()
        Actor.clear()
        PathFinder.setMapSize(Level.WIDTH, Level.HEIGHT)
        Scroll.initLabels()
        Potion.initColors()
        Wand.initWoods()
        Ring.initGems()
        Statistics.reset()
        Journal.reset()
        depth = 0
        gold = 0
        droppedItems = SparseArray()
        potionOfStrength = 0
        scrollsOfUpgrade = 0
        scrollsOfEnchantment = 0
        dewVial = true
        chapters = HashSet()
        Ghost.Quest.reset()
        Wandmaker.Quest.reset()
        Blacksmith.Quest.reset()
        Imp.Quest.reset()
        Room.shuffleTypes()
        QuickSlot.primaryValue = null
        QuickSlot.secondaryValue = null
        hero = Hero()
        hero?.live()
        Badges.reset()
        StartScene.curClass?.initHero(hero ?: return)
    }
    fun isChallenged(mask: Int): Boolean {
        return (challenges and mask) != 0
    }
    fun newLevel(): Level {
        Dungeon.level = null
        Actor.clear()
        depth++
        if (depth > Statistics.deepestFloor) {
            Statistics.deepestFloor = depth
            if (Statistics.qualifiedForNoKilling) {
                Statistics.completedWithNoKilling = true
            } else {
                Statistics.completedWithNoKilling = false
            }
        }
        Arrays.fill(visible, false)
        val level: Level
        when (depth) {
            1, 2, 3, 4 -> level = SewerLevel()
            5 -> level = SewerBossLevel()
            6, 7, 8, 9 -> level = PrisonLevel()
            10 -> level = PrisonBossLevel()
            11, 12, 13, 14 -> level = CavesLevel()
            15 -> level = CavesBossLevel()
            16, 17, 18, 19 -> level = CityLevel()
            20 -> level = CityBossLevel()
            21 -> level = LastShopLevel()
            22, 23, 24, 25 -> level = HallsLevel() // Wait, original code case 22,23,24. 25 is boss.
            // Original: 22,23,24 -> HallsLevel. 25 -> HallsBossLevel.
            // My when has 25 in HallsLevel?? No, wait. 22..24.
            // Let me check orig code.
            // case 22: case 23: case 24: level = new HallsLevel(); break;
            // case 25: level = new HallsBossLevel(); break;
            26 -> level = LastLevel()
            else -> {
                if (depth == 25) {
                    level = HallsBossLevel()
                } else {
                    level = DeadEndLevel()
                    Statistics.deepestFloor--
                }
            }
        }
        // Correction for my when:
        // Kotlin when supports multiple values.
        /*
        22, 23, 24 -> level = HallsLevel()
        25 -> level = HallsBossLevel()
         */
        // I will rewrite the when properly.
        level.create()
        Statistics.qualifiedForNoKilling = !bossLevel()
        return level
    }
    // Helper for newLevel logic to avoid the messy when above in initial thought
    private fun createLevelForDepth(d: Int): Level {
        return when (d) {
            1, 2, 3, 4 -> SewerLevel()
            5 -> SewerBossLevel()
            6, 7, 8, 9 -> PrisonLevel()
            10 -> PrisonBossLevel()
            11, 12, 13, 14 -> CavesLevel()
            15 -> CavesBossLevel()
            16, 17, 18, 19 -> CityLevel()
            20 -> CityBossLevel()
            21 -> LastShopLevel()
            22, 23, 24 -> HallsLevel()
            25 -> HallsBossLevel()
            26 -> LastLevel()
            else -> {
                Statistics.deepestFloor--
                DeadEndLevel()
            }
        }
    }
    fun resetLevel() {
        Actor.clear()
        Arrays.fill(visible, false)
        val currentLevel = level ?: return
        currentLevel.reset()
        switchLevel(currentLevel, currentLevel.entrance)
    }
    fun shopOnLevel(): Boolean {
        return depth == 6 || depth == 11 || depth == 16
    }
    fun bossLevel(): Boolean {
        return bossLevel(depth)
    }
    fun bossLevel(depth: Int): Boolean {
        return depth == 5 || depth == 10 || depth == 15 || depth == 20 || depth == 25
    }
    @Suppress("DEPRECATION")
    fun switchLevel(level: Level?, pos: Int) {
        nightMode = Date().hours < 7
        Dungeon.level = level
        Actor.init()
        val currentLevel = level ?: return
        val respawner = currentLevel.respawner()
        if (respawner != null) {
            Actor.add(respawner)
        }
        val currentHero = hero ?: return
        currentHero.pos = if (pos != -1) pos else currentLevel.exit
        val light = currentHero.buff(Light::class.java)
        currentHero.viewDistance = light?.let { Math.max(Light.DISTANCE, currentLevel.viewDistance) } ?: currentLevel.viewDistance
        observe()
    }
    fun dropToChasm(item: Item) {
        val depth = Dungeon.depth + 1
        val items = droppedItems ?: return
        var dropped = items.get(depth)
        if (dropped == null) {
            dropped = ArrayList()
            items.put(depth, dropped)
        }
        dropped.add(item)
    }
    fun posNeeded(): Boolean {
        val quota = intArrayOf(4, 2, 9, 4, 14, 6, 19, 8, 24, 9)
        return chance(quota, potionOfStrength)
    }
    fun souNeeded(): Boolean {
        val quota = intArrayOf(5, 3, 10, 6, 15, 9, 20, 12, 25, 13)
        return chance(quota, scrollsOfUpgrade)
    }
    fun soeNeeded(): Boolean {
        return Random.Int(12 * (1 + scrollsOfEnchantment)) < depth
    }
    private fun chance(quota: IntArray, number: Int): Boolean {
        var i = 0
        while (i < quota.size) {
            val qDepth = quota[i]
            if (depth <= qDepth) {
                val qNumber = quota[i + 1]
                return Random.Float() < (qNumber - number).toFloat() / (qDepth - depth + 1)
            }
            i += 2
        }
        return false
    }
    private const val RG_GAME_FILE = "game.dat"
    private const val RG_DEPTH_FILE = "depth%d.dat"
    private const val WR_GAME_FILE = "warrior.dat"
    private const val WR_DEPTH_FILE = "warrior%d.dat"
    private const val MG_GAME_FILE = "mage.dat"
    private const val MG_DEPTH_FILE = "mage%d.dat"
    private const val RN_GAME_FILE = "ranger.dat"
    private const val RN_DEPTH_FILE = "ranger%d.dat"
    private const val VERSION = "version"
    private const val CHALLENGES = "challenges"
    private const val HERO = "hero"
    private const val GOLD = "gold"
    private const val DEPTH = "depth"
    private const val LEVEL = "level"
    private const val DROPPED = "dropped%d"
    private const val POS = "potionsOfStrength"
    private const val SOU = "scrollsOfEnhancement"
    private const val SOE = "scrollsOfEnchantment"
    private const val DV = "dewVial"
    private const val CHAPTERS = "chapters"
    private const val QUESTS = "quests"
    private const val BADGES = "badges"
    fun gameFile(cl: HeroClass): String {
        return when (cl) {
            HeroClass.WARRIOR -> WR_GAME_FILE
            HeroClass.MAGE -> MG_GAME_FILE
            HeroClass.HUNTRESS -> RN_GAME_FILE
            else -> RG_GAME_FILE
        }
    }
    private fun depthFile(cl: HeroClass): String {
        return when (cl) {
            HeroClass.WARRIOR -> WR_DEPTH_FILE
            HeroClass.MAGE -> MG_DEPTH_FILE
            HeroClass.HUNTRESS -> RN_DEPTH_FILE
            else -> RG_DEPTH_FILE
        }
    }
    @Throws(IOException::class)
    fun saveGame(fileName: String) {
        try {
            val bundle = Bundle()
            bundle.put(VERSION, Game.version ?: "")
            bundle.put(CHALLENGES, challenges)
            bundle.put(HERO, hero)
            bundle.put(GOLD, gold)
            bundle.put(DEPTH, depth)
            val items = droppedItems
            if (items != null) {
                for (d in items.keyArray()) {
                    val dropped = items.get(d)
                    if (dropped != null) {
                        bundle.put(String.format(DROPPED, d), dropped)
                    }
                }
            }
            bundle.put(POS, potionOfStrength)
            bundle.put(SOU, scrollsOfUpgrade)
            bundle.put(SOE, scrollsOfEnchantment)
            bundle.put(DV, dewVial)
            val currentChapters = chapters
            if (currentChapters != null) {
                var count = 0
                val ids = IntArray(currentChapters.size)
                for (id in currentChapters) {
                    ids[count++] = id
                }
                bundle.put(CHAPTERS, ids)
            }
            val quests = Bundle()
            Ghost.Quest.storeInBundle(quests)
            Wandmaker.Quest.storeInBundle(quests)
            Blacksmith.Quest.storeInBundle(quests)
            Imp.Quest.storeInBundle(quests)
            bundle.put(QUESTS, quests)
            Room.storeRoomsInBundle(bundle)
            Statistics.storeInBundle(bundle)
            Journal.storeInBundle(bundle)
            QuickSlot.save(bundle)
            Scroll.save(bundle)
            Potion.save(bundle)
            Wand.save(bundle)
            Ring.save(bundle)
            val badges = Bundle()
            Badges.saveLocal(badges)
            bundle.put(BADGES, badges)
            val output = Game.instance?.openFileOutput(fileName, android.content.Context.MODE_PRIVATE) ?: return
            Bundle.write(bundle, output)
            output.close()
        } catch (e: Exception) {
            val currentHero = hero
            if (currentHero != null) {
                GamesInProgress.setUnknown(currentHero.heroClass)
            }
        }
    }
    @Throws(IOException::class)
    fun saveLevel() {
        val bundle = Bundle()
        bundle.put(LEVEL, level)
        val currentHero = hero ?: return
        val output = Game.instance?.openFileOutput(Utils.format(depthFile(currentHero.heroClass), depth), android.content.Context.MODE_PRIVATE) ?: return
        Bundle.write(bundle, output)
        output.close()
    }
    @Throws(IOException::class)
    fun saveAll() {
        val currentHero = hero
        if (currentHero != null && currentHero.isAlive) {
            Actor.fixTime()
            saveGame(gameFile(currentHero.heroClass))
            saveLevel()
            GamesInProgress.set(currentHero.heroClass, depth, currentHero.lvl, challenges != 0)
        } else if (WndResurrect.instance != null) {
            WndResurrect.instance?.hide()
            Hero.reallyDie(WndResurrect.causeOfDeath)
        }
    }
    @Throws(IOException::class)
    fun loadGame(cl: HeroClass) {
        loadGame(gameFile(cl), true)
    }
    @Throws(IOException::class)
    fun loadGame(fileName: String) {
        loadGame(fileName, false)
    }
    @Throws(IOException::class)
    fun loadGame(fileName: String, fullLoad: Boolean) {
        val bundle = gameBundle(fileName) ?: return
        Dungeon.challenges = bundle.getInt(CHALLENGES)
        Dungeon.level = null
        Dungeon.depth = -1
        if (fullLoad) {
            PathFinder.setMapSize(Level.WIDTH, Level.HEIGHT)
        }
        Scroll.restore(bundle)
        Potion.restore(bundle)
        Wand.restore(bundle)
        Ring.restore(bundle)
        potionOfStrength = bundle.getInt(POS)
        scrollsOfUpgrade = bundle.getInt(SOU)
        scrollsOfEnchantment = bundle.getInt(SOE)
        dewVial = bundle.getBoolean(DV)
        if (fullLoad) {
            val newChapters = HashSet<Int>()
            chapters = newChapters
            val ids = bundle.getIntArray(CHAPTERS)
            if (ids != null) {
                for (id in ids) {
                    newChapters.add(id)
                }
            }
            val quests = bundle.getBundle(QUESTS)
            if (!quests.isNull()) {
                Ghost.Quest.restoreFromBundle(quests)
                Wandmaker.Quest.restoreFromBundle(quests)
                Blacksmith.Quest.restoreFromBundle(quests)
                Imp.Quest.restoreFromBundle(quests)
            } else {
                Ghost.Quest.reset()
                Wandmaker.Quest.reset()
                Blacksmith.Quest.reset()
                Imp.Quest.reset()
            }
            Room.restoreRoomsFromBundle(bundle)
        }
        val badges = bundle.getBundle(BADGES)
        if (!badges.isNull()) {
            Badges.loadLocal(badges)
        } else {
            Badges.reset()
        }
        QuickSlot.restore(bundle)
        @Suppress("UNUSED_VARIABLE")
        val version = bundle.getString(VERSION)
        hero = null
        hero = bundle.get(HERO) as Hero?
        QuickSlot.compress()
        gold = bundle.getInt(GOLD)
        depth = bundle.getInt(DEPTH)
        Statistics.restoreFromBundle(bundle)
        Journal.restoreFromBundle(bundle)
        val items = SparseArray<ArrayList<Item>>()
        droppedItems = items
        for (i in 2..Statistics.deepestFloor + 1) {
            val dropped = ArrayList<Item>()
            for (b in bundle.getCollection(String.format(DROPPED, i))) {
                dropped.add(b as Item)
            }
            if (!dropped.isEmpty()) {
                items.put(i, dropped)
            }
        }
    }
    @Throws(IOException::class)
    fun loadLevel(cl: HeroClass): Level {
        Dungeon.level = null
        Actor.clear()
        val instance = Game.instance ?: throw IOException("Game instance is null")
        val input = instance.openFileInput(Utils.format(depthFile(cl), depth))
        val bundle = Bundle.read(input)
        input.close()
        return (bundle?.get("level") as? Level) ?: throw IOException("Level not found in bundle")
    }
    fun deleteGame(cl: HeroClass, deleteLevels: Boolean) {
        val instance = Game.instance ?: return
        instance.deleteFile(gameFile(cl))
        if (deleteLevels) {
            var depth = 1
            while (instance.deleteFile(Utils.format(depthFile(cl), depth))) {
                depth++
            }
        }
        GamesInProgress.delete(cl)
    }
    @Throws(IOException::class)
    fun gameBundle(fileName: String): Bundle? {
        val instance = Game.instance ?: return null
        val input = instance.openFileInput(fileName)
        val bundle = Bundle.read(input)
        input.close()
        return bundle
    }
    fun preview(info: GamesInProgress.Info, bundle: Bundle) {
        info.depth = bundle.getInt(DEPTH)
        info.challenges = (bundle.getInt(CHALLENGES) != 0)
        if (info.depth == -1) {
            info.depth = bundle.getInt("maxDepth")    // FIXME
        }
        Hero.preview(info, bundle.getBundle(HERO))
    }
    fun fail(desc: String) {
        val currentHero = hero ?: run {
            resultDescription = desc
            return
        }
        resultDescription = com.watabou.pixeldungeon.llm.LlmTextEnhancer.generateDeathEpitaph(
            desc,
            currentHero.heroClass.title(),
            depth,
            currentHero.lvl,
            desc
        )
        if (currentHero.belongings.getItem(Ankh::class.java) == null) {
            Rankings.submit(false)
        }
    }
    fun win(desc: String) {
        val currentHero = hero ?: return
        currentHero.belongings.identify()
        if (challenges != 0) {
            Badges.validateChampion()
        }
        resultDescription = desc
        Rankings.submit(true)
    }
    fun observe() {
        val currentLevel = level ?: return
        val currentHero = hero ?: return
        currentLevel.updateFieldOfView(currentHero)
        System.arraycopy(Level.fieldOfView, 0, visible, 0, visible.size)
        BArray.or(currentLevel.visited, visible, currentLevel.visited)
        GameScene.afterObserve()
    }
    private val passable = BooleanArray(Level.LENGTH)
    fun findPath(ch: Char, from: Int, to: Int, pass: BooleanArray, visible: BooleanArray): Int {
        if (Level.adjacent(from, to)) {
            return if (Actor.findChar(to) == null && (pass[to] || Level.avoid[to])) to else -1
        }
        if (ch.flying || ch.buff(Amok::class.java) != null || ch.buff(Rage::class.java) != null) {
            BArray.or(pass, Level.avoid, passable)
        } else {
            System.arraycopy(pass, 0, passable, 0, Level.LENGTH)
        }
        for (actor in Actor.all()) {
            if (actor is Char) {
                val pos = actor.pos
                if (visible[pos]) {
                    passable[pos] = false
                }
            }
        }
        return PathFinder.getStep(from, to, passable)
    }
    fun flee(ch: Char, cur: Int, from: Int, pass: BooleanArray, visible: BooleanArray): Int {
        if (ch.flying) {
            BArray.or(pass, Level.avoid, passable)
        } else {
            System.arraycopy(pass, 0, passable, 0, Level.LENGTH)
        }
        for (actor in Actor.all()) {
            if (actor is Char) {
                val pos = actor.pos
                if (visible[pos]) {
                    passable[pos] = false
                }
            }
        }
        passable[cur] = true
        return PathFinder.getStepBack(cur, from, passable)
    }
}

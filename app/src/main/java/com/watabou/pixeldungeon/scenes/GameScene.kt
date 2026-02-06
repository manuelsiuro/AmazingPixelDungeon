package com.watabou.pixeldungeon.scenes
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.SkinnedBlock
import com.watabou.noosa.Visual
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.Emitter
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.FogOfWar
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.BannerSprites
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.EmoIcon
import com.watabou.pixeldungeon.effects.Flare
import com.watabou.pixeldungeon.effects.FloatingText
import com.watabou.pixeldungeon.effects.Ripple
import com.watabou.pixeldungeon.effects.SpellSprite
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.potions.Potion
import com.watabou.pixeldungeon.items.wands.WandOfBlink
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.RegularLevel
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.levels.features.Chasm
import com.watabou.pixeldungeon.plants.Plant
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.DiscardedItemSprite
import com.watabou.pixeldungeon.sprites.HeroSprite
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.PlantSprite
import com.watabou.pixeldungeon.ui.AttackIndicator
import com.watabou.pixeldungeon.ui.Banner
import com.watabou.pixeldungeon.ui.BusyIndicator
import com.watabou.pixeldungeon.ui.GameLog
import com.watabou.pixeldungeon.ui.HealthIndicator
import com.watabou.pixeldungeon.ui.QuickSlot
import com.watabou.pixeldungeon.ui.StatusPane
import com.watabou.pixeldungeon.ui.Toast
import com.watabou.pixeldungeon.ui.Toolbar
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndBag
import com.watabou.pixeldungeon.windows.WndGame
import com.watabou.pixeldungeon.windows.WndStory
import com.watabou.utils.Random
import java.io.IOException
import java.util.ArrayList
class GameScene : PixelScene() {
    private var water: SkinnedBlock? = null
    private var tiles: DungeonTilemap? = null
    private var fog: FogOfWar? = null
    private var hero: HeroSprite? = null
    private var log: GameLog? = null
    private var busy: BusyIndicator? = null
    private var terrain: Group? = null
    private var ripples: Group? = null
    private var plants: Group? = null
    private var heaps: Group? = null
    private var mobs: Group? = null
    private var emitters: Group? = null
    private var effects: Group? = null
    private var gases: Group? = null
    private var spells: Group? = null
    private var statuses: Group? = null
    private var emoicons: Group? = null
    private var toolbar: Toolbar? = null
    private var prompt: Toast? = null
    override fun create() {
        Music.play(Assets.TUNE, true)
        Music.volume(1f)
        // Ensure Dungeon.hero is not null, or handle it. Assuming it's initialized before GameScene.
        if (Dungeon.hero != null) {
            Dungeon.hero?.let { PixelDungeon.lastClass(it.heroClass.ordinal) }
        }
        super.create()
        Camera.main?.zoom(defaultZoom + PixelDungeon.zoom())
        scene = this
        terrain = Group()
        terrain?.let { add(it) }
        val level = Dungeon.level ?: return
        val waterTex = level.waterTex() ?: return
        water = SkinnedBlock(
            Level.WIDTH * DungeonTilemap.SIZE.toFloat(),
            Level.HEIGHT * DungeonTilemap.SIZE.toFloat(),
            waterTex
        )
        terrain?.add(water ?: return)
        ripples = Group()
        terrain?.add(ripples ?: return)
        tiles = DungeonTilemap()
        terrain?.add(tiles ?: return)
        level.addVisuals(this)
        plants = Group()
        plants?.let { add(it) }
        var size = level.plants.size()
        for (i in 0 until size) {
            addPlantSprite(level.plants.valueAt(i))
        }
        heaps = Group()
        heaps?.let { add(it) }
        size = level.heaps.size()
        for (i in 0 until size) {
            addHeapSprite(level.heaps.valueAt(i))
        }
        emitters = Group()
        effects = Group()
        emoicons = Group()
        mobs = Group()
        mobs?.let { add(it) }
        val hero = Dungeon.hero
        for (mob in level.mobs) {
            addMobSprite(mob)
            if (Statistics.amuletObtained && hero != null) {
                mob.beckon(hero.pos)
            }
        }
        emitters?.let { add(it) }
        effects?.let { add(it) }
        gases = Group()
        gases?.let { add(it) }
        for (blob in level.blobs.values) {
            blob.emitter = null
            addBlobSprite(blob)
        }
        fog = FogOfWar(Level.WIDTH, Level.HEIGHT)
        fog?.updateVisibility(Dungeon.visible, level.visited, level.mapped)
        fog?.let { add(it) }
        brightness(PixelDungeon.brightness())
        spells = Group()
        spells?.let { add(it) }
        statuses = Group()
        statuses?.let { add(it) }
        emoicons?.let { add(it) }
        this.hero = HeroSprite()
        val h = Dungeon.hero ?: return
        this.hero?.place(h.pos)
        this.hero?.updateArmor()
        this.hero?.let { mobs?.add(it) }
        add(HealthIndicator())
        tiles?.let { cellSelector = CellSelector(it) }
        cellSelector?.let { add(it) }
        val sb = StatusPane()
        sb.camera = PixelScene.uiCamera
        sb.setSize(PixelScene.uiCamera.width.toFloat(), 0f)
        add(sb)
        toolbar = Toolbar()
        val tb = toolbar ?: return
        tb.camera = PixelScene.uiCamera
        tb.setRect(0f, PixelScene.uiCamera.height - tb.height(), PixelScene.uiCamera.width.toFloat(), tb.height())
        add(tb)
        val attack = AttackIndicator()
        attack.camera = PixelScene.uiCamera
        attack.setPos(
            PixelScene.uiCamera.width - attack.width(),
            tb.top() - attack.height()
        )
        add(attack)
        log = GameLog()
        log?.camera = PixelScene.uiCamera
        log?.setRect(0f, tb.top(), attack.left(), 0f)
        log?.let { add(it) }
        busy = BusyIndicator()
        busy?.camera = PixelScene.uiCamera
        busy?.x = 1f
        busy?.y = sb.bottom() + 1
        busy?.let { add(it) }
        when (InterlevelScene.mode) {
            InterlevelScene.Mode.RESURRECT -> {
                WandOfBlink.appear(h, level.entrance)
                h.sprite?.let { Flare(8, 32f).color(0xFFFF66.toInt(), true).show(it, 2f) }
            }
            InterlevelScene.Mode.RETURN -> WandOfBlink.appear(h, h.pos)
            InterlevelScene.Mode.FALL -> Chasm.heroLand()
            InterlevelScene.Mode.DESCEND -> {
                when (Dungeon.depth) {
                    1 -> WndStory.showChapter(WndStory.ID_SEWERS)
                    6 -> WndStory.showChapter(WndStory.ID_PRISON)
                    11 -> WndStory.showChapter(WndStory.ID_CAVES)
                    16 -> WndStory.showChapter(WndStory.ID_METROPOLIS)
                    22 -> WndStory.showChapter(WndStory.ID_HALLS)
                }
                if (h.isAlive && Dungeon.depth != 22) {
                    Badges.validateNoKilling()
                }
            }
            else -> {}
        }
        val droppedItems = Dungeon.droppedItems ?: return
        val dropped = droppedItems[Dungeon.depth]
        if (dropped != null) {
            for (item in dropped) {
                val pos = level.randomRespawnCell()
                if (item is Potion) {
                    item.shatter(pos)
                } else if (item is Plant.Seed) {
                    level.plant(item, pos)
                } else {
                    level.drop(item, pos)
                }
            }
            droppedItems.remove(Dungeon.depth)
        }
        Camera.main?.target = this.hero
        if (InterlevelScene.mode != InterlevelScene.Mode.NONE) {
            if (Dungeon.depth < Statistics.deepestFloor) {
                GLog.h(TXT_WELCOME_BACK, Dungeon.depth)
            } else {
                GLog.h(TXT_WELCOME, Dungeon.depth)
                Sample.play(Assets.SND_DESCEND)
            }
            val regionName = when {
                Dungeon.depth <= 5 -> "Sewers"
                Dungeon.depth <= 10 -> "Prison"
                Dungeon.depth <= 15 -> "Caves"
                Dungeon.depth <= 20 -> "Dwarven Metropolis"
                else -> "Demon Halls"
            }
            val heroClass = Dungeon.hero?.className() ?: "adventurer"
            when (level.feeling) {
                Level.Feeling.CHASM -> GLog.w(LlmTextEnhancer.enhanceLevelFeeling("chasm", regionName, Dungeon.depth, heroClass, TXT_CHASM))
                Level.Feeling.WATER -> GLog.w(LlmTextEnhancer.enhanceLevelFeeling("water", regionName, Dungeon.depth, heroClass, TXT_WATER))
                Level.Feeling.GRASS -> GLog.w(LlmTextEnhancer.enhanceLevelFeeling("grass", regionName, Dungeon.depth, heroClass, TXT_GRASS))
                else -> {}
            }
            if (level is RegularLevel &&
                level.secretDoors > Random.IntRange(3, 4)
            ) {
                GLog.w(LlmTextEnhancer.enhanceLevelFeeling("secrets", regionName, Dungeon.depth, heroClass, TXT_SECRETS))
            }
            if (Dungeon.nightMode && !Dungeon.bossLevel()) {
                GLog.w(TXT_NIGHT_MODE)
            }
            InterlevelScene.mode = InterlevelScene.Mode.NONE
            fadeIn()
        }
    }
    override fun destroy() {
        scene = null
        Badges.saveGlobal()
        super.destroy()
    }
    @Synchronized
    override fun pause() {
        try {
            Dungeon.saveAll()
            Badges.saveGlobal()
        } catch (e: IOException) {
            //
        }
    }
    @Synchronized
    override fun update() {
        if (Dungeon.hero == null) {
            return
        }
        super.update()
        water?.offset(0f, -5 * Game.elapsed)
        Actor.process()
        val hero = Dungeon.hero ?: return
        if (hero.ready && !hero.paralysed) {
            log?.newLine()
        }
        cellSelector?.enabled = hero.ready
    }
    override fun onBackPressed() {
        if (!cancel()) {
            add(WndGame())
        }
    }
    override fun onMenuPressed() {
        val hero = Dungeon.hero ?: return
        if (hero.ready) {
            selectItem(null, WndBag.Mode.ALL, null)
        }
    }
    fun brightness(value: Boolean) {
        val bright = if (value) 1.5f else 1.0f
        // water and tiles are SkinnedBlock and DungeonTilemap (Visuals)
        // Accessing rm, gm, bm from Visual
        water?.bm = bright
        water?.gm = bright
        water?.rm = bright
        tiles?.bm = bright
        tiles?.gm = bright
        tiles?.rm = bright
        if (value) {
            fog?.am = +2f
            fog?.aa = -1f
        } else {
            fog?.am = +1f
            fog?.aa = 0f
        }
    }
    private fun addHeapSprite(heap: Heap) {
        // heaps is Group, recycle returns Visual? cast to ItemSprite
        val sprite = heaps?.recycle(ItemSprite::class.java) as? ItemSprite ?: return
        heap.sprite = sprite
        sprite.revive()
        sprite.link(heap)
        heaps?.add(sprite)
    }
    private fun addDiscardedSprite(heap: Heap) {
        val sprite = heaps?.recycle(DiscardedItemSprite::class.java) as? DiscardedItemSprite ?: return
        heap.sprite = sprite
        sprite.revive()
        sprite.link(heap)
        heaps?.add(sprite)
    }
    private fun addPlantSprite(plant: Plant) {
        val sprite = plants?.recycle(PlantSprite::class.java) as? PlantSprite ?: return
        plant.sprite = sprite
        sprite.reset(plant)
    }
    private fun addBlobSprite(gas: Blob) {
        if (gas.emitter == null) {
            gases?.add(BlobEmitter(gas))
        }
    }
    private fun addMobSprite(mob: Mob) {
        mob.sprite()?.let { sprite ->
            val visible = Dungeon.visible
            sprite.visible = visible[mob.pos]
            mobs?.add(sprite)
            sprite.link(mob)
        }
    }
    private fun prompt(text: String?) {
        prompt?.killAndErase()
        prompt = null
        if (!text.isNullOrEmpty()) {
            prompt = object : Toast(text) {
                override fun onClose() {
                    cancel()
                }
            }
            val p = prompt ?: return
            p.camera = PixelScene.uiCamera
            p.setPos((PixelScene.uiCamera.width - p.width()) / 2, PixelScene.uiCamera.height - 60f)
            add(p)
        }
    }
    private fun showBanner(banner: Banner) {
        banner.camera = uiCamera
        banner.x = PixelScene.align(uiCamera, (uiCamera.width - banner.width) / 2)
        banner.y = PixelScene.align(uiCamera, (uiCamera.height - banner.height) / 3)
        add(banner)
    }
    companion object {
        private const val TXT_WELCOME = "Welcome to the level %d of Pixel Dungeon!"
        private const val TXT_WELCOME_BACK = "Welcome back to the level %d of Pixel Dungeon!"
        private const val TXT_NIGHT_MODE = "Be cautious, since the dungeon is even more dangerous at night!"
        private const val TXT_CHASM = "Your steps echo across the dungeon."
        private const val TXT_WATER = "You hear the water splashing around you."
        private const val TXT_GRASS = "The smell of vegetation is thick in the air."
        private const val TXT_SECRETS = "The atmosphere hints that this floor hides many secrets."
        var scene: GameScene? = null
        // cellSelector needs to be static static? Java: private static CellSelector cellSelector;
        var cellSelector: CellSelector? = null
        fun add(plant: Plant) {
            scene?.addPlantSprite(plant)
        }
        fun add(gas: Blob) {
            Actor.add(gas)
            scene?.addBlobSprite(gas)
        }
        fun add(heap: Heap) {
            scene?.addHeapSprite(heap)
        }
        fun discard(heap: Heap) {
            scene?.addDiscardedSprite(heap)
        }
        fun add(mob: Mob) {
            val level = Dungeon.level ?: return
            level.mobs.add(mob)
            Actor.add(mob)
            Actor.occupyCell(mob)
            scene?.addMobSprite(mob)
        }
        fun add(mob: Mob, delay: Float) {
            val level = Dungeon.level ?: return
            level.mobs.add(mob)
            Actor.addDelayed(mob, delay)
            Actor.occupyCell(mob)
            scene?.addMobSprite(mob)
        }
        fun add(icon: EmoIcon) {
            scene?.emoicons?.add(icon)
        }
        fun effect(effect: Visual) {
            scene?.effects?.add(effect)
        }
        fun ripple(pos: Int): Ripple {
            // Ripples group
            val s = scene ?: throw IllegalStateException("Scene not available")
            val ripple = s.ripples?.recycle(Ripple::class.java) as? Ripple ?: throw IllegalStateException("Ripple not available")
            ripple.reset(pos)
            return ripple
        }
        fun spellSprite(): SpellSprite {
            val s = scene ?: throw IllegalStateException("Scene not available")
            return s.spells?.recycle(SpellSprite::class.java) as? SpellSprite ?: throw IllegalStateException("SpellSprite not available")
        }
        fun emitter(): Emitter? {
            val s = scene ?: return null
            val emitter = s.emitters?.recycle(Emitter::class.java) as? Emitter ?: return null
            emitter.revive()
            return emitter
        }
        fun status(): FloatingText? {
            return scene?.statuses?.recycle(FloatingText::class.java) as? FloatingText
        }
        fun pickUp(item: Item) {
            scene?.toolbar?.pickup(item)
        }
        fun updateMap() {
            scene?.tiles?.updated?.set(0, 0, Level.WIDTH, Level.HEIGHT)
        }
        fun updateMap(cell: Int) {
            scene?.tiles?.updated?.union(cell % Level.WIDTH, cell / Level.WIDTH)
        }
        fun discoverTile(pos: Int, oldValue: Int) {
            scene?.tiles?.discover(pos, oldValue)
        }
        fun show(wnd: Window) {
            cancelCellSelector()
            scene?.add(wnd)
        }
        fun afterObserve() {
            val s = scene ?: return
            val level = Dungeon.level ?: return
            val visible = Dungeon.visible
            s.fog?.updateVisibility(visible, level.visited, level.mapped)
            for (mob in level.mobs) {
                mob.sprite?.visible = visible[mob.pos]
            }
        }
        fun flash(color: Int) {
            scene?.fadeIn(0xFF000000.toInt() or color, true)
        }
        fun gameOver() {
            val s = scene ?: return
            val gameOver = Banner(BannerSprites.get(BannerSprites.Type.GAME_OVER))
            gameOver.show(0x000000, 1f)
            s.showBanner(gameOver)
            Sample.play(Assets.SND_DEATH)
        }
        fun bossSlain() {
            val hero = Dungeon.hero ?: return
            val s = scene ?: return
            if (hero.isAlive) {
                val bossSlain = Banner(BannerSprites.get(BannerSprites.Type.BOSS_SLAIN))
                bossSlain.show(0xFFFFFF, 0.3f, 5f)
                s.showBanner(bossSlain)
                Sample.play(Assets.SND_BOSS)
            }
        }
        fun handleCell(cell: Int) {
            cellSelector?.select(cell)
        }
        fun selectCell(listener: CellSelector.Listener) {
            val cs = cellSelector ?: return
            cs.listener = listener
            scene?.prompt(listener.prompt())
        }
        private fun cancelCellSelector(): Boolean {
            val cs = cellSelector ?: return false
            if (cs.listener != null && cs.listener !== defaultCellListener) {
                cs.cancel()
                return true
            } else {
                return false
            }
        }
        fun selectItem(listener: WndBag.Listener?, mode: WndBag.Mode, title: String?): WndBag {
            cancelCellSelector()
            val wnd = if (mode == WndBag.Mode.SEED)
                WndBag.seedPouch(listener, mode, title)
            else
                WndBag.lastBag(listener, mode, title)
            scene?.add(wnd)
            return wnd
        }
        fun cancel(): Boolean {
            val hero = Dungeon.hero ?: return cancelCellSelector()
            if (hero.curAction != null || hero.restoreHealth) {
                hero.curAction = null
                hero.restoreHealth = false
                return true
            } else {
                return cancelCellSelector()
            }
        }
        fun ready() {
            selectCell(defaultCellListener)
            QuickSlot.cancel()
        }
        private val defaultCellListener = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                val hero = Dungeon.hero ?: return
                val c = cell ?: return
                if (hero.handle(c)) {
                    hero.next()
                }
            }
            override fun prompt(): String {
                return ""
            }
        }
    }
}

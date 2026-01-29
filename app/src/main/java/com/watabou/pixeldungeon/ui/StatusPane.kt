package com.watabou.pixeldungeon.ui
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Image
import com.watabou.noosa.NinePatch
import com.watabou.noosa.TouchArea
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.BitmaskEmitter
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.ui.Button
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.particles.BloodParticle
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.HeroSprite
import com.watabou.pixeldungeon.windows.WndGame
import com.watabou.pixeldungeon.windows.WndHero
class StatusPane : Component() {
    private lateinit var shield: NinePatch
    private lateinit var avatar: Image
    private lateinit var blood: Emitter
    private var lastTier = 0
    private lateinit var hp: Image
    private lateinit var exp: Image
    private var lastLvl = -1
    private var lastKeys = -1
    private lateinit var level: BitmapText
    private lateinit var depth: BitmapText
    private lateinit var keys: BitmapText
    private lateinit var danger: DangerIndicator
    private lateinit var loot: LootIndicator
    private lateinit var resume: ResumeButton
    private lateinit var buffs: BuffIndicator
    private lateinit var compass: Compass
    private lateinit var btnMenu: MenuButton
    private var tagDanger = false
    private var tagLoot = false
    private var tagResume = false
    override fun createChildren() {
        shield = NinePatch(Assets.STATUS, 80, 0, 30 + 18, 0)
        add(shield)
        add(object : TouchArea(0f, 1f, 30f, 30f) {
            override fun onClick(touch: Touch) {
                // Ensure Dungeon.hero is reachable? Yes, static.
                // Assuming Dungeon.kt migrated
                val sprite = Dungeon.hero?.sprite
                if (sprite?.isVisible() == false) {
                    Camera.main?.focusOn(sprite)
                }
                GameScene.show(WndHero())
            }
        })
        btnMenu = MenuButton()
        add(btnMenu)
        val hero = Dungeon.hero ?: return
        avatar = HeroSprite.avatar(hero.heroClass, lastTier)
        add(avatar)
        blood = BitmaskEmitter(avatar)
        blood.pour(BloodParticle.FACTORY, 0.3f)
        blood.autoKill = false
        blood.on = false
        add(blood)
        val dungeonLevel = Dungeon.level ?: return
        compass = Compass(dungeonLevel.exit)
        add(compass)
        hp = Image(Assets.HP_BAR)
        add(hp)
        exp = Image(Assets.XP_BAR)
        add(exp)
        level = BitmapText(PixelScene.font1x)
        level.hardlight(0xFFEBA4.toInt()) // 0xFFEBA4 is unsigned, toInt?
        add(level)
        depth = BitmapText(Dungeon.depth.toString(), PixelScene.font1x)
        depth.hardlight(0xCACFC2.toInt())
        depth.measure()
        add(depth)
        hero.belongings.countIronKeys()
        keys = BitmapText(PixelScene.font1x)
        keys.hardlight(0xCACFC2.toInt())
        add(keys)
        danger = DangerIndicator()
        add(danger)
        loot = LootIndicator()
        add(loot)
        resume = ResumeButton()
        add(resume)
        buffs = BuffIndicator(hero)
        add(buffs)
    }
    override fun layout() {
        height = 32f
        shield.size(width, shield.height.toFloat())
        val cam = camera() ?: return
        avatar.x = PixelScene.align(cam, shield.x + 15 - avatar.width / 2)
        avatar.y = PixelScene.align(cam, shield.y + 16 - avatar.height / 2)
        compass.x = avatar.x + avatar.width / 2 - compass.origin.x
        compass.y = avatar.y + avatar.height / 2 - compass.origin.y
        hp.x = 30f
        hp.y = 3f
        depth.x = width - 24 - depth.width() - 18
        depth.y = 6f
        keys.y = 6f
        layoutTags()
        buffs.setPos(32f, 11f)
        btnMenu.setPos(width - btnMenu.width(), 1f)
    }
    private fun layoutTags() {
        var pos = 18f
        if (tagDanger) {
            danger.setPos(width - danger.width(), pos)
            pos = danger.bottom() + 1
        }
        if (tagLoot) {
            loot.setPos(width - loot.width(), pos)
            pos = loot.bottom() + 1
        }
        if (tagResume) {
            resume.setPos(width - resume.width(), pos)
        }
    }
    override fun update() {
        super.update()
        if (tagDanger != danger.visible || tagLoot != loot.visible || tagResume != resume.visible) {
            tagDanger = danger.visible
            tagLoot = loot.visible
            tagResume = resume.visible
            layoutTags()
        }
        val hero = Dungeon.hero ?: return
        val health = hero.HP.toFloat() / hero.HT
        if (health == 0f) {
            avatar.tint(0x000000, 0.6f)
            blood.on = false
        } else if (health < 0.25f) {
            avatar.tint(0xcc0000.toInt(), 0.4f)
            blood.on = true
        } else {
            avatar.resetColor()
            blood.on = false
        }
        hp.scale.x = health
        // exp depends on width / exp.width (float?)
        exp.scale.x = (width / exp.width) * hero.exp / hero.maxExp()
        if (hero.lvl != lastLvl) {
            if (lastLvl != -1) {
                // Emitter.class -> Emitter::class.java
                val emitter = recycle(Emitter::class.java) as Emitter
                emitter.revive()
                emitter.pos(27f, 27f)
                emitter.burst(Speck.factory(Speck.STAR), 12)
            }
            lastLvl = hero.lvl
            level.text(lastLvl.toString())
            level.measure()
            level.x = PixelScene.align(27.5f - level.width() / 2)
            level.y = PixelScene.align(28.0f - level.baseLine() / 2)
        }
        val k = IronKey.curDepthQuantity
        if (k != lastKeys) {
            lastKeys = k
            keys.text(lastKeys.toString())
            keys.measure()
            keys.x = width - 8 - keys.width() - 18
        }
        val tier = hero.tier()
        if (tier != lastTier) {
            lastTier = tier
            avatar.copy(HeroSprite.avatar(hero.heroClass, tier))
        }
    }
    private class MenuButton : Button {
        private lateinit var image: Image
        constructor() : super() {
            // Can't access image yet
        }
        override fun createChildren() {
            super.createChildren()
            image = Image(Assets.STATUS, 114, 3, 12, 11)
            add(image)
            // In Java constructor: width = image.width + 4. But image was null there?
            // Actually in Java: 
            /*
            public MenuButton() {
                super();
                // width = ... relies on image initialized? 
                // Wait, Java createChildren called from super constructor?
                // Yes, Component constructor calls createChildren().
            }
            */
            // In Kotlin, init block runs after super...
            // But createChildren is open, called from Component init.
            // Component calls createChildren().
            // So if I initialize image in createChildren, it works.
            width = image.width + 4
            height = image.height + 4
        }
        override fun layout() {
            super.layout()
            image.x = x + 2
            image.y = y + 2
        }
        override fun onTouchDown() {
            image.brightness(1.5f)
            Sample.play(Assets.SND_CLICK)
        }
        override fun onTouchUp() {
            image.resetColor()
        }
        override fun onClick() {
            GameScene.show(WndGame())
        }
    }
}

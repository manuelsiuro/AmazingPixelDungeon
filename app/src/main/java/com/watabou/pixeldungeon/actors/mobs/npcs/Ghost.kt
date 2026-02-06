package com.watabou.pixeldungeon.actors.mobs.npcs
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Challenges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.actors.buffs.Roots
import com.watabou.pixeldungeon.actors.mobs.CursePersonification
import com.watabou.pixeldungeon.actors.mobs.FetidRat
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.particles.ShadowParticle
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.armor.ClothArmor
import com.watabou.pixeldungeon.items.quest.DriedRose
import com.watabou.pixeldungeon.items.quest.RatSkull
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.items.weapon.missiles.MissileWeapon
import com.watabou.pixeldungeon.levels.SewerLevel
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.GhostSprite
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.windows.WndQuest
import com.watabou.pixeldungeon.windows.WndSadGhost
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
class Ghost : NPC() {
    init {
        name = "sad ghost"
        spriteClass = GhostSprite::class.java
        flying = true
        state = WANDERING
        Sample.load(Assets.SND_GHOST)
    }
    override fun defenseSkill(enemy: Char?): Int {
        return 1000
    }
    override fun defenseVerb(): String {
        return "evaded"
    }
    override fun speed(): Float {
        return 0.5f
    }
    override fun chooseEnemy(): Char? {
        return null
    }
    override fun damage(dmg: Int, src: Any?) {
    }
    override fun add(buff: Buff) {
    }
    override fun reset(): Boolean {
        return true
    }
    override fun interact() {
        val hero = Dungeon.hero ?: return
        sprite?.turnTo(pos, hero.pos)
        Sample.play(Assets.SND_GHOST)
        Quest.type.handler?.interact(this)
    }
    override fun description(): String {
        return "The ghost is barely visible. It looks like a shapeless spot of faint light with a sorrowful face."
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private val IMMUNITIES = hashSetOf<Class<*>>(Paralysis::class.java, Roots::class.java)
        fun replace(a: Mob, b: Mob) {
            val FADE_TIME = 0.5f
            a.destroy()
            val asprite = a.sprite
            if (asprite != null) {
                asprite.parent?.add(object : AlphaTweener(asprite, 0f, FADE_TIME) {
                    override fun onComplete() {
                        asprite.killAndErase()
                        parent?.erase(this)
                    }
                })
                b.sprite?.flipHorizontal = asprite.flipHorizontal
            } else {
                b.sprite?.flipHorizontal = false
            }
            b.pos = a.pos
            GameScene.add(b)
            val bsprite = b.sprite
            if (bsprite != null) {
                bsprite.alpha(0f)
                bsprite.parent?.add(AlphaTweener(bsprite, 1f, FADE_TIME))
            }
        }
    }
    object Quest {
        enum class Type(val handler: QuestHandler?) {
            ILLEGAL(null), ROSE(roseQuest), RAT(ratQuest), CURSE(curseQuest)
        }
        var type: Type = Type.ILLEGAL
        var spawned: Boolean = false
        var given: Boolean = false
        var processed: Boolean = false
        private var depth: Int = 0
        private var left2kill: Int = 0
        var weapon: Weapon? = null
        var armor: Armor? = null
        fun reset() {
            spawned = false
            weapon = null
            armor = null
        }
        private const val NODE = "sadGhost"
        private const val SPAWNED = "spawned"
        private const val TYPE_KEY = "type"
        private const val ALTERNATIVE = "alternative"
        private const val LEFT2KILL = "left2kill"
        private const val GIVEN = "given"
        private const val PROCESSED = "processed"
        private const val DEPTH = "depth"
        private const val WEAPON = "weapon"
        private const val ARMOR = "armor"
        fun storeInBundle(bundle: Bundle) {
            val node = Bundle()
            node.put(SPAWNED, spawned)
            if (spawned) {
                node.put(TYPE_KEY, type.toString())
                if (type == Type.ROSE) {
                    node.put(LEFT2KILL, left2kill)
                }
                node.put(GIVEN, given)
                node.put(DEPTH, depth)
                node.put(PROCESSED, processed)
                node.put(WEAPON, weapon)
                node.put(ARMOR, armor)
            }
            bundle.put(NODE, node)
        }
        fun restoreFromBundle(bundle: Bundle) {
            val node = bundle.getBundle(NODE)
            if (!node.isNull() && node.getBoolean(SPAWNED).also { spawned = it }) {
                type = node.getEnum(TYPE_KEY, Type::class.java)
                if (type == Type.ILLEGAL) {
                    type = if (node.getBoolean(ALTERNATIVE)) Type.RAT else Type.ROSE
                }
                if (type == Type.ROSE) {
                    left2kill = node.getInt(LEFT2KILL)
                }
                given = node.getBoolean(GIVEN)
                depth = node.getInt(DEPTH)
                processed = node.getBoolean(PROCESSED)
                weapon = node.get(WEAPON) as? Weapon
                armor = node.get(ARMOR) as? Armor
            } else {
                reset()
            }
        }
        fun spawn(level: SewerLevel) {
            if (!spawned && Dungeon.depth > 1 && Random.Int(5 - Dungeon.depth) == 0) {
                val ghost = Ghost()
                do {
                    ghost.pos = level.randomRespawnCell()
                } while (ghost.pos == -1)
                level.mobs.add(ghost)
                Actor.occupyCell(ghost)
                spawned = true
                when (Random.Int(3)) {
                    0 -> {
                        type = Type.ROSE
                        left2kill = 8
                    }
                    1 -> type = Type.RAT
                    2 -> type = Type.CURSE
                }
                given = false
                processed = false
                depth = Dungeon.depth
                for (i in 0 until 4) {
                    var another: Item?
                    do {
                        another = Generator.random(Generator.Category.WEAPON)
                    } while (another == null || another is MissileWeapon)
                    val currentWeapon = weapon
                    if (currentWeapon == null || another.level() > currentWeapon.level()) {
                        weapon = another as Weapon
                    }
                }
                if (Dungeon.isChallenged(Challenges.NO_ARMOR)) {
                    armor = ClothArmor().degrade() as Armor
                } else {
                    armor = Generator.random(Generator.Category.ARMOR) as? Armor
                    for (i in 0 until 3) {
                        val another = Generator.random(Generator.Category.ARMOR) ?: continue
                        val currentArmor = armor
                        if (currentArmor == null || another.level() > currentArmor.level()) {
                            armor = another as Armor
                        }
                    }
                }
                weapon?.identify()
                armor?.identify()
            }
        }
        fun processSewersKill(pos: Int) {
            if (spawned && given && !processed && depth == Dungeon.depth) {
                when (type) {
                    Type.ROSE -> if (Random.Int(left2kill) == 0) {
                        Dungeon.level?.drop(DriedRose(), pos)?.sprite?.drop()
                        processed = true
                    } else {
                        left2kill--
                    }
                    Type.RAT -> {
                        val level = Dungeon.level ?: return
                        val rat = FetidRat()
                        rat.pos = level.randomRespawnCell()
                        if (rat.pos != -1) {
                            GameScene.add(rat)
                            processed = true
                        }
                    }
                    else -> {}
                }
            }
        }
        fun complete() {
            weapon = null
            armor = null
            Journal.remove(Journal.Feature.GHOST)
        }
    }
    abstract class QuestHandler {
        abstract fun interact(ghost: Ghost)
        protected fun relocate(ghost: Ghost) {
            var newPos = -1
            for (i in 0 until 10) {
                newPos = Dungeon.level?.randomRespawnCell() ?: -1
                if (newPos != -1) {
                    break
                }
            }
            if (newPos != -1) {
                Actor.freeCell(ghost.pos)
                CellEmitter.get(ghost.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3)
                ghost.pos = newPos
                ghost.sprite?.place(ghost.pos)
                ghost.sprite?.visible = Dungeon.visible[ghost.pos]
            }
        }
    }
}
private val roseQuest = object : Ghost.QuestHandler() {
    private val TXT_ROSE1 = "Hello adventurer... Once I was like you - strong and confident... And now I'm dead... But I can't leave this place... Not until I have my _dried rose_... It's very important to me... Some monster stole it from my body..."
    private val TXT_ROSE2 = "Please... Help me... _Find the rose_..."
    private val TXT_ROSE3 = "Yes! Yes!!! This is it! Please give it to me! And you can take one of these items, maybe they will be useful to you in your journey..."
    override fun interact(ghost: Ghost) {
        val heroClass = Dungeon.hero?.className() ?: "adventurer"
        val depth = Dungeon.depth
        if (Ghost.Quest.given) {
            val item = Dungeon.hero?.belongings?.getItem(DriedRose::class.java)
            if (item != null) {
                val text = LlmTextEnhancer.enhanceNpcDialog("sad ghost", "rose_complete", heroClass, depth, TXT_ROSE3)
                GameScene.show(WndSadGhost(ghost, item, text))
            } else {
                val text = LlmTextEnhancer.enhanceNpcDialog("sad ghost", "rose_reminder", heroClass, depth, TXT_ROSE2)
                GameScene.show(WndQuest(ghost, text))
                relocate(ghost)
            }
        } else {
            val text = LlmTextEnhancer.enhanceNpcDialog("sad ghost", "rose_initial", heroClass, depth, TXT_ROSE1)
            GameScene.show(WndQuest(ghost, text))
            Ghost.Quest.given = true
            Journal.add(Journal.Feature.GHOST)
        }
    }
}
private val ratQuest = object : Ghost.QuestHandler() {
    private val TXT_RAT1 = "Hello adventurer... Once I was like you - strong and confident... And now I'm dead... But I can't leave this place... Not until I have my revenge... Slay the _fetid rat_, that has taken my life..."
    private val TXT_RAT2 = "Please... Help me... _Slay the abomination_..."
    private val TXT_RAT3 = "Yes! The ugly creature is slain and I can finally rest... Please take one of these items, maybe they will be useful to you in your journey..."
    override fun interact(ghost: Ghost) {
        val heroClass = Dungeon.hero?.className() ?: "adventurer"
        val depth = Dungeon.depth
        if (Ghost.Quest.given) {
            val item = Dungeon.hero?.belongings?.getItem(RatSkull::class.java)
            if (item != null) {
                val text = LlmTextEnhancer.enhanceNpcDialog("sad ghost", "rat_complete", heroClass, depth, TXT_RAT3)
                GameScene.show(WndSadGhost(ghost, item, text))
            } else {
                val text = LlmTextEnhancer.enhanceNpcDialog("sad ghost", "rat_reminder", heroClass, depth, TXT_RAT2)
                GameScene.show(WndQuest(ghost, text))
                relocate(ghost)
            }
        } else {
            val text = LlmTextEnhancer.enhanceNpcDialog("sad ghost", "rat_initial", heroClass, depth, TXT_RAT1)
            GameScene.show(WndQuest(ghost, text))
            Ghost.Quest.given = true
            Journal.add(Journal.Feature.GHOST)
        }
    }
}
private val curseQuest = object : Ghost.QuestHandler() {
    private val TXT_CURSE1 = "Hello adventurer... Once I was like you - strong and confident... And now I'm dead... But I can't leave this place, as I am bound by a horrid curse... Please... Help me... _Destroy the curse_..."
    private val TXT_CURSE2 = "Thank you, %s! The curse is broken and I can finally rest... Please take one of these items, maybe they will be useful to you in your journey..."
    private val TXT_YES = "Yes, I will do it for you"
    private val TXT_NO = "No, I can't help you"
    override fun interact(ghost: Ghost) {
        val hero = Dungeon.hero ?: return
        val heroClass = hero.className()
        val depth = Dungeon.depth
        if (Ghost.Quest.given) {
            val baseText = Utils.format(TXT_CURSE2, heroClass)
            val text = LlmTextEnhancer.enhanceNpcDialog("sad ghost", "curse_complete", heroClass, depth, baseText)
            GameScene.show(WndSadGhost(ghost, null, text))
        } else {
            val text = LlmTextEnhancer.enhanceNpcDialog("sad ghost", "curse_initial", heroClass, depth, TXT_CURSE1)
            GameScene.show(object : WndQuest(ghost, text, TXT_YES, TXT_NO) {
                override fun onSelect(index: Int) {
                    if (index == 0) {
                        Ghost.Quest.given = true
                        val d = CursePersonification()
                        Ghost.replace(ghost, d)
                        d.sprite?.emitter()?.burst(ShadowParticle.CURSE, 5)
                        Sample.play(Assets.SND_GHOST)
                        Dungeon.hero?.next()
                    } else {
                        relocate(ghost)
                    }
                }
            })
            Journal.add(Journal.Feature.GHOST)
        }
    }
}

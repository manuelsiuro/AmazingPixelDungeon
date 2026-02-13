package com.watabou.pixeldungeon.actors.hero
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Bones
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.GamesInProgress
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.*
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.actors.mobs.npcs.NPC
import com.watabou.pixeldungeon.effects.CheckedCell
import com.watabou.pixeldungeon.effects.Flare
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Amulet
import com.watabou.pixeldungeon.items.Ankh
import com.watabou.pixeldungeon.items.DewVial
import com.watabou.pixeldungeon.items.Dewdrop
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Heap.Type
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.KindOfWeapon
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.keys.GoldenKey
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.items.keys.Key
import com.watabou.pixeldungeon.items.keys.SkeletonKey
import com.watabou.pixeldungeon.items.potions.Potion
import com.watabou.pixeldungeon.items.potions.PotionOfMight
import com.watabou.pixeldungeon.items.potions.PotionOfStrength
import com.watabou.pixeldungeon.items.rings.*
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.items.scrolls.ScrollOfEnchantment
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.watabou.pixeldungeon.items.scrolls.ScrollOfRecharging
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.pixeldungeon.items.wands.Wand
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.items.weapon.missiles.MissileWeapon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.levels.features.AlchemyPot
import com.watabou.pixeldungeon.levels.features.Chasm
import com.watabou.pixeldungeon.levels.features.Sign
import com.watabou.pixeldungeon.plants.Earthroot
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.InterlevelScene
import com.watabou.pixeldungeon.scenes.SurfaceScene
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.HeroSprite
import com.watabou.pixeldungeon.ui.AttackIndicator
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndMessage
import com.watabou.pixeldungeon.windows.WndResurrect
import com.watabou.pixeldungeon.windows.WndTradeItem
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
class Hero : Char() {
    var heroClass = HeroClass.ROGUE
    var subClass = HeroSubClass.NONE
    private var attackSkill = 10
    private var defenseSkill = 5
    var ready = false
    var curAction: HeroAction? = null
    var lastAction: HeroAction? = null
    private var enemy: Char? = null
    var killerGlyph: Armor.Glyph? = null
    private var theKey: Item? = null
    var restoreHealth = false
    var rangedWeapon: MissileWeapon? = null
    var belongings: Belongings
    var STR: Int = 0
    var weakened = false
    var awareness: Float = 0.toFloat()
    var lvl = 1
    var exp = 0
    private var visibleEnemies: ArrayList<Mob>
    init {
        name = "you"
        HT = 20
        HP = 20
        STR = STARTING_STR
        awareness = 0.1f
        belongings = Belongings(this)
        visibleEnemies = ArrayList()
    }
    fun STR(): Int {
        return if (weakened) STR - 2 else STR
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        heroClass.storeInBundle(bundle)
        subClass.storeInBundle(bundle)
        bundle.put(ATTACK, attackSkill)
        bundle.put(DEFENSE, defenseSkill)
        bundle.put(STRENGTH, STR)
        bundle.put(LEVEL, lvl)
        bundle.put(EXPERIENCE, exp)
        belongings.storeInBundle(bundle)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        heroClass = HeroClass.restoreInBundle(bundle)
        subClass = HeroSubClass.restoreInBundle(bundle)
        attackSkill = bundle.getInt(ATTACK)
        defenseSkill = bundle.getInt(DEFENSE)
        STR = bundle.getInt(STRENGTH)
        updateAwareness()
        lvl = bundle.getInt(LEVEL)
        exp = bundle.getInt(EXPERIENCE)
        belongings.restoreFromBundle(bundle)
    }
    fun className(): String {
        return if (subClass === HeroSubClass.NONE) heroClass.title() else subClass.title() ?: heroClass.title()
    }
    fun live() {
        Buffs.affect(this, Regeneration::class.java)
        Buffs.affect(this, Hunger::class.java)
    }
    fun tier(): Int {
        return belongings.armor?.tier ?: 0
    }
    fun shoot(enemy: Char, wep: MissileWeapon): Boolean {
        rangedWeapon = wep
        val result = attack(enemy)
        rangedWeapon = null
        return result
    }
    override fun attackSkill(target: Char?): Int {
        var bonus = 0
        for (buff in buffs(RingOfAccuracy.Accuracy::class.java)) {
            bonus += buff.level
        }
        var accuracy = if (bonus == 0) 1f else 1.4f.pow(bonus.toFloat())
        if (rangedWeapon != null && target != null && Level.distance(pos, target.pos) == 1) {
            accuracy *= 0.5f
        }
        val wep = if (rangedWeapon != null) rangedWeapon else belongings.weapon
        return if (wep != null) {
            (attackSkill * accuracy * wep.acuracyFactor(this)).toInt()
        } else {
            (attackSkill * accuracy).toInt()
        }
    }
    override fun defenseSkill(enemy: Char?): Int {
        var bonus = 0
        for (buff in buffs(RingOfEvasion.Evasion::class.java)) {
            bonus += buff.level
        }
        var evasion = if (bonus == 0) 1f else 1.2f.pow(bonus.toFloat())
        if (paralysed) {
            evasion /= 2f
        }
        val armor = belongings.armor
        val aEnc = if (armor != null) armor.STR - STR() else 0
        return if (aEnc > 0) {
            (defenseSkill * evasion / 1.5.pow(aEnc.toDouble())).toInt()
        } else {
            if (heroClass === HeroClass.ROGUE) {
                if (curAction != null && subClass === HeroSubClass.FREERUNNER && !isStarving) {
                    evasion *= 2f
                }
                ((defenseSkill - aEnc) * evasion).toInt()
            } else {
                (defenseSkill * evasion).toInt()
            }
        }
    }
    override fun dr(): Int {
        var dr = belongings.armor?.let { max(it.DR(), 0) } ?: 0
        val barkskin = buff(Barkskin::class.java)
        if (barkskin != null) {
            dr += barkskin.level()
        }
        return dr
    }
    override fun damageRoll(): Int {
        val wep = if (rangedWeapon != null) rangedWeapon else belongings.weapon
        val dmg: Int
        dmg = if (wep != null) {
            wep.damageRoll(this)
        } else {
            if (STR() > 10) Random.IntRange(1, STR() - 9) else 1
        }
        return if (buff(Fury::class.java) != null) (dmg * 1.5f).toInt() else dmg
    }
    override fun speed(): Float {
        val aEnc = belongings.armor?.let { it.STR - STR() } ?: 0
        return if (aEnc > 0) {
            (super.speed() * 1.3.pow((-aEnc).toDouble())).toFloat()
        } else {
            val speed = super.speed()
            if ((sprite as HeroSprite).sprint(subClass === HeroSubClass.FREERUNNER && !isStarving)) 1.6f * speed else speed
        }
    }
    fun attackDelay(): Float {
        val wep = if (rangedWeapon != null) rangedWeapon else belongings.weapon
        return if (wep != null) {
            wep.speedFactor(this)
        } else {
            1f
        }
    }
    override fun spend(time: Float) {
        var hasteLevel = 0
        for (buff in buffs(RingOfHaste.Haste::class.java)) {
            hasteLevel += buff.level
        }
        super.spend(if (hasteLevel == 0) time else (time * 1.1.pow((-hasteLevel).toDouble())).toFloat())
    }
    fun spendAndNext(time: Float) {
        busy()
        spend(time)
        next()
    }
    override fun act(): Boolean {
        super.act()
        if (paralysed) {
            curAction = null
            spendAndNext(TICK)
            return false
        }
        checkVisibleMobs()
        AttackIndicator.updateState()
        val action = curAction
        if (action == null) {
            if (restoreHealth) {
                if (isStarving || HP >= HT) {
                    restoreHealth = false
                } else {
                    spend(TIME_TO_REST)
                    next()
                    return false
                }
            }
            ready()
            return false
        } else {
            restoreHealth = false
            ready = false
            when (action) {
                is HeroAction.Move -> return actMove(action)
                is HeroAction.Interact -> return actInteract(action)
                is HeroAction.Buy -> return actBuy(action)
                is HeroAction.PickUp -> return actPickUp(action)
                is HeroAction.OpenChest -> return actOpenChest(action)
                is HeroAction.Unlock -> return actUnlock(action)
                is HeroAction.Descend -> return actDescend(action)
                is HeroAction.Ascend -> return actAscend(action)
                is HeroAction.Attack -> return actAttack(action)
                is HeroAction.Cook -> return actCook(action)
                is HeroAction.UseStation -> return actUseStation(action)
            }
        }
        return false
    }
    fun busy() {
        ready = false
    }
    private fun ready() {
        sprite?.idle()
        curAction = null
        ready = true
        GameScene.ready()
    }
    fun interrupt() {
        val action = curAction
        if (isAlive && action != null && action.dst != pos) {
            lastAction = action
        }
        curAction = null
    }
    fun resume() {
        curAction = lastAction
        lastAction = null
        act()
    }
    private fun actMove(action: HeroAction.Move): Boolean {
        if (getCloser(action.dst)) {
            return true
        } else {
            val currentLevel = Dungeon.level
            if (currentLevel?.map?.get(pos) == Terrain.SIGN) {
                Sign.read(pos)
            }
            ready()
            return false
        }
    }
    private fun actInteract(action: HeroAction.Interact): Boolean {
        val npc = action.npc
        if (Level.adjacent(pos, npc.pos)) {
            ready()
            sprite?.turnTo(pos, npc.pos)
            npc.interact()
            return false
        } else {
            if (Level.fieldOfView[npc.pos] && getCloser(npc.pos)) {
                return true
            } else {
                ready()
                return false
            }
        }
    }
    private fun actBuy(action: HeroAction.Buy): Boolean {
        val dst = action.dst
        if (pos == dst || Level.adjacent(pos, dst)) {
            ready()
            val heap = Dungeon.level?.heaps?.get(dst)
            if (heap != null && heap.type === Type.FOR_SALE && heap.size() == 1) {
                GameScene.show(WndTradeItem(heap, true))
            }
            return false
        } else if (getCloser(dst)) {
            return true
        } else {
            ready()
            return false
        }
    }
    private fun actCook(action: HeroAction.Cook): Boolean {
        val dst = action.dst
        if (Dungeon.visible[dst]) {
            ready()
            AlchemyPot.operate(this, dst)
            return false
        } else if (getCloser(dst)) {
            return true
        } else {
            ready()
            return false
        }
    }
    private fun actUseStation(action: HeroAction.UseStation): Boolean {
        val dst = action.dst
        if (Dungeon.visible[dst]) {
            ready()
            val currentLevel = Dungeon.level ?: return false
            when (currentLevel.map[dst]) {
                Terrain.CRAFTING_TABLE -> GameScene.show(
                    com.watabou.pixeldungeon.windows.WndCrafting(
                        this, com.watabou.pixeldungeon.crafting.StationType.CRAFTING_TABLE
                    )
                )
                Terrain.FURNACE -> GameScene.show(
                    com.watabou.pixeldungeon.windows.WndFurnace(this)
                )
            }
            return false
        } else if (getCloser(dst)) {
            return true
        } else {
            ready()
            return false
        }
    }
    private fun actPickUp(action: HeroAction.PickUp): Boolean {
        val dst = action.dst
        val currentLevel = Dungeon.level
        if (pos == dst) {
            val heap = currentLevel?.heaps?.get(pos)
            if (heap != null) {
                val item = heap.pickUp()
                if (item.doPickUp(this)) {
                    if (item !is Dewdrop) {
                        val important = (item is Scroll && item.isKnown) ||
                                (item is Potion && item.isKnown)
                        if (important) {
                            GLog.p(TXT_YOU_NOW_HAVE, item.name())
                        } else {
                            GLog.i(TXT_YOU_NOW_HAVE, item.name())
                        }
                    }
                    if (!heap.isEmpty) {
                        GLog.i(TXT_SOMETHING_ELSE)
                    }
                    curAction = null
                } else {
                    currentLevel.drop(item, pos).sprite?.drop()
                    ready()
                }
            } else {
                ready()
            }
            return false
        } else if (getCloser(dst)) {
            return true
        } else {
            ready()
            return false
        }
    }
    private fun actOpenChest(action: HeroAction.OpenChest): Boolean {
        val dst = action.dst
        if (Level.adjacent(pos, dst) || pos == dst) {
            val heap = Dungeon.level?.heaps?.get(dst)
            if (heap != null && heap.type !== Type.HEAP && heap.type !== Type.FOR_SALE) {
                theKey = null
                if (heap.type === Type.LOCKED_CHEST || heap.type === Type.CRYSTAL_CHEST) {
                    theKey = belongings.getKey(GoldenKey::class.java, Dungeon.depth)
                    if (theKey == null) {
                        GLog.w(TXT_LOCKED_CHEST)
                        ready()
                        return false
                    }
                }
                when (heap.type) {
                    Type.TOMB -> {
                        Sample.play(Assets.SND_TOMB)
                        Camera.main?.shake(1f, 0.5f)
                    }
                    Type.SKELETON -> {
                    }
                    else -> Sample.play(Assets.SND_UNLOCK)
                }
                spend(Key.TIME_TO_UNLOCK)
                sprite?.operate(dst)
            } else {
                ready()
            }
            return false
        } else if (getCloser(dst)) {
            return true
        } else {
            ready()
            return false
        }
    }
    private fun actUnlock(action: HeroAction.Unlock): Boolean {
        val doorCell = action.dst
        if (Level.adjacent(pos, doorCell)) {
            theKey = null
            val currentLevel = Dungeon.level ?: return false
            val door = currentLevel.map[doorCell]
            if (door == Terrain.LOCKED_DOOR) {
                theKey = belongings.getKey(IronKey::class.java, Dungeon.depth)
            } else if (door == Terrain.LOCKED_EXIT) {
                theKey = belongings.getKey(SkeletonKey::class.java, Dungeon.depth)
            }
            if (theKey != null) {
                spend(Key.TIME_TO_UNLOCK)
                sprite?.operate(doorCell)
                Sample.play(Assets.SND_UNLOCK)
            } else {
                GLog.w(TXT_LOCKED_DOOR)
                ready()
            }
            return false
        } else if (getCloser(doorCell)) {
            return true
        } else {
            ready()
            return false
        }
    }
    private fun actDescend(action: HeroAction.Descend): Boolean {
        val stairs = action.dst
        val currentLevel = Dungeon.level ?: return false
        if (pos == stairs && pos == currentLevel.exit) {
            curAction = null
            val hunger = buff(Hunger::class.java)
            if (hunger != null && !hunger.isStarving) {
                hunger.satisfy(-Hunger.STARVING / 10f)
            }
            InterlevelScene.mode = InterlevelScene.Mode.DESCEND
            Game.switchScene(InterlevelScene::class.java)
            return false
        } else if (getCloser(stairs)) {
            return true
        } else {
            ready()
            return false
        }
    }
    private fun actAscend(action: HeroAction.Ascend): Boolean {
        val stairs = action.dst
        val currentLevel = Dungeon.level ?: return false
        if (pos == stairs && pos == currentLevel.entrance) {
            if (Dungeon.depth == 0) {
                // At village entrance - can't go further
                GameScene.show(WndMessage(TXT_LEAVE))
                ready()
            } else if (Dungeon.depth == 1 && belongings.getItem(Amulet::class.java) != null) {
                // Win: ascending from dungeon with the Amulet
                Dungeon.win(ResultDescriptions.WIN)
                Dungeon.hero?.let { Dungeon.deleteGame(it.heroClass, true) }
                Game.switchScene(SurfaceScene::class.java)
            } else {
                curAction = null
                val hunger = buff(Hunger::class.java)
                if (hunger != null && !hunger.isStarving) {
                    hunger.satisfy(-Hunger.STARVING / 10f)
                }
                InterlevelScene.mode = InterlevelScene.Mode.ASCEND
                Game.switchScene(InterlevelScene::class.java)
            }
            return false
        } else if (getCloser(stairs)) {
            return true
        } else {
            ready()
            return false
        }
    }
    private fun actAttack(action: HeroAction.Attack): Boolean {
        enemy = action.target
        val currentEnemy = enemy ?: return false
        if (Level.adjacent(pos, currentEnemy.pos) && currentEnemy.isAlive && !isCharmedBy(currentEnemy)) {
            spend(attackDelay())
            sprite?.attack(currentEnemy.pos)
            return false
        } else {
            if (Level.fieldOfView[currentEnemy.pos] && getCloser(currentEnemy.pos)) {
                return true
            } else {
                ready()
                return false
            }
        }
    }
    fun rest(tillHealthy: Boolean) {
        spendAndNext(TIME_TO_REST)
        if (!tillHealthy) {
            sprite?.showStatus(CharSprite.DEFAULT, TXT_WAIT)
        }
        restoreHealth = tillHealthy
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        var effectiveDamage = damage
        val wep = if (rangedWeapon != null) rangedWeapon else belongings.weapon
        if (wep != null) {
            wep.proc(this, enemy, effectiveDamage)
            when (subClass) {
                HeroSubClass.GLADIATOR -> if (wep is MeleeWeapon) {
                    effectiveDamage += Buffs.affect(this, Combo::class.java)?.hit(enemy, effectiveDamage) ?: 0
                }
                HeroSubClass.BATTLEMAGE -> if (wep is Wand) {
                    if (wep.curCharges >= wep.maxCharges) {
                        wep.use()
                    } else if (effectiveDamage > 0) {
                        wep.curCharges++
                        wep.updateQuickslot()
                        ScrollOfRecharging.charge(this)
                    }
                    effectiveDamage += wep.curCharges
                }
                HeroSubClass.SNIPER -> if (rangedWeapon != null) {
                    Buffs.prolong(this, SnipersMark::class.java, attackDelay() * 1.1f)?.`object` = enemy.id()
                }
                else -> {
                }
            }
        }
        return effectiveDamage
    }
    override fun defenseProc(enemy: Char, damage: Int): Int {
        var effectiveDamage = damage
        val thorns = buff(RingOfThorns.Thorns::class.java)
        if (thorns != null) {
            val dmg = Random.IntRange(0, effectiveDamage)
            if (dmg > 0) {
                enemy.damage(dmg, thorns)
            }
        }
        val armorBuff = buff(Earthroot.Armor::class.java)
        if (armorBuff != null) {
            effectiveDamage = armorBuff.absorb(effectiveDamage)
        }
        belongings.armor?.let {
            effectiveDamage = it.proc(enemy, this, effectiveDamage)
        }
        return effectiveDamage
    }
    override fun damage(dmg: Int, src: Any?) {
        restoreHealth = false
        super.damage(dmg, src)
        if (subClass === HeroSubClass.BERSERKER && 0 < HP && HP <= HT * Fury.LEVEL) {
            Buffs.affect(this, Fury::class.java)
        }
    }
    private fun checkVisibleMobs() {
        val visible = ArrayList<Mob>()
        var newMob = false
        val currentLevel = Dungeon.level ?: return
        for (m in currentLevel.mobs) {
            if (Level.fieldOfView[m.pos] && m.hostile) {
                visible.add(m)
                if (!visibleEnemies.contains(m)) {
                    newMob = true
                }
            }
        }
        if (newMob) {
            interrupt()
            restoreHealth = false
        }
        visibleEnemies = visible
    }
    fun visibleEnemies(): Int {
        return visibleEnemies.size
    }
    fun visibleEnemy(index: Int): Mob {
        return visibleEnemies[index % visibleEnemies.size]
    }
    private fun getCloser(target: Int): Boolean {
        if (rooted) {
            Camera.main?.shake(1f, 1f)
            return false
        }
        var step = -1
        if (Level.adjacent(pos, target)) {
            if (Actor.findChar(target) == null) {
                if (Level.pit[target] && !flying && !Chasm.jumpConfirmed) {
                    Chasm.heroJump(this)
                    interrupt()
                    return false
                }
                if (Level.passable[target] || Level.avoid[target]) {
                    step = target
                }
            }
        } else {
            val len = Level.LENGTH
            val p = Level.passable
            val currentLevel = Dungeon.level ?: return false
            val v = currentLevel.visited
            val m = currentLevel.mapped
            val passable = BooleanArray(len)
            for (i in 0 until len) {
                passable[i] = p[i] && (v[i] || m[i])
            }
            step = Dungeon.findPath(this, pos, target, passable, Level.fieldOfView)
        }
        return if (step != -1) {
            val oldPos = pos
            move(step)
            sprite?.move(oldPos, pos)
            spend(1 / speed())
            true
        } else {
            false
        }
    }
    fun handle(cell: Int): Boolean {
        if (cell == -1) {
            return false
        }
        var ch: Char?
        val heap: Heap?
        val currentLevel = Dungeon.level ?: return false
        if (currentLevel.map[cell] == Terrain.ALCHEMY && cell != pos) {
            curAction = HeroAction.Cook(cell)
        } else if (currentLevel.map[cell] == Terrain.CRAFTING_TABLE || currentLevel.map[cell] == Terrain.FURNACE) {
            curAction = HeroAction.UseStation(cell)
        } else {
            ch = Actor.findChar(cell)
            if (Level.fieldOfView[cell] && ch is Mob) {
                if (ch is NPC) {
                    curAction = HeroAction.Interact(ch)
                } else {
                    curAction = HeroAction.Attack(ch)
                }
            } else {
                heap = currentLevel.heaps[cell]
                if (Level.fieldOfView[cell] && heap != null && heap.type !== Type.HIDDEN) {
                    when (heap.type) {
                        Type.HEAP -> curAction = HeroAction.PickUp(cell)
                        Type.FOR_SALE -> curAction = if (heap.size() == 1 && (heap.peek()?.price() ?: 0) > 0)
                            HeroAction.Buy(cell)
                        else
                            HeroAction.PickUp(cell)
                        else -> curAction = HeroAction.OpenChest(cell)
                    }
                } else if (currentLevel.map[cell] == Terrain.LOCKED_DOOR || currentLevel.map[cell] == Terrain.LOCKED_EXIT) {
                    curAction = HeroAction.Unlock(cell)
                } else if (cell == currentLevel.exit) {
                    curAction = HeroAction.Descend(cell)
                } else if (cell == currentLevel.entrance) {
                    curAction = HeroAction.Ascend(cell)
                } else {
                    curAction = HeroAction.Move(cell)
                    lastAction = null
                }
            }
        }
        return act()
    }
    fun earnExp(exp: Int) {
        this.exp += exp
        var levelUp = false
        while (this.exp >= maxExp()) {
            this.exp -= maxExp()
            lvl++
            HT += 5
            HP += 5
            attackSkill++
            defenseSkill++
            if (lvl < 10) {
                updateAwareness()
            }
            levelUp = true
        }
        if (levelUp) {
            GLog.p(TXT_NEW_LEVEL, lvl)
            sprite?.showStatus(CharSprite.POSITIVE, TXT_LEVEL_UP)
            Sample.play(Assets.SND_LEVELUP)
            Badges.validateLevelReached()
        }
        if (subClass === HeroSubClass.WARLOCK) {
            val value = min(HT - HP, 1 + (Dungeon.depth - 1) / 5)
            if (value > 0) {
                HP += value
                sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
            }
            (buff(Hunger::class.java) as Hunger).satisfy(10f)
        }
    }
    fun maxExp(): Int {
        return 5 + lvl * 5
    }
    fun updateAwareness() {
        awareness = (1 - (if (heroClass === HeroClass.ROGUE) 0.85 else 0.90).pow((1 + min(lvl, 9)) * 0.5)).toFloat()
    }
    val isStarving: Boolean
        get() = (buff(Hunger::class.java) as Hunger).isStarving
    override fun add(buff: Buff) {
        super.add(buff)
        if (sprite != null) {
            when (buff) {
                is Burning -> {
                    GLog.w("You catch fire!")
                    interrupt()
                }
                is Paralysis -> {
                    GLog.w("You are paralysed!")
                    interrupt()
                }
                is Poison -> {
                    GLog.w("You are poisoned!")
                    interrupt()
                }
                is Ooze -> GLog.w("Caustic ooze eats your flesh. Wash away it!")
                is Roots -> GLog.w("You can't move!")
                is Weakness -> GLog.w("You feel weakened!")
                is Blindness -> GLog.w("You are blinded!")
                is Fury -> {
                    GLog.w("You become furious!")
                    sprite?.showStatus(CharSprite.POSITIVE, "furious")
                }
                is Charm -> GLog.w("You are charmed!")
                is Cripple -> GLog.w("You are crippled!")
                is Bleeding -> GLog.w("You are bleeding!")
                is Vertigo -> {
                    GLog.w("Everything is spinning around you!")
                    interrupt()
                }
                is Light -> sprite?.add(CharSprite.State.ILLUMINATED)
            }
        }
        BuffIndicator.refreshHero()
    }
    override fun remove(buff: Buff) {
        super.remove(buff)
        if (buff is Light) {
            sprite?.remove(CharSprite.State.ILLUMINATED)
        }
        BuffIndicator.refreshHero()
    }
    override fun stealth(): Int {
        var stealth = super.stealth()
        for (buff in buffs(RingOfShadows.Shadows::class.java)) {
            stealth += buff.level
        }
        return stealth
    }
    override fun die(src: Any?) {
        curAction = null
        DewVial.autoDrink(this)
        if (isAlive) {
            val s = sprite
            if (s != null) {
                Flare(8, 32f).color(0xFFFF66.toInt(), true).show(s, 2f)
            }
            return
        }
        Actor.fixTime()
        super.die(src)
        val ankh = belongings.getItem(Ankh::class.java)
        if (ankh == null) {
            reallyDie(src)
        } else {
            Dungeon.hero?.let { Dungeon.deleteGame(it.heroClass, false) }
            GameScene.show(WndResurrect(ankh, src))
        }
    }
    override fun move(step: Int) {
        super.move(step)
        if (!flying) {
            if (Level.water[pos]) {
                Sample.play(Assets.SND_WATER, 1f, 1f, Random.Float(0.8f, 1.25f))
            } else {
                Sample.play(Assets.SND_STEP)
            }
            Dungeon.level?.press(pos, this)
        }
    }
    override fun onMotionComplete() {
        Dungeon.observe()
        search(false)
        super.onMotionComplete()
    }
    override fun onAttackComplete() {
        val currentEnemy = enemy ?: return
        AttackIndicator.target(currentEnemy)
        attack(currentEnemy)
        curAction = null
        Invisibility.dispel()
        super.onAttackComplete()
    }
    override fun onOperateComplete() {
        val currentLevel = Dungeon.level
        if (curAction is HeroAction.Unlock) {
            theKey?.detach(belongings.backpack)
            theKey = null
            val doorCell = (curAction as HeroAction.Unlock).dst
            val door = currentLevel?.map?.get(doorCell) ?: Terrain.EMPTY
            Level.set(doorCell, if (door == Terrain.LOCKED_DOOR) Terrain.DOOR else Terrain.UNLOCKED_EXIT)
            GameScene.updateMap(doorCell)
        } else if (curAction is HeroAction.OpenChest) {
            theKey?.detach(belongings.backpack)
            theKey = null
            val heap = currentLevel?.heaps?.get((curAction as HeroAction.OpenChest).dst)
            if (heap != null) {
                if (heap.type === Type.SKELETON) {
                    Sample.play(Assets.SND_BONES)
                }
                heap.open(this)
            }
        }
        curAction = null
        super.onOperateComplete()
    }
    fun search(intentional: Boolean): Boolean {
        var smthFound = false
        var positive = 0
        var negative = 0
        for (buff in buffs(RingOfDetection.Detection::class.java)) {
            val bonus = buff.level
            if (bonus > positive) {
                positive = bonus
            } else if (bonus < 0) {
                negative += bonus
            }
        }
        var distance = 1 + positive + negative
        var level = if (intentional) (2 * awareness - awareness * awareness) else awareness
        if (distance <= 0) {
            level /= (2 - distance).toFloat()
            distance = 1
        }
        val cx = pos % Level.WIDTH
        val cy = pos / Level.WIDTH
        var ax = cx - distance
        if (ax < 0) {
            ax = 0
        }
        var bx = cx + distance
        if (bx >= Level.WIDTH) {
            bx = Level.WIDTH - 1
        }
        var ay = cy - distance
        if (ay < 0) {
            ay = 0
        }
        var by = cy + distance
        if (by >= Level.HEIGHT) {
            by = Level.HEIGHT - 1
        }
        for (y in ay..by) {
            var p = ax + y * Level.WIDTH
            for (x in ax..bx) {
                if (Dungeon.visible[p]) {
                    if (intentional) {
                        sprite?.parent?.addToBack(CheckedCell(p))
                    }
                    val searchLevel = Dungeon.level
                    if (Level.secret[p] && (intentional || Random.Float() < level)) {
                        val oldValue = searchLevel?.map?.get(p) ?: Terrain.EMPTY
                        GameScene.discoverTile(p, oldValue)
                        Level.set(p, Terrain.discover(oldValue))
                        GameScene.updateMap(p)
                        ScrollOfMagicMapping.discover(p)
                        smthFound = true
                    }
                    if (intentional) {
                        val heap = searchLevel?.heaps?.get(p)
                        if (heap != null && heap.type === Type.HIDDEN) {
                            heap.open(this)
                            smthFound = true
                        }
                    }
                }
                p++
            }
        }
        if (intentional) {
            sprite?.showStatus(CharSprite.DEFAULT, TXT_SEARCH)
            sprite?.operate(pos)
            if (smthFound) {
                spendAndNext(if (Random.Float() < level) TIME_TO_SEARCH else TIME_TO_SEARCH * 2)
            } else {
                spendAndNext(TIME_TO_SEARCH)
            }
        }
        if (smthFound) {
            GLog.w(TXT_NOTICED_SMTH)
            Sample.play(Assets.SND_SECRET)
            interrupt()
        }
        return smthFound
    }
    fun resurrect(resetLevel: Int) {
        HP = HT
        Dungeon.gold = 0
        exp = 0
        belongings.resurrect(resetLevel)
        live()
    }
    override fun resistances(): HashSet<Class<*>> {
        val r = buff(RingOfElements.Resistance::class.java)
        return r?.resistances() ?: super.resistances()
    }
    override fun immunities(): HashSet<Class<*>> {
        val buff = buff(GasesImmunity::class.java)
        return if (buff == null) super.immunities() else GasesImmunity.IMMUNITIES
    }
    override fun next() {
        super.next()
    }
    interface Doom {
        fun onDeath()
    }
    companion object {
        private const val TXT_LEAVE = "One does not simply leave Pixel Dungeon."
        private const val TXT_LEVEL_UP = "level up!"
        private const val TXT_NEW_LEVEL =
            "Welcome to level %d! Now you are healthier and more focused. " +
                    "It's easier for you to hit enemies and dodge their attacks."
        const val TXT_YOU_NOW_HAVE = "You now have %s"
        private const val TXT_SOMETHING_ELSE = "There is something else here"
        private const val TXT_LOCKED_CHEST = "This chest is locked and you don't have matching key"
        private const val TXT_LOCKED_DOOR = "You don't have a matching key"
        private const val TXT_NOTICED_SMTH = "You noticed something"
        private const val TXT_WAIT = "..."
        private const val TXT_SEARCH = "search"
        const val STARTING_STR = 10
        private const val TIME_TO_REST = 1f
        private const val TIME_TO_SEARCH = 2f
        private const val ATTACK = "attackSkill"
        private const val DEFENSE = "defenseSkill"
        private const val STRENGTH = "STR"
        private const val LEVEL = "lvl"
        private const val EXPERIENCE = "exp"
        fun preview(info: GamesInProgress.Info, bundle: Bundle) {
            info.level = bundle.getInt(LEVEL)
        }
        fun reallyDie(cause: Any?) {
            val currentLevel = Dungeon.level ?: return
            val currentHero = Dungeon.hero ?: return
            val length = Level.LENGTH
            val map = currentLevel.map
            val visited = currentLevel.visited
            val discoverable = Level.discoverable
            for (i in 0 until length) {
                val terr = map[i]
                if (discoverable[i]) {
                    visited[i] = true
                    if ((Terrain.flags[terr] and Terrain.SECRET) != 0) {
                        Level.set(i, Terrain.discover(terr))
                        GameScene.updateMap(i)
                    }
                }
            }
            Bones.leave()
            Dungeon.observe()
            currentHero.belongings.identify()
            val pos = currentHero.pos
            val passable = ArrayList<Int>()
            for (ofs in Level.NEIGHBOURS8) {
                val cell = pos + ofs
                if ((Level.passable[cell] || Level.avoid[cell]) && currentLevel.heaps[cell] == null) {
                    passable.add(cell)
                }
            }
            passable.shuffle()
            val items = ArrayList(currentHero.belongings.backpack.items)
            for (cell in passable) {
                if (items.isEmpty()) {
                    break
                }
                val item = Random.element(items) ?: continue
                currentLevel.drop(item, cell).sprite?.drop(pos)
                items.remove(item)
            }
            GameScene.gameOver()
            if (cause is Doom) {
                cause.onDeath()
            }
            Dungeon.deleteGame(currentHero.heroClass, true)
        }
    }
}

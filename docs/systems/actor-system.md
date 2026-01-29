# Actor System

This document describes the turn-based actor system in Amazing Pixel Dungeon.

## Overview

The actor system implements turn-based gameplay through a time-based priority queue. All game entities that take actions (heroes, mobs, buffs) inherit from `Actor`.

## Actor Hierarchy

```
Actor                      # Base turn-based entity
  ├── Char                 # Character with HP, combat
  │     ├── Hero           # Player character
  │     ├── Mob            # Enemy base class
  │     │     ├── [43+ mob types]
  │     │     └── npcs/NPC # Non-hostile NPCs
  │     └── [other chars]
  ├── Blob                 # Environmental effects
  └── Buff                 # Status effects
        └── [41 buff types]
```

## Actor Base Class

### Actor.kt
**Path**: `actors/Actor.kt`

Core turn scheduling:

```kotlin
abstract class Actor : Bundlable {
    companion object {
        const val TICK = 1f  // Base time unit

        private val all = HashSet<Actor>()
        private val chars = HashSet<Char>()
        private var current: Actor? = null

        var now: Float = 0f  // Global game time

        fun process(): Boolean {
            if (current != null) return false

            // Find actor with earliest action time
            var next: Actor? = null
            var minTime = Float.MAX_VALUE

            all.forEach { actor ->
                if (actor.time < minTime) {
                    minTime = actor.time
                    next = actor
                }
            }

            next?.let { actor ->
                now = actor.time
                current = actor

                // Execute actor's turn
                if (actor.act()) {
                    // Actor finished turn
                    current = null
                } else {
                    // Actor needs to wait (e.g., player input)
                    return false
                }
            }

            return true
        }

        fun add(actor: Actor) {
            all.add(actor)
            if (actor is Char) chars.add(actor)
            actor.onAdd()
        }

        fun remove(actor: Actor) {
            all.remove(actor)
            if (actor is Char) chars.remove(actor)
            actor.onRemove()
        }
    }

    protected var time: Float = 0f
    var id: Int = 0

    abstract fun act(): Boolean

    protected fun spend(time: Float) {
        this.time += time
    }

    protected fun postpone(time: Float) {
        if (this.time < now + time) {
            this.time = now + time
        }
    }

    protected fun cooldown(): Float {
        return time - now
    }

    open fun onAdd() { }
    open fun onRemove() { }
}
```

### Turn Scheduling Flow

```
┌─────────────────────────────────────────────────────────┐
│                  Actor.process()                         │
│  ┌───────────────────────────────────────────────────┐  │
│  │  1. Find actor with minimum time                   │  │
│  │  2. Set global now = actor.time                    │  │
│  │  3. Set current = actor                            │  │
│  │  4. Call actor.act()                               │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    actor.act()                           │
│  ┌───────────────────────────────────────────────────┐  │
│  │  1. Perform action (move, attack, etc.)            │  │
│  │  2. Call spend(actionTime)                         │  │
│  │  3. Return true (turn complete)                    │  │
│  │     OR false (waiting for input)                   │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    spend(time)                           │
│  ┌───────────────────────────────────────────────────┐  │
│  │  actor.time += time                                │  │
│  │  (Schedules next turn for this actor)              │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## Character Class

### Char.kt
**Path**: `actors/Char.kt`

Characters with health and combat:

```kotlin
abstract class Char : Actor() {
    var pos: Int = 0

    var name: String = "???"

    var HT: Int = 1    // Max HP
    var HP: Int = 1    // Current HP

    var sprite: CharSprite? = null

    var baseSpeed: Float = 1f

    // Movement flags
    var paralysed: Boolean = false
    var rooted: Boolean = false
    var flying: Boolean = false
    var invisible: Int = 0

    var viewDistance: Int = 8

    // Field of view
    var fieldOfView: BooleanArray? = null

    // Buff management
    val buffs = HashSet<Buff>()

    // Combat stats (override in subclasses)
    open fun attackSkill(target: Char): Int = 0
    open fun defenseSkill(enemy: Char): Int = 0
    open fun damageRoll(): Int = 0
    open fun dr(): Int = 0  // Damage reduction (armor)

    fun speed(): Float {
        return baseSpeed * Buff.speed(this)
    }

    // Movement
    fun move(step: Int) {
        if (Level.adjacent(pos, step)) {
            sprite?.move(pos, step)
            pos = step

            if (this !== Dungeon.hero) {
                sprite?.visible = Dungeon.visible[step]
            }
        } else {
            sprite?.place(step)
            pos = step
        }
    }

    // Combat
    fun attack(enemy: Char): Boolean {
        val hit = attackProc(enemy, damage)
        if (hit) {
            enemy.damage(damage, this)
            if (enemy.isAlive) {
                enemy.defenseProc(this, damage)
            }
        }
        return hit
    }

    open fun attackProc(enemy: Char, damage: Int): Int {
        // Called on attacker, can modify damage
        return damage
    }

    open fun defenseProc(enemy: Char, damage: Int): Int {
        // Called on defender, can modify damage
        return damage
    }

    fun damage(dmg: Int, src: Any) {
        if (!isAlive || dmg < 0) return

        HP -= dmg

        sprite?.showStatus(CharSprite.NEGATIVE, dmg.toString())

        if (HP <= 0) {
            die(src)
        }
    }

    open fun die(src: Any?) {
        destroy()
        sprite?.die()
    }

    // Buff system
    fun <T : Buff> buff(c: Class<T>): T? {
        return buffs.firstOrNull { c.isInstance(it) } as T?
    }

    fun add(buff: Buff) {
        buffs.add(buff)
        buff.attachTo(this)
    }

    fun remove(buff: Buff) {
        buffs.remove(buff)
        buff.detach()
    }
}
```

## Hero Class

### Hero.kt
**Path**: `actors/hero/Hero.kt`

Player character:

```kotlin
class Hero : Char() {
    var heroClass: HeroClass = HeroClass.WARRIOR
    var subClass: HeroSubClass = HeroSubClass.NONE

    var belongings: Belongings = Belongings(this)

    var STR: Int = 10    // Strength
    var lvl: Int = 1     // Level
    var exp: Int = 0     // Experience

    // Current action
    var curAction: HeroAction? = null
    var lastAction: HeroAction? = null

    // Visibility
    var restoreHealth: Boolean = false

    companion object {
        const val STARTING_STR = 10
    }

    override fun act(): Boolean {
        super.act()

        // Process buffs
        Hunger.hunger(this)

        // Check if action queued
        curAction?.let { action ->
            return actAction(action)
        }

        // Wait for player input
        return false
    }

    private fun actAction(action: HeroAction): Boolean {
        return when (action) {
            is HeroAction.Move -> actMove(action)
            is HeroAction.Attack -> actAttack(action)
            is HeroAction.PickUp -> actPickUp(action)
            is HeroAction.OpenChest -> actOpenChest(action)
            is HeroAction.Unlock -> actUnlock(action)
            is HeroAction.Descend -> actDescend(action)
            is HeroAction.Ascend -> actAscend(action)
            else -> {
                curAction = null
                true
            }
        }
    }

    private fun actMove(action: HeroAction.Move): Boolean {
        if (action.dst == pos) {
            curAction = null
            return true
        }

        val step = Dungeon.findPath(
            this, pos, action.dst,
            Level.passable,
            fieldOfView!!
        )

        if (step != -1) {
            spend(1f / speed())
            move(step)
            return true
        }

        curAction = null
        return true
    }

    private fun actAttack(action: HeroAction.Attack): Boolean {
        val enemy = action.target

        if (enemy.isAlive && canAttack(enemy)) {
            spend(attackDelay())
            sprite?.attack(enemy.pos)
            return false  // Wait for animation
        }

        curAction = null
        return true
    }

    fun earnExp(exp: Int) {
        this.exp += exp

        while (this.exp >= maxExp()) {
            this.exp -= maxExp()
            lvlUp()
        }
    }

    fun maxExp(): Int {
        return 5 + lvl * 5
    }

    private fun lvlUp() {
        lvl++
        HT += 5
        HP += 5

        sprite?.showStatus(CharSprite.POSITIVE, "+1")
        GLog.p("You are now level $lvl!")
    }

    // Attack timing
    fun attackDelay(): Float {
        return belongings.weapon?.speedFactor(this) ?: 1f
    }

    override fun attackSkill(target: Char): Int {
        var accuracy = 10 + lvl

        accuracy *= when {
            belongings.weapon != null ->
                belongings.weapon!!.accuracyFactor(this)
            else -> 1f
        }.toInt()

        return accuracy
    }

    override fun damageRoll(): Int {
        var damage = belongings.weapon?.damageRoll(this)
            ?: Random.Int(1, maxOf(STR - 8, 1))

        return damage
    }

    override fun dr(): Int {
        return belongings.armor?.dr ?: 0
    }

    fun rest(fullRest: Boolean) {
        spend(if (fullRest) 10f * TICK else TICK)
        if (fullRest) {
            Buff.affect(this, Healing::class.java)
        }
        search(false)
    }

    fun search(intentional: Boolean): Boolean {
        // Search for hidden doors and traps
        var found = false

        for (i in Level.NEIGHBOURS9) {
            val cell = pos + i

            if (Level.secret[cell]) {
                val chance = if (intentional) 0.5f else 0.1f
                if (Random.Float() < chance) {
                    Level.discover(cell)
                    GameScene.updateMap(cell)
                    found = true
                }
            }
        }

        if (intentional) {
            spend(TICK)
            sprite?.showStatus(CharSprite.DEFAULT, "...")
        }

        return found
    }
}
```

### HeroClass.kt
**Path**: `actors/hero/HeroClass.kt`

Hero class definitions:

```kotlin
enum class HeroClass(
    val title: String,
    val spritesheet: String
) {
    WARRIOR("warrior", Assets.WARRIOR),
    MAGE("mage", Assets.MAGE),
    ROGUE("rogue", Assets.ROGUE),
    HUNTRESS("huntress", Assets.HUNTRESS);

    fun initHero(hero: Hero) {
        hero.heroClass = this

        when (this) {
            WARRIOR -> {
                hero.STR = 11
                hero.belongings.weapon = ShortSword().identify()
                // ...
            }
            MAGE -> {
                hero.belongings.weapon = Knuckles().identify()
                hero.belongings.armor = ClothArmor().identify()
                // Wand of Magic Missile
                // ...
            }
            ROGUE -> {
                hero.belongings.weapon = Dagger().identify()
                hero.belongings.weapon!!.enchant(Leech())
                // ...
            }
            HUNTRESS -> {
                hero.belongings.weapon = Boomerang().identify()
                // ...
            }
        }
    }

    fun masteryBadge(): Badge {
        return when (this) {
            WARRIOR -> Badge.MASTERY_WARRIOR
            MAGE -> Badge.MASTERY_MAGE
            ROGUE -> Badge.MASTERY_ROGUE
            HUNTRESS -> Badge.MASTERY_HUNTRESS
        }
    }
}
```

## Mob Class

### Mob.kt
**Path**: `actors/mobs/Mob.kt`

Enemy base class with AI:

```kotlin
abstract class Mob : Char() {
    // AI state
    enum class State { SLEEPING, WANDERING, HUNTING, FLEEING, PASSIVE }

    var state: State = State.SLEEPING

    // Target
    var enemy: Char? = null

    // Loot
    var loot: Any? = null
    var lootChance: Float = 0f

    // Experience reward
    var EXP: Int = 1
    var maxLvl: Int = 30

    override fun act(): Boolean {
        super.act()

        // Update enemy reference
        enemy = chooseEnemy()

        // Check for state transitions
        if (enemy != null && state != State.PASSIVE) {
            if (state == State.SLEEPING) {
                notice()
            }
            state = if (HP < HT / 4 && enemy === Dungeon.hero) {
                State.FLEEING
            } else {
                State.HUNTING
            }
        }

        // Execute AI
        return when (state) {
            State.SLEEPING -> doSleep()
            State.WANDERING -> doWander()
            State.HUNTING -> doHunt()
            State.FLEEING -> doFlee()
            State.PASSIVE -> doNothing()
        }
    }

    protected open fun chooseEnemy(): Char? {
        if (enemy != null && enemy!!.isAlive &&
            Level.fieldOfView[enemy!!.pos]) {
            return enemy
        }

        if (Dungeon.visible[pos] && Dungeon.hero!!.isAlive) {
            return Dungeon.hero
        }

        return null
    }

    protected open fun doSleep(): Boolean {
        spend(TICK)
        return true
    }

    protected open fun doWander(): Boolean {
        if (enemy != null && Level.fieldOfView[enemy!!.pos]) {
            notice()
            state = State.HUNTING
            return doHunt()
        }

        val oldPos = pos
        if (target != -1 && getCloser(target)) {
            spend(1f / speed())
            return moveSprite(oldPos, pos)
        }

        target = Dungeon.level!!.randomDestination()
        spend(TICK)
        return true
    }

    protected open fun doHunt(): Boolean {
        if (enemy == null) {
            state = State.WANDERING
            target = Dungeon.level!!.randomDestination()
            return true
        }

        if (canAttack(enemy!!)) {
            return doAttack(enemy!!)
        }

        if (getCloser(enemy!!.pos)) {
            spend(1f / speed())
            return true
        }

        spend(TICK)
        return true
    }

    protected open fun doFlee(): Boolean {
        if (enemy == null) {
            state = State.WANDERING
            return true
        }

        if (getFurther(enemy!!.pos)) {
            spend(1f / speed())
            return true
        }

        spend(TICK)
        return true
    }

    protected fun doAttack(enemy: Char): Boolean {
        val visible = Dungeon.visible[pos]

        if (visible) {
            sprite?.attack(enemy.pos)
        }

        spend(attackDelay())

        if (hit(this, enemy, false)) {
            val damage = damageRoll()
            enemy.damage(damage, this)

            if (!enemy.isAlive && enemy === Dungeon.hero) {
                Dungeon.fail(this::class.java)
            }
        }

        return !visible
    }

    fun notice() {
        sprite?.showAlert()
    }

    override fun die(src: Any?) {
        super.die(src)

        val hero = Dungeon.hero!!
        if (hero.lvl <= maxLvl) {
            hero.earnExp(EXP)
        }

        if (Random.Float() < lootChance) {
            createLoot()?.let { item ->
                Dungeon.level!!.drop(item, pos)
            }
        }
    }

    protected open fun createLoot(): Item? {
        return loot as? Item
    }
}
```

## Buff System

### Buff.kt
**Path**: `actors/buffs/Buff.kt`

Status effect base class:

```kotlin
abstract class Buff : Actor() {
    var target: Char? = null

    companion object {
        fun <T : Buff> affect(char: Char, buffClass: Class<T>): T {
            var buff = char.buff(buffClass)
            if (buff == null) {
                buff = buffClass.newInstance()
                buff.attachTo(char)
            }
            return buff
        }

        fun <T : Buff> affect(char: Char, buffClass: Class<T>, duration: Float): T {
            val buff = affect(char, buffClass)
            buff.spend(duration)
            return buff
        }

        fun <T : Buff> prolong(char: Char, buffClass: Class<T>, duration: Float): T {
            val buff = affect(char, buffClass)
            buff.postpone(duration)
            return buff
        }

        fun detach(char: Char, buffClass: Class<out Buff>) {
            char.buff(buffClass)?.detach()
        }

        // Buff modifiers
        fun speed(char: Char): Float {
            var speed = 1f
            char.buff(Speed::class.java)?.let { speed *= 2f }
            char.buff(Slow::class.java)?.let { speed *= 0.5f }
            char.buff(Cripple::class.java)?.let { speed *= 0.5f }
            return speed
        }
    }

    open fun attachTo(target: Char): Boolean {
        this.target = target
        target.add(this)
        Actor.add(this)
        return true
    }

    open fun detach() {
        target?.remove(this)
        Actor.remove(this)
    }

    override fun act(): Boolean {
        // Called each turn
        spend(TICK)
        return true
    }

    open fun icon(): Int = NONE

    open fun desc(): String = ""
}
```

### Common Buffs

#### Burning.kt
**Path**: `actors/buffs/Burning.kt`

```kotlin
class Burning : Buff(), Hero.Doom {
    companion object {
        const val DURATION = 8f
    }

    private var left: Float = DURATION

    override fun act(): Boolean {
        if (target!!.isAlive) {
            val damage = Random.Int(1, 5)
            target!!.damage(damage, this)

            if (target!!.pos != -1) {
                // Spread fire
                Blob.seed(target!!.pos, 2, Fire::class.java)
            }
        }

        left -= TICK

        if (left <= 0 || target!!.HP <= 0) {
            detach()
        } else {
            // Extend if in fire
            if (Dungeon.level!!.flamable[target!!.pos]) {
                left = DURATION
            }
            spend(TICK)
        }

        return true
    }

    override fun icon(): Int = BuffIndicator.FIRE
}
```

#### Paralysis.kt
**Path**: `actors/buffs/Paralysis.kt`

```kotlin
class Paralysis : FlavourBuff() {
    companion object {
        const val DURATION = 10f
    }

    override fun attachTo(target: Char): Boolean {
        if (super.attachTo(target)) {
            target.paralysed = true
            return true
        }
        return false
    }

    override fun detach() {
        target?.paralysed = false
        super.detach()
    }

    override fun icon(): Int = BuffIndicator.PARALYSIS
}
```

#### Invisibility.kt
**Path**: `actors/buffs/Invisibility.kt`

```kotlin
class Invisibility : FlavourBuff() {
    companion object {
        const val DURATION = 15f

        fun dispel() {
            Dungeon.hero?.buff(Invisibility::class.java)?.detach()
        }
    }

    override fun attachTo(target: Char): Boolean {
        if (super.attachTo(target)) {
            target.invisible++
            return true
        }
        return false
    }

    override fun detach() {
        target?.let { it.invisible-- }
        super.detach()
    }

    override fun icon(): Int = BuffIndicator.INVISIBLE
}
```

## Combat System

### Hit Calculation
```kotlin
fun hit(attacker: Char, defender: Char, magic: Boolean): Boolean {
    val acuRoll = Random.Float(attacker.attackSkill(defender).toFloat())
    val defRoll = Random.Float(defender.defenseSkill(attacker).toFloat())

    return if (magic) {
        acuRoll * 2 >= defRoll
    } else {
        acuRoll >= defRoll
    }
}
```

### Damage Calculation
```kotlin
fun damageCalc(attacker: Char, defender: Char): Int {
    // Base damage roll
    var damage = attacker.damageRoll()

    // Apply damage reduction (armor)
    val dr = defender.dr()
    damage -= Random.IntRange(0, dr)

    // Minimum 0 damage
    return maxOf(0, damage)
}
```

## Blob System

### Blob.kt
**Path**: `actors/blobs/Blob.kt`

Environmental effect that spreads:

```kotlin
abstract class Blob : Actor() {
    companion object {
        fun seed(cell: Int, amount: Int, type: Class<out Blob>): Blob {
            var blob = Dungeon.level?.blobs?.get(type)
            if (blob == null) {
                blob = type.newInstance()
                blob.seed(Dungeon.level!!, cell, amount)
                Dungeon.level?.blobs?.put(type, blob)
                Actor.add(blob)
            } else {
                blob.seed(Dungeon.level!!, cell, amount)
            }
            return blob
        }
    }

    var cur: IntArray = IntArray(Level.LENGTH)
    var off: IntArray = IntArray(Level.LENGTH)

    var volume: Int = 0

    open fun seed(level: Level, cell: Int, amount: Int) {
        cur[cell] += amount
        volume += amount
    }

    override fun act(): Boolean {
        spend(TICK)

        if (volume > 0) {
            evolve()
            volume = cur.sum()
        }

        return true
    }

    protected open fun evolve() {
        // Swap buffers
        val tmp = off
        off = cur
        cur = tmp

        // Clear current
        cur.fill(0)

        // Evolve each cell
        for (cell in 0 until Level.LENGTH) {
            if (off[cell] > 0) {
                // Spread to neighbors
                val count = Level.NEIGHBOURS4.count {
                    Level.passable[cell + it]
                } + 1

                val spread = off[cell] / count
                cur[cell] += spread

                Level.NEIGHBOURS4.forEach { n ->
                    if (Level.passable[cell + n]) {
                        cur[cell + n] += spread
                    }
                }
            }
        }
    }
}
```

## See Also

- [Scene System](scene-system.md) - Game scenes
- [Entities: Mobs](../entities/mobs.md) - Enemy catalog
- [Entities: Buffs](../entities/buffs.md) - Buff catalog
- [Architecture Overview](../architecture/overview.md) - System architecture

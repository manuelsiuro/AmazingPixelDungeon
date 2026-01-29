package com.watabou.pixeldungeon.items.weapon.missiles
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.Fire
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random
class IncendiaryDart : MissileWeapon {
    constructor() : this(1)
    constructor(number: Int) : super() {
        quantity = number
    }
    init {
        name = "incendiary dart"
        image = ItemSpriteSheet.INCENDIARY_DART
        STR = 12
    }
    override fun min(): Int {
        return 1
    }
    override fun max(): Int {
        return 2
    }
    override fun onThrow(cell: Int) {
        val enemy = Actor.findChar(cell)
        val user = curUser
        if (enemy == null || enemy === user) {
            if (Level.flamable[cell]) {
                Blob.seed(cell, 4, Fire::class.java)?.let { GameScene.add(it) }
            } else {
                super.onThrow(cell)
            }
        } else {
            if (user == null || !user.shoot(enemy, this)) {
                Dungeon.level?.drop(this, cell)?.sprite?.drop()
            }
        }
    }
    override fun proc(attacker: Char, defender: Char, damage: Int) {
        Buffs.affect(defender, Burning::class.java)?.reignite(defender)
        super.proc(attacker, defender, damage)
    }
    override fun desc(): String {
        return "The spike on each of these darts is designed to pin it to its target " +
                "while the unstable compounds strapped to its length burst into brilliant flames."
    }
    override fun random(): Item {
        quantity = Random.Int(3, 6)
        return this
    }
    override fun price(): Int {
        return 10 * quantity
    }
}

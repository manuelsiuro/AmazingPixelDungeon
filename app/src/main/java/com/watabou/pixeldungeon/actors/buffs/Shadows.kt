package com.watabou.pixeldungeon.actors.buffs
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.utils.Bundle
class Shadows : Invisibility() {
    private var left = 0f
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(TAG_LEFT, left)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        left = bundle.getFloat(TAG_LEFT)
    }
    override fun attachTo(target: Char): Boolean {
        return if (super.attachTo(target)) {
            Sample.play(Assets.SND_MELD)
            Dungeon.observe()
            true
        } else {
            false
        }
    }
    override fun detach() {
        super.detach()
        Dungeon.observe()
    }
    override fun act(): Boolean {
        val target = target ?: return true
        if (target.isAlive) {
            spend(TICK * 2)
            left--
            val hero = Dungeon.hero
            if (left <= 0 || (hero != null && hero.visibleEnemies() > 0)) {
                detach()
            }
        } else {
            detach()
        }
        return true
    }
    fun prolong() {
        left = 2f
    }
    override fun icon(): Int {
        return BuffIndicator.SHADOWS
    }
    override fun toString(): String {
        return "Shadowmelded"
    }
    companion object {
        private const val TAG_LEFT = "left"
    }
}

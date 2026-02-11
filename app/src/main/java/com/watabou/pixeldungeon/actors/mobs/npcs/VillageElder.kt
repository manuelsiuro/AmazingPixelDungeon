package com.watabou.pixeldungeon.actors.mobs.npcs

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.WandmakerSprite
import com.watabou.pixeldungeon.windows.WndQuest

class VillageElder : NPC() {

    init {
        name = "village elder"
        spriteClass = WandmakerSprite::class.java
    }

    override fun act(): Boolean {
        throwItem()
        Dungeon.hero?.let { sprite?.turnTo(pos, it.pos) }
        spend(TICK)
        return true
    }

    override fun interact() {
        sprite?.turnTo(pos, Dungeon.hero?.pos ?: pos)

        val text = LlmTextEnhancer.enhanceNpcDialog(
            "village elder",
            "greeting",
            Dungeon.hero?.className() ?: "adventurer",
            Dungeon.depth,
            GREETING
        )
        GameScene.show(WndQuest(this, text))
    }

    override fun damage(dmg: Int, src: Any?) {
        // Village elder is invulnerable
    }

    override fun add(buff: Buff) {
        // Village elder is immune to debuffs
    }

    override fun reset(): Boolean = true

    override fun description(): String {
        return "A weathered old figure who has watched over this village for decades. " +
                "The elder knows much about the dungeon below and offers wisdom to those brave enough to descend."
    }

    companion object {
        private const val GREETING =
            "Ah, another brave soul. The dungeon below holds great treasures, " +
            "but also terrible dangers. Stock up at our shops before you descend. " +
            "The well in the square has healing waters — drink deep before your journey. " +
            "And remember: you can always return here from the first floor of the dungeon."
    }
}

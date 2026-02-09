package com.watabou.pixeldungeon.quests

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.mobs.npcs.AiNpc
import com.watabou.pixeldungeon.levels.Level
import com.watabou.utils.Random

object AiNpcSpawner {

    fun spawn(level: Level) {
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmAiNpcQuests()) {
            return
        }

        // Skip boss levels and shop levels
        if (Dungeon.bossLevel() || Dungeon.shopOnLevel() || Dungeon.depth == 21) {
            return
        }

        val count = AiQuestGenerator.npcCountForDepth(Dungeon.depth)
        val usedVariants = mutableSetOf<Int>()

        for (i in 0 until count) {
            var variant: Int
            do {
                variant = Random.Int(10)
            } while (variant in usedVariants)
            usedVariants.add(variant)

            val npc = AiNpc()
            npc.variant = variant
            npc.aiName = AiQuestGenerator.npcNameForVariant(variant)
            npc.personality = AiQuestGenerator.npcPersonalityForVariant(variant)
            npc.name = npc.aiName

            var pos: Int
            var attempts = 0
            do {
                pos = level.randomRespawnCell()
                attempts++
            } while ((pos == -1 || level.heaps[pos] != null) && attempts < 20)

            if (pos != -1) {
                npc.pos = pos
                level.mobs.add(npc)
                Actor.occupyCell(npc)
            }
        }
    }
}

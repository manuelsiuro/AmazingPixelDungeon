package com.watabou.pixeldungeon.windows

import com.watabou.pixeldungeon.actors.mobs.npcs.AiNpc
import com.watabou.pixeldungeon.quests.AiQuest

class WndAiQuestComplete(
    private val npc: AiNpc,
    private val quest: AiQuest,
    text: String
) : WndQuest(npc, text, TXT_CLAIM) {

    override fun onSelect(index: Int) {
        if (index == 0) {
            npc.giveRewards(quest)
        }
    }

    companion object {
        private const val TXT_CLAIM = "Claim Reward"
    }
}

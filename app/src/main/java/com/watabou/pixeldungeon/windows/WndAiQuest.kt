package com.watabou.pixeldungeon.windows

import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.mobs.npcs.AiNpc
import com.watabou.pixeldungeon.quests.AiQuest

class WndAiQuest(
    private val npc: AiNpc,
    private val quest: AiQuest,
    text: String
) : WndQuest(npc, text, TXT_ACCEPT, TXT_DECLINE) {

    override fun onSelect(index: Int) {
        if (index == 0) {
            quest.status = AiQuest.Status.ACTIVE
            quest.startTurn = Actor.now
            npc.questGiven = true
        }
    }

    companion object {
        private const val TXT_ACCEPT = "Accept Quest"
        private const val TXT_DECLINE = "Not now"
    }
}

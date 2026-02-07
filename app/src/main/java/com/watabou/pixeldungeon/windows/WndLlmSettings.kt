package com.watabou.pixeldungeon.windows

import com.watabou.noosa.Game
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.ui.CheckBox
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window

class WndLlmSettings : Window() {

    private val subToggles = mutableListOf<CheckBox>()

    init {
        val btnMaster = object : CheckBox(TXT_ENABLE) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.llmEnabled(checked())
                updateSubToggles(checked())
            }
        }
        btnMaster.setRect(0f, 0f, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        btnMaster.checked(PixelDungeon.llmEnabled())
        add(btnMaster)

        val btnNpc = object : CheckBox(TXT_NPC_DIALOG) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.llmNpcDialog(checked())
            }
        }
        btnNpc.setRect(0f, btnMaster.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        btnNpc.checked(PixelDungeon.llmNpcDialog())
        add(btnNpc)
        subToggles.add(btnNpc)

        val btnNarration = object : CheckBox(TXT_NARRATION) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.llmNarration(checked())
            }
        }
        btnNarration.setRect(0f, btnNpc.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        btnNarration.checked(PixelDungeon.llmNarration())
        add(btnNarration)
        subToggles.add(btnNarration)

        val btnItems = object : CheckBox(TXT_ITEM_DESC) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.llmItemDesc(checked())
            }
        }
        btnItems.setRect(0f, btnNarration.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        btnItems.checked(PixelDungeon.llmItemDesc())
        add(btnItems)
        subToggles.add(btnItems)

        val btnCombat = object : CheckBox(TXT_COMBAT) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.llmCombatNarration(checked())
            }
        }
        btnCombat.setRect(0f, btnItems.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        btnCombat.checked(PixelDungeon.llmCombatNarration())
        add(btnCombat)
        subToggles.add(btnCombat)

        val btnStory = object : CheckBox(TXT_STORY_MOMENTS) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.llmStoryMoments(checked())
            }
        }
        btnStory.setRect(0f, btnCombat.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        btnStory.checked(PixelDungeon.llmStoryMoments())
        add(btnStory)
        subToggles.add(btnStory)

        val btnBoss = object : CheckBox(TXT_BOSS_ENCOUNTERS) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.llmBossEncounters(checked())
            }
        }
        btnBoss.setRect(0f, btnStory.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        btnBoss.checked(PixelDungeon.llmBossEncounters())
        add(btnBoss)
        subToggles.add(btnBoss)

        val btnBestiary = object : CheckBox(TXT_BESTIARY) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.llmBestiary(checked())
            }
        }
        btnBestiary.setRect(0f, btnBoss.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        btnBestiary.checked(PixelDungeon.llmBestiary())
        add(btnBestiary)
        subToggles.add(btnBestiary)

        val btnAiNpc = object : CheckBox(TXT_AI_NPC_QUESTS) {
            override fun onClick() {
                super.onClick()
                PixelDungeon.llmAiNpcQuests(checked())
            }
        }
        btnAiNpc.setRect(0f, btnBestiary.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        btnAiNpc.checked(PixelDungeon.llmAiNpcQuests())
        add(btnAiNpc)
        subToggles.add(btnAiNpc)

        val btnModels = object : RedButton(TXT_MODELS) {
            override fun onClick() {
                hide()
                Game.scene()?.add(WndLlmModels())
            }
        }
        btnModels.setRect(0f, btnAiNpc.bottom() + GAP * 2, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnModels)

        updateSubToggles(PixelDungeon.llmEnabled())

        resize(WIDTH, btnModels.bottom().toInt())
    }

    private fun updateSubToggles(enabled: Boolean) {
        for (btn in subToggles) {
            btn.enable(enabled)
        }
    }

    companion object {
        private const val TXT_ENABLE = "Enable AI Features"
        private const val TXT_NPC_DIALOG = "NPC Dialog"
        private const val TXT_NARRATION = "Floor Narration"
        private const val TXT_ITEM_DESC = "Item Descriptions"
        private const val TXT_COMBAT = "Combat Narration"
        private const val TXT_STORY_MOMENTS = "Story Moments"
        private const val TXT_BOSS_ENCOUNTERS = "Boss Encounters"
        private const val TXT_BESTIARY = "Bestiary & Lore"
        private const val TXT_AI_NPC_QUESTS = "AI NPC Quests"
        private const val TXT_MODELS = "Manage Models"

        private const val WIDTH = 112
        private const val BTN_HEIGHT = 20
        private const val GAP = 2
    }
}

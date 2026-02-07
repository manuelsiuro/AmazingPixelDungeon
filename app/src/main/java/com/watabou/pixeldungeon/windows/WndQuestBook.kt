package com.watabou.pixeldungeon.windows

import com.watabou.noosa.BitmapText
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.quests.AiQuest
import com.watabou.pixeldungeon.quests.AiQuestBook
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.ScrollPane
import com.watabou.pixeldungeon.ui.Window

class WndQuestBook : Window() {

    init {
        val landscape = PixelDungeon.landscape()
        val windowH = if (landscape) HEIGHT_L else HEIGHT_P
        resize(WIDTH, windowH)

        val txtTitle = PixelScene.createText(TXT_TITLE, 9f)
        txtTitle.hardlight(TITLE_COLOR)
        txtTitle.measure()
        txtTitle.x = PixelScene.align(PixelScene.uiCamera, (WIDTH - txtTitle.width()) / 2)
        add(txtTitle)

        val allQuests = AiQuestBook.activeQuests.sortedWith(
            compareBy<AiQuest> { it.status == AiQuest.Status.COMPLETED }
                .thenBy { it.depth }
        )

        val content = Component()
        var pos = 0f

        for (quest in allQuests) {
            // NPC name line
            val name = PixelScene.createText(quest.npcName, 9f)
            name.measure()
            name.y = pos

            val depth = BitmapText(PixelScene.font1x)
            depth.text("D${quest.depth}")
            depth.measure()
            depth.x = WIDTH.toFloat() - depth.width()
            depth.y = pos

            if (quest.depth == Dungeon.depth) {
                name.hardlight(TITLE_COLOR)
                depth.hardlight(TITLE_COLOR)
            }

            if (quest.status == AiQuest.Status.COMPLETED) {
                name.hardlight(0x888888)
                depth.hardlight(0x888888)
            }

            content.add(name)
            content.add(depth)
            pos += name.baseLine() + GAP

            // Quest type + progress line
            val type = PixelScene.createText(quest.typeDesc(), 7f)
            type.measure()
            type.y = pos

            val progressStr = if (quest.status == AiQuest.Status.COMPLETED) "Complete!" else quest.progressText()
            val progress = PixelScene.createText(progressStr, 7f)
            progress.measure()
            progress.x = WIDTH.toFloat() - progress.width()
            progress.y = pos

            if (quest.status == AiQuest.Status.COMPLETED) {
                type.hardlight(0x888888)
                progress.hardlight(0x44AA44.toInt())
            }

            content.add(type)
            content.add(progress)
            pos += type.baseLine() + GAP * 2
        }

        if (allQuests.isEmpty()) {
            val empty = PixelScene.createText("No quests yet.", 8f)
            empty.measure()
            empty.x = (WIDTH - empty.width()) / 2
            empty.y = pos + GAP
            content.add(empty)
            pos += empty.baseLine() + GAP * 2
        }

        // Completed count
        val completed = PixelScene.createText("Completed: ${AiQuestBook.completedCount}", 7f)
        completed.hardlight(0x888888)
        completed.measure()
        completed.x = (WIDTH - completed.width()) / 2
        completed.y = pos
        content.add(completed)
        pos += completed.baseLine()

        content.setSize(WIDTH.toFloat(), pos)

        val list = ScrollPane(content)
        add(list)
        list.setRect(0f, txtTitle.height(), WIDTH.toFloat(), height - txtTitle.height())
    }

    companion object {
        private const val WIDTH = 112
        private const val HEIGHT_P = 160
        private const val HEIGHT_L = 144
        private const val GAP = 5
        private const val TXT_TITLE = "Quest Book"
    }
}

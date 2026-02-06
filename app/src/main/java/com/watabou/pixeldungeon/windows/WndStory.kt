package com.watabou.pixeldungeon.windows
import com.watabou.input.Touchscreen
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.Game
import com.watabou.noosa.TouchArea
import com.watabou.pixeldungeon.Chrome
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.Window
import com.watabou.utils.SparseArray
open class WndStory(text: String) : Window(0, 0, Chrome.get(Chrome.Type.SCROLL)!!) {
    private val tf: BitmapTextMultiline
    private var delay: Float = 0f
    init {
        tf = PixelScene.createMultiline(text, 7f)
        tf.maxWidth = WIDTH - MARGIN * 2
        tf.measure()
        tf.ra = bgR
        tf.ga = bgG
        tf.ba = bgB
        tf.rm = -bgR
        tf.gm = -bgG
        tf.bm = -bgB
        tf.x = MARGIN.toFloat()
        add(tf)
        add(object : TouchArea(chrome) {
            override fun onClick(touch: Touchscreen.Touch) {
                hide()
            }
        })
        resize((tf.width() + MARGIN * 2).toInt(), Math.min(tf.height().toFloat(), 180f).toInt())
    }
    override fun update() {
        super.update()
        delay -= Game.elapsed
        if (delay <= 0) {
            tf.visible = true
            chrome.visible = true
            shadow.visible = true
        }
    }
    companion object {
        private const val WIDTH = 120
        private const val MARGIN = 6
        private const val bgR = 0.77f
        private const val bgG = 0.73f
        private const val bgB = 0.62f
        const val ID_SEWERS = 0
        const val ID_PRISON = 1
        const val ID_CAVES = 2
        const val ID_METROPOLIS = 3
        const val ID_HALLS = 4
        private val CHAPTERS = SparseArray<String>()
        init {
            CHAPTERS.put(
                ID_SEWERS,
                "The Dungeon lies right beneath the City, its upper levels actually constitute the City's sewer system. " +
                        "Being nominally a part of the City, these levels are not that dangerous. No one will call it a safe place, " +
                        "but at least you won't need to deal with evil magic here."
            )
            CHAPTERS.put(
                ID_PRISON,
                "Many years ago an underground prison was built here for the most dangerous criminals. At the time it seemed " +
                        "like a very clever idea, because this place indeed was very hard to escape. But soon dark miasma started to permeate " +
                        "from below, driving prisoners and guards insane. In the end the prison was abandoned, though some convicts " +
                        "were left locked up here."
            )
            CHAPTERS.put(
                ID_CAVES,
                "The caves, which stretch down under the abandoned prison, are sparcely populated. They lie too deep to be exploited " +
                        "by the City and they are too poor in minerals to interest the dwarves. In the past there was a trade outpost " +
                        "somewhere here on the route between these two states, but it has perished since the decline of Dwarven Metropolis. " +
                        "Only omnipresent gnolls and subterranean animals dwell here now."
            )
            CHAPTERS.put(
                ID_METROPOLIS,
                "Dwarven Metropolis was once the greatest of dwarven city-states. In its heyday the mechanized army of dwarves " +
                        "has successfully repelled the invasion of the old god and his demon army. But it is said, that the returning warriors " +
                        "have brought seeds of corruption with them, and that victory was the beginning of the end for the underground kingdom."
            )
            CHAPTERS.put(
                ID_HALLS,
                "In the past these levels were the outskirts of Metropolis. After the costly victory in the war with the old god " +
                        "dwarves were too weakened to clear them of remaining demons. Gradually demons have tightened their grip on this place " +
                        "and now it's called Demon Halls.\n\n" +
                        "Very few adventurers have ever descended this far..."
            )
        }
        fun showChapter(id: Int) {
            val chapters = Dungeon.chapters ?: return
            if (chapters.contains(id)) {
                return
            }
            val staticText = CHAPTERS[id]
            if (staticText != null) {
                val regionName = when (id) {
                    ID_SEWERS -> "Sewers"
                    ID_PRISON -> "Prison"
                    ID_CAVES -> "Caves"
                    ID_METROPOLIS -> "Dwarven Metropolis"
                    ID_HALLS -> "Demon Halls"
                    else -> "Unknown"
                }
                val text = LlmTextEnhancer.generateFloorNarration(
                    regionName, Dungeon.depth,
                    Dungeon.hero?.className() ?: "adventurer", staticText
                )
                val wnd = WndStory(text)
                wnd.delay = 0.6f
                if (wnd.delay > 0) {
                     wnd.tf.visible = false
                     wnd.chrome.visible = false
                     wnd.shadow.visible = false
                }
                Game.scene()?.add(wnd)
                chapters.add(id)
            }
        }
    }
}

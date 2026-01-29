package com.watabou.pixeldungeon.ui
import com.watabou.noosa.Game
import com.watabou.noosa.SkinnedBlock
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Assets
class Archs : Component() {
    private lateinit var arcsBg: SkinnedBlock
    private lateinit var arcsFg: SkinnedBlock
    var reversed: Boolean = false
    override fun createChildren() {
        arcsBg = SkinnedBlock(1f, 1f, Assets.ARCS_BG)
        arcsBg.autoAdjust = true
        arcsBg.offsetTo(0f, offsB)
        add(arcsBg)
        arcsFg = SkinnedBlock(1f, 1f, Assets.ARCS_FG)
        arcsFg.autoAdjust = true
        arcsFg.offsetTo(0f, offsF)
        add(arcsFg)
    }
    override fun layout() {
        // width and height are from Component (Visual), accessible via property if same package or subclass, 
        // or ensure public accessibility. Component.kt shows protected properties.
        // But Archs extends Component. So it can access protected width/height.
        arcsBg.size(width, height)
        val bgTex = arcsBg.texture ?: return
        arcsBg.offset(bgTex.width / 4 - (width % bgTex.width) / 2, 0f)
        arcsFg.size(width, height)
        val fgTex = arcsFg.texture ?: return
        arcsFg.offset(fgTex.width / 4 - (width % fgTex.width) / 2, 0f)
    }
    override fun update() {
        super.update()
        var shift = Game.elapsed * SCROLL_SPEED
        if (reversed) {
            shift = -shift
        }
        arcsBg.offset(0f, shift)
        arcsFg.offset(0f, shift * 2)
        offsB = arcsBg.offsetY()
        offsF = arcsFg.offsetY()
    }
    companion object {
        private const val SCROLL_SPEED = 20f
        private var offsB = 0f
        private var offsF = 0f
    }
}

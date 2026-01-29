package com.watabou.pixeldungeon.effects
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.DungeonTilemap
class CheckedCell(pos: Int) : Image(TextureCache.createSolid(0xFF55AAFF.toInt())) {
    private var alphaVal: Float = 0.8f
    init {
        origin.set(0.5f)
        point(
            DungeonTilemap.tileToWorld(pos).offset(
                (DungeonTilemap.SIZE / 2).toFloat(),
                (DungeonTilemap.SIZE / 2).toFloat()
            )
        )
    }
    override fun update() {
        alphaVal -= Game.elapsed
        if (alphaVal > 0) {
            alpha(alphaVal)
            scale.set(DungeonTilemap.SIZE * alphaVal)
        } else {
            killAndErase()
        }
    }
}

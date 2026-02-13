package com.watabou.pixeldungeon.windows

import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.encyclopedia.EncyclopediaEntry
import com.watabou.pixeldungeon.encyclopedia.IconType
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.Utils

class WndEncyclopediaEntry(entry: EncyclopediaEntry) : Window() {

    init {
        val icon: Image = when (entry.iconType) {
            IconType.ITEM, IconType.CUSTOM -> ItemSprite(entry.iconImage, null)
            IconType.MOB -> {
                val img = Image(entry.spriteTexture)
                img.frame(img.texture!!.uvRect(0, 0, entry.spriteWidth, entry.spriteHeight))
                img
            }
            IconType.BUFF -> {
                if (entry.iconImage >= 0) {
                    val img = Image(Assets.BUFFS_SMALL)
                    val film = TextureFilm(img.texture!!, BuffIndicator.SIZE, BuffIndicator.SIZE)
                    film.get(entry.iconImage)?.let { img.frame(it) }
                    img
                } else {
                    ItemSprite(ItemSpriteSheet.TORCH, null)
                }
            }
        }

        val titlebar = IconTitle()
        titlebar.icon(icon)
        titlebar.label(Utils.capitalize(entry.name), TITLE_COLOR)
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)

        val txtDesc = PixelScene.createMultiline(entry.description, 6f)
        txtDesc.maxWidth = WIDTH
        txtDesc.measure()
        txtDesc.x = titlebar.left()
        txtDesc.y = titlebar.bottom() + GAP
        add(txtDesc)

        var bottom = txtDesc.y + txtDesc.height()

        if (entry.stats.isNotEmpty()) {
            bottom += GAP
            for ((key, value) in entry.stats) {
                val statLine = PixelScene.createMultiline("$key: $value", 6f)
                statLine.maxWidth = WIDTH
                statLine.measure()
                statLine.x = titlebar.left()
                statLine.y = bottom
                statLine.hardlight(0xCCCCCC)
                add(statLine)
                bottom += statLine.height() + 2
            }
        }

        resize(WIDTH, (bottom + GAP).toInt())
    }

    companion object {
        private const val GAP = 2f
        private const val WIDTH = 120
    }
}

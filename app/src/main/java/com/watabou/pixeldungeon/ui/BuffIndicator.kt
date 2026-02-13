package com.watabou.pixeldungeon.ui
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.utils.SparseArray
class BuffIndicator(private val ch: Char) : Component() {
    private lateinit var texture: SmartTexture
    private lateinit var film: TextureFilm
    private var icons = SparseArray<Image>()
    init {
        if (ch === Dungeon.hero) {
            heroInstance = this
        }
    }
    override fun destroy() {
        super.destroy()
        if (this === heroInstance) {
            heroInstance = null
        }
    }
    override fun createChildren() {
        texture = TextureCache.get(Assets.BUFFS_SMALL)
        film = TextureFilm(texture, SIZE, SIZE)
    }
    override fun layout() {
        clear()
        val newIcons = SparseArray<Image>()
        for (buff in ch.buffs()) {
            val icon = buff.icon()
            if (icon != NONE) {
                val img = Image(texture)
                film.get(icon)?.let { img.frame(it) }
                img.x = x + members.size * (SIZE + 2)
                img.y = y
                add(img)
                newIcons.put(icon, img)
            }
        }
        for (key in icons.keyArray()) {
            if (newIcons.get(key) == null) {
                val icon = icons.get(key)
                icon.origin.set(SIZE / 2f)
                add(icon)
                add(object : AlphaTweener(icon, 0f, 0.6f) {
                    override fun updateValues(progress: Float) {
                        super.updateValues(progress)
                        image.scale.set(1 + 5 * progress)
                    }
                })
            }
        }
        icons = newIcons
    }
    companion object {
        const val NONE = -1
        const val MIND_VISION = 0
        const val LEVITATION = 1
        const val FIRE = 2
        const val POISON = 3
        const val PARALYSIS = 4
        const val HUNGER = 5
        const val STARVATION = 6
        const val SLOW = 7
        const val OOZE = 8
        const val AMOK = 9
        const val TERROR = 10
        const val ROOTS = 11
        const val INVISIBLE = 12
        const val SHADOWS = 13
        const val WEAKNESS = 14
        const val FROST = 15
        const val BLINDNESS = 16
        const val COMBO = 17
        const val FURY = 18
        const val HEALING = 19
        const val ARMOR = 20
        const val HEART = 21
        const val LIGHT = 22
        const val CRIPPLE = 23
        const val BARKSKIN = 24
        const val IMMUNITY = 25
        const val BLEEDING = 26
        const val MARK = 27
        const val DEFERRED = 28
        const val VERTIGO = 29
        const val RAGE = 30
        const val SACRIFICE = 31
        const val WELL_FED = 32
        const val SIZE = 7
        private var heroInstance: BuffIndicator? = null
        fun refreshHero() {
            heroInstance?.layout()
        }
    }
}

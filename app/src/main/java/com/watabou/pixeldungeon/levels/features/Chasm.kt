package com.watabou.pixeldungeon.levels.features
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Cripple
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.levels.RegularLevel
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.InterlevelScene
import com.watabou.pixeldungeon.sprites.MobSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.windows.WndOptions
import com.watabou.utils.Random
object Chasm {
    private const val TXT_CHASM = "Chasm"
    private const val TXT_YES = "Yes, I know what I'm doing"
    private const val TXT_NO = "No, I changed my mind"
    private const val TXT_JUMP = "Do you really want to jump into the chasm? You can probably die."
    var jumpConfirmed = false
    fun heroJump(hero: Hero) {
        GameScene.show(
            object : WndOptions(TXT_CHASM, TXT_JUMP, TXT_YES, TXT_NO) {
                override fun onSelect(index: Int) {
                    if (index == 0) {
                        jumpConfirmed = true
                        hero.resume()
                    }
                }
            }
        )
    }
    fun heroFall(pos: Int) {
        jumpConfirmed = false
        Sample.play(Assets.SND_FALLING)
        val hero = Dungeon.hero ?: return
        if (hero.isAlive) {
            hero.interrupt()
            InterlevelScene.mode = InterlevelScene.Mode.FALL
            if (Dungeon.level is RegularLevel) {
                val room = (Dungeon.level as RegularLevel).room(pos)
                InterlevelScene.fallIntoPit = room != null && room.type == Room.Type.WEAK_FLOOR
            } else {
                InterlevelScene.fallIntoPit = false
            }
            Game.switchScene(InterlevelScene::class.java)
        } else {
            hero.sprite?.visible = false
        }
    }
    fun heroLand() {
        val hero = Dungeon.hero ?: return
        val sprite = hero.sprite ?: return
        sprite.burst(sprite.blood(), 10)
        Camera.main?.shake(4f, 0.2f)
        Buffs.prolong(hero, Cripple::class.java, Cripple.DURATION)
        hero.damage(Random.IntRange(hero.HT / 3, hero.HT / 2), object : Hero.Doom {
            override fun onDeath() {
                Badges.validateDeathFromFalling()
                Dungeon.fail(Utils.format(ResultDescriptions.FALL, Dungeon.depth))
                GLog.n("You fell to death...")
            }
        })
    }
    fun mobFall(mob: Mob) {
        mob.destroy()
        (mob.sprite as MobSprite).fall()
    }
}

package com.watabou.pixeldungeon.levels.features
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.ElmoParticle
import com.watabou.pixeldungeon.levels.DeadEndLevel
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndMessage
object Sign {
    private const val TXT_DEAD_END = "What are you doing here?!"
    private val TIPS = arrayOf(
        "Don't overestimate your strength, use weapons and armor you can handle.",
        "Not all doors in the dungeon are visible at first sight. If you are stuck, search for hidden doors.",
        "Remember, that raising your strength is not the only way to access better equipment. You can go " +
                "the other way, lowering its strength requirement with Scrolls of Upgrade.",
        "You can spend your gold in shops on deeper levels of the dungeon. The first one is on the 6th level.",
        "Beware of Goo!",
        "Pixel-Mart - all you need for successful adventure!",
        "Identify your potions and scrolls as soon as possible. Don't put it off to the moment " +
                "when you actually need them.",
        "Being hungry doesn't hurt, but starving does hurt.",
        "Surprise attack has a better chance to hit. For example, you can ambush your enemy behind " +
                "a closed door when you know it is approaching.",
        "Don't let The Tengu out!",
        "Pixel-Mart. Spend money. Live longer.",
        "When you're attacked by several monsters at the same time, try to retreat behind a door.",
        "If you are burning, you can't put out the fire in the water while levitating.",
        "There is no sense in possessing more than one Ankh at the same time, because you will lose them upon resurrecting.",
        "DANGER! Heavy machinery can cause injury, loss of limbs or death!",
        "Pixel-Mart. A safer life in dungeon.",
        "When you upgrade an enchanted weapon, there is a chance to destroy that enchantment.",
        "Weapons and armors deteriorate faster than wands and rings, but there are more ways to fix them.",
        "The only way to obtain a Scroll of Wipe Out is to receive it as a gift from the dungeon spirits.",
        "No weapons allowed in the presence of His Majesty!",
        "Pixel-Mart. Special prices for demon hunters!"
    )
    private const val TXT_BURN = "As you try to read the sign it bursts into greenish flames."
    fun read(pos: Int) {
        if (Dungeon.level is DeadEndLevel) {
            GameScene.show(WndMessage(TXT_DEAD_END))
        } else {
            val index = Dungeon.depth - 1
            if (index < TIPS.size) {
                val heroClass = Dungeon.hero?.heroClass?.title() ?: "adventurer"
                val tip = LlmTextEnhancer.enhanceSignTip(Dungeon.depth, heroClass, TIPS[index])
                GameScene.show(WndMessage(tip))
            } else {
                Dungeon.level?.destroy(pos)
                GameScene.updateMap(pos)
                GameScene.discoverTile(pos, Terrain.SIGN)
                CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6)
                Sample.play(Assets.SND_BURNING)
                GLog.w(TXT_BURN)
            }
        }
    }
}

package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class BattleAxe : MeleeWeapon(4, 1.2f, 1f) {
    init {
        name = "battle axe"
        image = ItemSpriteSheet.BATTLE_AXE
    }
    override fun desc(): String {
        return "The enormous steel head of this battle axe puts considerable heft behind each stroke."
    }
}

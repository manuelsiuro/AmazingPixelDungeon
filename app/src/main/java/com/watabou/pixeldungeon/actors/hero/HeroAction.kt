package com.watabou.pixeldungeon.actors.hero
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.npcs.NPC
open class HeroAction {
    var dst: Int = 0
    class Move(dst: Int) : HeroAction() {
        init {
            this.dst = dst
        }
    }
    class PickUp(dst: Int) : HeroAction() {
        init {
            this.dst = dst
        }
    }
    class OpenChest(dst: Int) : HeroAction() {
        init {
            this.dst = dst
        }
    }
    class Buy(dst: Int) : HeroAction() {
        init {
            this.dst = dst
        }
    }
    class Interact(
        var npc: NPC
    ) : HeroAction()
    class Unlock(door: Int) : HeroAction() {
        init {
            this.dst = door
        }
    }
    class Descend(stairs: Int) : HeroAction() {
        init {
            this.dst = stairs
        }
    }
    class Ascend(stairs: Int) : HeroAction() {
        init {
            this.dst = stairs
        }
    }
    class Cook(pot: Int) : HeroAction() {
        init {
            this.dst = pot
        }
    }
    class Attack(
        var target: Char
    ) : HeroAction()
    class UseStation(dst: Int) : HeroAction() {
        init {
            this.dst = dst
        }
    }
}

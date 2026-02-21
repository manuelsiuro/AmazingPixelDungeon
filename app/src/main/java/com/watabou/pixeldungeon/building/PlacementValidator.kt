package com.watabou.pixeldungeon.building

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain

object PlacementValidator {
    private const val MAX_BUILDS_PER_FLOOR = 15

    fun validate(cell: Int, hero: Hero): String? {
        val level = Dungeon.level ?: return "No level loaded."
        if (!Level.adjacent(hero.pos, cell)) return "Too far away to build."
        val terrain = level.map[cell]
        if (terrain != Terrain.EMPTY && terrain != Terrain.GRASS &&
            terrain != Terrain.EMBERS && terrain != Terrain.EMPTY_SP &&
            terrain != Terrain.EMPTY_DECO) return "You can't build here."
        if (cell == level.entrance || cell == level.exit) return "You can't block the entrance or exit."
        if (Actor.findChar(cell) != null) return "Something is in the way."
        if (Dungeon.bossLevel()) return "The dungeon resists your construction."
        if (level.heaps[cell] != null) return "There are items in the way."
        if (level.buildCount >= MAX_BUILDS_PER_FLOOR) return "This floor has too many structures already."
        return null // valid
    }
}

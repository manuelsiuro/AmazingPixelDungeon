package com.watabou.pixeldungeon.items.crafting

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.building.PlacementValidator
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog

class SafeRoomBlueprint : MaterialItem() {
    init {
        name = "safe room blueprint"
        image = ItemSpriteSheet.SAFE_ROOM_BLUEPRINT
        stackable = false
        unique = true
        defaultAction = AC_PLACE
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_PLACE)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_PLACE) {
            curUser = hero
            curItem = this
            GameScene.selectCell(placer)
        } else {
            super.execute(hero, action)
        }
    }

    override fun price(): Int = 50

    override fun info(): String =
        "A detailed architectural blueprint for constructing a 5x5 safe room. " +
        "The room has reinforced walls and a door on the south side. " +
        "Enemies cannot spawn inside the safe zone."

    override fun desc(): String = info()

    companion object {
        const val AC_PLACE = "PLACE"
        private const val TIME_TO_PLACE = 2f
        private const val ROOM_RADIUS = 2 // 5x5 room: center +/- 2

        private fun getCells(center: Int): List<Int> {
            val cells = mutableListOf<Int>()
            val cx = center % Level.WIDTH
            val cy = center / Level.WIDTH
            for (dy in -ROOM_RADIUS..ROOM_RADIUS) {
                for (dx in -ROOM_RADIUS..ROOM_RADIUS) {
                    val x = cx + dx
                    val y = cy + dy
                    if (x < 0 || x >= Level.WIDTH || y < 0 || y >= Level.HEIGHT) continue
                    cells.add(x + y * Level.WIDTH)
                }
            }
            return cells
        }

        private fun isBorder(dx: Int, dy: Int): Boolean {
            return dx == -ROOM_RADIUS || dx == ROOM_RADIUS ||
                   dy == -ROOM_RADIUS || dy == ROOM_RADIUS
        }

        private fun isDoor(dx: Int, dy: Int): Boolean {
            // South center
            return dx == 0 && dy == ROOM_RADIUS
        }

        private fun isInterior(dx: Int, dy: Int): Boolean {
            return !isBorder(dx, dy)
        }

        private fun validateArea(center: Int, level: Level, heroPos: Int): String? {
            val cx = center % Level.WIDTH
            val cy = center / Level.WIDTH

            // Check bounds
            if (cx - ROOM_RADIUS < 1 || cx + ROOM_RADIUS >= Level.WIDTH - 1 ||
                cy - ROOM_RADIUS < 1 || cy + ROOM_RADIUS >= Level.HEIGHT - 1) {
                return "Not enough space to build the safe room here."
            }

            // Check all 25 cells
            for (dy in -ROOM_RADIUS..ROOM_RADIUS) {
                for (dx in -ROOM_RADIUS..ROOM_RADIUS) {
                    val cell = (cx + dx) + (cy + dy) * Level.WIDTH
                    val terrain = level.map[cell]
                    if (terrain != Terrain.EMPTY && terrain != Terrain.GRASS &&
                        terrain != Terrain.EMBERS && terrain != Terrain.EMPTY_SP &&
                        terrain != Terrain.EMPTY_DECO) {
                        return "The area is not clear enough to build here."
                    }
                    if (cell == level.entrance || cell == level.exit) {
                        return "You can't block the entrance or exit."
                    }
                    val charAtCell = Actor.findChar(cell)
                    if (charAtCell != null && cell != heroPos) {
                        return "Something is in the way."
                    }
                    if (level.heaps[cell] != null) {
                        return "There are items in the way."
                    }
                }
            }
            return null
        }

        private val placer = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) return
                val hero = curUser ?: return
                val item = curItem ?: return
                val level = Dungeon.level ?: return

                if (!Level.adjacent(hero.pos, cell)) {
                    GLog.w("Too far away to build.")
                    return
                }
                if (Dungeon.bossLevel()) {
                    GLog.w("The dungeon resists your construction.")
                    return
                }
                if (level.buildCount >= 15) {
                    GLog.w("This floor has too many structures already.")
                    return
                }

                val error = validateArea(cell, level, hero.pos)
                if (error != null) {
                    GLog.w(error)
                    return
                }

                val cx = cell % Level.WIDTH
                val cy = cell / Level.WIDTH

                // Place the room
                for (dy in -ROOM_RADIUS..ROOM_RADIUS) {
                    for (dx in -ROOM_RADIUS..ROOM_RADIUS) {
                        val pos = (cx + dx) + (cy + dy) * Level.WIDTH
                        when {
                            isDoor(dx, dy) -> {
                                Level.set(pos, Terrain.SAFE_ROOM_DOOR)
                            }
                            isBorder(dx, dy) -> {
                                Level.set(pos, Terrain.SAFE_ROOM_WALL)
                            }
                            else -> {
                                // Interior - add to safe zones
                                level.safeZones.add(pos)
                            }
                        }
                        GameScene.updateMap(pos)
                    }
                }

                level.buildCount++

                CellEmitter.get(cell).burst(Speck.factory(Speck.ROCK), 6)
                Sample.play(Assets.SND_ROCKS)
                Dungeon.observe()

                item.detach(hero.belongings.backpack)
                hero.spend(TIME_TO_PLACE)
                hero.busy()
                hero.sprite?.operate(cell)

                GLog.p("You construct a safe room!")
            }

            override fun prompt(): String = "Choose the center tile for the safe room"
        }
    }
}

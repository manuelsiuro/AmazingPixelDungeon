package com.watabou.pixeldungeon.levels
object Terrain {
    const val CHASM = 0
    const val EMPTY = 1
    const val GRASS = 2
    const val EMPTY_WELL = 3
    const val WALL = 4
    const val DOOR = 5
    const val OPEN_DOOR = 6
    const val ENTRANCE = 7
    const val EXIT = 8
    const val EMBERS = 9
    const val LOCKED_DOOR = 10
    const val PEDESTAL = 11
    const val WALL_DECO = 12
    const val BARRICADE = 13
    const val EMPTY_SP = 14
    const val HIGH_GRASS = 15
    const val EMPTY_DECO = 24
    const val LOCKED_EXIT = 25
    const val UNLOCKED_EXIT = 26
    const val SIGN = 29
    const val WELL = 34
    const val STATUE = 35
    const val STATUE_SP = 36
    const val BOOKSHELF = 41
    const val ALCHEMY = 42
    const val CHASM_FLOOR = 43
    const val CHASM_FLOOR_SP = 44
    const val CHASM_WALL = 45
    const val CHASM_WATER = 46
    const val CRAFTING_TABLE = 64
    const val FURNACE = 65
    const val ENCHANTING_TABLE = 66
    const val ANVIL = 67
    const val SECRET_DOOR = 16
    const val TOXIC_TRAP = 17
    const val SECRET_TOXIC_TRAP = 18
    const val FIRE_TRAP = 19
    const val SECRET_FIRE_TRAP = 20
    const val PARALYTIC_TRAP = 21
    const val SECRET_PARALYTIC_TRAP = 22
    const val INACTIVE_TRAP = 23
    const val POISON_TRAP = 27
    const val SECRET_POISON_TRAP = 28
    const val ALARM_TRAP = 30
    const val SECRET_ALARM_TRAP = 31
    const val LIGHTNING_TRAP = 32
    const val SECRET_LIGHTNING_TRAP = 33
    const val GRIPPING_TRAP = 37
    const val SECRET_GRIPPING_TRAP = 38
    const val SUMMONING_TRAP = 39
    const val SECRET_SUMMONING_TRAP = 40
    const val WATER_TILES = 48
    const val WATER = 63
    const val PASSABLE = 0x01
    const val LOS_BLOCKING = 0x02
    const val FLAMABLE = 0x04
    const val SECRET = 0x08
    const val SOLID = 0x10
    const val AVOID = 0x20
    const val LIQUID = 0x40
    const val PIT = 0x80
    const val UNSTITCHABLE = 0x100
    val flags = IntArray(256)
    init {
        flags[CHASM] = AVOID or PIT or UNSTITCHABLE
        flags[EMPTY] = PASSABLE
        flags[GRASS] = PASSABLE or FLAMABLE
        flags[EMPTY_WELL] = PASSABLE
        flags[WATER] = PASSABLE or LIQUID or UNSTITCHABLE
        flags[WALL] = LOS_BLOCKING or SOLID or UNSTITCHABLE
        flags[DOOR] = PASSABLE or LOS_BLOCKING or FLAMABLE or SOLID or UNSTITCHABLE
        flags[OPEN_DOOR] = PASSABLE or FLAMABLE or UNSTITCHABLE
        flags[ENTRANCE] = PASSABLE/* | SOLID*/
        flags[EXIT] = PASSABLE
        flags[EMBERS] = PASSABLE
        flags[LOCKED_DOOR] = LOS_BLOCKING or SOLID or UNSTITCHABLE
        flags[PEDESTAL] = PASSABLE or UNSTITCHABLE
        flags[WALL_DECO] = flags[WALL]
        flags[BARRICADE] = FLAMABLE or SOLID or LOS_BLOCKING
        flags[EMPTY_SP] = flags[EMPTY] or UNSTITCHABLE
        flags[HIGH_GRASS] = PASSABLE or LOS_BLOCKING or FLAMABLE
        flags[EMPTY_DECO] = flags[EMPTY]
        flags[LOCKED_EXIT] = SOLID
        flags[UNLOCKED_EXIT] = PASSABLE
        flags[SIGN] = PASSABLE or FLAMABLE
        flags[WELL] = AVOID
        flags[STATUE] = SOLID
        flags[STATUE_SP] = flags[STATUE] or UNSTITCHABLE
        flags[BOOKSHELF] = flags[BARRICADE] or UNSTITCHABLE
        flags[ALCHEMY] = PASSABLE
        flags[CHASM_WALL] = flags[CHASM]
        flags[CHASM_FLOOR] = flags[CHASM]
        flags[CHASM_FLOOR_SP] = flags[CHASM]
        flags[CHASM_WATER] = flags[CHASM]
        flags[SECRET_DOOR] = flags[WALL] or SECRET or UNSTITCHABLE
        flags[TOXIC_TRAP] = AVOID
        flags[SECRET_TOXIC_TRAP] = flags[EMPTY] or SECRET
        flags[FIRE_TRAP] = AVOID
        flags[SECRET_FIRE_TRAP] = flags[EMPTY] or SECRET
        flags[PARALYTIC_TRAP] = AVOID
        flags[SECRET_PARALYTIC_TRAP] = flags[EMPTY] or SECRET
        flags[POISON_TRAP] = AVOID
        flags[SECRET_POISON_TRAP] = flags[EMPTY] or SECRET
        flags[ALARM_TRAP] = AVOID
        flags[SECRET_ALARM_TRAP] = flags[EMPTY] or SECRET
        flags[LIGHTNING_TRAP] = AVOID
        flags[SECRET_LIGHTNING_TRAP] = flags[EMPTY] or SECRET
        flags[GRIPPING_TRAP] = AVOID
        flags[SECRET_GRIPPING_TRAP] = flags[EMPTY] or SECRET
        flags[SUMMONING_TRAP] = AVOID
        flags[SECRET_SUMMONING_TRAP] = flags[EMPTY] or SECRET
        flags[INACTIVE_TRAP] = flags[EMPTY]
        flags[CRAFTING_TABLE] = SOLID
        flags[FURNACE] = SOLID
        flags[ENCHANTING_TABLE] = SOLID
        flags[ANVIL] = SOLID
        for (i in WATER_TILES until WATER_TILES + 16) {
            flags[i] = flags[WATER]
        }
    }
    fun discover(terr: Int): Int {
        return when (terr) {
            SECRET_DOOR -> DOOR
            SECRET_FIRE_TRAP -> FIRE_TRAP
            SECRET_PARALYTIC_TRAP -> PARALYTIC_TRAP
            SECRET_TOXIC_TRAP -> TOXIC_TRAP
            SECRET_POISON_TRAP -> POISON_TRAP
            SECRET_ALARM_TRAP -> ALARM_TRAP
            SECRET_LIGHTNING_TRAP -> LIGHTNING_TRAP
            SECRET_GRIPPING_TRAP -> GRIPPING_TRAP
            SECRET_SUMMONING_TRAP -> SUMMONING_TRAP
            else -> terr
        }
    }
}

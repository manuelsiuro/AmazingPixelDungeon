package com.watabou.pixeldungeon.levels

enum class AxeTier(val choppingDamage: Int, val choppingTime: Float, val maxDurability: Int, val strReq: Int, val hungerDivisor: Float) {
    WOOD(1, 3f, 30, 10, 15f),
    STONE(2, 2.5f, 60, 12, 12f),
    IRON(3, 2f, 120, 14, 10f),
    DIAMOND(4, 1.5f, 240, 16, 10f)
}

package com.watabou.pixeldungeon.items.food.farming

import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.farming.*
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndBag
import com.watabou.utils.Bundle

class PlanterBox : Item() {

    private var seedClassName: String? = null
    private var cropName: String? = null
    private var turnsRemaining: Float = 0f
    private var plantedCropType: CropType? = null

    init {
        name = "planter box"
        image = ItemSpriteSheet.PLANTER_BOX
        defaultAction = AC_PLANT_SEED
        unique = true
        stackable = false
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (plantedCropType == null) {
            actions.add(AC_PLANT_SEED)
        } else if (turnsRemaining <= 0f) {
            actions.add(AC_HARVEST)
        }
        actions.add(AC_CHECK)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        defaultAction = when {
            plantedCropType == null -> AC_PLANT_SEED
            turnsRemaining <= 0f -> AC_HARVEST
            else -> AC_CHECK
        }
        when (action) {
            AC_PLANT_SEED -> {
                curUser = hero
                curItem = this
                val backpack = hero.belongings.backpack
                // Find a crop seed in backpack
                var seed: CropSeed? = null
                for (item in backpack.items) {
                    if (item is CropSeed) {
                        seed = item
                        break
                    }
                }
                // Also check sub-bags
                if (seed == null) {
                    for (item in backpack.items) {
                        if (item is com.watabou.pixeldungeon.items.bags.Bag) {
                            for (subItem in item.items) {
                                if (subItem is CropSeed) {
                                    seed = subItem
                                    break
                                }
                            }
                            if (seed != null) break
                        }
                    }
                }

                if (seed != null) {
                    plantSeed(hero, seed)
                } else {
                    GLog.w("You don't have any crop seeds.")
                }
            }
            AC_HARVEST -> {
                if (turnsRemaining <= 0f && plantedCropType != null) {
                    val cropType = plantedCropType!!
                    val count = com.watabou.utils.Random.IntRange(cropType.minYield, cropType.maxYield)
                    for (i in 0 until count) {
                        val produce = when (cropType) {
                            CropType.WHEAT -> Wheat()
                            CropType.CARROT -> Carrot()
                            CropType.POTATO -> Potato()
                            CropType.MELON -> MelonSlice()
                        }
                        if (!produce.collect(hero.belongings.backpack)) {
                            com.watabou.pixeldungeon.Dungeon.level?.drop(produce, hero.pos)?.sprite?.drop()
                        }
                    }
                    GLog.i("You harvest %s from the planter box.", cropType.cropName.lowercase())
                    plantedCropType = null
                    seedClassName = null
                    cropName = null
                    turnsRemaining = 0f
                    hero.spend(1f)
                    hero.busy()
                    hero.sprite?.operate(hero.pos)
                } else {
                    GLog.w("The crop isn't ready yet.")
                }
            }
            AC_CHECK -> {
                if (plantedCropType == null) {
                    GLog.i("The planter box is empty.")
                } else if (turnsRemaining <= 0f) {
                    GLog.p("The %s is ready to harvest!", cropName ?: "crop")
                } else {
                    GLog.i("The %s needs about %d more turns to grow.",
                        cropName ?: "crop", turnsRemaining.toInt())
                }
            }
            else -> super.execute(hero, action)
        }
    }

    private fun plantSeed(hero: Hero, seed: CropSeed) {
        plantedCropType = seed.cropType
        seedClassName = seed.javaClass.name
        cropName = seed.cropType.cropName
        turnsRemaining = seed.cropType.growthTime.toFloat()

        seed.detach(hero.belongings.backpack)
        GLog.i("You plant %s in the planter box.", seed.name)
        hero.spend(1f)
        hero.busy()
        hero.sprite?.operate(hero.pos)
    }

    fun onTurnSpent(time: Float) {
        if (plantedCropType != null && turnsRemaining > 0f) {
            turnsRemaining -= time
            if (turnsRemaining < 0f) turnsRemaining = 0f
        }
    }

    override fun name(): String {
        return if (plantedCropType == null) {
            "planter box"
        } else if (turnsRemaining <= 0f) {
            "planter box (${cropName} - ready!)"
        } else {
            "planter box (${cropName})"
        }
    }

    override fun status(): String? {
        return if (plantedCropType != null && turnsRemaining <= 0f) {
            "!"
        } else if (plantedCropType != null) {
            "${turnsRemaining.toInt()}"
        } else {
            null
        }
    }

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    override fun info(): String =
        "A portable planter box. Plant a crop seed inside and it will grow as you explore."

    override fun desc(): String = info()

    override fun price(): Int = 15

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(SEED_CLASS, seedClassName ?: "")
        bundle.put(CROP_NAME, cropName ?: "")
        bundle.put(TURNS_LEFT, turnsRemaining)
        if (plantedCropType != null) {
            bundle.put(CROP_TYPE, plantedCropType!!.name)
        }
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        seedClassName = bundle.getString(SEED_CLASS).ifEmpty { null }
        cropName = bundle.getString(CROP_NAME).ifEmpty { null }
        turnsRemaining = bundle.getFloat(TURNS_LEFT)
        val ctName = bundle.getString(CROP_TYPE)
        plantedCropType = if (ctName.isNotEmpty()) {
            try { CropType.valueOf(ctName) } catch (e: Exception) { null }
        } else null
    }

    companion object {
        const val AC_PLANT_SEED = "PLANT SEED"
        const val AC_HARVEST = "HARVEST"
        const val AC_CHECK = "CHECK"
        private const val SEED_CLASS = "seedClass"
        private const val CROP_NAME = "cropName"
        private const val TURNS_LEFT = "turnsLeft"
        private const val CROP_TYPE = "cropType"
    }
}

package com.watabou.pixeldungeon.crafting

/**
 * Placeholder for Phase 2+ real-time smelting.
 * Currently smelting is handled instantly via CraftingManager/WndFurnace.
 */
object SmeltingManager {

    private val activeJobs = ArrayList<SmeltingJob>()

    fun start(job: SmeltingJob) {
        activeJobs.add(job)
    }

    fun jobs(): List<SmeltingJob> = activeJobs

    fun clear() {
        activeJobs.clear()
    }
}

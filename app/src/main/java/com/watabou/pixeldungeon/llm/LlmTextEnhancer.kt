package com.watabou.pixeldungeon.llm

import android.util.Log
import com.watabou.pixeldungeon.PixelDungeon

object LlmTextEnhancer {

    private const val TAG = "LLM"
    private const val MAX_NPC_LENGTH = 500
    private const val MAX_COMBAT_LENGTH = 200
    private const val MAX_ITEM_LENGTH = 400

    fun enhanceNpcDialog(
        npcName: String,
        questState: String,
        heroClass: String,
        depth: Int,
        fallbackText: String
    ): String {
        Log.d(TAG, "enhanceNpcDialog: npc=$npcName quest=$questState llmAvail=${LlmManager.isAvailable()}")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmNpcDialog() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceNpcDialog SKIP (disabled or model not ready)")
            return fallbackText
        }
        return try {
            val cacheKey = LlmResponseCache.key("npc", npcName, questState, heroClass, depth.toString())
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceNpcDialog CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceNpcDialog CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.npcDialog(npcName, questState, heroClass, depth, fallbackText)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_NPC_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceNpcDialog GENERATED (len=${sanitized.length}): ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceNpcDialog GENERATE FAILED (null result)")
                }
            }
            fallbackText
        } catch (e: Exception) {
            Log.e(TAG, "enhanceNpcDialog ERROR", e)
            PixelDungeon.reportException(e)
            fallbackText
        }
    }

    fun generateFloorNarration(
        regionName: String,
        depth: Int,
        heroClass: String,
        fallbackText: String
    ): String {
        Log.d(TAG, "floorNarration: region=$regionName depth=$depth")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmNarration() || !LlmManager.isAvailable()) {
            Log.d(TAG, "floorNarration SKIP (disabled or model not ready)")
            return fallbackText
        }
        return try {
            val cacheKey = LlmResponseCache.key("floor", regionName, depth.toString(), heroClass)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "floorNarration CACHE HIT")
                return cached
            }

            Log.d(TAG, "floorNarration CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.floorNarration(regionName, depth, heroClass, fallbackText)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_NPC_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "floorNarration GENERATED (len=${sanitized.length}): ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "floorNarration GENERATE FAILED (null result)")
                }
            }
            fallbackText
        } catch (e: Exception) {
            Log.e(TAG, "floorNarration ERROR", e)
            PixelDungeon.reportException(e)
            fallbackText
        }
    }

    fun enhanceItemInfo(
        itemName: String,
        itemType: String,
        level: Int,
        enchantment: String?,
        cursed: Boolean,
        fallbackDesc: String
    ): String {
        Log.d(TAG, "enhanceItemInfo: item=$itemName type=$itemType")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmItemDesc() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceItemInfo SKIP (disabled or model not ready)")
            return fallbackDesc
        }
        return try {
            val cacheKey = LlmResponseCache.key(
                "item", itemName, itemType, level.toString(),
                enchantment ?: "", cursed.toString()
            )
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceItemInfo CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceItemInfo CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.itemDescription(itemName, itemType, level, enchantment, cursed, fallbackDesc)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_ITEM_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceItemInfo GENERATED (len=${sanitized.length}): ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceItemInfo GENERATE FAILED (null result)")
                }
            }
            fallbackDesc
        } catch (e: Exception) {
            Log.e(TAG, "enhanceItemInfo ERROR", e)
            PixelDungeon.reportException(e)
            fallbackDesc
        }
    }

    fun enhanceCombatMessage(originalMessage: String): String? {
        Log.d(TAG, "enhanceCombatMessage: msg=$originalMessage")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmCombatNarration() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceCombatMessage SKIP (disabled or model not ready)")
            return null
        }
        return try {
            val cacheKey = LlmResponseCache.key("combat", originalMessage)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceCombatMessage CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceCombatMessage CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.combatNarration(originalMessage)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_COMBAT_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceCombatMessage GENERATED (len=${sanitized.length}): ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceCombatMessage GENERATE FAILED (null result)")
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "enhanceCombatMessage ERROR", e)
            PixelDungeon.reportException(e)
            null
        }
    }

    fun preWarmCache(regionName: String, depth: Int, heroClass: String) {
        Log.d(TAG, "preWarmCache: region=$regionName depth=$depth")
        if (!PixelDungeon.llmEnabled() || !LlmManager.isAvailable()) {
            Log.d(TAG, "preWarmCache SKIP (disabled or model not ready)")
            return
        }
        try {
            // Pre-warm floor narration
            if (PixelDungeon.llmNarration()) {
                val cacheKey = LlmResponseCache.key("floor", regionName, depth.toString(), heroClass)
                if (LlmResponseCache.get(cacheKey) == null) {
                    val regionText = getRegionLore(regionName)
                    if (regionText != null) {
                        Log.d(TAG, "preWarmCache CACHE MISS, submitting async generation")
                        val prompt = LlmPromptBuilder.floorNarration(regionName, depth, heroClass, regionText)
                        LlmManager.generateText(prompt) { result ->
                            if (result != null) {
                                val sanitized = sanitize(result, MAX_NPC_LENGTH)
                                LlmResponseCache.put(cacheKey, sanitized)
                                Log.d(TAG, "preWarmCache GENERATED (len=${sanitized.length}): ${sanitized.take(80)}")
                            } else {
                                Log.d(TAG, "preWarmCache GENERATE FAILED (null result)")
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "preWarmCache CACHE HIT")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "preWarmCache ERROR", e)
            PixelDungeon.reportException(e)
        }
    }

    private fun getRegionLore(regionName: String): String? {
        return when (regionName) {
            "Sewers" -> "The upper levels of the dungeon, part of the city's sewer system. Dangerous but no evil magic."
            "Prison" -> "An underground prison for dangerous criminals. Dark miasma drove everyone insane."
            "Caves" -> "Sparsely populated caves below the prison. Only gnolls and animals dwell here."
            "Dwarven Metropolis" -> "Once the greatest dwarven city-state. Victory over demons brought seeds of corruption."
            "Demon Halls" -> "Former outskirts of Metropolis, now controlled by demons. Very few adventurers reach here."
            else -> null
        }
    }

    private fun sanitize(text: String, maxLength: Int): String {
        var result = text.trim()
        if (result.length > maxLength) {
            result = result.substring(0, maxLength)
            val lastPeriod = result.lastIndexOf('.')
            if (lastPeriod > maxLength / 2) {
                result = result.substring(0, lastPeriod + 1)
            }
        }
        return result
    }
}

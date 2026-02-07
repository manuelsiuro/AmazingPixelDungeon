package com.watabou.pixeldungeon.llm

import android.util.Log
import com.watabou.pixeldungeon.PixelDungeon

object LlmTextEnhancer {

    private const val TAG = "LLM"
    private const val MAX_NPC_LENGTH = 500
    private const val MAX_COMBAT_LENGTH = 200
    private const val MAX_ITEM_LENGTH = 400
    private const val MAX_EPITAPH_LENGTH = 300
    private const val MAX_STORY_LENGTH = 500
    private const val MAX_BOSS_LENGTH = 200
    private const val MAX_FEELING_LENGTH = 200
    private const val MAX_SIGN_LENGTH = 300
    private const val MAX_MOB_LENGTH = 400
    private const val MAX_BADGE_LENGTH = 200
    private const val MAX_PLANT_LENGTH = 300
    private const val MAX_CELL_LENGTH = 200

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

    // Phase 1: Story Moments

    fun generateDeathEpitaph(
        causeDesc: String,
        heroClass: String,
        depth: Int,
        heroLevel: Int,
        fallback: String
    ): String {
        Log.d(TAG, "generateDeathEpitaph: cause=$causeDesc class=$heroClass depth=$depth")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmStoryMoments() || !LlmManager.isAvailable()) {
            Log.d(TAG, "generateDeathEpitaph SKIP")
            return fallback
        }
        return try {
            val cacheKey = LlmResponseCache.key("epitaph", causeDesc, heroClass, depth.toString(), heroLevel.toString())
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "generateDeathEpitaph CACHE HIT")
                return cached
            }

            // Async generation — avoid blocking GL thread (MediaPipe is not thread-safe)
            Log.d(TAG, "generateDeathEpitaph CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.deathEpitaph(causeDesc, heroClass, depth, heroLevel)
            LlmManager.generateText(prompt, 96, LlmManager.Priority.HIGH) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_EPITAPH_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "generateDeathEpitaph GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "generateDeathEpitaph GENERATE FAILED")
                }
            }
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "generateDeathEpitaph ERROR", e)
            PixelDungeon.reportException(e)
            fallback
        }
    }

    fun generateIntroNarration(heroClass: String, fallback: String): String {
        Log.d(TAG, "generateIntroNarration: class=$heroClass")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmStoryMoments() || !LlmManager.isAvailable()) {
            Log.d(TAG, "generateIntroNarration SKIP")
            return fallback
        }
        return try {
            val cacheKey = LlmResponseCache.key("intro", heroClass)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "generateIntroNarration CACHE HIT")
                return cached
            }

            Log.d(TAG, "generateIntroNarration CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.introNarration(heroClass, fallback)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_STORY_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "generateIntroNarration GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "generateIntroNarration GENERATE FAILED")
                }
            }
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "generateIntroNarration ERROR", e)
            PixelDungeon.reportException(e)
            fallback
        }
    }

    fun generateVictoryNarration(heroClass: String, fallback: String): String {
        Log.d(TAG, "generateVictoryNarration: class=$heroClass")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmStoryMoments() || !LlmManager.isAvailable()) {
            Log.d(TAG, "generateVictoryNarration SKIP")
            return fallback
        }
        return try {
            val cacheKey = LlmResponseCache.key("victory", heroClass)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "generateVictoryNarration CACHE HIT")
                return cached
            }

            Log.d(TAG, "generateVictoryNarration CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.victoryNarration(heroClass, fallback)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_STORY_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "generateVictoryNarration GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "generateVictoryNarration GENERATE FAILED")
                }
            }
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "generateVictoryNarration ERROR", e)
            PixelDungeon.reportException(e)
            fallback
        }
    }

    // Phase 2: Boss Encounters

    fun enhanceBossDialog(
        bossName: String,
        dialogType: String,
        heroClass: String,
        depth: Int,
        fallback: String
    ): String {
        Log.d(TAG, "enhanceBossDialog: boss=$bossName type=$dialogType")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmBossEncounters() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceBossDialog SKIP")
            return fallback
        }
        return try {
            val cacheKey = LlmResponseCache.key("boss", bossName, dialogType, heroClass)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceBossDialog CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceBossDialog CACHE MISS, submitting async generation")
            val prompt = when (dialogType) {
                "notice" -> LlmPromptBuilder.bossNotice(bossName, heroClass, depth, fallback)
                "death" -> LlmPromptBuilder.bossDeath(bossName, heroClass, fallback)
                "summon" -> LlmPromptBuilder.bossSummon(bossName, fallback)
                else -> LlmPromptBuilder.bossNotice(bossName, heroClass, depth, fallback)
            }
            LlmManager.generateText(prompt, LlmConfig.DEFAULT_MAX_TOKENS, LlmManager.Priority.HIGH) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_BOSS_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceBossDialog GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceBossDialog GENERATE FAILED")
                }
            }
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "enhanceBossDialog ERROR", e)
            PixelDungeon.reportException(e)
            fallback
        }
    }

    // Phase 3: Enhanced Atmosphere

    fun enhanceLevelFeeling(
        feelingType: String,
        regionName: String,
        depth: Int,
        heroClass: String,
        fallback: String
    ): String {
        Log.d(TAG, "enhanceLevelFeeling: type=$feelingType region=$regionName depth=$depth")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmNarration() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceLevelFeeling SKIP")
            return fallback
        }
        return try {
            val cacheKey = LlmResponseCache.key("feeling", feelingType, regionName, depth.toString())
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceLevelFeeling CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceLevelFeeling CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.levelFeeling(feelingType, regionName, depth, heroClass, fallback)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_FEELING_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceLevelFeeling GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceLevelFeeling GENERATE FAILED")
                }
            }
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "enhanceLevelFeeling ERROR", e)
            PixelDungeon.reportException(e)
            fallback
        }
    }

    fun enhanceSignTip(depth: Int, heroClass: String, fallbackTip: String): String {
        Log.d(TAG, "enhanceSignTip: depth=$depth")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmNarration() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceSignTip SKIP")
            return fallbackTip
        }
        return try {
            val cacheKey = LlmResponseCache.key("sign", depth.toString(), heroClass)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceSignTip CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceSignTip CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.signTip(depth, heroClass, fallbackTip)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_SIGN_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceSignTip GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceSignTip GENERATE FAILED")
                }
            }
            fallbackTip
        } catch (e: Exception) {
            Log.e(TAG, "enhanceSignTip ERROR", e)
            PixelDungeon.reportException(e)
            fallbackTip
        }
    }

    fun enhanceMobDescription(
        mobName: String,
        mobState: String,
        depth: Int,
        fallbackDesc: String
    ): String {
        Log.d(TAG, "enhanceMobDescription: mob=$mobName state=$mobState")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmBestiary() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceMobDescription SKIP")
            return fallbackDesc
        }
        return try {
            val cacheKey = LlmResponseCache.key("mob", mobName, mobState)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceMobDescription CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceMobDescription CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.mobDescription(mobName, mobState, depth, fallbackDesc)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_MOB_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceMobDescription GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceMobDescription GENERATE FAILED")
                }
            }
            fallbackDesc
        } catch (e: Exception) {
            Log.e(TAG, "enhanceMobDescription ERROR", e)
            PixelDungeon.reportException(e)
            fallbackDesc
        }
    }

    // Phase 4: Celebrations & Interactions

    fun enhanceBadgeText(
        badgeName: String,
        heroClass: String,
        fallbackDesc: String
    ): String {
        Log.d(TAG, "enhanceBadgeText: badge=$badgeName")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmBestiary() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceBadgeText SKIP")
            return fallbackDesc
        }
        return try {
            val cacheKey = LlmResponseCache.key("badge", badgeName, heroClass)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceBadgeText CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceBadgeText CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.badgeText(badgeName, heroClass, fallbackDesc)
            LlmManager.generateText(prompt, 48) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_BADGE_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceBadgeText GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceBadgeText GENERATE FAILED")
                }
            }
            fallbackDesc
        } catch (e: Exception) {
            Log.e(TAG, "enhanceBadgeText ERROR", e)
            PixelDungeon.reportException(e)
            fallbackDesc
        }
    }

    fun enhanceResurrectionText(heroClass: String, fallback: String): String {
        Log.d(TAG, "enhanceResurrectionText: class=$heroClass")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmStoryMoments() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceResurrectionText SKIP")
            return fallback
        }
        return try {
            val cacheKey = LlmResponseCache.key("resurrect", heroClass)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceResurrectionText CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceResurrectionText CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.resurrectionText(heroClass, fallback)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_STORY_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceResurrectionText GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceResurrectionText GENERATE FAILED")
                }
            }
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "enhanceResurrectionText ERROR", e)
            PixelDungeon.reportException(e)
            fallback
        }
    }

    fun enhanceShopkeeperGreeting(
        shopkeeperName: String,
        heroClass: String,
        depth: Int,
        fallback: String
    ): String {
        Log.d(TAG, "enhanceShopkeeperGreeting: name=$shopkeeperName")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmNpcDialog() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceShopkeeperGreeting SKIP")
            return fallback
        }
        return try {
            val cacheKey = LlmResponseCache.key("shopkeeper", shopkeeperName, heroClass, depth.toString())
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceShopkeeperGreeting CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceShopkeeperGreeting CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.shopkeeperGreeting(shopkeeperName, heroClass, depth, fallback)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_NPC_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceShopkeeperGreeting GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceShopkeeperGreeting GENERATE FAILED")
                }
            }
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "enhanceShopkeeperGreeting ERROR", e)
            PixelDungeon.reportException(e)
            fallback
        }
    }

    // Phase 5: Content Polish

    fun enhancePlantDescription(plantName: String, fallbackDesc: String): String {
        Log.d(TAG, "enhancePlantDescription: plant=$plantName")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmItemDesc() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhancePlantDescription SKIP")
            return fallbackDesc
        }
        return try {
            val cacheKey = LlmResponseCache.key("plant", plantName)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhancePlantDescription CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhancePlantDescription CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.plantDescription(plantName, fallbackDesc)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_PLANT_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhancePlantDescription GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhancePlantDescription GENERATE FAILED")
                }
            }
            fallbackDesc
        } catch (e: Exception) {
            Log.e(TAG, "enhancePlantDescription ERROR", e)
            PixelDungeon.reportException(e)
            fallbackDesc
        }
    }

    fun enhanceBuffDescription(buffName: String, fallbackDesc: String): String {
        Log.d(TAG, "enhanceBuffDescription: buff=$buffName")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmBestiary() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceBuffDescription SKIP")
            return fallbackDesc
        }
        return try {
            val cacheKey = LlmResponseCache.key("buff", buffName)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceBuffDescription CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceBuffDescription CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.buffDescription(buffName, fallbackDesc)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_BADGE_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceBuffDescription GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceBuffDescription GENERATE FAILED")
                }
            }
            fallbackDesc
        } catch (e: Exception) {
            Log.e(TAG, "enhanceBuffDescription ERROR", e)
            PixelDungeon.reportException(e)
            fallbackDesc
        }
    }

    fun enhanceCellDescription(tileName: String, fallbackDesc: String): String {
        Log.d(TAG, "enhanceCellDescription: tile=$tileName")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmBestiary() || !LlmManager.isAvailable()) {
            Log.d(TAG, "enhanceCellDescription SKIP")
            return fallbackDesc
        }
        return try {
            val cacheKey = LlmResponseCache.key("cell", tileName)
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "enhanceCellDescription CACHE HIT")
                return cached
            }

            Log.d(TAG, "enhanceCellDescription CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.cellDescription(tileName, fallbackDesc)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_CELL_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "enhanceCellDescription GENERATED: ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "enhanceCellDescription GENERATE FAILED")
                }
            }
            fallbackDesc
        } catch (e: Exception) {
            Log.e(TAG, "enhanceCellDescription ERROR", e)
            PixelDungeon.reportException(e)
            fallbackDesc
        }
    }

    // AI Quest NPCs

    fun generateAiQuestText(
        npcName: String,
        personality: String,
        questType: String,
        targetDesc: String,
        heroClass: String,
        depth: Int,
        fallback: String
    ): String {
        Log.d(TAG, "generateAiQuestText: npc=$npcName quest=$questType")
        if (!PixelDungeon.llmEnabled() || !PixelDungeon.llmAiNpcQuests() || !LlmManager.isAvailable()) {
            Log.d(TAG, "generateAiQuestText SKIP")
            return fallback
        }
        return try {
            val cacheKey = LlmResponseCache.key("aiquest", npcName, questType, heroClass, depth.toString())
            val cached = LlmResponseCache.get(cacheKey)
            if (cached != null) {
                Log.d(TAG, "generateAiQuestText CACHE HIT")
                return cached
            }

            Log.d(TAG, "generateAiQuestText CACHE MISS, submitting async generation")
            val prompt = LlmPromptBuilder.aiQuestDescription(npcName, personality, questType, targetDesc, heroClass, depth, fallback)
            LlmManager.generateText(prompt) { result ->
                if (result != null) {
                    val sanitized = sanitize(result, MAX_NPC_LENGTH)
                    LlmResponseCache.put(cacheKey, sanitized)
                    Log.d(TAG, "generateAiQuestText GENERATED (len=${sanitized.length}): ${sanitized.take(80)}")
                } else {
                    Log.d(TAG, "generateAiQuestText GENERATE FAILED (null result)")
                }
            }
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "generateAiQuestText ERROR", e)
            PixelDungeon.reportException(e)
            fallback
        }
    }

    // Pre-warm cache

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
                        Log.d(TAG, "preWarmCache floor CACHE MISS, submitting async generation")
                        val prompt = LlmPromptBuilder.floorNarration(regionName, depth, heroClass, regionText)
                        LlmManager.generateText(prompt) { result ->
                            if (result != null) {
                                val sanitized = sanitize(result, MAX_NPC_LENGTH)
                                LlmResponseCache.put(cacheKey, sanitized)
                                Log.d(TAG, "preWarmCache floor GENERATED: ${sanitized.take(80)}")
                            }
                        }
                    }
                }

                // Pre-warm level feelings
                val feelings = arrayOf("chasm", "water", "grass", "secrets")
                for (feeling in feelings) {
                    val feelingKey = LlmResponseCache.key("feeling", feeling, regionName, depth.toString())
                    if (LlmResponseCache.get(feelingKey) == null) {
                        val fallback = getFeelingFallback(feeling)
                        val prompt = LlmPromptBuilder.levelFeeling(feeling, regionName, depth, heroClass, fallback)
                        LlmManager.generateText(prompt, LlmConfig.DEFAULT_MAX_TOKENS, LlmManager.Priority.LOW) { result ->
                            if (result != null) {
                                val sanitized = sanitize(result, MAX_FEELING_LENGTH)
                                LlmResponseCache.put(feelingKey, sanitized)
                                Log.d(TAG, "preWarmCache feeling=$feeling GENERATED: ${sanitized.take(80)}")
                            }
                        }
                    }
                }
            }

            // Pre-warm boss dialog on boss floors
            if (PixelDungeon.llmBossEncounters() && depth % 5 == 0 && depth <= 25) {
                val bossName = getBossForDepth(depth) ?: return
                val dialogTypes = if (bossName == "King of Dwarves") {
                    arrayOf("notice", "death", "summon")
                } else {
                    arrayOf("notice", "death")
                }
                for (type in dialogTypes) {
                    val bossKey = LlmResponseCache.key("boss", bossName, type, heroClass)
                    if (LlmResponseCache.get(bossKey) == null) {
                        val fallback = getBossDialogFallback(bossName, type)
                        val prompt = when (type) {
                            "notice" -> LlmPromptBuilder.bossNotice(bossName, heroClass, depth, fallback)
                            "death" -> LlmPromptBuilder.bossDeath(bossName, heroClass, fallback)
                            "summon" -> LlmPromptBuilder.bossSummon(bossName, fallback)
                            else -> continue
                        }
                        LlmManager.generateText(prompt, LlmConfig.DEFAULT_MAX_TOKENS, LlmManager.Priority.CRITICAL) { result ->
                            if (result != null) {
                                val sanitized = sanitize(result, MAX_BOSS_LENGTH)
                                LlmResponseCache.put(bossKey, sanitized)
                                Log.d(TAG, "preWarmCache boss=$bossName type=$type GENERATED: ${sanitized.take(80)}")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "preWarmCache ERROR", e)
            PixelDungeon.reportException(e)
        }
    }

    private fun getBossForDepth(depth: Int): String? {
        return when (depth) {
            5 -> "Goo"
            10 -> "Tengu"
            15 -> "DM-300"
            20 -> "King of Dwarves"
            25 -> "Yog-Dzewa"
            else -> null
        }
    }

    private fun getBossDialogFallback(bossName: String, dialogType: String): String {
        return when (bossName) {
            "Goo" -> when (dialogType) {
                "notice" -> "GLURP-GLURP!"
                "death" -> "glurp... glurp..."
                else -> ""
            }
            "Tengu" -> when (dialogType) {
                "notice" -> "Gotcha!"
                "death" -> "Free at last..."
                else -> ""
            }
            "DM-300" -> when (dialogType) {
                "notice" -> "Unauthorised personnel detected."
                "death" -> "Mission failed. Shutting down."
                else -> ""
            }
            "King of Dwarves" -> when (dialogType) {
                "notice" -> "How dare you!"
                "death" -> "You cannot kill me... I am... immortal..."
                "summon" -> "Arise, slaves!"
                else -> ""
            }
            "Yog-Dzewa" -> when (dialogType) {
                "notice" -> "Hope is an illusion..."
                "death" -> "..."
                else -> ""
            }
            else -> ""
        }
    }

    private fun getFeelingFallback(feeling: String): String {
        return when (feeling) {
            "chasm" -> "Your steps echo across the dungeon."
            "water" -> "You hear the water splashing around you."
            "grass" -> "The smell of vegetation is thick in the air."
            "secrets" -> "The atmosphere hints that this floor hides many secrets."
            else -> ""
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

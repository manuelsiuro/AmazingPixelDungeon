package com.watabou.pixeldungeon
import android.content.SharedPreferences
import com.watabou.noosa.Game
object Preferences {
    const val KEY_LANDSCAPE = "landscape"
    const val KEY_IMMERSIVE = "immersive"
    const val KEY_GOOGLE_PLAY = "google_play"
    const val KEY_SCALE_UP = "scaleup"
    const val KEY_MUSIC = "music"
    const val KEY_SOUND_FX = "soundfx"
    const val KEY_ZOOM = "zoom"
    const val KEY_LAST_CLASS = "last_class"
    const val KEY_CHALLENGES = "challenges"
    const val KEY_DONATED = "donated"
    const val KEY_INTRO = "intro"
    const val KEY_BRIGHTNESS = "brightness"
    const val KEY_LLM_ENABLED = "llm_enabled"
    const val KEY_LLM_NPC_DIALOG = "llm_npc_dialog"
    const val KEY_LLM_NARRATION = "llm_narration"
    const val KEY_LLM_ITEM_DESC = "llm_item_desc"
    const val KEY_LLM_COMBAT_NARRATION = "llm_combat_narration"
    const val KEY_LLM_STORY_MOMENTS = "llm_story_moments"
    const val KEY_LLM_BOSS_ENCOUNTERS = "llm_boss_encounters"
    const val KEY_LLM_BESTIARY = "llm_bestiary"
    const val KEY_LLM_AI_NPC_QUESTS = "llm_ai_npc_quests"
    const val KEY_LLM_SELECTED_MODEL = "llm_selected_model"
    const val KEY_LLM_HF_TOKEN = "llm_hf_token"
    private var prefs: SharedPreferences? = null
    private fun get(): SharedPreferences {
        if (prefs == null) {
            prefs = Game.instance!!.getPreferences(android.content.Context.MODE_PRIVATE)
        }
        return prefs!!
    }
    fun getInt(key: String, defValue: Int): Int {
        return get().getInt(key, defValue)
    }
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return get().getBoolean(key, defValue)
    }
    fun getString(key: String, defValue: String): String {
        return get().getString(key, defValue) ?: defValue
    }
    fun put(key: String, value: Int) {
        get().edit().putInt(key, value).commit()
    }
    fun put(key: String, value: Boolean) {
        get().edit().putBoolean(key, value).commit()
    }
    fun put(key: String, value: String) {
        get().edit().putString(key, value).commit()
    }
}

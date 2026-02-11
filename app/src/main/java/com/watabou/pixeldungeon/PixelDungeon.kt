package com.watabou.pixeldungeon
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.llm.LlmManager
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.scenes.TitleScene
import javax.microedition.khronos.opengles.GL10
class PixelDungeon : Game(TitleScene::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateImmersiveMode()
        /*val metrics = resources.displayMetrics
        val landscape = metrics.widthPixels > metrics.heightPixels
        if (Preferences.getBoolean(Preferences.KEY_LANDSCAPE, false) != landscape) {
            landscape(!landscape)
        }*/
        Music.enable(music())
        Sample.enable(soundFx())
        Sample.load(
            Assets.SND_CLICK,
            Assets.SND_BADGE,
            Assets.SND_GOLD,
            Assets.SND_DESCEND,
            Assets.SND_STEP,
            Assets.SND_WATER,
            Assets.SND_OPEN,
            Assets.SND_UNLOCK,
            Assets.SND_ITEM,
            Assets.SND_DEWDROP,
            Assets.SND_HIT,
            Assets.SND_MISS,
            Assets.SND_EAT,
            Assets.SND_READ,
            Assets.SND_LULLABY,
            Assets.SND_DRINK,
            Assets.SND_SHATTER,
            Assets.SND_ZAP,
            Assets.SND_LIGHTNING,
            Assets.SND_LEVELUP,
            Assets.SND_DEATH,
            Assets.SND_CHALLENGE,
            Assets.SND_CURSED,
            Assets.SND_EVOKE,
            Assets.SND_TRAP,
            Assets.SND_TOMB,
            Assets.SND_ALERT,
            Assets.SND_MELD,
            Assets.SND_BOSS,
            Assets.SND_BLAST,
            Assets.SND_PLANT,
            Assets.SND_RAY,
            Assets.SND_BEACON,
            Assets.SND_TELEPORT,
            Assets.SND_CHARMS,
            Assets.SND_MASTERY,
            Assets.SND_PUFF,
            Assets.SND_ROCKS,
            Assets.SND_BURNING,
            Assets.SND_FALLING,
            Assets.SND_GHOST,
            Assets.SND_SECRET,
            Assets.SND_BONES,
            Assets.SND_BEE,
            Assets.SND_DEGRADE,
            Assets.SND_MIMIC
        )
    }
    override fun onPause() {
        super.onPause()
        LlmManager.unloadModel()
    }

    override fun onResume() {
        super.onResume()
        if (llmEnabled()) {
            LlmManager.loadModel()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            updateImmersiveMode()
        }
    }
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        if (immersiveModeChanged) {
            requestedReset = true
            immersiveModeChanged = false
        }
    }
    companion object {
        fun switchNoFade(c: Class<out PixelScene>) {
            PixelScene.noFade = true
            switchScene(c)
        }

        fun landscape(): Boolean {
            return width > height
        }
        // *** IMMERSIVE MODE ****
        private var immersiveModeChanged = false
        fun immerse(value: Boolean) {
            Preferences.put(Preferences.KEY_IMMERSIVE, value)
            instance?.runOnUiThread {
                updateImmersiveMode()
                immersiveModeChanged = true
            }
        }

        fun updateImmersiveMode() {
            try {
                val window = instance?.window ?: return
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                if (immersed()) {
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    WindowCompat.setDecorFitsSystemWindows(window, true)
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }
            } catch (e: Exception) {
                reportException(e)
            }
        }
        fun immersed(): Boolean {
            return Preferences.getBoolean(Preferences.KEY_IMMERSIVE, false)
        }
        // *****************************
        fun scaleUp(value: Boolean) {
            Preferences.put(Preferences.KEY_SCALE_UP, value)
            switchScene(TitleScene::class.java)
        }
        fun scaleUp(): Boolean {
            return Preferences.getBoolean(Preferences.KEY_SCALE_UP, true)
        }
        fun zoom(value: Int) {
            Preferences.put(Preferences.KEY_ZOOM, value)
        }
        fun zoom(): Int {
            return Preferences.getInt(Preferences.KEY_ZOOM, 0)
        }
        fun music(value: Boolean) {
            Music.enable(value)
            Preferences.put(Preferences.KEY_MUSIC, value)
        }
        fun music(): Boolean {
            return Preferences.getBoolean(Preferences.KEY_MUSIC, true)
        }
        fun soundFx(value: Boolean) {
            Sample.enable(value)
            Preferences.put(Preferences.KEY_SOUND_FX, value)
        }
        fun soundFx(): Boolean {
            return Preferences.getBoolean(Preferences.KEY_SOUND_FX, true)
        }
        fun brightness(value: Boolean) {
            Preferences.put(Preferences.KEY_BRIGHTNESS, value)
            if (scene() is GameScene) {
                (scene() as GameScene).brightness(value)
            }
        }
        fun brightness(): Boolean {
            return Preferences.getBoolean(Preferences.KEY_BRIGHTNESS, false)
        }
        fun lastClass(value: Int) {
            Preferences.put(Preferences.KEY_LAST_CLASS, value)
        }
        fun lastClass(): Int {
            return Preferences.getInt(Preferences.KEY_LAST_CLASS, 0)
        }
        fun challenges(value: Int) {
            Preferences.put(Preferences.KEY_CHALLENGES, value)
        }
        fun challenges(): Int {
            return Preferences.getInt(Preferences.KEY_CHALLENGES, 0)
        }
        fun intro(value: Boolean) {
            Preferences.put(Preferences.KEY_INTRO, value)
        }
        fun intro(): Boolean {
            return Preferences.getBoolean(Preferences.KEY_INTRO, true)
        }
        // LLM Preferences
        fun llmEnabled(): Boolean = Preferences.getBoolean(Preferences.KEY_LLM_ENABLED, false)
        fun llmEnabled(value: Boolean) { Preferences.put(Preferences.KEY_LLM_ENABLED, value) }

        fun llmNpcDialog(): Boolean = Preferences.getBoolean(Preferences.KEY_LLM_NPC_DIALOG, true)
        fun llmNpcDialog(value: Boolean) { Preferences.put(Preferences.KEY_LLM_NPC_DIALOG, value) }

        fun llmNarration(): Boolean = Preferences.getBoolean(Preferences.KEY_LLM_NARRATION, true)
        fun llmNarration(value: Boolean) { Preferences.put(Preferences.KEY_LLM_NARRATION, value) }

        fun llmItemDesc(): Boolean = Preferences.getBoolean(Preferences.KEY_LLM_ITEM_DESC, true)
        fun llmItemDesc(value: Boolean) { Preferences.put(Preferences.KEY_LLM_ITEM_DESC, value) }

        fun llmCombatNarration(): Boolean = Preferences.getBoolean(Preferences.KEY_LLM_COMBAT_NARRATION, false)
        fun llmCombatNarration(value: Boolean) { Preferences.put(Preferences.KEY_LLM_COMBAT_NARRATION, value) }

        fun llmStoryMoments(): Boolean = Preferences.getBoolean(Preferences.KEY_LLM_STORY_MOMENTS, true)
        fun llmStoryMoments(value: Boolean) { Preferences.put(Preferences.KEY_LLM_STORY_MOMENTS, value) }

        fun llmBossEncounters(): Boolean = Preferences.getBoolean(Preferences.KEY_LLM_BOSS_ENCOUNTERS, true)
        fun llmBossEncounters(value: Boolean) { Preferences.put(Preferences.KEY_LLM_BOSS_ENCOUNTERS, value) }

        fun llmBestiary(): Boolean = Preferences.getBoolean(Preferences.KEY_LLM_BESTIARY, false)
        fun llmBestiary(value: Boolean) { Preferences.put(Preferences.KEY_LLM_BESTIARY, value) }

        fun llmAiNpcQuests(): Boolean = Preferences.getBoolean(Preferences.KEY_LLM_AI_NPC_QUESTS, true)
        fun llmAiNpcQuests(value: Boolean) { Preferences.put(Preferences.KEY_LLM_AI_NPC_QUESTS, value) }

        fun llmSelectedModel(): String = Preferences.getString(Preferences.KEY_LLM_SELECTED_MODEL, "")
        fun llmSelectedModel(value: String) { Preferences.put(Preferences.KEY_LLM_SELECTED_MODEL, value) }

        fun llmHfToken(): String = Preferences.getString(Preferences.KEY_LLM_HF_TOKEN, "")
        fun llmHfToken(value: String) { Preferences.put(Preferences.KEY_LLM_HF_TOKEN, value) }

        /*
         * <--- Preferences
         */
        fun reportException(tr: Throwable) {
            Log.e("PD", Log.getStackTraceString(tr))
        }
    }
}

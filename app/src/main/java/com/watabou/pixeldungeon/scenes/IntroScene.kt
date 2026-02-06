package com.watabou.pixeldungeon.scenes
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.windows.WndStory
class IntroScene : PixelScene() {
    override fun create() {
        super.create()
        val heroClass = StartScene.curClass?.title() ?: Dungeon.hero?.heroClass?.title() ?: "adventurer"
        val text = LlmTextEnhancer.generateIntroNarration(heroClass, TEXT)
        add(object : WndStory(text) {
            override fun hide() {
                super.hide()
                Game.switchScene(InterlevelScene::class.java)
            }
        })
        fadeIn()
    }
    companion object {
        private const val TEXT =
                "Many heroes of all kinds ventured into the Dungeon before you. Some of them have returned with treasures and magical " +
                "artifacts, most have never been heard of since. But none have succeeded in retrieving the Amulet of Yendor, " +
                "which is told to be hidden in the depths of the Dungeon.\n\n" +
                "" +
                "You consider yourself ready for the challenge, but most importantly, you feel that fortune smiles on you. " +
                "It's time to start your own adventure!"
    }
}

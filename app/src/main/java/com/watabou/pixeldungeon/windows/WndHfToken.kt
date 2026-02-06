package com.watabou.pixeldungeon.windows

import android.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.PixelDungeon

object WndHfToken {

    fun show(onToken: (String) -> Unit) {
        val activity = Game.instance ?: return

        activity.runOnUiThread {
            val layout = LinearLayout(activity)
            layout.orientation = LinearLayout.VERTICAL
            val pad = (16 * activity.resources.displayMetrics.density).toInt()
            layout.setPadding(pad, pad / 2, pad, 0)

            val input = EditText(activity)
            input.hint = "hf_xxxxxxxxxxxxxxxxx"
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            input.setSingleLine()

            val savedToken = PixelDungeon.llmHfToken()
            if (savedToken.isNotBlank()) {
                input.setText(savedToken)
            }

            layout.addView(input)

            AlertDialog.Builder(activity)
                .setTitle("HuggingFace Token")
                .setMessage(
                    "This model requires a free HuggingFace account.\n\n" +
                    "1. Sign up at huggingface.co\n" +
                    "2. Accept the Gemma license on the model page\n" +
                    "3. Create a token at huggingface.co/settings/tokens\n" +
                    "4. Paste it below"
                )
                .setView(layout)
                .setPositiveButton("Download") { _, _ ->
                    val token = input.text.toString().trim()
                    if (token.isNotBlank()) {
                        PixelDungeon.llmHfToken(token)
                        onToken(token)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}

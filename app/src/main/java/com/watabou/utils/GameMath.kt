package com.watabou.utils
import com.watabou.noosa.Game
object GameMath {
    fun speed(speed: Float, acc: Float): Float {
        return if (acc != 0f) {
            speed + acc * Game.elapsed
        } else {
            speed
        }
    }
    fun gate(min: Float, value: Float, max: Float): Float {
        return if (value < min) {
            min
        } else if (value > max) {
            max
        } else {
            value
        }
    }
}

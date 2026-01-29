package com.watabou.noosa.tweeners
open class Delayer : Tweener {
    constructor() : super(null, 0f)
    constructor(time: Float) : super(null, time)
    override fun updateValues(progress: Float) {
    }
}

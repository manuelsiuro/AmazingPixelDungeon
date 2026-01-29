package com.watabou.pixeldungeon.ui
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Image
import com.watabou.noosa.TouchArea
import com.watabou.noosa.ui.Component
open class SimpleButton(image: Image) : Component() {
    private lateinit var image: Image
    init {
        // Here usage of 'image' refers to property or parameter?
        // Parameter name shadows property name.
        // So 'image.width' refers to parameter.
        // But we want to initialize property using parameter content?
        // Java code: this.image.copy(image).
        // createChildren makes new Image().
        // So property image gets new instance.
        // Then we copy content.
        // Wait, property `image` is initialized by `createChildren`.
        // Then we access it via `this.image`?
        // But in init block, `image` refers to parameter.
        this.image.copy(image)
        width = image.width.toFloat()
        height = image.height.toFloat()
    }
    override fun createChildren() {
        image = Image()
        add(image)
        add(object : TouchArea(image) {
            override fun onTouchDown(touch: Touch) {
                image.brightness(1.2f)
            }
            override fun onTouchUp(touch: Touch) {
                image.brightness(1.0f)
            }
            override fun onClick(touch: Touch) {
                this@SimpleButton.onClick()
            }
        })
    }
    override fun layout() {
        image.x = x
        image.y = y
    }
    protected open fun onClick() {}
}

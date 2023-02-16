package me.jiyun233.nya.utils.animations.plus

import net.minecraft.util.math.Vec2f

class Easing2DPlus (pos : Vec2f) {
    private var lastPos : Vec2f
    private var newPos : Vec2f
    private val offset: Vec2f
        get() = Vec2f(
            (newPos.x - lastPos.x),
            (newPos.y - lastPos.y)
        )
    private val animationX: AnimationPlus
    private val animationY: AnimationPlus
    private var startTime : Long

    init {
        lastPos = pos
        newPos = pos
        animationX = AnimationPlus(EasingExpend.ELASTIC_IN_OUT,1000f).also {
            it.forceUpdate(pos.x,pos.x)
        }
        animationY = AnimationPlus(EasingExpend.ELASTIC_IN_OUT,1000f).also {
            it.forceUpdate(pos.y,pos.y)
        }
        startTime = System.currentTimeMillis()
    }

    fun reset(){
        animationX.forceUpdate(0f,0f)
        animationY.forceUpdate(0f,0f)
    }

    fun updatePos(pos : Vec2f){
        lastPos = newPos
        newPos = pos
    }

    fun getUpdate() = Vec2f(
        animationX.getAndUpdate(offset.x + lastPos.x),
        animationY.getAndUpdate(offset.y + lastPos.y)
    )
}
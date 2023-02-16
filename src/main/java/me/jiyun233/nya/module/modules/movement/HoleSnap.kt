package me.jiyun233.nya.module.modules.movement

import me.jiyun233.nya.event.events.client.PacketEvent
import me.jiyun233.nya.event.events.player.MovementEvent
import me.jiyun233.nya.event.events.player.UpdateWalkingPlayerEvent
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.settings.FloatSetting
import me.jiyun233.nya.settings.IntegerSetting
import me.jiyun233.nya.utils.holes.HoleUtil
import me.jiyun233.nya.utils.holes.SurroundUtils
import me.jiyun233.nya.utils.holes.SurroundUtils.betterPosition
import me.jiyun233.nya.utils.holes.SurroundUtils.checkHole
import me.jiyun233.nya.utils.holes.VectorUtils
import me.jiyun233.nya.utils.holes.VectorUtils.distanceTo
import me.jiyun233.nya.utils.holes.VectorUtils.toBlockPos
import me.jiyun233.nya.utils.holes.VectorUtils.toVec3dCenter
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.MovementInput
import net.minecraft.util.MovementInputFromOptions
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.InputUpdateEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.jvm.internal.Intrinsics
import kotlin.math.*

@ModuleInfo(name = "HoleSnap+", descriptions =  "Auto hook into hole",category = Category.MOVEMENT)
object HoleSnap : Module() {
    private var range: IntegerSetting = registerSetting("Range", 5, 1, 50)
    private var timer: FloatSetting = registerSetting("Timer", 3.4f, 1f, 4f)
    private var timeoutTicks: IntegerSetting = registerSetting("TimeOutTicks", 60, 0, 1000)
    private var packetListReset: me.jiyun233.nya.utils.Timer = me.jiyun233.nya.utils.Timer()
    private var holePos: BlockPos? = null
    private var timerBypassing = false
    private var normalLookPos = 0
    private var rotationMode = 1
    private var enabledTicks = 0
    private var stuckTicks = 0
    private var lastPitch = 0f
    private var lastYaw = 0f
    private var normalPos = 0
    private var ranTicks = 0

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        lastYaw = mc.player.rotationYaw
        lastPitch = mc.player.rotationPitch
    }

    override fun onDisable() {
        holePos = null
        stuckTicks = 0
        ranTicks = 0
        enabledTicks = 0
        rotationMode = 1
        timerBypassing = false
        packetListReset.reset()
        mc.timer.tickLength = 50f
    }

    private val Entity.speed get() = hypot(motionX, motionZ)
    private val EntityPlayer.isFlying: Boolean
        get() = this.isElytraFlying || this.capabilities.isFlying

    @SubscribeEvent
    fun onPacketReceive(event: PacketEvent.Receive) {
        if (fullNullCheck()) {
            return
        }
        if (event.getPacket<Packet<*>>() is SPacketPlayerPosLook) {
            disable()
        }
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvent.Send) {
        if (fullNullCheck()) {
            return
        }
        if (event.getPacket<Packet<*>>() is CPacketPlayer.Position && rotationMode == 1) {
            normalPos++
            if (normalPos > 20) {
                rotationMode = if (normalLookPos > 20) {
                    3
                } else {
                    2
                }
            }
        } else if (event.getPacket<Packet<*>>() is CPacketPlayer.PositionRotation && rotationMode == 2) {
            normalLookPos++
            if (normalLookPos > 20) {
                rotationMode = if (normalPos > 20) {
                    3
                } else {
                    1
                }
            }
        }
    }

    @SubscribeEvent
    fun onInput(event: InputUpdateEvent) {
        if (fullNullCheck()) {
            return
        }
        if (event.movementInput is MovementInputFromOptions && holePos != null) {
            event.movementInput.resetMove()
        }
    }

    private fun MovementInput.resetMove() {
        moveForward = 0.0f
        moveStrafe = 0.0f
        forwardKeyDown = false
        backKeyDown = false
        leftKeyDown = false
        rightKeyDown = false
    }

    @SubscribeEvent
    fun onTick(event: UpdateWalkingPlayerEvent) {
        if (fullNullCheck()) {
            return
        }
        if (packetListReset.passedMs(1000)) {
            normalPos = 0
            normalLookPos = 0
            lastYaw = mc.player.rotationYaw
            lastPitch = mc.player.rotationPitch
            packetListReset.reset()
        }
    }

    @SubscribeEvent
    fun onMove(event: MovementEvent) {
        if (fullNullCheck()) {
            return
        }
        if (++enabledTicks > timeoutTicks.value) {
            disable()
            return
        }

        if (!mc.player.isEntityAlive || mc.player.isFlying) return

        val currentSpeed = mc.player.speed

        if (shouldDisable(currentSpeed)) {
            timerBypassing = false
            disable()
            return
        }
        getHole()?.let {
            mc.timer.tickLength = 50f / timer.value
            timerBypassing = true
            val playerPos = mc.player.positionVector
            val targetPos: Vec3d = if (HoleUtil.is2HoleB(it)) {
                //Vec3d(it.x.toDouble() + ((it.x - playerPos.x) / 2), mc.player.posY, it.z.toDouble() + ((it.z - playerPos.z) / 2))
                Vec3d(it.toVec3dCenter().x, mc.player.posY, it.toVec3dCenter().z)
            } else {
                Vec3d(it.toVec3dCenter().x, mc.player.posY, it.toVec3dCenter().z)
            }
            val yawRad = getRotationTo(playerPos, targetPos).x.toRadian()
            val dist = hypot(targetPos.x - playerPos.x, targetPos.z - playerPos.z)
            val baseSpeed = mc.player.applySpeedPotionEffects(0.2873)
            val speed = if (mc.player.onGround) baseSpeed else max(currentSpeed + 0.02, baseSpeed)
            val cappedSpeed = min(speed, dist)

            event.x = -sin(yawRad) * cappedSpeed
            event.z = cos(yawRad) * cappedSpeed

            if (mc.player.collidedHorizontally) stuckTicks++
            else stuckTicks = 0
        }
    }

    private fun EntityLivingBase.applySpeedPotionEffects(speed: Double): Double {
        return this.getActivePotionEffect(MobEffects.SPEED)?.let {
            speed * this.speedEffectMultiplier
        } ?: speed
    }

    private val EntityLivingBase.speedEffectMultiplier: Double
        get() = this.getActivePotionEffect(MobEffects.SPEED)?.let {
            1.0 + (it.amplifier + 1.0) * 0.2
        } ?: 1.0

    private fun getRotationTo(posFrom: Vec3d, posTo: Vec3d): Vec2f {
        return getRotationFromVec(posTo.subtract(posFrom))
    }

    private fun getRotationFromVec(vec: Vec3d): Vec2f {
        val xz = hypot(vec.x, vec.z)
        val yaw = normalizeAngle(Math.toDegrees(atan2(vec.z, vec.x)) - 90.0)
        val pitch = normalizeAngle(Math.toDegrees(-atan2(vec.y, xz)))
        return Vec2f(yaw.toFloat(), pitch.toFloat())
    }

    private fun normalizeAngle(angleIn: Double): Double {
        var angle = angleIn
        angle %= 360.0
        if (angle >= 180.0) {
            angle -= 360.0
        }
        if (angle < -180.0) {
            angle += 360.0
        }
        return angle
    }
    private const val PI_FLOAT: Float = 3.1415927f
    private fun Float.toRadian(): Float = this / 180.0f * PI_FLOAT

    private fun shouldDisable(currentSpeed: Double) =
        holePos?.let { mc.player.posY < it.y } ?: false
                || stuckTicks > 5 && currentSpeed < 0.1
                || currentSpeed < 0.01 && getHole()?.let { mc.player.isCentered(it) } == true || (checkHole(mc.player) != SurroundUtils.HoleType.NONE)


    private fun EntityPlayerSP.isCentered(center: BlockPos): Boolean {
        return this.isCentered(center.x + 0.5, center.z + 0.5)
    }

    private fun EntityPlayerSP.isCentered(x: Double, z: Double): Boolean {
        return abs(this.posX - x) < 0.2
                && abs(this.posZ - z) < 0.2
    }

    private fun getHole(): BlockPos? {
        val entityPlayerSP: EntityPlayerSP = mc.player
        return if (mc.player.ticksExisted % 10 == 0 && !Intrinsics.areEqual(
                SurroundUtils.getFlooredPosition(entityPlayerSP as Entity),
                holePos
            )
        ) findHole() else findHole()
    }

    private fun findHole(): BlockPos? {
        var closestHole = Pair(69.69, BlockPos.ORIGIN)
        val playerPos = mc.player.betterPosition
        val ceilRange = range.value
        val posList = VectorUtils.getBlockPositionsInArea(
            playerPos.add(ceilRange, -1, ceilRange),
            playerPos.add(-ceilRange, -1, -ceilRange)
        )

        for (posXZ in posList) {
            val dist = mc.player.distanceTo(posXZ)
            if (dist > range.value || dist > closestHole.first) continue

            for (posY in 0..5) {
                val pos = posXZ.add(0, -posY, 0)
                if (!mc.world.isAirBlock(pos.up())) break
                if (HoleUtil.is2HoleB(pos)) {
                    closestHole = dist to pos.toVec3dCenter().toBlockPos()
                    continue
                }
                if (checkHole(pos) == SurroundUtils.HoleType.NONE) continue
                closestHole = dist to pos
            }
        }

        return if (closestHole.second != BlockPos.ORIGIN) closestHole.second.also { holePos = it }
        else null
    }
}

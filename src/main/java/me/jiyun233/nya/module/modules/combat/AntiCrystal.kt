package me.jiyun233.nya.module.modules.combat

import me.jiyun233.nya.event.events.client.PacketEvent
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.settings.BooleanSetting
import me.jiyun233.nya.settings.FloatSetting
import me.jiyun233.nya.settings.IntegerSetting
import me.jiyun233.nya.utils.Timer
import me.jiyun233.nya.utils.calculate.MathUtil
import me.jiyun233.nya.utils.combat.DamageUtil
import me.jiyun233.nya.utils.player.BlockUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.init.Items
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

@ModuleInfo(name = "AntiCrystal", category = Category.COMBAT, descriptions = "Anti crystal bomb")
class AntiCrystal : Module() {
    private val targets: MutableList<BlockPos> = ArrayList()
    private val timer = Timer()
    private val breakTimer = Timer()
    private val checkTimer = Timer()
    var range: FloatSetting = registerSetting("Range", 6.0f, 0.0f, 10.0f)
    private var wallsRange: FloatSetting = registerSetting("WallsRange", 3.5f, 0.0f, 10.0f)
    private var minDmg: FloatSetting = registerSetting("MinDmg", 6.0f, 0.0f, 100.0f)
    private var selfDmg: FloatSetting = registerSetting("SelfDmg", 2.0f, 0.0f, 10.0f)
    private var placeDelay: IntegerSetting = registerSetting("PlaceDelay", 0, 0, 500)
    private var breakDelay: IntegerSetting = registerSetting("BreakDelay", 0, 0, 500)
    private var checkDelay: IntegerSetting = registerSetting("CheckDelay", 0, 0, 500)
    private var wasteAmount: IntegerSetting = registerSetting("WasteAmount", 1, 1, 5)
    private var switcher: BooleanSetting = registerSetting("Switch", true)
    private var rotate: BooleanSetting = registerSetting("Rotate", true)
    private var packet: BooleanSetting = registerSetting("Packet", true)
    private var rotations: IntegerSetting = registerSetting("Spoofs", 1, 1, 20)
    private var yaw = 0.0f
    private var pitch = 0.0f
    private var rotating = false
    private var rotationPacketsSpoofed = 0
    private var breakTarget: Entity? = null
    override fun onEnable() {
        rotating = false
    }

    override fun onDisable() {
        rotating = false
    }

    private val deadlyCrystal: Entity?
        get() {
            var bestCrystal: Entity? = null
            var highestDamage = 0.0f
            for (crystal in mc.world.loadedEntityList) {
                var damage = 0.0f
                if (crystal !is EntityEnderCrystal || mc.player.getDistanceSq(crystal) > 169.0 || DamageUtil.calculateDamage(
                        crystal,
                        mc.player
                    ).also { damage = it } < minDmg.value
                ) continue
                if (bestCrystal == null) {
                    bestCrystal = crystal
                    highestDamage = damage
                    continue
                }
                if (damage <= highestDamage) continue
                bestCrystal = crystal
                highestDamage = damage
            }
            return bestCrystal
        }

    private fun getSafetyCrystals(deadlyCrystal: Entity): Int {
        var count = 0
        for (entity in mc.world.loadedEntityList) {
            if (entity is EntityEnderCrystal || DamageUtil.calculateDamage(
                    entity,
                    mc.player
                ) > 2.0f || deadlyCrystal.getDistanceSq(entity) > 144.0
            ) continue
            ++count
        }
        return count
    }

    private fun getPlaceTarget(deadlyCrystal: Entity): BlockPos? {
        var closestPos: BlockPos? = null
        var smallestDamage = 10.0f
        for (pos in BlockUtil.possiblePlacePositions(range.value)) {
            val damage = DamageUtil.calculateDamage(pos, mc.player)
            if (damage > 2.0f || deadlyCrystal.getDistanceSq(pos) > 144.0 || mc.player.getDistanceSq(pos) >= MathUtil.square(
                    wallsRange.value
                ) && BlockUtil.rayTracePlaceCheck(pos, true, 1.0f)
            ) continue
            if (closestPos == null) {
                smallestDamage = damage
                closestPos = pos
                continue
            }
            if (damage >= smallestDamage && (damage != smallestDamage || mc.player.getDistanceSq(pos) >= mc.player.getDistanceSq(
                    closestPos
                ))
            ) continue
            smallestDamage = damage
            closestPos = pos
        }
        return closestPos
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvent.Send) {
        if (event.stage == 0 && rotate.value && rotating) {
            if (event.getPacket<Packet<*>>() is CPacketPlayer) {
                val packet = event.getPacket<CPacketPlayer>()
                packet.yaw = yaw
                packet.pitch = pitch
            }
            ++rotationPacketsSpoofed
            if (rotationPacketsSpoofed >= rotations.value) {
                rotating = false
                rotationPacketsSpoofed = 0
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent?) {
        if (!fullNullCheck() && checkTimer.passedMs(checkDelay.value.toLong())) {
            val deadlyCrystal = deadlyCrystal
            if (deadlyCrystal != null) {
                val placeTarget = getPlaceTarget(deadlyCrystal)
                if (placeTarget != null) {
                    targets.add(placeTarget)
                }
                placeCrystal(deadlyCrystal)
                breakTarget = getBreakTarget(deadlyCrystal)
                breakCrystal()
            }
            checkTimer.reset()
        }
    }

    private fun getBreakTarget(deadlyCrystal: Entity?): Entity? {
        var smallestCrystal: Entity? = null
        var smallestDamage = 10.0f
        for (entity in mc.world.loadedEntityList) {
            var damage = 0.0f
            if (entity !is EntityEnderCrystal || DamageUtil.calculateDamage(entity, mc.player).also {
                    damage = it
                } > selfDmg.value || deadlyCrystal?.let { entity.getDistanceSq(it) }!! > 144.0 || mc.player.getDistanceSq(entity) > MathUtil.square(
                    wallsRange.value
                ) && BlockUtil.rayTraceHitCheck(entity, true)) continue
            if (smallestCrystal == null) {
                smallestCrystal = entity
                smallestDamage = damage
                continue
            }
            if (damage >= smallestDamage && (smallestDamage != damage || mc.player.getDistanceSq(entity) >= mc.player.getDistanceSq(
                    smallestCrystal
                ))
            ) continue
            smallestCrystal = entity
            smallestDamage = damage
        }
        return smallestCrystal
    }

    private fun placeCrystal(deadlyCrystal: Entity) {
        val offhand: Boolean = mc.player.heldItemOffhand.getItem() === Items.END_CRYSTAL
        if (timer.passedMs(placeDelay.value.toLong()) && (switcher.value || mc.player.heldItemMainhand.getItem() === Items.END_CRYSTAL || offhand) && targets.isNotEmpty() && getSafetyCrystals(
                deadlyCrystal
            ) <= wasteAmount.value
        ) {
            if (switcher.value && mc.player.heldItemMainhand.getItem() !== Items.END_CRYSTAL && !offhand) {
                doSwitch()
            }
            rotateToPos(targets[targets.size - 1])
            BlockUtil.placeCrystalOnBlock(
                targets[targets.size - 1],
                if (offhand) EnumHand.OFF_HAND else EnumHand.MAIN_HAND,
                true,
                true
            )
            timer.reset()
        }
    }

    private fun doSwitch() {
        var crystalSlot: Int
        crystalSlot =
            if (mc.player.heldItemMainhand.getItem() === Items.END_CRYSTAL) mc.player.inventory.currentItem else -1
        if (crystalSlot == -1) {
            for (l in 0..8) {
                if (mc.player.inventory.getStackInSlot(l).getItem() !== Items.END_CRYSTAL) continue
                crystalSlot = l
                break
            }
        }
        if (crystalSlot != -1) {
            mc.player.inventory.currentItem = crystalSlot
        }
    }

    private fun breakCrystal() {
        if (breakTimer.passedMs(breakDelay.value.toLong()) && breakTarget != null && DamageUtil.canBreakWeakness(mc.player)) {
            rotateTo(breakTarget)
            BlockUtil.attackEntity(breakTarget, packet.value)
            breakTimer.reset()
            targets.clear()
        }
    }

    private fun rotateTo(entity: Entity?) {
        if (rotate.value) {
            val angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.renderPartialTicks), entity!!.positionVector)
            yaw = angle[0]
            pitch = angle[1]
            rotating = true
        }
    }

    private fun rotateToPos(pos: BlockPos) {
        if (rotate.value) {
            val angle = MathUtil.calcAngle(
                mc.player.getPositionEyes(mc.renderPartialTicks),
                Vec3d(
                    (pos.x.toFloat() + 0.5f).toDouble(),
                    (pos.y.toFloat() - 0.5f).toDouble(),
                    (pos.z.toFloat() + 0.5f).toDouble()
                )
            )
            yaw = angle[0]
            pitch = angle[1]
            rotating = true
        }
    }
}
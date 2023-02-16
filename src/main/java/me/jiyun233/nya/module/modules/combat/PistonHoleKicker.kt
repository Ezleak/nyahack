package me.jiyun233.nya.module.modules.combat

import me.jiyun233.nya.event.events.player.UpdateWalkingPlayerEvent
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.settings.BooleanSetting
import me.jiyun233.nya.settings.IntegerSetting
import me.jiyun233.nya.utils.player.BlockPlacement
import me.jiyun233.nya.utils.player.InventoryUtil
import net.minecraft.block.BlockCompressedPowered
import net.minecraft.block.BlockPistonBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@ModuleInfo(name = "HoleKicker", descriptions = "Use piston kick target form hole", category = Category.COMBAT)
object PistonHoleKicker : Module() {
    private val range: IntegerSetting = registerSetting("Range", 4, 1, 6)
    val packet: BooleanSetting = registerSetting("Packet", false)
    private val autoDisable: BooleanSetting = registerSetting("AutoDisable", false)
    private var progress = 0
    private var facing: EnumFacing? = null
    override fun onEnable() {
        progress = 0
        super.onEnable()
    }

    override fun onUpdate() {
        for (entity in mc.world.loadedEntityList) {
            if (entity === mc.player) continue
            if (mc.player.getDistance(entity) > range.value) continue
            if (entity is EntityPlayer) {
                val piston = InventoryUtil.findHotbarBlock(BlockPistonBase::class.java)
                val power = InventoryUtil.findHotbarBlock(BlockCompressedPowered::class.java)
                var pos = BlockPos(entity).offset(EnumFacing.UP)
                if (piston == -1 || power == -1 || progress < 2) {
                    facing = getFacing(pos)
                    if (facing != null) {
                        progress++
                    } else {
                        progress = 0
                    }
                    return
                }
                mc.player.inventory.currentItem = piston
                var event = BlockPlacement.isPlaceable(facing?.let { pos.offset(it) }, 0.0, true)
                if (event != null) {
                    if (!event.doPlace(true)) {
                        return
                    }
                    mc.player.inventory.currentItem = power
                    for (f in EnumFacing.values()) {
                        pos = facing?.let { BlockPos(entity).offset(EnumFacing.UP).offset(it) }!!
                        event = BlockPlacement.isPlaceable(pos.offset(f), 0.0, true)
                        if (BlockPlacement.doPlace(event, true)) {
                            BlockPlacement.doPlace(BlockPlacement.isPlaceable(BlockPos(entity), 0.0, false), false)
                            if (autoDisable.value) {
                                disable()
                            }
                            return
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onMove(event: UpdateWalkingPlayerEvent) {
        if (progress > 0) {
            when (facing) {
                EnumFacing.NORTH -> event.yaw = 180f
                EnumFacing.SOUTH -> event.yaw = 0f
                EnumFacing.WEST -> event.yaw = 90f
                EnumFacing.EAST -> event.yaw = -90f
                else -> {
                    event.yaw = mc.player.rotationYaw
                }
            }
            event.pitch = 0f
            progress++
        }
    }

    private fun getFacing(position: BlockPos): EnumFacing? {
        for (f in EnumFacing.values()) {
            val pos = BlockPos(position)
            if (pos.offset(f).y != position.y) continue
            if (!mc.world.isAirBlock(pos.offset(f, -1).offset(EnumFacing.DOWN))) {
                if (mc.world.isAirBlock(pos.offset(f, -1))) {
                    if (mc.world.isAirBlock(pos.offset(f))) {
                        return f
                    }
                }
            }
        }
        return null
    }
}
package me.jiyun233.nya.module.modules.combat

import me.jiyun233.nya.event.events.world.BlockBreakEvent
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.settings.BooleanSetting
import me.jiyun233.nya.utils.holes.SurroundUtils
import me.jiyun233.nya.utils.player.BlockUtil
import me.jiyun233.nya.utils.player.InventoryUtil
import net.minecraft.block.BlockObsidian
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@ModuleInfo(name =  "SmartAntiCity", descriptions =  "Smart anti surround miner",category = Category.COMBAT)
class SmartAntiCity : Module() {
    var packet: BooleanSetting = registerSetting("Packet", true)
    private var rotate: BooleanSetting = registerSetting("Rotate", true)

    private var potion = arrayOf(
        BlockPos(1, 0, 0),
        BlockPos(-1, 0, 0),
        BlockPos(0, 0, 1),
        BlockPos(0, 0, -1)
    )

    private var currentPos: BlockPos? = null

    private var lastPlace: BlockPos? = null

    override fun onDisable() {
        currentPos = null
        lastPlace = null
    }

    @SubscribeEvent
    fun onBlock(event: BlockBreakEvent) {
        if (SurroundUtils.checkHole(mc.player) == SurroundUtils.HoleType.NONE) return
        if (event.position.isSurroundMiner()) currentPos = event.position

        if (currentPos == null) {
            lastPlace = null
            return
        }
        var contains = false
        if (currentPos!! == event.position) contains = true
        if (!contains) {
            currentPos = null
        }
        if (currentPos == null) {
            lastPlace = null
            return
        }
        if (lastPlace == currentPos) return
        val playerPos = BlockPos(mc.player)
        when (currentPos!!.getFacingAdd()) {
            EnumFacing.NORTH -> {
                val obiSlot = InventoryUtil.findHotbarBlock(BlockObsidian::class.java)
                val oldSlot = mc.player.inventory.currentItem
                if (obiSlot == -1) return
                InventoryUtil.switchToHotbarSlot(obiSlot, false)
                val offset = playerPos.getFacingMax(EnumFacing.NORTH)
                BlockUtil.placeBlock(playerPos.north(offset), EnumHand.MAIN_HAND, rotate.value, packet.value)
                BlockUtil.placeBlock(
                    playerPos.north(if (offset > 1) offset - 1 else offset).up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.north(if (offset > 1) offset - 1 else offset).up().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.north(if (offset > 1) offset - 1 else offset).east(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.north(if (offset > 1) offset - 1 else offset).west(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.north(if (offset > 1) offset - 1 else offset).east().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.north(if (offset > 1) offset - 1 else offset).west().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                InventoryUtil.switchToHotbarSlot(oldSlot, false)
            }

            EnumFacing.SOUTH -> {
                val obiSlot = InventoryUtil.findHotbarBlock(BlockObsidian::class.java)
                val oldSlot = mc.player.inventory.currentItem
                if (obiSlot == -1) return
                InventoryUtil.switchToHotbarSlot(obiSlot, false)
                val offset = playerPos.getFacingMax(EnumFacing.SOUTH)
                BlockUtil.placeBlock(playerPos.south(offset), EnumHand.MAIN_HAND, rotate.value, packet.value)
                BlockUtil.placeBlock(
                    playerPos.south(if (offset > 1) offset - 1 else offset).up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.south(if (offset > 1) offset - 1 else offset).up().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.south(if (offset > 1) offset - 1 else offset).east(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.south(if (offset > 1) offset - 1 else offset).west(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.south(if (offset > 1) offset - 1 else offset).east().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.south(if (offset > 1) offset - 1 else offset).west().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                InventoryUtil.switchToHotbarSlot(oldSlot, false)
            }

            EnumFacing.EAST -> {
                val obiSlot = InventoryUtil.findHotbarBlock(BlockObsidian::class.java)
                val oldSlot = mc.player.inventory.currentItem
                if (obiSlot == -1) return
                InventoryUtil.switchToHotbarSlot(obiSlot, false)
                val offset = playerPos.getFacingMax(EnumFacing.EAST)
                BlockUtil.placeBlock(playerPos.east(offset), EnumHand.MAIN_HAND, rotate.value, packet.value)
                BlockUtil.placeBlock(
                    playerPos.east(if (offset > 1) offset - 1 else offset).up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.east(if (offset > 1) offset - 1 else offset).up().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.east(if (offset > 1) offset - 1 else offset).south(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.east(if (offset > 1) offset - 1 else offset).north(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.east(if (offset > 1) offset - 1 else offset).south().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.east(if (offset > 1) offset - 1 else offset).north().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                InventoryUtil.switchToHotbarSlot(oldSlot, false)
            }

            EnumFacing.WEST -> {
                val obiSlot = InventoryUtil.findHotbarBlock(BlockObsidian::class.java)
                val oldSlot = mc.player.inventory.currentItem
                if (obiSlot == -1) return
                InventoryUtil.switchToHotbarSlot(obiSlot, false)
                val offset = playerPos.getFacingMax(EnumFacing.WEST)
                BlockUtil.placeBlock(playerPos.west(offset), EnumHand.MAIN_HAND, rotate.value, packet.value)
                BlockUtil.placeBlock(
                    playerPos.west(if (offset > 1) offset - 1 else offset).up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.west(if (offset > 1) offset - 1 else offset).up().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.west(if (offset > 1) offset - 1 else offset).south(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.west(if (offset > 1) offset - 1 else offset).north(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.west(if (offset > 1) offset - 1 else offset).south().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                BlockUtil.placeBlock(
                    playerPos.west(if (offset > 1) offset - 1 else offset).north().up(),
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value
                )
                InventoryUtil.switchToHotbarSlot(oldSlot, false)
            }

            else -> {}
        }
        lastPlace = currentPos
    }

    private fun BlockPos.getFacingMax(facing: EnumFacing): Int {
        for (i in 1..50) {
            when (facing) {
                EnumFacing.SOUTH -> if (mc.world.getBlockState(this.south(i)).block.equals(Blocks.AIR)) return i
                EnumFacing.NORTH -> if (mc.world.getBlockState(this.north(i)).block.equals(Blocks.AIR)) return i
                EnumFacing.WEST -> if (mc.world.getBlockState(this.west(i)).block.equals(Blocks.AIR)) return i
                EnumFacing.EAST -> if (mc.world.getBlockState(this.east(i)).block.equals(Blocks.AIR)) return i
                else -> {}
            }
        }
        return 1
    }

    private fun BlockPos.isSurroundMiner(): Boolean {
        for (blockPos in potion) {
            if (BlockPos(mc.player).add(blockPos) == this) return true
        }
        return false
    }

    private fun BlockPos.getFacingAdd(): EnumFacing {
        val blockPos = BlockPos(mc.player)
        if (blockPos.north().equals(this)) return EnumFacing.NORTH
        if (blockPos.south().equals(this)) return EnumFacing.SOUTH
        if (blockPos.east().equals(this)) return EnumFacing.EAST
        if (blockPos.west().equals(this)) return EnumFacing.WEST
        return EnumFacing.UP
    }
}

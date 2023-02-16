package me.jiyun233.nya.module.modules.combat

import me.jiyun233.nya.utils.holes.SurroundUtils
import me.jiyun233.nya.event.events.world.BlockBreakEvent
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.settings.BooleanSetting
import me.jiyun233.nya.utils.Timer
import me.jiyun233.nya.utils.player.BlockUtil
import me.jiyun233.nya.utils.player.InventoryUtil
import net.minecraft.block.BlockObsidian
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@ModuleInfo(name = "AntiCev+", descriptions = "Smart anti cev", category = Category.COMBAT)
object AntiCevPlus : Module() {
    var packet: BooleanSetting = registerSetting("Packet", true)
    private var rotate: BooleanSetting = registerSetting("Rotate", true)
    private var potion = arrayOf(
        BlockPos(1, 0, 0),
        BlockPos(-1, 0, 0),
        BlockPos(0, 0, 1),
        BlockPos(0, 0, -1),
        BlockPos(0, 2, 0)
    )

    private var lastPos: BlockPos? = null

    private val map: HashMap<BlockPos, Timer> = HashMap()

    override fun onDisable() {
        lastPos = null
        map.clear()
    }

    override fun onUpdate() {
        val pos = BlockPos(mc.player)
        if (SurroundUtils.checkHole(mc.player) == SurroundUtils.HoleType.NONE) {
            return
        } else {
            if (lastPos == null) {
                lastPos = BlockPos(mc.player)
            } else {
                if (lastPos != BlockPos(mc.player)) {
                    lastPos = null
                    map.clear()
                }
            }
        }
        for (blockPos in potion) {
            if (pos.add(blockPos).getHighestBlock() == null) continue
            for (entity in pos.add(blockPos).getHighestBlock()?.let { AxisAlignedBB(it) }?.let {
                mc.world.getEntitiesWithinAABB(
                    Entity::class.java, it
                )
            }!!) {
                var broken = false
                if (entity is EntityEnderCrystal && BlockPos(entity) == pos.add(blockPos).getHighestBlock() && pos.add(
                        blockPos
                    ).getHighestBlock()!!
                        .add(0, -1, 0).hasPlayerBreaking()
                ) {
                    mc.player.swingArm(EnumHand.MAIN_HAND)
                    mc.playerController.connection.sendPacket(CPacketUseEntity(entity))
                    val obySlot = InventoryUtil.findHotbarBlock(BlockObsidian::class.java)
                    val oldSlot = mc.player.inventory.currentItem
                    if (obySlot != -1) {
                        InventoryUtil.switchToHotbarSlot(obySlot, false)
                    }
                    BlockUtil.placeBlock(
                        pos.add(blockPos).getHighestBlock(),
                        EnumHand.MAIN_HAND,
                        rotate.value,
                        packet.value
                    )
                    InventoryUtil.switchToHotbarSlot(oldSlot, false)
                    broken = true
                }
                if (broken) return
            }
        }
        map.forEach {
            if (it.key.getDistance(
                    mc.player.posX.toInt(),
                    mc.player.posY.toInt(),
                    mc.player.posZ.toInt()
                ) > 5.0
            ) map.remove(it.key)
            if (it.value.passedS(1.0)) map.remove(it.key)
        }
    }

    private fun BlockPos.hasPlayerBreaking(): Boolean {
        if (map.contains(this)) return true
        for (damagedBlock in mc.renderGlobal.damagedBlocks) {
            if (damagedBlock.value.position.equals(this)) return true
        }
        return false
    }

    private fun BlockPos.getHighestBlock(): BlockPos? {
        for (i in 0..5) {
            if (mc.world.getBlockState(this.add(0, i, 0)).block == Blocks.AIR) return this.add(0, i, 0)
        }
        return null
    }

    @SubscribeEvent
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.position.getDistance(
                mc.player.posX.toInt(),
                mc.player.posY.toInt(),
                mc.player.posZ.toInt()
            ) > 5.0
        ) return
        if (mc.world.getEntityByID(event.breakerId) == null) return
        map[event.position] = Timer()
    }
}
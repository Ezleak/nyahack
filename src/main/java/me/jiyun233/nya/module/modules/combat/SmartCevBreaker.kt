package me.jiyun233.nya.module.modules.combat

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.utils.client.ChatUtil
import me.jiyun233.nya.utils.player.BlockUtil
import me.jiyun233.nya.utils.player.InventoryUtil
import net.minecraft.block.BlockObsidian
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemEndCrystal
import net.minecraft.item.ItemPickaxe
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap

@ModuleInfo(name = "SmartCevBreaker", descriptions = "Smart fuck it", category = Category.COMBAT)
class SmartCevBreaker : Module() {
    private val rangeSetting = registerSetting("DetectRange", 5.5, 0.1, 10.0)
    private val cevModeSetting = registerSetting("CevMode", CevModeEnum.SIDE)
    private val packetSetting = registerSetting("Packet", false)
    private var stage = -1
    private var currentBlockPos: BlockPos? = null

    override fun onUpdate() {
        if (currentBlockPos == null || stage == -1) {
            currentBlockPos = findCurrentBreakPos() ?: return
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                    currentBlockPos!!,
                    EnumFacing.UP
                )
            )
            stage = 0
        }
        when (stage) {
            0 -> {
                when (cevModeSetting.value) {
                    CevModeEnum.SIDE -> {
                        currentBlockPos!!.add(0, -2, 0).placeGhostHand()
                        currentBlockPos!!.add(0, -1, 0).placeGhostHand()
                        currentBlockPos!!.placeGhostHand()
                    }

                    CevModeEnum.HEAD -> {
                        currentBlockPos!!.south().down(2).placeGhostHand()
                        currentBlockPos!!.south().down().placeGhostHand()
                        currentBlockPos!!.south().placeGhostHand()
                        currentBlockPos!!.placeGhostHand()
                    }
                }
                stage++
            }

            1 -> {
                val currentItem = mc.player.inventory.currentItem
                val item = InventoryUtil.findHotbarItem(ItemEndCrystal::class.java)
                if (item == -1) {
                    ChatUtil.sendMessage("&cHas no any EndCrystal")
                    this.disable()
                    return
                }
                InventoryUtil.switchToHotbarSlot(item, false)
                if (packetSetting.value) {
                    mc.player.connection.sendPacket(
                        CPacketPlayerTryUseItemOnBlock(
                            currentBlockPos!!,
                            EnumFacing.UP,
                            EnumHand.MAIN_HAND,
                            0.5f,
                            1f,
                            0.5f
                        )
                    )
                } else {
                    mc.playerController.processRightClickBlock(
                        mc.player, mc.world, currentBlockPos!!, EnumFacing.UP,
                        Vec3d(0.5, 1.0, 0.5), EnumHand.MAIN_HAND
                    )
                }
                InventoryUtil.switchToHotbarSlot(currentItem, false)

                if (mc.world.getEntitiesWithinAABB(
                        EntityEnderCrystal::class.java,
                        AxisAlignedBB(currentBlockPos!!.up())
                    ).size >= 1
                ) {
                    stage++
                }
            }

            2 -> {
                val currentItem = mc.player.inventory.currentItem
                val item = InventoryUtil.findHotbarItem(ItemPickaxe::class.java)
                if (item == -1) {
                    ChatUtil.sendMessage("&cHas no any Pickaxe")
                    this.disable()
                    return
                }
                InventoryUtil.switchToHotbarSlot(item, false)
                mc.player.connection.sendPacket(
                    CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        currentBlockPos!!,
                        EnumFacing.UP
                    )
                )
                InventoryUtil.switchToHotbarSlot(currentItem, false)
                if (mc.world.getBlockState(currentBlockPos!!).block == Blocks.AIR) {
                    stage++
                }
            }

            3 -> {
                mc.world.getEntitiesWithinAABB(EntityEnderCrystal::class.java, AxisAlignedBB(currentBlockPos!!.up()))
                    .forEach {
                        if (packetSetting.value) {
                            mc.playerController.connection.sendPacket(CPacketUseEntity(it, EnumHand.MAIN_HAND))
                        } else {
                            mc.playerController.attackEntity(mc.player, it)
                        }
                        it.setDead()
                        ChatUtil.sendMessage("Attack Entity: ${BlockPos(it)}")
                    }
                if (mc.world.getEntitiesWithinAABB(
                        EntityEnderCrystal::class.java,
                        AxisAlignedBB(currentBlockPos!!.up())
                    ).size <= 0
                ) {
                    stage = 0
                }
            }
        }
        if (findClosestTarget(rangeSetting.value) == null) {
            stage = -1
            currentBlockPos = null
        }
    }

    override fun onDisable() {
        stage = -1
        currentBlockPos = null
    }

    private fun BlockPos.placeGhostHand() {
        if (mc.world.getBlockState(this).block == Blocks.OBSIDIAN) return
        val currentItem = mc.player.inventory.currentItem
        val obsidianSlot = InventoryUtil.findHotbarBlock(BlockObsidian::class.java)
        if (obsidianSlot == -1) return
        InventoryUtil.switchToHotbarSlot(obsidianSlot, false)
        BlockUtil.placeBlock(this, EnumHand.MAIN_HAND, true, packetSetting.value)
        InventoryUtil.switchToHotbarSlot(currentItem, false)
    }

    private fun findCurrentBreakPos(): BlockPos? {
        val closestTarget = findClosestTarget(rangeSetting.value) ?: return null
        when (cevModeSetting.value) {
            CevModeEnum.HEAD -> {
                return BlockPos(closestTarget).up(2)
            }

            CevModeEnum.SIDE -> {
                return when (closestTarget.getHeightFacing()) {
                    EnumFacing.NORTH -> BlockPos(closestTarget).north().up()
                    EnumFacing.SOUTH -> BlockPos(closestTarget).south().up()
                    EnumFacing.WEST -> BlockPos(closestTarget).west().up()
                    EnumFacing.EAST -> BlockPos(closestTarget).east().up()
                    else -> null
                }
            }
        }
        return null
    }

    private fun EntityPlayer.getHeightFacing(): EnumFacing {
        val map: EnumMap<EnumFacing, Int> = EnumMap(EnumFacing::class.java)
        map[EnumFacing.SOUTH] = (BlockPos(this).south().getHighestBlock() ?: Int.MAX_VALUE) as Int?
        map[EnumFacing.NORTH] = (BlockPos(this).north().getHighestBlock() ?: Int.MAX_VALUE) as Int?
        map[EnumFacing.EAST] = (BlockPos(this).east().getHighestBlock() ?: Int.MAX_VALUE) as Int?
        map[EnumFacing.WEST] = (BlockPos(this).west().getHighestBlock() ?: Int.MAX_VALUE) as Int?
        return map.toList().sortedByDescending { it.second }[0].first
    }

    private fun BlockPos.getHighestBlock(): Int? {
        for (i in 0..5) {
            if (mc.world.getBlockState(this.add(0, i, 0)).block == Blocks.AIR) return i
        }
        return null
    }

    private fun findClosestTarget(range: Double): EntityPlayer? {
        val temp = HashMap<EntityPlayer, Double>()
        mc.world.loadedEntityList.forEach(Consumer { entity: Entity ->
            if (entity is EntityPlayer && mc.player.getDistanceSq(entity) <= range && !NyaHack.friendManager.isFriend(
                    entity
                ) && entity !== mc.player
            ) {
                temp[entity] = mc.player.getDistanceSq(entity)
            }
        })
        val list = ArrayList<Map.Entry<EntityPlayer, Double>>(temp.entries)
        list.sortWith(java.util.Map.Entry.comparingByValue())
        return if (list.isEmpty()) null else list[0].key
    }

    internal enum class CevModeEnum {
        HEAD,
        SIDE
    }
}
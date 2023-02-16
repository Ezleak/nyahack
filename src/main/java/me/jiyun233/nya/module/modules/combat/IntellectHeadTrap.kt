package me.jiyun233.nya.module.modules.combat

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.event.events.world.BlockBreakEvent
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.settings.BooleanSetting
import me.jiyun233.nya.settings.DoubleSetting
import me.jiyun233.nya.utils.player.BlockUtil
import me.jiyun233.nya.utils.player.InventoryUtil
import net.minecraft.block.BlockButton
import net.minecraft.block.BlockObsidian
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*
import java.util.stream.Collectors
import kotlin.Comparator
import kotlin.collections.HashMap

@ModuleInfo(name = "IntellectHeadTrap", descriptions = "Smart hea traps", category = Category.COMBAT)
object IntellectHeadTrap : Module() {
    private val range: DoubleSetting = registerSetting("Range", 5.0, 1.0, 6.0)
    private val packet: BooleanSetting = registerSetting("Packet", true)
    private val rotate: BooleanSetting = registerSetting("Rotate", true)
    private val buttonEnable: BooleanSetting = registerSetting("Button", false)
    private val extraPlace: BooleanSetting = registerSetting("ExtraPlace", true)

    private val autoToggle: BooleanSetting = registerSetting("AutoToggle", false)

    private val smart: BooleanSetting = registerSetting("IntellectDisable", true).booleanDisVisible(autoToggle)
    private var target: EntityPlayer? = null
    private var place = true

    override fun onEnable() {
        if (fullNullCheck()) return
        breakCrystal()
    }

    private val map: HashMap<EntityPlayer, Breaker> = HashMap()

    private val posList = arrayListOf(
        BlockPos(0, 3, 0),
        BlockPos(1, 2, 0),
        BlockPos(-1, 2, 0),
        BlockPos(0, 2, 1),
        BlockPos(0, 2, -1),
        BlockPos(0, 2, 0),
    )

    override fun onUpdate() {
        if (fullNullCheck() || !place) return
        target = getTarget(range.value)
        if (target == null) return
        val playerPos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
        val obsidianSlot = InventoryUtil.findHotbarBlock(BlockObsidian::class.java)
        val buttonSlot = InventoryUtil.findHotbarBlock(BlockButton::class.java)
        if (obsidianSlot == -1) return
        val oldSlot = mc.player.inventory.currentItem
        if (buttonEnable.value) {
            if (mc.world.getBlockState(playerPos).block === Blocks.AIR && buttonSlot != -1) {
                InventoryUtil.switchToHotbarSlot(buttonSlot, false)
                BlockUtil.placeBlock(
                    playerPos,
                    EnumHand.MAIN_HAND,
                    rotate.value,
                    packet.value,
                )
                InventoryUtil.switchToHotbarSlot(oldSlot, false)
            }
        }
        if (mc.world.getBlockState(playerPos.add(0, 2, 0)).block === Blocks.AIR) {
            when (target!!.getHeightFacing()) {
                EnumFacing.NORTH -> {
                    BlockPos(target!!).north().placeGhostHand()
                    BlockPos(target!!).north().up().placeGhostHand()
                    BlockPos(target!!).north().up(2).placeGhostHand()
                    BlockPos(target!!).up(2).placeGhostHand()
                    if (extraPlace.value) {
                        posList.forEach { it.add(BlockPos(target!!)).placeGhostHand() }
                    }
                }

                EnumFacing.WEST -> {
                    BlockPos(target!!).west().placeGhostHand()
                    BlockPos(target!!).west().up().placeGhostHand()
                    BlockPos(target!!).west().up(2).placeGhostHand()
                    BlockPos(target!!).up(2).placeGhostHand()
                    if (extraPlace.value) {
                        posList.forEach { it.add(BlockPos(target!!)).placeGhostHand() }
                    }
                }

                EnumFacing.EAST -> {
                    BlockPos(target!!).east().placeGhostHand()
                    BlockPos(target!!).east().up().placeGhostHand()
                    BlockPos(target!!).east().up(2).placeGhostHand()
                    BlockPos(target!!).up(2).placeGhostHand()
                    if (extraPlace.value) {
                        posList.forEach { it.add(BlockPos(target!!)).placeGhostHand() }
                    }
                }

                EnumFacing.SOUTH -> {
                    BlockPos(target!!).south().placeGhostHand()
                    BlockPos(target!!).south().up().placeGhostHand()
                    BlockPos(target!!).south().up(2).placeGhostHand()
                    BlockPos(target!!).up(2).placeGhostHand()
                    if (extraPlace.value) {
                        posList.forEach { it.add(BlockPos(target!!)).placeGhostHand() }
                    }
                }

                else -> {
                    BlockPos(target!!).south().placeGhostHand()
                    BlockPos(target!!).south().up().placeGhostHand()
                    BlockPos(target!!).south().up(2).placeGhostHand()
                    BlockPos(target!!).up(2).placeGhostHand()
                    if (extraPlace.value) {
                        posList.forEach { it.add(BlockPos(target!!)).placeGhostHand() }
                    }
                }
            }
        }
        if (autoToggle.value) {
            toggle()
            return
        }
        if (!smart.value) return
        map.forEach {
            if (mc.world.getBlockState(it.value.pos).block == Blocks.AIR) {
                it.value.count++
            }
            if (it.value.count >= 2) {
                place = false
            }
        }
    }

    override fun onDisable() {
        target = null
        place = true
        repeat(11514) {

        }
        map.clear()
    }

    private fun BlockPos.placeGhostHand() {
        if (mc.world.getBlockState(this).block == Blocks.OBSIDIAN) return
        val currentItem = mc.player.inventory.currentItem
        val obsidianSlot = InventoryUtil.findHotbarBlock(BlockObsidian::class.java)
        if (obsidianSlot == -1) return
        InventoryUtil.switchToHotbarSlot(obsidianSlot, false)
        BlockUtil.placeBlock(this, EnumHand.MAIN_HAND, rotate.value, packet.value)
        InventoryUtil.switchToHotbarSlot(currentItem, false)
    }

    private fun getTarget(range: Double): EntityPlayer? {
        var target: EntityPlayer? = null
        var distance = range
        for (player in mc.world.playerEntities) {
            if (player !is EntityPlayer || player.getDistance(mc.player) > range || player.isDead || player == mc.player) continue
            if (NyaHack.friendManager.isFriend(player.name) || mc.player.posY - player.posY >= 5) {
                continue
            }
            if (target == null) {
                target = player
                distance = mc.player.getDistanceSq(player)
                continue
            }
            if (mc.player.getDistanceSq(player) >= distance) continue
            target = player
            distance = mc.player.getDistanceSq(player)
        }
        return target
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
            if (mc.world.getBlockState(this.add(0, i, 0)).block == Blocks.AIR) return i + 1
        }
        return null
    }

    @SubscribeEvent
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!smart.value || target == null) return
        val key = mc.world.getEntityByID(event.breakerId)
        if (key !is EntityPlayer) {
            return
        }

        if (extraPlace.value) {
            posList.forEach {
                if (BlockPos(target!!).add(it) == event.position) {
                    if (!map.containsKey(key)) {
                        map[key] = Breaker(event.position, 0)
                    }
                }
            }
        } else {
            if (event.position == BlockPos(target!!).up(2)) {
                if (!map.containsKey(key)) {
                    map[key] = Breaker(event.position, 0)
                }
            }
        }

        if (map.containsKey(key)) {
            if (event.position != map[key]!!.pos) {
                map.clear()
                place = true
            }
        }
    }

    private fun breakCrystal() {
        for (crystal in mc.world.loadedEntityList.stream().filter { e: Entity -> e is EntityEnderCrystal && !e.isDead }
            .sorted(Comparator.comparing { e ->
                mc.player.getDistance(e)
            }).collect(Collectors.toList())) {
            if (crystal is EntityEnderCrystal && mc.player.getDistance(crystal) <= 4) {
                mc.player.connection.sendPacket(CPacketUseEntity(crystal))
                mc.player.connection.sendPacket(CPacketAnimation(EnumHand.OFF_HAND))
            }
        }
    }

    class Breaker(var pos: BlockPos, var count: Int)
}
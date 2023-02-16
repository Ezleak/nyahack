package me.jiyun233.nya.module.modules.function

import me.jiyun233.nya.event.events.client.PacketEvent
import me.jiyun233.nya.event.events.player.BlockEvent
import me.jiyun233.nya.event.events.world.Render3DEvent
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.settings.BooleanSetting
import me.jiyun233.nya.utils.Timer
import me.jiyun233.nya.utils.player.BlockUtil
import me.jiyun233.nya.utils.player.InventoryUtil
import me.jiyun233.nya.utils.render.Render3DUtil
import net.minecraft.init.Blocks
import net.minecraft.item.ItemPickaxe
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent
import java.awt.Color

@ModuleInfo(name = "InstantMine+", descriptions = "Double break", category = Category.FUNCTION)
object InstantMinePlus : Module() {

    private val ghostHand: BooleanSetting = registerSetting("GhostHand", false)
    private val doubleBreak: BooleanSetting = registerSetting("DoubleBreak", false)
    val render: BooleanSetting = registerSetting("Render", true)
    private val outline: BooleanSetting = registerSetting("Outline", false).booleanVisible(render)

    private val godBlocks =
        listOf(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.BEDROCK)
    private var cancelStart = false
    private var empty = false
    private var facing: EnumFacing? = null

    private val breakSuccess = Timer()

    @JvmField
    var breakPos: BlockPos? = null

    @JvmField
    var breakPos2: BlockPos? = null


    private val lock = Any()

    override fun onEnable() {
        if (fullNullCheck()) {
            rend()
            return
        }
    }

    override fun onDisable() {
        if (fullNullCheck()) {
            return
        }
        rend()

    }

    @SubscribeEvent
    fun onRenderTick(event: RenderTickEvent) {
        if (fullNullCheck()) {
            rend()
            return
        }
        if (!cancelStart) {
            return
        }
        if (InventoryUtil.findHotbarItem(ItemPickaxe::class.java) == -1) {
            return
        }
        if (doubleBreak.value && breakPos2 != null && facing != null) {
            synchronized(lock) {
                val slotMains = mc.player.inventory.currentItem
                if (mc.world.getBlockState(breakPos2!!).block !== Blocks.AIR
                    && InventoryUtil.findHotbarItem(ItemPickaxe::class.java) != -1
                ) {
                    if (mc.world.getBlockState(breakPos2!!).block === Blocks.OBSIDIAN) {
                        if (!breakSuccess.passedMs(1234L)) {
                            return
                        }
                    }
                    mc.player.connection.sendPacket(CPacketHeldItemChange(InventoryUtil.findHotbarItem(ItemPickaxe::class.java)))
                    mc.player.connection.sendPacket(
                        CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            breakPos2!!,
                            facing!!
                        )
                    )
                }
                if (mc.world.getBlockState(breakPos2!!).block == Blocks.AIR) {
                    breakPos2 = null
                    mc.player.connection.sendPacket(CPacketHeldItemChange(slotMains))
                }
            }

        }
        if (godBlocks.contains(mc.world.getBlockState(breakPos ?: return).block)) {
            return
        }
        if (ghostHand.value
            && (InventoryUtil.findHotbarItem(ItemPickaxe::class.java) != -1)
            && InventoryUtil.findHotbarItem(ItemPickaxe::class.java) != -1
        ) {
            val slotMain = mc.player.inventory.currentItem
            if (mc.world.getBlockState(breakPos ?: return).block === Blocks.OBSIDIAN) {
                if (!breakSuccess.passedMs(1234L)) {
                    return
                }
                mc.player.inventory.currentItem = InventoryUtil.findHotbarItem(ItemPickaxe::class.java)
                mc.playerController.updateController()
                if (breakPos != null && facing != null) {
                    synchronized(lock) {
                        mc.player.connection.sendPacket(
                            CPacketPlayerDigging(
                                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                                breakPos!!,
                                facing!!
                            )
                        )
                    }
                }
                mc.player.inventory.currentItem = slotMain
                mc.playerController.updateController()
                return
            }
            mc.player.inventory.currentItem = InventoryUtil.findHotbarItem(ItemPickaxe::class.java)
            mc.playerController.updateController()
            if (breakPos != null && facing != null) {
                synchronized(lock) {
                    mc.player.connection.sendPacket(
                        CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            breakPos!!,
                            facing!!
                        )
                    )
                }
            }
            mc.player.inventory.currentItem = slotMain
            mc.playerController.updateController()
            return
        }

        if (breakPos != null && facing != null) {
            synchronized(lock) {
                mc.player.connection.sendPacket(
                    CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos!!,
                        facing!!
                    )
                )
                if (doubleBreak.value) {
                    mc.player.connection.sendPacket(
                        CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos2!!,
                            facing!!
                        )
                    )
                }
            }
        }
    }

    private fun rend() {
        empty = false
        cancelStart = false
        breakPos = null
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvent.Send) {
        if (fullNullCheck()) {
//            rend()
            return
        }
        if (!isEnabled) {
//            rend()
            return
        }
        if (event.getPacket<Packet<*>>() !is CPacketPlayerDigging) {
            return
        }
        if (!(event.getPacket<Packet<*>>() as CPacketPlayerDigging).action.equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
            return
        }
        event.isCanceled = cancelStart
    }


    @SubscribeEvent
    fun onPlayerDamageBlock(event: BlockEvent) {
        if (fullNullCheck()) {
            rend()
            return
        }
        if (!isEnabled) {
            return
        }
        if (!BlockUtil.canBreak(event.pos)) {
            return
        }
        if (breakPos != null) {
            if (breakPos == event.pos) {
                return
            }
        }

        empty = false
        cancelStart = false
        breakPos2 = breakPos
        synchronized(lock) {
            breakPos = event.pos
            breakSuccess.reset()
            facing = event.facing
        }
        if (breakPos == null || facing == null) {
            return
        }
        synchronized(lock) {
            mc.player.swingArm(EnumHand.MAIN_HAND)
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                    breakPos!!,
                    facing!!
                )
            )
            cancelStart = true
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    breakPos!!,
                    facing!!
                )
            )
        }
        event.isCanceled = true
    }

    override fun onRender3D(event: Render3DEvent?) {
        if (fullNullCheck()) {
            return
        }
        val color = Color(0, 255, 0,55)
        val color2 = Color(255, 0, 0,55)
        if (render.value && breakPos != null && BlockUtil.canBreak(breakPos)) {
            Render3DUtil.drawBlockBox(
                breakPos,
                if (breakPos?.let { mc.world.getBlockState(it).block } == Blocks.AIR) color else color2,
                outline.value,
                1.0f
            )
        }

        if (breakPos2 != null) {
            if (render.value && BlockUtil.canBreak(
                    breakPos2
                ) && breakPos2?.let { mc.world.getBlockState(it).block } != Blocks.AIR
            ) {
                Render3DUtil.drawBlockBox(
                    breakPos2,
                    Color(160, 32, 240,35),
                    outline.value,
                    1.0f
                )
            }
        }
    }
}
package me.jiyun233.nya.module.modules.world

import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.settings.BindSetting
import me.jiyun233.nya.settings.BooleanSetting
import me.jiyun233.nya.utils.player.BlockUtil
import me.jiyun233.nya.utils.player.InventoryUtil
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "KeyPlace", descriptions = "Press key place block", category = Category.WORLD)
class KeyPlace : Module() {

    private var bind: BindSetting = registerSetting("BlockBind", BindSetting.KeyBind(0))

    var packet: BooleanSetting = registerSetting("Packet", true)

    private var ghost: BooleanSetting = registerSetting("Ghost", true)


    override fun onUpdate() {
        var blockpos: BlockPos? = null
        if (Keyboard.getEventKeyState() && Keyboard.isKeyDown(bind.value.keyCode)) {
            val ray = mc.objectMouseOver
            if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK && mc.world.getBlockState(ray.blockPos.also {
                    blockpos = it
                }).material !== Material.AIR && blockpos?.let { mc.world.worldBorder.contains(it) } == true) {
                val oldSlot = mc.player.inventory.currentItem
                if (ghost.value) {
                    val blockSlot: Int = InventoryUtil.findHotbarItem(ItemBlock::class.java)
                    if (blockSlot == -1) return
                    InventoryUtil.switchToHotbarSlot(blockSlot, false)
                }
                BlockUtil.placeBlock(blockpos, EnumHand.MAIN_HAND, true, packet.value)
                if (ghost.value) {
                    InventoryUtil.switchToHotbarSlot(oldSlot, false)
                }
            }
        }
    }
}
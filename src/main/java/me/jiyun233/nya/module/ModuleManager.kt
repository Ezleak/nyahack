package me.jiyun233.nya.module

import me.jiyun233.nya.event.events.world.Render3DEvent
import me.jiyun233.nya.module.huds.ItemsRender
import me.jiyun233.nya.module.huds.ModuleArrayList
import me.jiyun233.nya.module.huds.NotificationModule
import me.jiyun233.nya.module.huds.WaterMark
import me.jiyun233.nya.module.modules.client.*
import me.jiyun233.nya.module.modules.combat.*
import me.jiyun233.nya.module.modules.exploits.*
import me.jiyun233.nya.module.modules.function.*
import me.jiyun233.nya.module.modules.movement.AlwaysJump
import me.jiyun233.nya.module.modules.movement.AntiVoid
import me.jiyun233.nya.module.modules.movement.AutoWalk
import me.jiyun233.nya.module.modules.movement.HoleSnap
import me.jiyun233.nya.module.modules.movement.ReverseStep
import me.jiyun233.nya.module.modules.movement.Sprint
import me.jiyun233.nya.module.modules.movement.Strafe
import me.jiyun233.nya.module.modules.visual.*
import me.jiyun233.nya.module.modules.world.AutoRespawn
import me.jiyun233.nya.module.modules.world.FakePlayer
import me.jiyun233.nya.module.modules.world.KeyPlace

class ModuleManager {
    var moduleList = ArrayList<AbstractModule>()
    var functionList = ArrayList<AbstractModule>()
    var hudList = ArrayList<AbstractModule>()

    init {
        //Client
        registerModule(ClickGui())
        registerModule(HudEditor())
        registerModule(ChatSuffix())
        registerModule(ChatTimeStamp())
        registerModule(IRC)
        registerModule(NotificationModule())
        registerModule(CustomMainMenu)

        //Combat
        registerModule(Surround())
        registerModule(KillAura())
        registerModule(AntiCevPlus)
        registerModule(SphereSurround())
        registerModule(SmartAntiCity())
        registerModule(IntellectHeadTrap)
        registerModule(PistonHoleKicker)
        registerModule(AntiBurrow())
        registerModule(AntiPiston())
        registerModule(SmartCevBreaker())
        registerModule(PistonCrystal())
        registerModule(AntiCrystal())
        //exploits
        registerModule(BowSpoof)
        registerModule(PingSpoof)
        registerModule(PortalGodMode)
        registerModule(Timer)
        registerModule(ServerCrasher)
        //Function
        registerModule(MiddleClick())
        registerModule(FakeKick())
        registerModule(NoRotate())
        registerModule(AutoPorn())
//        registerModule(ColorChat())
        registerModule(InstantMinePlus)
        registerModule(ReplaceWeb())
        //Movement
        registerModule(Sprint())
        registerModule(AutoWalk())
        registerModule(HoleSnap)
        registerModule(AlwaysJump())
        registerModule(AntiVoid())
        registerModule(ReverseStep())
        registerModule(Strafe())
        //Visual
        registerModule(FullBright())
        registerModule(BlockHighlight())
        registerModule(SkyColor())
        registerModule(CityESPPlus)
        registerModule(BreadCrumbs())
        registerModule(ViewChange())
        //World
        registerModule(FakePlayer())
        registerModule(AutoRespawn())
        registerModule(KeyPlace())
        //Hud
        registerModule(WaterMark())
        registerModule(ModuleArrayList())
        registerModule(ItemsRender())
    }

    private fun registerModule(module: AbstractModule) {
        if (!moduleList.contains(module)) moduleList.add(module)
        if (module.isHud) {
            if (!hudList.contains(module)) hudList.add(module)
        } else if (!functionList.contains(module)) {
            functionList.add(module)
        }
    }

    fun getModulesByCategory(category: Category): List<AbstractModule> {
        return moduleList.filter { it.category == category }
    }

    fun getModuleByClass(clazz: Class<*>): AbstractModule? {
        for (abstractModule in moduleList) {
            if (abstractModule::class.java == clazz) return abstractModule
        }
        return null
    }


    fun getModuleByName(name: String): AbstractModule? {
        for (abstractModule in moduleList) {
            if (abstractModule.name.lowercase() == name.lowercase()) return abstractModule
        }
        return null
    }

    fun onUpdate() {
        moduleList.filter { it.isEnabled }.forEach { it.onUpdate() }
    }

    fun onLogin() {
        moduleList.filter { it.isEnabled }.forEach { it.onLogin() }
    }

    fun onLogout() {
        moduleList.filter { it.isEnabled }.forEach { it.onLogout() }
    }

    fun onRender3D(event: Render3DEvent) {
        moduleList.filter { it.isEnabled }.forEach { it.onRender3D(event) }
    }

    fun onRender2D() {
        moduleList.filter { it.isEnabled }.forEach { it.onRender2D() }
    }
}
package me.mazexd.propertools;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// TODO: KeyBinding to toggle on/off

@Mod(modid = "propertools", name = "ProperTools", useMetadata = true)
@SideOnly(Side.CLIENT)
public class ProperTools {

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        FMLCommonHandler.instance().bus().register(SwitchHandler.instance());
    }
}

package me.mazexd.propertools;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;


@Mod(modid = "propertools", name = "ProperTools", useMetadata = true)
@SideOnly(Side.CLIENT)
public class ProperTools {

    public static final KeyBinding toggleBinding = new KeyBinding("Toggle", Keyboard.KEY_V, "ProperTools");

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        ClientRegistry.registerKeyBinding(toggleBinding);
        FMLCommonHandler.instance().bus().register(SwitchHandler.instance());
    }
}

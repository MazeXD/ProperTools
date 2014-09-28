package me.mazexd.propertools;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.common.ForgeHooks;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tconstruct.library.tools.HarvestTool;
import tconstruct.library.tools.Weapon;

@SideOnly(Side.CLIENT)
public class SwitchHandler {

    private static SwitchHandler instance = new SwitchHandler();

    public static SwitchHandler instance() {
        return instance;
    }

    private Minecraft minecraft;
    private boolean hasTinkersConstruct;

    private SwitchHandler() {
        minecraft = Minecraft.getMinecraft();
        hasTinkersConstruct = Loader.isModLoaded("TConstruct");
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent e) {
        if (e.phase == Phase.START)
            return;

        if (minecraft.theWorld == null)
            return;

        MovingObjectPosition object = minecraft.objectMouseOver;
        if (object == null || object.typeOfHit != MovingObjectType.BLOCK)
            return;

        boolean attackKeyDown = isKeyDown(minecraft.gameSettings.keyBindAttack.getKeyCode());

        if (!attackKeyDown)
            return;

        Block block = minecraft.theWorld.getBlock(object.blockX, object.blockY, object.blockZ);
        int metadata = minecraft.theWorld.getBlockMetadata(object.blockX, object.blockY, object.blockZ);

        int newActive = -1;

        for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
            ItemStack item = minecraft.thePlayer.inventory.mainInventory[i];

            if (item == null)
                continue;

            if (canHarvest(block, metadata, item)) {
                newActive = i;
                break;
            }
        }

        if (newActive == -1 || newActive == minecraft.thePlayer.inventory.currentItem)
            return;

        minecraft.thePlayer.inventory.currentItem = newActive;
    }

    private boolean canHarvest(Block block, int metadata, ItemStack item) {
        boolean result = false;

        if (hasTinkersConstruct) {
            result = checkTinkerTool(block, metadata, item);
        }

        if (!result) {
            result = ForgeHooks.isToolEffective(item, block, metadata) || item.func_150997_a(block) > 1.5f;
        }

        return result;
    }

    @Optional.Method(modid = "TConstruct")
    private boolean checkTinkerTool(Block block, int metadata, ItemStack item) {
        if (item.getItem() instanceof Weapon) {
            Weapon weapon = (Weapon) item.getItem();

            // TC returns 15.0f for web materials and 1.5f for any other
            // material. If the weapon is broken it returns 0.1f.
            return weapon.getDigSpeed(item, block, metadata) > 1.5f;
        }

        if (!(item.getItem() instanceof HarvestTool)) {
            return false;
        }

        HarvestTool tool = (HarvestTool) item.getItem();
        return tool.isEffective(block, metadata);
    }

    private boolean isKeyDown(int keyCode) {
        if (keyCode < 0) {
            return Mouse.isButtonDown(keyCode + 100);
        } else {
            return Keyboard.isKeyDown(keyCode);
        }
    }
}

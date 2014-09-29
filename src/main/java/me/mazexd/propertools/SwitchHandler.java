package me.mazexd.propertools;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.mazexd.propertools.util.BlockProperties;
import me.mazexd.propertools.util.ItemProperties;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Map;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public class SwitchHandler {

    private static SwitchHandler instance = new SwitchHandler();

    public static SwitchHandler instance() {
        return instance;
    }

    private Minecraft minecraft;

    private boolean isActive = true;
    private boolean hasJoined = false;

    private SwitchHandler() {
        minecraft = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onKeyBind(InputEvent.KeyInputEvent e) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

        if (Minecraft.getMinecraft().currentScreen != null)
            return;

        if (player == null)
            return;

        if (ProperTools.toggleBinding.isPressed()) {
            isActive = !isActive;
            player.addChatMessage(new ChatComponentText("ProperTools has been " + (isActive ? "activated" : "deactivated")));
        }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent e) {
        if (e.phase == Phase.START)
            return;

        WorldClient world = minecraft.theWorld;
        if (world == null) {
            hasJoined = false;
            return;
        }

        if (!hasJoined) {
            minecraft.thePlayer.addChatMessage(new ChatComponentText("ProperTools is " + (isActive ? "active" : "inactive")));
            hasJoined = true;
        }

        if (!isActive)
            return;

        MovingObjectPosition object = minecraft.objectMouseOver;
        if (object == null || object.typeOfHit != MovingObjectType.BLOCK)
            return;

        boolean attackKeyDown = isKeyDown(minecraft.gameSettings.keyBindAttack.getKeyCode());

        if (!attackKeyDown || minecraft.currentScreen != null)
            return;

        if (world.getTileEntity(object.blockX, object.blockY, object.blockZ) != null)
            return;

        Block block = world.getBlock(object.blockX, object.blockY, object.blockZ);
        int metadata = world.getBlockMetadata(object.blockX, object.blockY, object.blockZ);

        Map<Integer, ItemProperties> items = Maps.newTreeMap();
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
            ItemStack item = minecraft.thePlayer.inventory.mainInventory[i];

            if (item == null)
                continue;

            ItemProperties itemProps = ItemProperties.fromItem(item, block, metadata);
            if (itemProps.canBreak()) {
                items.put(i, itemProps);
            }
        }

        if (items.isEmpty())
            return;

        int newActive = -1;

        BlockProperties blockProps = BlockProperties.fromBlock(world, object.blockX, object.blockY, object.blockZ, block, metadata);
        System.out.println(blockProps);

        if (blockProps.supportsSilktouch()) {
            for (Entry<Integer, ItemProperties> entry : items.entrySet()) {
                ItemProperties item = entry.getValue();
                if (item.hasSilktouch()) {
                    if (newActive != -1 && items.get(newActive).getDigSpeed() >= item.getDigSpeed())
                        continue;

                    newActive = entry.getKey();
                }
            }
        }

        if (blockProps.supportsFortune() && newActive == -1) {
            for (Entry<Integer, ItemProperties> entry : items.entrySet()) {
                ItemProperties item = entry.getValue();
                if (item.hasFortune()) {
                    if (newActive != -1 && items.get(newActive).getDigSpeed() >= item.getDigSpeed())
                        continue;

                    newActive = entry.getKey();
                }
            }
        }

        if (newActive == -1) {
            for (Entry<Integer, ItemProperties> entry : items.entrySet()) {
                ItemProperties item = entry.getValue();

                if (newActive == -1) {
                    newActive = entry.getKey();
                    continue;
                }

                ItemProperties activeItem = items.get(newActive);

                if (activeItem.hasFortune() || activeItem.hasSilktouch()) {
                    if (!item.hasFortune() && !item.hasSilktouch()) {
                        newActive = entry.getKey();
                        continue;
                    }
                } else if (item.hasFortune() || item.hasSilktouch()) {
                    continue;
                }

                if (activeItem.getDigSpeed() >= item.getDigSpeed())
                    continue;

                newActive = entry.getKey();
            }
        }

        if (newActive == -1 || newActive == minecraft.thePlayer.inventory.currentItem)
            return;

        minecraft.thePlayer.inventory.currentItem = newActive;
    }

    private boolean isKeyDown(int keyCode) {
        if (keyCode < 0) {
            return Mouse.isButtonDown(keyCode + 100);
        } else {
            return Keyboard.isKeyDown(keyCode);
        }
    }
}

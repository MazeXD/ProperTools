package me.mazexd.propertools.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;


public class BlockHelper {


    private BlockHelper() {}

    // Hacky solution to determine fortune support
    public static boolean supportsFortune(World world, int x, int y, int z, Block block, int metadata) {
        List<ItemStack> dropsNormal = block.getDrops(world, x, y, z, metadata, 0);
        List<ItemStack> dropsFortune = block.getDrops(world, x, y, z, metadata, 100);

        if (dropsFortune.size() > dropsNormal.size())
            return true;

        for (ItemStack dropNormal : dropsNormal) {
            for (ItemStack dropFortune : dropsFortune) {
                if (!dropNormal.isItemEqual(dropFortune))
                    continue;

                if (dropNormal.stackSize < dropFortune.stackSize)
                    return true;
            }
        }

        return false;
    }

    public static boolean supportsSilktouch(World world, int x, int y, int z, Block block, int metadata) {
        return block.canSilkHarvest(world, Minecraft.getMinecraft().thePlayer, x, y, z, metadata);
    }
}

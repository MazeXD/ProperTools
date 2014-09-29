package me.mazexd.propertools.util;

import net.minecraft.block.Block;
import net.minecraft.world.World;


public class BlockProperties {

    private boolean fortune;
    private boolean silktouch;

    private BlockProperties() {
        fortune = false;
        silktouch = false;
    }

    public boolean supportsFortune() {
        return fortune;
    }

    public boolean supportsSilktouch() {
        return silktouch;
    }

    @Override
    public String toString() {
        return "BlockProperties [fortune=" + fortune + ", silktouch=" + silktouch + "]";
    }

    public static BlockProperties fromBlock(World world, int x, int y, int z, Block block, int metadata) {
        BlockProperties properties = new BlockProperties();

        properties.fortune = BlockHelper.supportsFortune(world, x, y, z, block, metadata);
        properties.silktouch = BlockHelper.supportsSilktouch(world, x, y, z, block, metadata);

        return properties;
    }
}

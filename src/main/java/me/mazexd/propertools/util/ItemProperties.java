package me.mazexd.propertools.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;


@SideOnly(Side.CLIENT)
public class ItemProperties {

    private boolean canBreak;
    private float digSpeed;

    private boolean silktouch;
    private int fortune;

    private double damage;
    private int looting;

    private ItemProperties() {
        canBreak = false;
        digSpeed = 0.0f;
        silktouch = false;
        fortune = 0;
        looting = 0;
        damage = 0.0d;
    }

    public boolean canBreak() {
        return canBreak;
    }

    public float getDigSpeed() {
        return digSpeed;
    }

    public boolean hasSilktouch() {
        return silktouch;
    }

    public boolean hasFortune() {
        return fortune > 0;
    }

    public int getFortune() {
        return fortune;
    }

    public boolean hasLooting() {
        return looting > 0;
    }

    public int getLooting() {
        return looting;
    }

    public double getDamage() {
        return damage;
    }

    @Override
    public String toString() {
        return "ItemProperties [canBreak=" + canBreak + ", digSpeed=" + digSpeed + ", silktouch=" + silktouch + ", fortune=" + fortune + ", damage="
                + damage + ", looting=" + looting + "]";
    }

    public static ItemProperties fromItem(ItemStack item, Block block, int metadata) {
        ItemProperties properties = new ItemProperties();

        properties.canBreak = ItemHelper.canBreak(item, block, metadata);

        if (!properties.canBreak)
            return properties;

        properties.digSpeed = ItemHelper.getDigSpeed(item, block, metadata);
        properties.silktouch = ItemHelper.getEnchantmentLevel(Enchantment.silkTouch, item) > 0;

        properties.fortune = ItemHelper.getEnchantmentLevel(Enchantment.fortune, item);
        properties.looting = ItemHelper.getEnchantmentLevel(Enchantment.looting, item);

        properties.damage = ItemHelper.getDamage(item);

        return properties;
    }
}

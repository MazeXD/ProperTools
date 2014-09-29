package me.mazexd.propertools.util;

import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeHooks;
import tconstruct.library.tools.HarvestTool;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.Weapon;

import java.util.UUID;


public class ItemHelper {

    private static final UUID damageAttribute = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final boolean hasTinkers;

    private ItemHelper() {}

    public static boolean canBreak(ItemStack item, Block block, int metadata) {
        boolean result = false;

        if (hasTinkers) {
            result = canBreakTinker(item, block, metadata);
        }

        if (!result) {
            result = ForgeHooks.isToolEffective(item, block, metadata) || item.func_150997_a(block) > 1.5f;
        }

        return result;
    }

    @Optional.Method(modid = "TConstruct")
    private static boolean canBreakTinker(ItemStack item, Block block, int metadata) {
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

    public static float getDigSpeed(ItemStack item, Block block, int metadata) {
        return item.getItem().getDigSpeed(item, block, metadata);
    }

    public static int getEnchantmentLevel(Enchantment enchantment, ItemStack item) {
        return EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, item);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static double getDamage(ItemStack item) {
        double result = 0;

        if (hasTinkers) {
            result = getDamageTinkers(item);
        }

        if (result > 0) {
            Multimap attributes = item.getAttributeModifiers();

            if (!attributes.containsKey(damageAttribute)) {
                result = 1;
            } else {
                AttributeModifier attribute = (AttributeModifier) item.getAttributeModifiers().get(damageAttribute);
                result = attribute.getAmount();
                result *= EnchantmentHelper.func_152377_a(item, EnumCreatureAttribute.UNDEFINED);
            }

        }

        return result;
    }

    @Optional.Method(modid = "TConstruct")
    private static double getDamageTinkers(ItemStack item) {
        if (item.getItem() instanceof ToolCore) {
            ToolCore tool = (ToolCore) item.getItem();
            NBTTagCompound tags = item.getTagCompound().getCompoundTag("InfiTool");

            double damage = 1.0d;

            if (!tags.getBoolean("Broken")) {
                damage = tags.getInteger("Attack");
                damage *= tool.getDamageModifier();

                int durability = tags.getInteger("Damage");
                float stonebound = tags.getFloat("Shoddy");
                float stoneboundDamage = (float) Math.log(durability / 72f + 1) * -2 * stonebound;

                damage += stoneboundDamage;
                if (damage < 1)
                    damage = 1;
            }

            return damage;
        }

        return 0.0d;
    }

    static {
        hasTinkers = Loader.isModLoaded("TConstruct");
    }
}

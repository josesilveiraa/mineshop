package com.mineshop.utils;

import org.bukkit.enchantments.*;

public class EnchantLibrary
{
    public static Enchantment getTranslateEnchant(final String enchant) {
        Enchantment en = null;
        if (enchant.contains("sharpness") || enchant.contains("DAMAGE_ALL") || enchant.contains("alldamage")) {
            en = Enchantment.DAMAGE_ALL;
        }
        if (enchant.contains("knockback")) {
            en = Enchantment.KNOCKBACK;
        }
        if (enchant.contains("baneofarthropods") || enchant.contains("arthropodsdamage") || enchant.contains("DAMAGE_ARTHROPODS")) {
            en = Enchantment.DAMAGE_ARTHROPODS;
        }
        if (enchant.contains("undeaddamage") || enchant.contains("smite") || enchant.contains("DAMAGE_UNDEAD")) {
            en = Enchantment.DAMAGE_UNDEAD;
        }
        if (enchant.contains("digspeed") || enchant.contains("efficiency") || enchant.contains("DIG_SPEED")) {
            en = Enchantment.DIG_SPEED;
        }
        if (enchant.contains("durability") || enchant.contains("unbreaking") || enchant.contains("DURABILITY")) {
            en = Enchantment.DURABILITY;
        }
        if (enchant.contains("fireaspect") || enchant.contains("fire") || enchant.contains("FIRE_ASPECT")) {
            en = Enchantment.FIRE_ASPECT;
        }
        if (enchant.contains("blockslootbonus") || enchant.contains("fortune") || enchant.contains("LOOT_BONUS_BLOCKS")) {
            en = Enchantment.LOOT_BONUS_BLOCKS;
        }
        if (enchant.contains("mobslootbonus") || enchant.contains("mobloot") || enchant.contains("looting") || enchant.contains("LOOT_BONUS_MOBS")) {
            en = Enchantment.LOOT_BONUS_MOBS;
        }
        if (enchant.contains("respiration") || enchant.contains("oxygen") || enchant.contains("OXYGEN")) {
            en = Enchantment.OXYGEN;
        }
        if (enchant.contains("PROTECTION_ENVIRONMENTAL") || enchant.contains("protection")) {
            en = Enchantment.PROTECTION_ENVIRONMENTAL;
        }
        if (enchant.contains("explosionsprotection") || enchant.contains("blastprotection") || enchant.contains("PROTECTION_EXPLOSIONS")) {
            en = Enchantment.PROTECTION_EXPLOSIONS;
        }
        if (enchant.contains("fallprotection") || enchant.contains("fallprot") || enchant.contains("PROTECTION_FALL")) {
            en = Enchantment.PROTECTION_FALL;
        }
        if (enchant.contains("fireprotection") || enchant.contains("PROTECTION_FIRE")) {
            en = Enchantment.PROTECTION_FIRE;
        }
        if (enchant.contains("projectileprotection") || enchant.contains("PROTECTION_PROJECTILE")) {
            en = Enchantment.PROTECTION_PROJECTILE;
        }
        if (enchant.contains("silktouch") || enchant.contains("SILK_TOUCH")) {
            en = Enchantment.SILK_TOUCH;
        }
        if (enchant.contains("aquaaffinity") || enchant.contains("waterworker") || enchant.contains("WATER_WORKER")) {
            en = Enchantment.WATER_WORKER;
        }
        if (enchant.contains("flame") || enchant.contains("firearrow") || enchant.contains("ARROW_FIRE")) {
            en = Enchantment.ARROW_FIRE;
        }
        if (enchant.contains("arrowdamage") || enchant.contains("power") || enchant.contains("ARROW_DAMAGE")) {
            en = Enchantment.ARROW_DAMAGE;
        }
        if (enchant.contains("arrowknockback") || enchant.contains("punch") || enchant.contains("ARROW_KNOCKBACK")) {
            en = Enchantment.ARROW_KNOCKBACK;
        }
        if (enchant.contains("infinitearrows") || enchant.contains("infinity") || enchant.contains("ARROW_INFINITE")) {
            en = Enchantment.ARROW_INFINITE;
        }
        return en;
    }
}

package me.wolf.wquakecraft.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class ItemUtils {

    private ItemUtils() {
    }

    public static ItemStack createItem(final Material mat, final String display, final int amount) {
        final ItemStack is = new ItemStack(mat, amount);
        final ItemMeta meta = is.getItemMeta();

        assert meta != null;
        meta.setDisplayName(Utils.colorize(display));
        is.setItemMeta(meta);

        return is;
    }

    public static ItemStack createItem(final Material mat, final List<String> lore) {
        final ItemStack is = new ItemStack(mat);
        final ItemMeta meta = is.getItemMeta();
        final List<String> coloredLore = new ArrayList<>();

        for (final String s : lore) {
            coloredLore.add(Utils.colorize(s));
        }

        assert meta != null;
        meta.setLore(coloredLore);
        is.setItemMeta(meta);

        return is;
    }

    public static ItemStack createItem(final Material mat, final String display, final int amount, final short data) {
        final ItemStack is = new ItemStack(mat, amount, data);
        final ItemMeta meta = is.getItemMeta();

        assert meta != null;
        meta.setDisplayName(Utils.colorize(display));
        is.setItemMeta(meta);

        return is;
    }

    public static ItemStack createItem(final ItemStack is, final List<String> lore) {
        final ItemMeta itemMeta = is.getItemMeta();
        final List<String> newLore = new ArrayList<>();
        for (final String s : lore) {
            newLore.add(Utils.colorize(s));
        }
        assert itemMeta != null;
        itemMeta.setLore(newLore);
        is.setItemMeta(itemMeta);
        return is;
    }

    public static ItemStack createItem(final Material mat, final String display, final List<String> lore) {
        final ItemStack is = new ItemStack(mat);
        final ItemMeta meta = is.getItemMeta();

        assert meta != null;
        meta.setDisplayName(Utils.colorize(display));
        meta.setLore(Utils.colorize(lore));
        is.setItemMeta(meta);

        return is;
    }

    public static ItemStack createItem(final Material mat, final String display, final short data) {
        final ItemStack is = new ItemStack(mat, data);
        final ItemMeta meta = is.getItemMeta();

        assert meta != null;
        meta.setDisplayName(Utils.colorize(display));
        is.setItemMeta(meta);

        return is;
    }

    public static ItemStack createItem(final Material mat, final String display) {
        final ItemStack is = new ItemStack(mat);
        final ItemMeta meta = is.getItemMeta();

        assert meta != null;
        meta.setDisplayName(Utils.colorize(display));
        is.setItemMeta(meta);

        return is;
    }

}

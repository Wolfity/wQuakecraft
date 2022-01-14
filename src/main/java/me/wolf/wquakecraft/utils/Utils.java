package me.wolf.wquakecraft.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class Utils {

    private Utils() {
    }

    public static String colorize(final String input) {
        return input == null ? "Null value" : ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<String> colorize(final List<String> list) {
        final List<String> coloredList = new ArrayList<>();
        for (final String s : list) {
            coloredList.add(colorize(s));
        }
        return coloredList;
    }

    public static String[] colorize(String... messages) {
        String[] colorized = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            colorized[i] = ChatColor.translateAlternateColorCodes('&', messages[i]);
        }
        return colorized;
    }

    public static void sendCenteredMessage(Player player, String message) {
        int CENTER_PX = 154;
        if (message == null || message.equals("")) player.sendMessage("");
        assert message != null;
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb + message);
    }

    public static String locationToString(final Location location) {
        return location.getWorld().getName() + " " +
                location.getX() + " " +
                location.getY() + " " +
                location.getZ() + " " +
                location.getYaw() + " " +
                location.getPitch();
    }

    public static Location stringToLoc(final String[] strings) {
        return new Location(Bukkit.getWorld(strings[0]),
                Double.parseDouble(strings[1]),
                Double.parseDouble(strings[2]),
                Double.parseDouble(strings[3]),
                Float.parseFloat(strings[4]),
                Float.parseFloat(strings[5]));
    }

}

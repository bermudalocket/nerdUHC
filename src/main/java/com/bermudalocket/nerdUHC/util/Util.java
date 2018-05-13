package com.bermudalocket.nerdUHC.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.Random;

public class Util {

    public static final int TPS = 20;

    public static final char COLORCHAR = "&".charAt(0);

    public static final String MAIN_OBJECTIVE = "main";

    public static final Random random = new Random();

    public static int minsToTicks(int mins) {
        return mins * 60 * TPS;
    }

    public static int secToTicks(int sec) {
        return sec * TPS;
    }

    public static DyeColor chatColorToDyeColor(ChatColor chatColor) {
        DyeColor dyeColor;
        switch (chatColor) {
            case AQUA:
                dyeColor = DyeColor.LIGHT_BLUE;
                break;
            case BLACK:
                dyeColor = DyeColor.BLACK;
                break;
            case BLUE:
                dyeColor = DyeColor.BLUE;
                break;
            case BOLD:
                dyeColor = DyeColor.WHITE;
                break;
            case DARK_AQUA:
                dyeColor = DyeColor.BLUE;
                break;
            case DARK_BLUE:
                dyeColor = DyeColor.BLUE;
                break;
            case DARK_GRAY:
                dyeColor = DyeColor.GRAY;
                break;
            case DARK_GREEN:
                dyeColor = DyeColor.GREEN;
                break;
            case DARK_PURPLE:
                dyeColor = DyeColor.PURPLE;
                break;
            case DARK_RED:
                dyeColor = DyeColor.RED;
                break;
            case GOLD:
                dyeColor = DyeColor.ORANGE;
                break;
            case GRAY:
                dyeColor = DyeColor.GRAY;
                break;
            case GREEN:
                dyeColor = DyeColor.LIME;
                break;
            case ITALIC:
                dyeColor = DyeColor.WHITE;
                break;
            case LIGHT_PURPLE:
                dyeColor = DyeColor.PINK;
                break;
            case MAGIC:
                dyeColor = DyeColor.WHITE;
                break;
            case RED:
                dyeColor = DyeColor.RED;
                break;
            case RESET:
                dyeColor = DyeColor.WHITE;
                break;
            case STRIKETHROUGH:
                dyeColor = DyeColor.WHITE;
                break;
            case UNDERLINE:
                dyeColor = DyeColor.WHITE;
                break;
            case WHITE:
                dyeColor = DyeColor.WHITE;
                break;
            case YELLOW:
                dyeColor = DyeColor.YELLOW;
                break;
            default:
                dyeColor = DyeColor.WHITE;
                break;
        }
        return dyeColor;
    }

}

package uwu.flauxy.utils;

import net.minecraft.client.gui.GuiScreen;

import java.util.Random;
import java.util.regex.Pattern;
public class StringUtils
{
    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    /**
     * Returns the time elapsed for the given number of ticks, in "mm:ss" format.
     */

    public static String getTrimmedClipboardContents() {
        String data = GuiScreen.getClipboardString();
        data = data.trim();
        if (data.indexOf('\n') != -1)
            data = data.replace("\n", "");
        return data;
    }


    public static String ticksToElapsedTime(int ticks)
    {
        int i = ticks / 20;
        int j = i / 60;
        i = i % 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }

    public static String stripControlCodes(String p_76338_0_)
    {
        return patternControlCode.matcher(p_76338_0_).replaceAll("");
    }

    /**
     * Returns a value indicating whether the given string is null or empty.
     */
    public static boolean isNullOrEmpty(String string)
    {
        return org.apache.commons.lang3.StringUtils.isEmpty(string);
    }

    public static String generateRandomStringName(int min){
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        String randomString = "";
        Random rand = new Random();
        String prefix = "FL_";
        int length = Math.max(min, rand.nextInt(16-prefix.length()));
        char[] randomText = new char[length];
        for(int i = 0; i < randomText.length; i++){
            randomText[i] = characters.charAt(rand.nextInt(characters.length()));
        }
        for(int i = 0; i < randomText.length; i++){
            randomString += randomText[i];
        }
        return prefix + randomString;
    }

    public static String generateRandomString(int min, int max){
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        String randomString = "";
        Random rand = new Random();
        int length = Math.max(min, rand.nextInt(max));
        char[] randomText = new char[length];
        for(int i = 0; i < randomText.length; i++){
            randomText[i] = characters.charAt(rand.nextInt(characters.length()));
        }
        for(int i = 0; i < randomText.length; i++){
            randomString += randomText[i];
        }
        return randomString;
    }
}

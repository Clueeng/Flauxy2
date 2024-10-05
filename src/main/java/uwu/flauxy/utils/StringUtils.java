package uwu.flauxy.utils;

import net.minecraft.client.gui.GuiScreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public static String generateRandomStringName() {
        Random rand = new Random();

        List<String> adjectives = null;
        List<String> nouns = null;

        // Use ClassLoader to load the text files from within the JAR
        try {
            ClassLoader classLoader = StringUtils.class.getClassLoader();

            // Load adjectives2.txt
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream("adjectives2.txt"))))) {
                adjectives = reader.lines().collect(Collectors.toList());
            }

            // Load nouns2.txt
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream("nouns2.txt"))))) {
                nouns = reader.lines().collect(Collectors.toList());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Pick random adjective and noun
        String randomAdjective = adjectives.get(rand.nextInt(adjectives.size()));
        String randomNoun = nouns.get(rand.nextInt(nouns.size()));

        // Generate random number and format the result
        int randomNumber = rand.nextInt(100);
        String result = randomAdjective + "_" + randomNoun + String.format("%02d", randomNumber);

        // Ensure the result is no longer than 16 characters
        if (result.length() > 16) {
            result = result.substring(0, 16);
        }

        return result;
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

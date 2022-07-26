package uwu.flauxy.utils;

import java.util.concurrent.ThreadLocalRandom;

public class NumberUtil {

    public static double round(double number, double decimals){
        double numbera = number * Math.pow(1, decimals);
        double numb = (double)Math.round(number) / Math.pow(1, decimals);
        return (double)numb;
    }

    public static int generateRandom(int min, int max){
        int result = ThreadLocalRandom.current().nextInt(min, max);
        return result;
    }

    public static float generateRandomFloat(int min, int max, int divider){
        int thing = divider * 10;
        int resultToInt = ThreadLocalRandom.current().nextInt(min, max);
        float resultToFloat = (float)resultToInt / thing;
        return resultToFloat;
    }

}

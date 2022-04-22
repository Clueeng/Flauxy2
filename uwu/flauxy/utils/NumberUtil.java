package uwu.flauxy.utils;

import java.util.concurrent.ThreadLocalRandom;

public class NumberUtil {

    public static int generateRandom(int min, int max){
        int result = ThreadLocalRandom.current().nextInt(min, max);
        return result;
    }

    public static float generateRandomFloat(int min, int max, int divisor){
        int thing = divisor * 10;
        int resultToInt = ThreadLocalRandom.current().nextInt(min * thing, max * thing);
        float resultToFloat = (float)resultToInt / thing;
        return resultToFloat;
    }

}

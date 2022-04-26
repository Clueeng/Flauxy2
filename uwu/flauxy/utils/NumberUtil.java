package uwu.flauxy.utils;

import java.util.concurrent.ThreadLocalRandom;

public class NumberUtil {

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

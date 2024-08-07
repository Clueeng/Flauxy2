package uwu.flauxy.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NumberUtil {

    public static double round(double number, double decimals){
        double numbera = number * Math.pow(1, decimals);
        double numb = (double)Math.round(number) / Math.pow(1, decimals);
        return (double)numb;
    }

    public static double getAverage(ArrayList<Long> list1, ArrayList<Long> list2, ArrayList<Long> list3) {
        long sum1 = list1.stream().mapToLong(l -> l).sum();
        int count1 = list1.size();
        double res1 = (double) sum1 / count1;

        long sum2 = list2.stream().mapToLong(l -> l).sum();
        int count2 = list2.size();
        double res2 = (double) sum2 / count2;

        long sum3 = list3.stream().mapToLong(l -> l).sum();
        int count3 = list3.size();
        double res3 = (double) sum3 / count3;
        System.out.println(list1);
        return (double) (res1 + res2 + res3) / 3;
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

    public static double getAverage(List<Long> allIntervals) {
        return (double) allIntervals.stream().mapToLong(l -> l).sum() / allIntervals.size();
    }
}

package uwu.flauxy.utils;

import net.minecraft.item.ItemStack;

public class MathHelper {

    public static <T> void reverse(T[] array) {
        int left = 0;
        int right = array.length - 1;

        while (left < right) {
            T temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
    }


    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }
    public static double easeInQuad(double delta, double start, double end) {
        return start + (end - start) * Math.pow(delta, 2);
    }
    public static double easeOutQuad(double delta, double start, double end) {
        return start + (end - start) * (1 - Math.pow(1 - delta, 2));
    }
    public static double easeInOutQuad(double delta, double start, double end) {
        if (delta < 0.5) {
            return start + (end - start) * 2 * Math.pow(delta, 2);
        } else {
            return start + (end - start) * (1 - 2 * Math.pow(1 - delta, 2));
        }
    }
    public static double easeInCubic(double delta, double start, double end) {
        return start + (end - start) * Math.pow(delta, 3);
    }
    public static double easeOutCubic(double delta, double start, double end) {
        return start + (end - start) * (1 - Math.pow(1 - delta, 3));
    }
    public static double easeInOutCubic(double delta, double start, double end) {
        if (delta < 0.5) {
            return start + (end - start) * 4 * Math.pow(delta, 3);
        } else {
            return start + (end - start) * (1 - 4 * Math.pow(1 - delta, 3));
        }
    }
    public static double easeInBounce(double delta, double start, double end) {
        return start + (end - start) * (1 - easeOutBounce(1 - delta, 0, 1));
    }

    private static double easeOutBounce(double delta, double start, double end) {
        if (delta < (1 / 2.75)) {
            return end * (7.5625 * delta * delta) + start;
        } else if (delta < (2 / 2.75)) {
            return end * (7.5625 * (delta -= (1.5 / 2.75)) * delta + 0.75) + start;
        } else if (delta < (2.5 / 2.75)) {
            return end * (7.5625 * (delta -= (2.25 / 2.75)) * delta + 0.9375) + start;
        } else {
            return end * (7.5625 * (delta -= (2.625 / 2.75)) * delta + 0.984375) + start;
        }
    }
    public static double easeInOutBounce(double delta, double start, double end) {
        if (delta < 0.5) {
            return easeInBounce(delta * 2, 0, end - start) * 0.5 + start;
        } else {
            return easeOutBounce(delta * 2 - 1, 0, end - start) * 0.5 + (end - start) * 0.5 + start;
        }
    }

}

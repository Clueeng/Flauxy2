package uwu.noctura.utils;

import de.florianmichael.viamcp.ViaMCP;

public class ViaUtil {

    public static boolean versionLowerThan(String ver) {
        String curVer = ViaMCP.INSTANCE.getAsyncVersionSlider().displayString.split("-")[0].replace("x", "0");
        String compareVer = ver.split("-")[0].replace("x", "0");
        String[] currentVersionParts = curVer.split("\\.");
        String[] compareVersionParts = compareVer.split("\\.");
        int maxLength = Math.max(currentVersionParts.length, compareVersionParts.length);
        for (int i = 0; i < maxLength; i++) {
            int curPart = i < currentVersionParts.length ? Integer.parseInt(currentVersionParts[i]) : 0;
            int verPart = i < compareVersionParts.length ? Integer.parseInt(compareVersionParts[i]) : 0;
            if (curPart < verPart) {
                return true;
            } else if (curPart > verPart) {
                return false;
            }
        }

        // Versions are equal if all parts match
        return false;
    }

}

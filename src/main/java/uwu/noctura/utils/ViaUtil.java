package uwu.noctura.utils;

import de.florianmichael.viamcp.ViaMCP;

public class ViaUtil {

    public static boolean versionLowerThan(String ver) {
        // Get the current version and remove anything after a dash (-), then replace 'x' with '0'
        String curVer = ViaMCP.INSTANCE.getAsyncVersionSlider().displayString.split("-")[0].replace("x", "0");

        // Also remove anything after a dash (-) in the version to compare and replace 'x' with '0'
        String compareVer = ver.split("-")[0].replace("x", "0");

        // Split the current version and the comparison version by "."
        String[] currentVersionParts = curVer.split("\\.");
        String[] compareVersionParts = compareVer.split("\\.");

        // Determine the maximum length between the two versions
        int maxLength = Math.max(currentVersionParts.length, compareVersionParts.length);

        // Compare each part of the version
        for (int i = 0; i < maxLength; i++) {
            // Parse the current version part, default to 0 if it doesn't exist
            int curPart = i < currentVersionParts.length ? Integer.parseInt(currentVersionParts[i]) : 0;
            int verPart = i < compareVersionParts.length ? Integer.parseInt(compareVersionParts[i]) : 0;

            // Compare the two parts
            if (curPart < verPart) {
                return true; // Current version is lower
            } else if (curPart > verPart) {
                return false; // Current version is higher
            }
        }

        // Versions are equal if all parts match
        return false;
    }

}

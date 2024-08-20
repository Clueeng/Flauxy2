package uwu.flauxy.module.impl.other.util;

import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;

public class Folder {

    /*
    imported from Auxware
     */

    public static File dir;
    public static File auxware;
    public static File yawDir;  // New Yaw directory

    public Folder() {
        init();
    }

    public static void init() {
        auxware = new File((Minecraft.getMinecraft()).mcDataDir, "Flauxy");
        if (!auxware.exists())
            auxware.mkdir();
        dir = new File(auxware + "/Killsult");
        if (!dir.exists())
            dir.mkdir();

        yawDir = new File(auxware, "Yaw");
        if (!yawDir.exists()) {
            yawDir.mkdir();
        }
    }

    public static File getFile(String name) {
        File file = new File( Folder.auxware + "/" + name);
        return file;
    }

    public static void createFile(String name) throws IOException {
        File file = new File( Folder.auxware + "/" + name);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}

package uwu.flauxy.alts;

import uwu.flauxy.Flauxy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AltManager {
    public static Alt lastAlt;
    public static ArrayList<Alt> registry;

    static {
        registry = new ArrayList();
    }

    public ArrayList<Alt> getRegistry() {
        return registry;
    }

    public void setLastAlt(Alt alt2) {
        lastAlt = alt2;
    }

    public void loadAlts() {
        File altsFile = new File(Flauxy.INSTANCE.clientDirectory + "/alts.txt");
        if(altsFile.exists()){
            try{
                AltManager.registry.clear();
                List<String> alts = Files.readAllLines(altsFile.toPath());
                for(String alt : alts){
                    String mail = alt.split(":", 2)[0];
                    String pass = alt.split(":", 2)[1];
                    AltManager.registry.add(new Alt(mail, pass));
                }
                if(!Objects.isNull(alts.get(0))){
                    String alt = alts.get(0);
                    String mail = alt.split(":", 2)[0];
                    String pass = alt.split(":", 2)[1];
                    AltManager.lastAlt = new Alt(mail, pass);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}


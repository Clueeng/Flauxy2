package uwu.flauxy.module.impl.other;

import net.minecraft.network.play.server.S02PacketChat;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.impl.other.util.Folder;
import uwu.flauxy.module.setting.Setting;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.Wrapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

@ModuleInfo(name = "Killsults", displayName = "Killsults", cat = Category.Other, key = -1)
public class Killsults extends Module {

    public Killsults(){

    }

    ArrayList<String> insults = new ArrayList<>();

    @Override
    public void onEnable() {
        insults.clear();
        load();
        if(lines.isEmpty()){
            Wrapper.instance.log("The killsults file is empty, add something in it before enabling the module");
        }
        for(String s : lines){
            insults.add(s);
        }
    }

    Random random = new Random();

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventReceivePacket){
            EventReceivePacket eventReceivePacket = (EventReceivePacket) e;
            if(eventReceivePacket.getPacket() instanceof S02PacketChat){
                S02PacketChat packet = (S02PacketChat) eventReceivePacket.getPacket();
                String chat = packet.getChatComponent().getUnformattedText();
                String player = mc.thePlayer.getName();
                if(chat.contains("foi morto por " + player) || chat.contains("was killed by " + player)){
                    Wrapper.instance.log("Found kill message");
                    // Chat
                    int index = random.nextInt(insults.size() - 1);
                    String insult = insults.get(index);
                    String message = insult.replaceAll("<victim>", "lol");

                    mc.thePlayer.sendChatMessage("a" + message);
                }
            }
        }
    }

    ArrayList<String> lines = new ArrayList<>();
    private File dir;

    private File dataFile;

    /*public void save(String name) {
        this.dir = new File(String.valueOf(Folder.dir));
        if (!this.dir.exists())
            this.dir.mkdir();
        this.dataFile = new File(this.dir, name + ".txt");
        if (!this.dataFile.exists())
            try {
                this.dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        ArrayList<String> toSave = new ArrayList<>();
        try {
            PrintWriter pw = new PrintWriter(this.dataFile);
            for (String str : toSave)
                pw.println(str);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }*/

    /*public void delete(String name) {
        this.dir = new File(String.valueOf(Folder.dir));
        if (!this.dir.exists())
            this.dir.mkdir();
        this.dataFile = new File(this.dir, name + ".txt");
        try {
            this.dataFile.delete();
        } catch (Exception exception) {}
    }*/

    public void load() {
        String fileName = "killsults";
        this.dir = new File(String.valueOf(Folder.dir));
        if (!this.dir.exists())
            this.dir.mkdir();
        this.dataFile = new File(this.dir, fileName + ".txt");
        if(!this.dataFile.exists()){
            try{
                this.dataFile.createNewFile();
            }catch (java.io.IOException e){
                e.printStackTrace();
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.dataFile));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            for (String s : lines) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

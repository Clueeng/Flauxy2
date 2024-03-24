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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            return;
        }
        insults.addAll(lines);
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

                String victim = getVictimName(chat, player);

                if(chat.contains("foi morto por " + player) || chat.contains("was killed by " + player)){
                    // Chat
                    int index = random.nextInt(insults.size() - 1);
                    String insult = insults.get(index);
                    String message = insult.replaceAll("<victim>", victim);
                    //Wrapper.instance.log(String.valueOf(index));

                    mc.thePlayer.sendChatMessage(message);
                }
            }
        }
    }

    public static String getVictimName(String chat, String player) {
        // Pattern to match the word before " foi morto por" or " was killed by"
        Pattern pattern = Pattern.compile("(\\S+)\\s+(?:foi\\smorto\\spor|was\\skilled\\sby)\\s+" + Pattern.quote(player));
        Matcher matcher = pattern.matcher(chat);

        if (matcher.find()) {
            // The victim's name will be the word before the matched pattern
            return matcher.group(1);
        } else {
            return "victim";
        }
    }

    ArrayList<String> lines = new ArrayList<>();
    private File dir;

    private File dataFile;

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

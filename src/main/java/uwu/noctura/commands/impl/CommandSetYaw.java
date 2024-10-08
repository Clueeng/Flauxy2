package uwu.noctura.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.noctura.Noctura;
import uwu.noctura.commands.Command;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.utils.Wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandSetYaw extends Command {
    @Override
    public String getName() {
        return "setyaw";
    }

    @Override
    public String getSyntax() {
        return "setyaw <value/file/list>";
    }

    @Override
    public String getDescription() {
        return "sets your yaw";
    }

    public File getYawFile(){
        return new File(Noctura.INSTANCE.clientDirectory + File.separator + "Yaw" + File.separator + "yaw.txt");
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        String mode = args[1];
        boolean file = mode.equals("file");
        boolean value = mode.equals("value");
        boolean list = mode.equals("list");
        if(file){
            if(args.length < 3){
                Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Yaw Command", "Too few arguments", 2500));
                return;
            }
            if(!getYawFile().exists()){
                Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Yaw Command", "Create a yaw.txt file", 2500));
            }
            String yawType = args[2];

            try{
                float converted = Float.parseFloat(getFromFile(yawType));
                mc.thePlayer.rotationYaw = converted;
                Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Yaw Command", "Set your yaw to " + yawType, 2500));
            }catch (NumberFormatException e){
                Wrapper.instance.log("Error, parsing " + getFromFile(yawType));
                return;
            }
        }else if(value){
            if(args.length < 3){
                Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Yaw Command", "Too few arguments", 2500));
                return;
            }
            String val = args[2];
            try{
                mc.thePlayer.rotationYaw = Float.parseFloat(val);
                Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Yaw Command", "Set your yaw to " + val, 2500));
            }catch (NumberFormatException e){
                Wrapper.instance.log("Error, parsing " + val);
                return;
            }
        }else if(list){
            Wrapper.instance.log("List of yaw presets:");
            Wrapper.instance.log(String.valueOf(getNames()));
        }
        else{
            Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Yaw Command", "1st argument must be file or value", 2500));
            return;
        }
        // .setyaw 1

    }

    public ArrayList<String> getNames() {
        File yawFile = getYawFile();
        ArrayList<String> names = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(yawFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    names.add(parts[0].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return names;
    }

    public String getFromFile(String yawType) {
        File yawFile = getYawFile();
        String value = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(yawFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].trim().equals(yawType.trim())) {
                    value = parts[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return value;
    }
}

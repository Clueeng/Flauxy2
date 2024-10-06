package uwu.noctura.commands.impl;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import uwu.noctura.Noctura;
import uwu.noctura.commands.Command;
import uwu.noctura.module.Module;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;

public class CommandBind extends Command {
    @Override
    public String getName() {
        return "bind";
    }

    @Override
    public String getSyntax() {
        return ".bind <module> <key>";
    }

    @Override
    public String getDescription() {
        return "binds stuff to a key";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        if(args[1] == null){
            Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Error", "Wrong usage, use .help"));
        }else{
            if(args[2] == null){
                Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Error", "Wrong usage, use .help"));
            }else{
                int key = Keyboard.getKeyIndex(args[2].toUpperCase());
                String modName = args[1];
                if(Noctura.INSTANCE.moduleManager.getModule(modName) != null){
                    Module mod = Noctura.INSTANCE.moduleManager.getModule(modName);
                    mod.setKey(key);
                    //Wrapper.instance.log("Bound " + modName + " to " + args[2]);
                    Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Bind successful", "Bound " + mod.getName() + " to " + key));
                }
            }
        }
    }
}

package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.Flauxy;
import uwu.flauxy.commands.Command;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.notification.Notification;
import uwu.flauxy.notification.NotificationType;
import uwu.flauxy.utils.Wrapper;

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
            Flauxy.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Error", "Wrong usage, use .help"));
        }else{
            if(args[2] == null){
                Flauxy.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Error", "Wrong usage, use .help"));
            }else{
                int key = Keyboard.getKeyIndex(args[2].toUpperCase());
                String modName = args[1];
                if(Flauxy.INSTANCE.moduleManager.getModule(modName) != null){
                    Module mod = Flauxy.INSTANCE.moduleManager.getModule(modName);
                    mod.setKey(key);
                    //Wrapper.instance.log("Bound " + modName + " to " + args[2]);
                    Flauxy.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Bind successful", "Bound " + mod.getName() + " to " + key));
                }
            }
        }
    }
}

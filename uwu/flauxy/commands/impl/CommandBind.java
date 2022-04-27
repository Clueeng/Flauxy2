package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.Flauxy;
import uwu.flauxy.commands.Command;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.utils.Wrapper;

public class CommandBind extends Command {
    @Override
    public String getName() {
        return "bind";
    }

    @Override
    public String getSyntax() {
        return "bind";
    }

    @Override
    public String getDescription() {
        return "binds stuff to a key";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        if(args[1] == null){
            Wrapper.instance.log("Error");
        }else{
            if(args[2] == null){
                Wrapper.instance.log("Error");
            }else{
                int key = Keyboard.getKeyIndex(args[2].toUpperCase());
                String modName = args[1];
                if(Flauxy.INSTANCE.moduleManager.getModule(modName) != null){
                    Module mod = Flauxy.INSTANCE.moduleManager.getModule(modName);
                    mod.setKey(key);
                    Wrapper.instance.log("Bound " + modName + " to " + args[2]);
                }
            }
        }
    }
}

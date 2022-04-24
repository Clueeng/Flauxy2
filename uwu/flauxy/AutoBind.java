package uwu.flauxy;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.module.impl.exploit.Disabler;
import uwu.flauxy.module.impl.movement.Fly;
import uwu.flauxy.module.impl.movement.Longjump;
import uwu.flauxy.module.impl.movement.Speed;

public class AutoBind {

    public static void setKeyBinds(dev dev){
        if(dev == AutoBind.dev.t9a){
            Flauxy.INSTANCE.moduleManager.getModule(Speed.class).setKey(Keyboard.KEY_V);
            Flauxy.INSTANCE.moduleManager.getModule(Fly.class).setKey(Keyboard.KEY_F); // what did you do to the fly :allahcry:
            Flauxy.INSTANCE.moduleManager.getModule(Disabler.class).setKey(Keyboard.KEY_O);
            Flauxy.INSTANCE.moduleManager.getModule(Longjump.class).setKey(Keyboard.KEY_G);
        }
    }

    public static enum dev{
        t9a, Flaily;
    }

}
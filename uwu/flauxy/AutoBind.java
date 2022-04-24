package uwu.flauxy;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.module.impl.exploit.Disabler;
import uwu.flauxy.module.impl.movement.Fly;
import uwu.flauxy.module.impl.movement.Longjump;
import uwu.flauxy.module.impl.movement.Speed;
import uwu.flauxy.module.impl.player.Scaffold;

public class AutoBind {

    public static void setKeyBinds(dev dev){
        if(dev == AutoBind.dev.t9a){
            Flauxy.INSTANCE.moduleManager.getModule(Speed.class).setKey(Keyboard.KEY_V);
            Flauxy.INSTANCE.moduleManager.getModule(Fly.class).setKey(Keyboard.KEY_F); // what did you do to the fly :allahcry:
            Flauxy.INSTANCE.moduleManager.getModule(Disabler.class).setKey(Keyboard.KEY_O);
            Flauxy.INSTANCE.moduleManager.getModule(Longjump.class).setKey(Keyboard.KEY_G);
        }
        if(dev == AutoBind.dev.Flaily){
            Flauxy.INSTANCE.moduleManager.getModule(Speed.class).setKey(Keyboard.KEY_X);
            Flauxy.INSTANCE.moduleManager.getModule(Fly.class).setKey(Keyboard.KEY_G);
            Flauxy.INSTANCE.moduleManager.getModule(Longjump.class).setKey(Keyboard.KEY_V);
            Flauxy.INSTANCE.moduleManager.getModule(Scaffold.class).setKey(Keyboard.KEY_W);
        }
    }

    public static enum dev{
        t9a, Flaily;
    }

}
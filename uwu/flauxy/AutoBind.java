package uwu.flauxy;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.module.impl.exploit.Disabler;
import uwu.flauxy.module.impl.movement.Fly;
import uwu.flauxy.module.impl.movement.Longjump;
import uwu.flauxy.module.impl.movement.Phase;
import uwu.flauxy.module.impl.movement.Speed;
import uwu.flauxy.module.impl.player.InventoryManager;
import uwu.flauxy.module.impl.player.Nofall;
import uwu.flauxy.module.impl.player.Scaffold;
import uwu.flauxy.module.impl.player.Stealer;

public class AutoBind {

    public static void setKeyBinds(dev dev){
        if(dev == AutoBind.dev.t9a){
            Flauxy.INSTANCE.moduleManager.getModule(Speed.class).setKey(Keyboard.KEY_V);
            Flauxy.INSTANCE.moduleManager.getModule(Fly.class).setKey(Keyboard.KEY_F); // what did you do to the fly :allahcry:
            Flauxy.INSTANCE.moduleManager.getModule(Disabler.class).setKey(Keyboard.KEY_O);
            Flauxy.INSTANCE.moduleManager.getModule(Longjump.class).setKey(Keyboard.KEY_G);
            Flauxy.INSTANCE.moduleManager.getModule(Phase.class).setKey(Keyboard.KEY_P);
            Flauxy.INSTANCE.moduleManager.getModule(InventoryManager.class).setKey(Keyboard.KEY_J);
            Flauxy.INSTANCE.moduleManager.getModule(Scaffold.class).setKey(Keyboard.KEY_C);
            Flauxy.INSTANCE.moduleManager.getModule(Nofall.class).setKey(Keyboard.KEY_N);
            Flauxy.INSTANCE.moduleManager.getModule(Longjump.class).setKey(Keyboard.KEY_G);
            Flauxy.INSTANCE.moduleManager.getModule(Stealer.class).setKey(Keyboard.KEY_K);



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
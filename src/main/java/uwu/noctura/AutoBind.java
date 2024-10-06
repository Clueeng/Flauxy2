package uwu.noctura;

import org.lwjgl.input.Keyboard;
import uwu.noctura.module.impl.exploit.Disabler;
import uwu.noctura.module.impl.movement.Fly;
import uwu.noctura.module.impl.movement.Longjump;
import uwu.noctura.module.impl.movement.Phase;
import uwu.noctura.module.impl.movement.Speed;
import uwu.noctura.module.impl.player.InventoryManager;
import uwu.noctura.module.impl.player.Nofall;
import uwu.noctura.module.impl.player.Scaffold;
import uwu.noctura.module.impl.player.Stealer;

public class AutoBind {

    public static void setKeyBinds(dev dev){
        if(dev == AutoBind.dev.t9a){
            Noctura.INSTANCE.moduleManager.getModule(Speed.class).setKey(Keyboard.KEY_V);
            Noctura.INSTANCE.moduleManager.getModule(Fly.class).setKey(Keyboard.KEY_F); // what did you do to the fly :allahcry:
            Noctura.INSTANCE.moduleManager.getModule(Disabler.class).setKey(Keyboard.KEY_O);
            Noctura.INSTANCE.moduleManager.getModule(Longjump.class).setKey(Keyboard.KEY_G);
            Noctura.INSTANCE.moduleManager.getModule(Phase.class).setKey(Keyboard.KEY_P);
            Noctura.INSTANCE.moduleManager.getModule(InventoryManager.class).setKey(Keyboard.KEY_J);
            Noctura.INSTANCE.moduleManager.getModule(Scaffold.class).setKey(Keyboard.KEY_C);
            Noctura.INSTANCE.moduleManager.getModule(Nofall.class).setKey(Keyboard.KEY_N);
            Noctura.INSTANCE.moduleManager.getModule(Longjump.class).setKey(Keyboard.KEY_G);
            Noctura.INSTANCE.moduleManager.getModule(Stealer.class).setKey(Keyboard.KEY_K);



        }
        if(dev == AutoBind.dev.Flaily){
            Noctura.INSTANCE.moduleManager.getModule(Speed.class).setKey(Keyboard.KEY_X);
            Noctura.INSTANCE.moduleManager.getModule(Fly.class).setKey(Keyboard.KEY_G);
            Noctura.INSTANCE.moduleManager.getModule(Longjump.class).setKey(Keyboard.KEY_V);
            Noctura.INSTANCE.moduleManager.getModule(Scaffold.class).setKey(Keyboard.KEY_W);
        }

        if(dev == AutoBind.dev.teqhs){
            Noctura.INSTANCE.moduleManager.getModule(Speed.class).setKey(Keyboard.KEY_V);
            Noctura.INSTANCE.moduleManager.getModule(Fly.class).setKey(Keyboard.KEY_F);
            Noctura.INSTANCE.moduleManager.getModule(Disabler.class).setKey(Keyboard.KEY_NONE);
            Noctura.INSTANCE.moduleManager.getModule(Longjump.class).setKey(Keyboard.KEY_X);
            Noctura.INSTANCE.moduleManager.getModule(Phase.class).setKey(Keyboard.KEY_H);
            Noctura.INSTANCE.moduleManager.getModule(InventoryManager.class).setKey(Keyboard.KEY_O);
            Noctura.INSTANCE.moduleManager.getModule(Scaffold.class).setKey(Keyboard.KEY_G);
            Noctura.INSTANCE.moduleManager.getModule(Nofall.class).setKey(Keyboard.KEY_NONE);
            Noctura.INSTANCE.moduleManager.getModule(Longjump.class).setKey(Keyboard.KEY_X);
            Noctura.INSTANCE.moduleManager.getModule(Stealer.class).setKey(Keyboard.KEY_O);
        }

    }

    public static enum dev{
        t9a, Flaily, teqhs;
    }

}
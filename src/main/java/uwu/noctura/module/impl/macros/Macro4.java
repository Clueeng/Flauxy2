package uwu.noctura.module.impl.macros;

import uwu.noctura.commands.impl.CommandMacro;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.utils.Wrapper;

@ModuleInfo(name = "Macro4", displayName = "Macro 4", key = -1, cat = Category.Macro)
public class Macro4 extends Module {

    int macroId = 3;

    @Override
    public void onEnable() {
        if(CommandMacro.macros[macroId] != null){
            if(!CommandMacro.macros[macroId].isEmpty()){
                mc.thePlayer.sendChatMessage(CommandMacro.macros[macroId]);
            }else{
                Wrapper.instance.log("Macro empty");
            }
        }else{
            Wrapper.instance.log("Macro empty");
        }
        this.toggle();
    }
}
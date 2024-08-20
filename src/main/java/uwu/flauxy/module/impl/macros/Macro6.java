package uwu.flauxy.module.impl.macros;

import uwu.flauxy.commands.impl.CommandMacro;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Macro6", displayName = "Macro 6", key = -1, cat = Category.Macro)
public class Macro6 extends Module {
    int macroId = 5;

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

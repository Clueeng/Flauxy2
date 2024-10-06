package uwu.noctura.module.impl.macros;

import uwu.noctura.commands.impl.CommandMacro;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.utils.Wrapper;

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

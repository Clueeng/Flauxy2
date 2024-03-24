package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.flauxy.commands.Command;
import uwu.flauxy.module.impl.other.AutoRegister;
import uwu.flauxy.utils.Wrapper;

public class CommandPassword extends Command {
    @Override
    public String getName() {
        return "password";
    }

    @Override
    public String getSyntax() {
        return "password";
    }

    @Override
    public String getDescription() {
        return "Sets your autoregister password";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        if(args.length < 1){
            Wrapper.instance.log("Needs one argument");
            return;
        }
        String password = args[1];
        if(args.length > 2){
            Wrapper.instance.log("Too many arguments (Only one is required)");
            return;
        }
        boolean warning = !password.matches(".*\\d.*") || !password.matches("[A-Za-z0-9]*") || password.length() < 10;
        if(warning){
            Wrapper.instance.log("§4§bWarning! §rThe password you set is not secure, and does not contain either numbers or characters or is too small. Consider changing that to bypass some servers' restrictions");
        }
        Wrapper.instance.log("Changed your old password (" + AutoRegister.password + ") to " + password);
        AutoRegister.password = password;
    }
}

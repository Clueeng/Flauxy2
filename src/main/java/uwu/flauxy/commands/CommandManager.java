package uwu.flauxy.commands;

import java.util.ArrayList;
import java.util.Set;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import org.reflections.Reflections;
import uwu.flauxy.commands.impl.CommandBind;
import uwu.flauxy.commands.impl.CommandSay;
import uwu.flauxy.module.Module;
import uwu.flauxy.utils.Wrapper;

public class CommandManager {

    private static ArrayList<Command> commands = Lists.newArrayList();
    private static String commandPrefix = "."; // command prefix here make a getter and setter :p

    static  {
        final Reflections reflections = new Reflections("uwu.flauxy.commands.impl");
        final Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);
        for (Class<?> aClass : classes) {
            try {
                final Command mod = (Command) aClass.newInstance();
                commands.add(mod);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onMessageSent(String message) {
        if(message.startsWith(commandPrefix)) {
            message = message.substring(commandPrefix.length()); // removes the command prefix
        }
        String[] args = message.split(" ");

        for(Command c : commands) {
            if(c.getName().equalsIgnoreCase(args[0])) {
                try {
                    c.onCommandRun(Minecraft.getMinecraft(), args); // ih hi
                    return; 
                } catch(Exception e) {
                    e.printStackTrace();
                    Wrapper.instance.log("Usage Error: try " + c.getSyntax() + ".");
                    return;
                }
            }
        }
        Wrapper.instance.log("Command does not exist! Try " + commandPrefix + "help for a list of commands."); // <- better!!

    }

    public static String getCommandPrefix() {
        return commandPrefix;
    }
    public static void setCommandPrefix(String pref) {
    	commandPrefix = pref;
    }

    public static ArrayList<Command> getCommands() {
        return commands;
    }
    
    public static void setCommands(ArrayList<Command> commands) {
        CommandManager.commands = commands;
    }


    public <T extends Command> T getCommand(Class<T> tClass) {
        return (T) commands.stream().filter(command -> command.getClass().equals(tClass)).findFirst().orElse(null);
    }

}

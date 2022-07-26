package uwu.flauxy.commands;

import net.minecraft.client.Minecraft;

public abstract class Command {

    public abstract String getName();
    public abstract String getSyntax();
    public abstract String getDescription();

    public abstract void onCommandRun(Minecraft mc, String[] args) throws Exception;


}

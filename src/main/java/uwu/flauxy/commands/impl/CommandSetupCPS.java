package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import uwu.flauxy.commands.Command;
import uwu.flauxy.module.impl.combat.Killaura;
import uwu.flauxy.utils.Wrapper;

public class CommandSetupCPS extends Command {
    @Override
    public String getName() {
        return "cps";
    }

    @Override
    public String getSyntax() {
        return ".cps";
    }

    @Override
    public String getDescription() {
        return "ts should be removed wtf is it doing i dont even know help";
    }

    public static boolean runSetup;

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Start clicking"));
        runSetup = false;
        Killaura.dataClickOne.clear();
        Killaura.dataClickTwo.clear();
        Killaura.dataClickThree.clear();
        runSetup = true;
    }
}

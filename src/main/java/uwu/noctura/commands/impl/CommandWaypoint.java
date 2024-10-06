package uwu.noctura.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.noctura.Noctura;
import uwu.noctura.commands.Command;
import uwu.noctura.utils.Wrapper;

public class CommandWaypoint extends Command {

    @Override
    public String getName() {
        return "waypoint";
    }

    @Override
    public String getSyntax() {
        return ".waypoint <add/remove> <name>";
    }

    @Override
    public String getDescription() {
        return "Adds or removes a waypoint";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        if (args.length < 2) {
            Wrapper.instance.log("Incorrect syntax. Use: " + getSyntax());
            return;
        }

        String action = args[1].toLowerCase();
        String wayName = args[2];

        switch (action) {
            case "add":
                Noctura.INSTANCE.getWaypointManager().addWaypoint(mc, wayName);
                break;

            case "remove":
                Noctura.INSTANCE.getWaypointManager().removeWaypoint(wayName);
                break;

            default:
                Wrapper.instance.log("Unknown action. Use: " + getSyntax());
                break;
        }
    }
}
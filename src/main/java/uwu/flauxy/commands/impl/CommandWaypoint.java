package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.flauxy.Flauxy;
import uwu.flauxy.commands.Command;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.waypoint.Waypoint;

import java.util.Iterator;

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
                Flauxy.INSTANCE.getWaypointManager().addWaypoint(mc, wayName);
                break;

            case "remove":
                Flauxy.INSTANCE.getWaypointManager().removeWaypoint(wayName);
                break;

            default:
                Wrapper.instance.log("Unknown action. Use: " + getSyntax());
                break;
        }
    }
}
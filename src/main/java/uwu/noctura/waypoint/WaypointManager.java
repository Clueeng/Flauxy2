package uwu.noctura.waypoint;

import net.minecraft.client.Minecraft;
import uwu.noctura.Noctura;
import uwu.noctura.module.impl.other.util.Folder;
import uwu.noctura.utils.Wrapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class WaypointManager {

    public ArrayList<Waypoint> waypoints;

    private File dir;
    private File dataFile;

    public WaypointManager(){
        waypoints = new ArrayList<>();
        loadFromFile();
    }

    // Save waypoints to a file
    public void saveWaypoints() {
        String fileName = "waypoints";
        this.dir = new File(String.valueOf(Folder.dir));
        if (!this.dir.exists()) {
            this.dir.mkdir();
        }
        this.dataFile = new File(this.dir, fileName + ".txt");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.dataFile));
            for (Waypoint waypoint : waypoints) {
                String line = String.format("%s,%f,%f,%f,%s",
                        waypoint.name, waypoint.x, waypoint.y, waypoint.z, waypoint.identifier);
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load waypoints from a file
    public void loadFromFile() {
        String fileName = "waypoints";
        this.dir = new File(String.valueOf(Folder.dir));
        if (!this.dir.exists()) {
            this.dir.mkdir();
        }
        this.dataFile = new File(this.dir, fileName + ".txt");
        if(!this.dataFile.exists()){
            try{
                this.dataFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.dataFile));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String name = parts[0];
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    String identifier = parts[4];

                    Waypoint waypoint = new Waypoint(name, x, y, z, identifier);
                    waypoints.add(waypoint);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addWaypoint(Minecraft mc, String wayName) {
        String identifier;
        if(mc.isSingleplayer() && mc.getCurrentServerData() == null){
            identifier = getWorldName();
        }else{
            identifier = mc.getCurrentServerData().serverIP;
        }
        Noctura.INSTANCE.waypointManager.waypoints.add(new Waypoint(wayName, (int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ, identifier));
        Wrapper.instance.log("Added " + wayName + " at " + (int)mc.thePlayer.posX + " " + (int)mc.thePlayer.posY + " " + (int)mc.thePlayer.posZ);
    }

    public void removeWaypoint(String wayName) {
        Iterator<Waypoint> iterator = Noctura.INSTANCE.waypointManager.waypoints.iterator();
        boolean found = false;

        while (iterator.hasNext()) {
            Waypoint waypoint = iterator.next();
            if (waypoint.name.equalsIgnoreCase(wayName)) {
                iterator.remove();
                Wrapper.instance.log("Removed waypoint " + wayName);
                found = true;
                break;
            }
        }

        if (!found) {
            Wrapper.instance.log("No waypoint found with the name " + wayName);
        }
    }
    public String getWorldName() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.isSingleplayer()) {
            return "0";
        }
        return "Unknown World";
    }
}
package uwu.noctura.module.impl.other;

import net.minecraft.util.ResourceLocation;
import org.json.JSONObject;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.impl.other.util.Folder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ModuleInfo(name = "RestoreSkin", key = -1, cat = Category.Other, displayName = "Restore Skin")
public class RestoreSkin extends Module {

    private File dir;
    private File dataFile;

    public String oldUsername;

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            if(!oldUsername.equals(mc.session.getUsername()) || !oldUsername.equals(mc.thePlayer.getName())){

            }
        }
    }

    public long getUnix(){
        return (int)System.currentTimeMillis() / 1000;
    }

    public String nameToUUID(String username){
        String req = String.format("https://api.mojang.com/users/profiles/minecraft/%s?at=%d",username, getUnix());
        System.out.println(req);
        return (String) getRequest(req);
    }

    public JSONObject getProfile(String UUID){
        JSONObject json = new JSONObject(getRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + UUID));
        return json;
    }

    public void downloadSkin(String urlString){
        try{
            URL url = new URL(urlString);
            BufferedImage downloadedImg = ImageIO.read(url);
            File skinFolder = getFolder();
            File skin = new File(skinFolder, "temp.png");
            ImageIO.write(downloadedImg,"png",skin);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public File getFolder(){
        String fileName = "waypoints";
        this.dir = new File(String.valueOf(Folder.dir));
        if (!this.dir.exists()) {
            this.dir.mkdir();
        }
        return this.dir;
    }

    public String getSkinURL(JSONObject profile){
        JSONObject base64JSON = profile.getJSONObject("properties").getJSONObject("value");
        String base64String = base64JSON.toString();
        String decoded = new String(Base64.getDecoder().decode(base64String), StandardCharsets.UTF_8);
        // decoded = value = skin base 64
        JSONObject skinJson = new JSONObject(decoded); // since its encoded in base64 you need to decode it
        // now you need to get textures, and then SKIN within it AND THEN URL DUDE ITS SO LONG
        JSONObject textures = skinJson.getJSONObject("textures");
        JSONObject skin = textures.getJSONObject("SKIN");
        return (String) skin.get("url");
    }

    public Object getRequest(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } catch (IOException ignored){

        }
        return null;
    }
    // DefaultPlayerSkin.getDefaultSkin(this.getUniqueID())
    public ResourceLocation getSkin() {
        // just get it from the folder now
        // gotta use class loader or smth ask error later
        return null;
    }
}

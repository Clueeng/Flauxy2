package uwu.flauxy.module.impl.other;

import net.minecraft.world.WorldSettings;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;

@ModuleInfo(name = "GamemodeSpoofer", displayName = "Gamemode Spoofer", key = -1, cat = Category.Other)
public class GamemodeSpoofer extends Module {

    public ModeSetting gameMode = new ModeSetting("Mode","Creative","Creative", "Survival", "Spectator", "Adventure", "Not_Set");

    public GamemodeSpoofer(){
        addSettings(gameMode);
    }

    WorldSettings.GameType gameType = WorldSettings.GameType.SURVIVAL;

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            switch (gameMode.getMode()){
                case "Creative":{
                    gameType = WorldSettings.GameType.CREATIVE;
                    break;
                }
                case "Survival":{
                    gameType = WorldSettings.GameType.SURVIVAL;
                    break;
                }
                case "Adventure":{
                    gameType = WorldSettings.GameType.ADVENTURE;
                    break;
                }
                case "Spectator":{
                    gameType = WorldSettings.GameType.SPECTATOR;
                    break;
                }
                case "Not_Set":{
                    gameType = WorldSettings.GameType.NOT_SET;
                    break;
                }
            }
            mc.thePlayer.setGameType(gameType);
            mc.playerController.setGameType(gameType);
        }
    }
}

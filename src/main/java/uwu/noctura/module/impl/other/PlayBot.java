package uwu.noctura.module.impl.other;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.AxisAlignedBB;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.event.impl.EventRender3D;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.impl.display.ArrayList;
import uwu.noctura.module.impl.other.util.PlayBotWrapper;
import uwu.noctura.module.impl.player.AutoTool;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;

@ModuleInfo(name = "PlayBot", cat = Category.Other, key = -1, displayName = "Play Bot")
public class PlayBot extends Module {

    PlayBotWrapper playBotWrapper = new PlayBotWrapper(mc.thePlayer);

    @Override
    public void onEnable() {
        playBotWrapper = new PlayBotWrapper(mc.thePlayer);
        playBotWrapper.sinceLastChange = System.currentTimeMillis();
        playBotWrapper.yawGoal = mc.thePlayer.rotationYaw;

        AutoTool autoTool = Noctura.INSTANCE.moduleManager.getModule(AutoTool.class);
        autoTool.setToggled(true);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        playBotWrapper.stop();
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRender2D){
            java.util.ArrayList<String> debugInfos = new java.util.ArrayList<>();
            debugInfos.add("Goal Yaw: " + playBotWrapper.yawGoal);
            debugInfos.add("Current Yaw: " + mc.thePlayer.rotationYaw);
            debugInfos.add("Wrapped Yaw: " + playBotWrapper.getWrappedYaw());
            debugInfos.add("Current Desire: " + playBotWrapper.currentDesire);
            debugInfos.add("Goal Pitch: " + playBotWrapper.pitchGoal);
            debugInfos.add("Current Pitch: " + mc.thePlayer.rotationPitch);
            debugInfos.add("Wrapped Pitch: " + playBotWrapper.getPitch());

            int y = 0;
            for(String s : debugInfos){
                mc.fontRendererObj.drawStringWithShadow(s, 4, 4 + y, -1);
                y += 12;
            }

            playBotWrapper.executePendingYaw();
            playBotWrapper.executePendingPitch();
        }
        if(e instanceof EventRender3D){

            if(true){
                PacketUtil.sendSilentPacket(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
            }
            if(mc.thePlayer.ticksExisted > 10 && playBotWrapper.blockToBreak != null){
                RenderUtil.renderBox(playBotWrapper.blockToBreak.getX() - RenderManager.renderPosX + 0.5,
                        playBotWrapper.blockToBreak.getY() - RenderManager.renderPosY,
                        playBotWrapper.blockToBreak.getZ() - RenderManager.renderPosZ + 0.5,
                        0.5, 1, new Color(255, 0, 0, 90), false);
            }
        }

        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate)e;
            if(!ev.isPre())return;
            // update desire
            playBotWrapper.updateDesire();

            mc.thePlayer.inventory.currentItem = playBotWrapper.getSlotFromDesire();

            // act in function of desire

            // TODO: fix java being stupid (this doesnt work in a fucking swtich statement am i retarded or what)
            if (playBotWrapper.currentDesire.equals(PlayBotWrapper.Desire.EAT)) {
                playBotWrapper.eatFood();
            }
            switch (playBotWrapper.currentDesire){
                case WALK:{
                    playBotWrapper.move();
                    playBotWrapper.changeYawRandomly();
                    playBotWrapper.updateRotations();
                    break;
                }
                case ATTACK:{
                    playBotWrapper.attack();
                    playBotWrapper.updateRotations();
                    break;
                }
                case COLLECT_WOOD:{
                    playBotWrapper.mine();
                    playBotWrapper.updateRotations();
                    break;
                }
                case FLEE:{
                    Wrapper.instance.log("Fleeing");
                    playBotWrapper.move();
                    break;
                }
            }

            // update data set
            playBotWrapper.entityAround = playBotWrapper.getEntitiesAround(12f);
            if(!playBotWrapper.entityAround.isEmpty()){
                playBotWrapper.closestEntity = playBotWrapper.entityAround.get(0);
            }

            if(!mc.thePlayer.isCollidedHorizontally){
                playBotWrapper.ticksWallColliding = 0;
                playBotWrapper.tickSinceWallCollision++;
            }else{
                playBotWrapper.ticksWallColliding ++;
                playBotWrapper.tickSinceWallCollision = 0;
            }
            playBotWrapper.getGoalPos();

            playBotWrapper.hunger = mc.thePlayer.getFoodStats().getFoodLevel();
            playBotWrapper.health = mc.thePlayer.getHealth();
        }
    }


}

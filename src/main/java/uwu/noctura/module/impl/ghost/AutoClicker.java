package uwu.noctura.module.impl.ghost;

import lombok.SneakyThrows;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventFrame;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.timer.Timer;

import java.util.Objects;
import java.util.Random;

@ModuleInfo(name = "AutoClicker", displayName = "AutoClicker", key = -1, cat = Category.Ghost)
public class AutoClicker extends Module {

    public NumberSetting cps = new NumberSetting("CPS", 12.0, 1.0, 20.0, 0.1);
    public BooleanSetting leftClicking = new BooleanSetting("Left Click", true);
    public BooleanSetting rightClicking = new BooleanSetting("Right Click", true);
    public ModeSetting clickingMode = new ModeSetting("Pattern", "Default", "Default", "Butterfly", "Jitter", "Dragclick");
    public BooleanSetting addRandom = new BooleanSetting("Randomness", true);
    public ModeSetting randomMode = new ModeSetting("Random Mode", "Default", "Default").setCanShow(r -> addRandom.getValue());
    public BooleanSetting jitterAim = new BooleanSetting("Jitter Aim", true).setCanShow(c -> clickingMode.is("Jitter"));
    public NumberSetting jitterAimFactor = new NumberSetting("Jitter Aim Factor", 1, 0.1, 2, 0.1).setCanShow(c -> clickingMode.is("Jitter"));
    private Timer cpsTimer = new Timer();

    public boolean isClickingLeft = false, isClickingRight = false;

    public AutoClicker(){
        addSettings(cps, jitterAim, leftClicking, rightClicking, clickingMode, jitterAimFactor, addRandom, randomMode);
    }

    int amountOfClicks = 0;
    int amountOfClicks2 = 0;

    @Override
    public void onEnable() {
        amountOfClicks = 0;
        amountOfClicks2 = 0;
        toggledModule = 0;
    }
    float randomYaw = 0;
    float newSetYaw = 0;
    float newSetPitch = 0;
    float randomPitch = 0;
    float toggledPitch = 0;
    float toggledYaw = 0;
    public int toggledModule = 0;
    @SneakyThrows
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventFrame){
            boolean holdingLeft = Mouse.isButtonDown(0);
            boolean holdingRight = Mouse.isButtonDown(1);
            switch (clickingMode.getMode()){
                case "Jitter":{
                    if(jitterAim.getValue() && (holdingLeft && shouldUseLeftClick() && leftClicking.getValue()
                            || holdingRight && shouldUseRightClick() && rightClicking.getValue())){

                        Random r = new Random();
                        jitterAiming(r);
                    }
                    break;
                }
            }
        }

        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate)e;
            if(!ev.isPre())return;
            toggledModule++;
            boolean holdingLeft = Mouse.isButtonDown(0);
            boolean holdingRight = Mouse.isButtonDown(1);
            Random r = new Random();
            double firstRandom = r.nextDouble();
            double secondRandom = r.nextDouble();
            double randomness = 0;
            if(addRandom.getValue()){
                switch(randomMode.getMode()){
                    case "Default":{
                        randomness = Math.max(-3, Math.min(((firstRandom - (firstRandom/2)) * cps.getValue()) / (cps.getValue() * (secondRandom - (secondRandom/2))), 3));
                        break;
                    }
                }
            }
            if(clickingMode.getMode().equals("Butterfly")){
                randomness = Math.max(-3, Math.min(((firstRandom - (firstRandom/2)) * cps.getValue()) / (cps.getValue() * (secondRandom - (secondRandom/2))), 3));
            }
            float msTime = (float) (1000f / (cps.getValue() + randomness));

            switch (clickingMode.getMode()){
                case "Default":{
                    if(cpsTimer.hasTimeElapsed(msTime, true)){
                        if(holdingLeft && shouldUseLeftClick() && leftClicking.getValue()){
                            KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
                            //isClickingLeft = true;
                            //legitAttack(ev);
                        }
                        if(holdingRight && shouldUseRightClick() && rightClicking.getValue()){
                            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                            //legitRightClick();
                            //isClickingRight = true;
                        }
                    }else{
                        isClickingLeft = false;
                        isClickingRight = false;
                    }
                    break;
                }
                case "Jitter":{
                    if(toggledModule < 3){
                        toggledPitch = mc.thePlayer.rotationPitch;
                        toggledYaw = mc.thePlayer.rotationYaw;
                    }
                    boolean anyClick = holdingLeft && shouldUseLeftClick() && leftClicking.getValue() || holdingRight && shouldUseRightClick() && rightClicking.getValue();
                    double failPercent = r.nextDouble() * 100;
                    double percentToFail = 18;
                    if(anyClick){
                        if(failPercent < percentToFail){
                            // Wrapper.instance.log("Failed click");
                        }
                        if(cpsTimer.hasTimeElapsed(msTime, true) && failPercent > percentToFail){
                            if(holdingLeft && shouldUseLeftClick() && leftClicking.getValue()){
                                legitAttack(ev);
                            }
                            if(holdingRight && shouldUseRightClick() && rightClicking.getValue()){
                                legitRightClick();
                            }
                        }
                    }else{
                        toggledModule = 0;
                    }
                    break;
                }
                case "Butterfly":{
                    boolean anyClick = holdingLeft && shouldUseLeftClick() && leftClicking.getValue() || holdingRight && shouldUseRightClick() && rightClicking.getValue();
                    if(!anyClick){
                        amountOfClicks2 = 0;
                    }
                    double time = amountOfClicks > 2 ? msTime - (msTime / 2.5f - (r.nextDouble() * 4.5f)) : msTime * 1.1f;
                    if(amountOfClicks2 % 10 == 0){
                        time += r.nextDouble() * 350;
                    }
                    if(cpsTimer.hasTimeElapsed(time, true)){
                        //Wrapper.instance.log("Test (" + (amountOfClicks > 2) + ") " + time);
                        if(holdingLeft && shouldUseLeftClick() && leftClicking.getValue()){
                            legitAttack(ev);
                        }
                        if(holdingRight && shouldUseRightClick() && rightClicking.getValue()){
                            legitRightClick();
                        }
                        amountOfClicks++;
                        amountOfClicks2++;
                    }
                    if(amountOfClicks > 3){
                        amountOfClicks = 0;
                    }
                    break;
                }
            }

        }
    }

    public void jitterAiming(Random r) {
        randomYaw = ((float) r.nextDouble() * 4) - 0.5f;
        randomPitch = ((float) r.nextDouble() * 4) - 0.5f;
        if (toggledModule < 2) {
            newSetPitch = mc.thePlayer.rotationPitch;
            newSetYaw = mc.thePlayer.rotationYaw;
        }

        // Add jittery movement within a range
        float factor = (float) jitterAimFactor.getValue();
        float jitterPitch = (float) ((r.nextFloat() * 2 - 1) / (jitterAimFactor.getMaximum() - (factor + 0.1f)));
        float jitterYaw = (float) ((r.nextFloat() * 2 - 1) / (jitterAimFactor.getMaximum() - (factor + 0.1f)));

        // Apply the jitter within the range
        mc.thePlayer.rotationPitch += jitterPitch;
        mc.thePlayer.rotationYaw += jitterYaw;

        // Apply constraints to keep the jitter within the specified range
        mc.thePlayer.rotationPitch = Math.min(Math.max(mc.thePlayer.rotationPitch, mc.thePlayer.rotationPitch - 5), mc.thePlayer.rotationPitch + 5);
        mc.thePlayer.rotationYaw = Math.min(Math.max(mc.thePlayer.rotationYaw, mc.thePlayer.rotationYaw - 3), mc.thePlayer.rotationYaw + 3);
    }

    public void legitRightClick() {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if(heldItem == null
                || ( !(heldItem.getItem() instanceof ItemEgg
                || heldItem.getItem() instanceof ItemPotion
                || heldItem.getItem() instanceof ItemBlock
        ))){
            return;
        }
        if(heldItem.getItem() instanceof ItemBlock){
            if (mc.objectMouseOver != null) {
                if (Objects.requireNonNull(mc.objectMouseOver.typeOfHit) == MovingObjectPosition.MovingObjectType.BLOCK) {
                    BlockPos blockPos = mc.objectMouseOver.getBlockPos();
                    if (mc.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
                        int i = heldItem != null ? heldItem.stackSize : 0;
                        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, heldItem, blockPos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec)) {
                            mc.thePlayer.swingItem();
                            mc.entityRenderer.itemRenderer.resetEquippedProgress();
                        }
                        if (heldItem == null)
                        {
                            return;
                        }

                        if (heldItem.stackSize == 0)
                        {
                            mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem] = null;
                        }
                        else if (heldItem.stackSize != i || mc.playerController.isInCreativeMode())
                        {
                            mc.entityRenderer.itemRenderer.resetEquippedProgress();
                        }
                    }
                }
            }
        }else{
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
        }
    }

    public void legitAttack(EventUpdate e) {
        if (mc.objectMouseOver == null || mc.thePlayer.ridingEntity instanceof EntityBoat) {
            return;
        }
        if(e.isPre()){
            mc.thePlayer.swingItem();
        }

        switch (mc.objectMouseOver.typeOfHit) {
            case ENTITY:
                if (mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
                    EntityLivingBase targetEntity = (EntityLivingBase) mc.objectMouseOver.entityHit;
                    if (mc.thePlayer.canEntityBeSeen(targetEntity)) {
                        mc.playerController.attackEntity(mc.thePlayer, targetEntity);
                    }
                }
                break;

            case BLOCK:
                BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                if (!mc.theWorld.isAirBlock(blockpos)) {
                    mc.playerController.clickBlock(blockpos, mc.objectMouseOver.sideHit);
                    break;
                }
        }

    }

    public boolean shouldUseLeftClick(){
        boolean notInAnyGui = mc.currentScreen == null;
        return notInAnyGui;
    }
    public boolean shouldUseRightClick(){
        boolean isEating = mc.thePlayer.isEating();
        boolean notInAnyGui = mc.currentScreen == null;
        return !isEating && notInAnyGui;
    }

    @Override
    public void onDisable() {
    }
}
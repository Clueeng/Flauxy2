package uwu.noctura.module.impl.other.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.WorldUtil;
import uwu.noctura.utils.Wrapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayBotWrapper {

    EntityPlayerSP player;
    Minecraft mc;

    public PlayBotWrapper(EntityPlayerSP player){
        this.player = player;
        mc = Minecraft.getMinecraft();
    }



    public float hunger;
    public float health;
    public boolean isDead;

    public List<BlockPos> blocksAround = new ArrayList<>();
    public List<EntityLivingBase> entityAround = new ArrayList<>();
    public Entity closestEntity = null;
    public BlockPos goal;
    public boolean reachedGoal;
    public Desire currentDesire;
    public BlockPos blockToBreak;

    public int tickSinceWallCollision = 0;
    public int ticksWallColliding = 0;

    public int wallCollisions;
    public long checkCollisionTime;

    public long sinceLastChange = 0L;
    public float yawGoal = 0f;
    public float pitchGoal = 0f;

    public boolean startTowering;
    public boolean placeDown;
    public boolean startEating;

    public void changeYawRandomly() {
        if (Math.abs(System.currentTimeMillis() - sinceLastChange) > 50000) {
            double playerX = mc.thePlayer.posX;
            double playerZ = mc.thePlayer.posZ;
            double randomX = playerX + (Math.random() * 80 - 40);
            double randomZ = playerZ + (Math.random() * 80 - 40);

            double deltaX = randomX - playerX;
            double deltaZ = randomZ - playerZ;
            float targetYaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90;
            targetYaw = MathHelper.wrapAngleTo180_float(targetYaw);
            yawGoal = targetYaw;
            pitchGoal = 0f;
            sinceLastChange = System.currentTimeMillis();
            Wrapper.instance.log("Changed destination");
        }
        requestRotateYaw(yawGoal, 0.5f);
        requestRotatePitch(pitchGoal, 0.5f);
    }

    public void updateRotations(){
        requestRotateYaw(yawGoal, 0.5f);
        requestRotatePitch(pitchGoal, 0.5f);
    }

    public void move(){
        setAction(mc.gameSettings.keyBindScreenshot, false);
        setAction(mc.gameSettings.keyBindJump, false);
        setAction(mc.gameSettings.keyBindForward, false);
        setAction(mc.gameSettings.keyBindBack, false);
        setAction(mc.gameSettings.keyBindLeft, false);
        setAction(mc.gameSettings.keyBindRight, false);


        /// just for funsies ill take some screenshots every 5m
        if(System.currentTimeMillis() % (3600/12) * 1000 == 0){
            mc.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(mc.mcDataDir, mc.displayWidth, mc.displayHeight, mc.framebufferMc));
        }


        if(mc.thePlayer.isInWater()){
            boolean canJump = hasSpaceAbove(inFront(1));
            if(canJump){
                walk(Direction.FORWARDS);
                jump();
            }else{
                tickSinceWallCollision = 0;
                ticksWallColliding++;
                if (ticksWallColliding == 3) {
                    //rotate180();
                    rotate90(Direction.LEFT);
                }
            }
        }else{
            boolean canJump = hasSpaceAbove(inFront(1));

            if(mc.thePlayer.isCollidedHorizontally){
                tickSinceWallCollision = 0;
                ticksWallColliding++;
                if(ticksWallColliding == 1){
                    wallCollisions++;
                }

                //startTowering = wallCollisions > 6 && currentDesire.equals(Desire.WALK);

                //if(Math.abs(System.currentTimeMillis() - checkCollisionTime) < 5000 && !startTowering){
                //    wallCollisions-=1;
                //    checkCollisionTime = System.currentTimeMillis();
                //}else{
                    //if(!startTowering){
                setUseItem(false);
                if(canJump){
                    walk(Direction.FORWARDS);
                    jump();
                }else{
                    if(ticksWallColliding == 1){
                        rotate90(Direction.LEFT);
                    }
                    if(ticksWallColliding > 20){
                        walk(Direction.BACKWARDS);
                    }
                }
                    //}
                //}
                /*
                if(startTowering){
                    Wrapper.instance.log("Can Jump: " + hasSpaceAboveHead(mc.thePlayer.getPosition()));
                    placeDown = hasSpaceAboveHead(mc.thePlayer.getPosition().add(-0.5, 0, -0.5));
                    if(placeDown){
                        Wrapper.instance.log("Place");
                        setUseItem(true);
                        setLeftClick(false);
                        jump();
                        pitchGoal = 90f;
                        mc.thePlayer.inventory.currentItem = getSlotFromDesire();
                    }else{
                        Wrapper.instance.log("Dig");
                        pitchGoal = -90f;
                        setLeftClick(true);
                        setUseItem(false);
                    }
                }
                 */
                return;
            }
            // no blocks preventing movement

            walk(Direction.FORWARDS);
            startTowering = false;
            if(!isSafeAhead(4)){
                stop();
                rotate180();
                Wrapper.instance.log("Danger");
            }
        }
    }

    Entity currentAimedEntity;

    public void attack(){
        if(currentAimedEntity == null) return;

        if(mc.thePlayer.getDistanceToEntity(currentAimedEntity) > 1){
            move();
        }
        mc.thePlayer.inventory.currentItem = getSlotFromDesire();
        requestRotateYaw(yawGoal, 4.5f);
        requestRotatePitch(pitchGoal, 4.5f);
        attackIfAvailable();
    }


    public void mine() {
        if(blocksAround.isEmpty()){
            setLeftClick(false);
            currentDesire = Desire.WALK;
            return;
        }
        if(blockToBreak == null){
            setLeftClick(false);
            return;
        }
        double distXZ = Math.abs(
                Math.sqrt(
                        Math.pow(mc.thePlayer.posX - blockToBreak.getX(), 2)
                                + Math.pow(mc.thePlayer.posZ - blockToBreak.getZ(), 2)
                )
        );
        if(distXZ > 5){
            setLeftClick(false);
        }

        if(distXZ > 3 && !mc.thePlayer.isInWater()){
            move();
        }else{
            stop();
        }

        MovingObjectPosition.MovingObjectType type = mc.objectMouseOver.typeOfHit;
        if(type.equals(MovingObjectPosition.MovingObjectType.BLOCK)){
            setLeftClick(true);
        }else{
            move();
        }

        requestRotateYaw(yawGoal, 4.5f);
        requestRotateYaw(pitchGoal, 4.5f);
    }

    public int getItemCount(Item targetItem) {
        int count = 0;

        for (int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack != null && itemStack.getItem().getUnlocalizedName().equals(targetItem.getUnlocalizedName())) {
                count += itemStack.stackSize;
            }
        }

        return count;
    }

    public float getWrappedYaw(){
        return MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
    }
    public float getPitch(){
        return mc.thePlayer.rotationPitch;
    }

    private boolean hasSpaceAbove(BlockPos pos) {
        BlockPos above1 = pos.up(1);
        BlockPos above2 = pos.up(2);
        return mc.theWorld.getBlockState(above2).getBlock().isPassable(mc.theWorld, above2)
                && mc.theWorld.getBlockState(above1).getBlock().isPassable(mc.theWorld, above1);
    }
    private boolean hasSpaceAboveHead(BlockPos pos) {
        BlockPos above2 = pos.up(1);
        return mc.theWorld.getBlockState(above2).getBlock().isPassable(mc.theWorld, above2);
    }
    private boolean isSafeAhead(int holeDepth) {
        BlockPos posAhead = inFront(1);
        for (int i = 0; i < holeDepth; i++) {
            BlockPos below = posAhead.down(i);
            if (!mc.theWorld.getBlockState(below).getBlock().isPassable(mc.theWorld, below) || !(mc.theWorld.getBlockState(below).getBlock().getMaterial().equals(Material.lava))) {
                return true;
            }
        }
        return false;
    }


    public void getGoalPos(){
        switch (currentDesire){
            case WALK:{
                goal = inFront(1);
                break;
            }
        }
    }

    public BlockPos inFront(int blocks){
        BlockPos playerPos = mc.thePlayer.getPosition();
        EnumFacing facing = mc.thePlayer.getHorizontalFacing();
        return playerPos.offset(facing, blocks);
    }

    public int getSlotFromDesire() {
        int bestSlot = 0;

        switch (currentDesire) {
            case ATTACK:
                bestSlot = getBestWeaponSlot();
                break;
            case EAT:
                bestSlot = getBestFoodSlot();
                break;
            case HEAL:
                bestSlot = getBestHealingItemSlot();
                break;
            case WALK:{
                if(placeDown && startTowering){
                    bestSlot = getFirstBlockSlot();
                }
                break;
            }
            default:
                break;
        }
        return bestSlot;
    }

    private int getFirstBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
            if (item != null && item.getItem() instanceof ItemBlock) {
                return i;
            }
        }
        return 0;
    }

    private int getBestWeaponSlot() {
        int bestSlot = 0;
        float highestDamage = 0;

        for (int i = 0; i < 9; i++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);

            if (item != null && (item.getItem() instanceof ItemSword || item.getItem() instanceof ItemAxe || item.getItem() instanceof ItemPickaxe)) {
                float damage = getWeaponDamage(item);
                if (damage > highestDamage) {
                    highestDamage = damage;
                    bestSlot = i;
                }
            }
        }
        return bestSlot;
    }

    private int getBestFoodSlot() {
        int bestSlot = 0;
        int highestHealing = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);

            if (item != null && item.getItem() instanceof ItemFood) {
                int healing = getFoodHealing(item);
                if (healing > highestHealing) {
                    highestHealing = healing;
                    bestSlot = i;
                }
            }
        }
        return bestSlot;
    }

    private int getBestHealingItemSlot() {
        int bestSlot = 0;
        int highestHealing = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
            if (item != null) {
                if (item.getItem() instanceof ItemPotion) {
                    int healing = getPotionHealing(item);

                    if (healing > highestHealing) {
                        highestHealing = healing;
                        bestSlot = i;
                    }
                } else if (item.getItem() instanceof ItemAppleGold) {
                    int healing = 4;
                    if (healing > highestHealing) {
                        highestHealing = healing;
                        bestSlot = i;
                    }
                }
            }
        }
        return bestSlot;
    }

    private float getWeaponDamage(ItemStack item) {
        if (item.getItem() instanceof ItemSword) {
            ItemSword sword = (ItemSword) item.getItem();
            return sword.getDamageVsEntity();
        }
        if (item.getItem() instanceof ItemAxe) {
            ItemAxe sword = (ItemAxe) item.getItem();
            return sword.damageVsEntity;
        }
        if (item.getItem() instanceof ItemPickaxe) {
            ItemTool sword = (ItemTool) item.getItem();
            return sword.damageVsEntity;
        }
        return 0;
    }

    private int getFoodHealing(ItemStack item) {
        if (item.getItem() instanceof ItemFood) {
            ItemFood food = (ItemFood) item.getItem();
            return food.getHealAmount(item);
        }
        return 0;
    }

    private int getPotionHealing(ItemStack item) {
        if (item.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) item.getItem();
            if(WorldUtil.hasEffects(item, Potion.regeneration, Potion.heal)){
                return 5;
            }
        }
        return 0;
    }

    public void updateDesire() {
        // pretty much where the magic happens :scream:


        if(hunger <= 7){
            startEating = true;
            currentDesire = Desire.EAT;
            return;
        }
        if(health <= 10){
            currentDesire = Desire.HEAL;
            return;
        }
        if(entityAround != null){
            if(currentDesire == Desire.HEAL && !foesAround(16f).isEmpty()){ // enemy around
                currentDesire = Desire.FLEE;
                return;
            }
            if(!foesAround(12f).isEmpty() || !animalsAround(12f).isEmpty()){
                boolean skip = false;
                if(!foesAround(12f).isEmpty()){
                    EntityMob prioritized = foesAround(12f).get(0);
                    yawGoal = yawToEntity(prioritized);
                    pitchGoal = pitchToEntity(prioritized);
                    currentAimedEntity = prioritized;
                    skip = true;
                    currentDesire = Desire.ATTACK;
                }
                if(!skip && !animalsAround(12f).isEmpty()){
                    EntityAnimal prioritized = animalsAround(12f).get(0);
                    yawGoal = yawToEntity(prioritized);
                    pitchGoal = pitchToEntity(prioritized);
                    currentAimedEntity = prioritized;
                    currentDesire = Desire.ATTACK;
                }
                return;
            }
        }
        blocksAround = getAllBlocksAroundPlayer(12, 3, 1);
        if(!blocksAround.isEmpty()){
            for(BlockPos b : blocksAround){
                Material m = mc.theWorld.getBlockState(b).getBlock().getMaterial();
                if(m.equals(Material.wood) && (getItemCount(Item.getItemFromBlock(Blocks.log)) < 6)){
                    currentDesire = Desire.COLLECT_WOOD;
                    blockToBreak = b;
                    yawGoal = yawToBlockPos(blockToBreak);
                    pitchGoal = pitchToBlockPos(blockToBreak);
                    break;
                }
            }
        }
        // else adventure
        if (currentDesire != Desire.COLLECT_WOOD) {
            currentDesire = Desire.WALK;
        }
    }

    public void attackIfAvailable(){
        MovingObjectPosition.MovingObjectType type = mc.objectMouseOver.typeOfHit;
        if(type.equals(MovingObjectPosition.MovingObjectType.BLOCK)){
            BlockPos hitVec = mc.objectMouseOver.getBlockPos();
            if(mc.theWorld.getBlockState(hitVec).getBlock() instanceof BlockTallGrass){
                sendLeftClick();
            }
        }

        if(mc.objectMouseOver.entityHit == null) return;

        Entity victim = mc.objectMouseOver.entityHit;
        if(!(victim instanceof EntityMob || victim instanceof EntityAnimal || (victim instanceof EntityPlayer && victim.getEntityId() != mc.thePlayer.getEntityId()))){
            return;
        }
        if(mc.thePlayer.ticksExisted % 4 == 0){
            sendLeftClick();
        }
    }

    public float yawToBlockPos(BlockPos targetPos) {
        if (targetPos == null) {
            return mc.thePlayer.rotationYaw;
        }

        double deltaX = (targetPos.getX() + 0.5) - mc.thePlayer.posX;
        double deltaZ = (targetPos.getZ() + 0.5) - mc.thePlayer.posZ;

        float yaw = (float) (Math.atan2(deltaZ, deltaX) * (180.0 / Math.PI)) - 90.0F;
        return MathHelper.wrapAngleTo180_float(yaw);
    }

    public float pitchToBlockPos(BlockPos targetPos) {
        if (targetPos == null) {
            return mc.thePlayer.rotationPitch;
        }

        double deltaX = targetPos.getX() - mc.thePlayer.posX;
        double deltaY = (targetPos.getY() + 0.5) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double deltaZ = targetPos.getZ() - mc.thePlayer.posZ;

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, distanceXZ));
        return MathHelper.clamp_float(pitch, -90.0F, 90.0F);
    }

    public float pitchToEntity(Entity targetEntity) {
        if (targetEntity == null) {
            return mc.thePlayer.rotationPitch;
        }

        double deltaX = targetEntity.posX - mc.thePlayer.posX;
        double deltaY = (targetEntity.posY + targetEntity.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double deltaZ = targetEntity.posZ - mc.thePlayer.posZ;

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, distanceXZ));
        return MathHelper.clamp_float(pitch, -60.0F, 50.0F);
    }

    public float yawToEntity(Entity targetEntity) {
        if (targetEntity == null) {
            return mc.thePlayer.rotationYaw;
        }
        double deltaX = targetEntity.posX - mc.thePlayer.posX;
        double deltaZ = targetEntity.posZ - mc.thePlayer.posZ;
        float yaw = (float) (Math.atan2(deltaZ, deltaX) * (180.0 / Math.PI)) - 90.0F;
        return MathHelper.wrapAngleTo180_float(yaw);
    }

    public void rotateYawTo(float wrappedGoal, float speed) {
        float threshold = 8f;

        float currentYaw = getWrappedYaw();
        float deltaYaw = (wrappedGoal - currentYaw + 360) % 360;
        if (deltaYaw > 180) {
            deltaYaw -= 360;
        }
        if (Math.abs(deltaYaw) < threshold) {
            return;
        }
        mc.thePlayer.rotationYaw += Math.signum(deltaYaw) * Math.min(Math.abs(deltaYaw), speed);
    }

    public void rotate180(){
        mc.thePlayer.rotationYaw += 180;
        yawGoal += 180;
        yawGoal = (yawGoal % 360) - 180;
    }
    public void rotate90(Direction direction){
        switch (direction){
            case LEFT:{
                mc.thePlayer.rotationYaw -= 90;
                yawGoal -= 90;
                break;
            }
            case RIGHT:{
                mc.thePlayer.rotationYaw += 90;
                yawGoal += 90;
                break;
            }
        }
        yawGoal = (yawGoal % 360) - 180;
    }

    public void rotatePitchTo(float goal, float speed) {
        float threshold = 1f;
        float currentPitch = getPitch();
        float deltaPitch = goal - currentPitch;
        if (Math.abs(deltaPitch) < threshold) {
            return;
        }
        float newPitch = currentPitch + Math.signum(deltaPitch) * Math.min(Math.abs(deltaPitch), speed);

        newPitch = MathHelper.clamp_float(newPitch, -90f, 90f);

        mc.thePlayer.rotationPitch = newPitch;
    }

    public List<BlockPos> getAllBlocksAroundPlayer(int radius, int radiusY, int radiusYStart) {
        List<BlockPos> blocks = new ArrayList<>();
        BlockPos playerPos = mc.thePlayer.getPosition();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radiusYStart; y <= radiusY; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos blockPos = playerPos.add(x, y, z);
                    blocks.add(blockPos);
                }
            }
        }
        blocks.sort(Comparator.comparingDouble(block -> block.distanceSq(playerPos.getX(), playerPos.getY(), playerPos.getZ())));
        return blocks;
    }

    public ArrayList<EntityMob> foesAround(float radius){
        List<EntityLivingBase> entities = entityAround;
        if(entities == null){
            return null;
        }

        return entities.stream()
                .filter(e -> e instanceof EntityMob).filter(e -> mc.thePlayer.getDistanceToEntity(e) < radius).filter(e -> !e.isDead)
                .map(e -> (EntityMob) e)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<EntityAnimal> animalsAround(float radius){
        List<EntityLivingBase> entities = entityAround;
        if(entities == null){
            return null;
        }
        return entities.stream()
                .filter(e -> e instanceof EntityAnimal).filter(e -> mc.thePlayer.getDistanceToEntity(e) < radius)
                .map(e -> (EntityAnimal) e)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void sprint(){
        setAction(mc.gameSettings.keyBindSprint, true);
    }

    public void jump(){
        setAction(mc.gameSettings.keyBindJump, true);
    }

    public void walk(Direction direction){
        switch (direction){
            case LEFT:
                setAction(mc.gameSettings.keyBindLeft, true);
                break;
            case RIGHT:
                setAction(mc.gameSettings.keyBindRight, true);
                break;
            case FORWARDS:
                setAction(mc.gameSettings.keyBindForward, true);
                break;
            case BACKWARDS:
                setAction(mc.gameSettings.keyBindBack, true);
                break;
        }
    }

    public void stop(){
        setAction(mc.gameSettings.keyBindJump, false);
        setAction(mc.gameSettings.keyBindForward, false);
        setAction(mc.gameSettings.keyBindBack, false);
        setAction(mc.gameSettings.keyBindLeft, false);
        setAction(mc.gameSettings.keyBindRight, false);
        setAction(mc.gameSettings.keyBindUseItem, false);
        setAction(mc.gameSettings.keyBindAttack, false);
    }

    public void setLeftClick(boolean click){
        setAction(mc.gameSettings.keyBindAttack, click);
    }
    public void setUseItem(boolean click){
        setAction(mc.gameSettings.keyBindUseItem, click);
    }

    public void eatFood(){
        if(mc.thePlayer.inventory.getCurrentItem() == null) {
            currentDesire = Desire.WALK;
            return;
        }
        if(!(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFood)) {
            currentDesire = Desire.WALK;
            setUseItem(false);
            return;
        }
        if(startEating){
            setUseItem(true);
            if(mc.thePlayer.getItemInUseDuration() > 8){
                startEating =false;
            }
        }else{
            setUseItem(false);
        }
    }

    public void useItem(){
        Wrapper.instance.log(mc.gameSettings.keyBindUseItem.isKeyDown());
        setAction(mc.gameSettings.keyBindUseItem, true);
    }

    public void setAction(KeyBinding key, boolean click){
        KeyBinding.setKeyBindState(key.getKeyCode(), click);
    }

    public void sendLeftClick(){
        mc.clickMouse();
    }

    private boolean rotateYawFlag = false;
    private float pendingYawGoal = 0f;
    private float pendingYawSpeed = 0f;

    public void requestRotateYaw(float yawGoal, float speed) {
        this.pendingYawGoal = yawGoal;
        this.pendingYawSpeed = speed;
        this.rotateYawFlag = true;
    }

    private boolean rotatePitchFlag = false;
    private float pendingPitchGoal = 0f;
    private float pendingPitchSpeed = 0f;

    public void requestRotatePitch(float yawGoal, float speed) {
        this.pendingPitchGoal = yawGoal;
        this.pendingPitchSpeed = speed;
        this.rotatePitchFlag = true;
    }
    public void executePendingPitch() {
        if (rotatePitchFlag) {
            rotatePitchTo(pendingPitchGoal, pendingPitchSpeed);
            if (Math.abs(getPitch() - pendingPitchGoal) < 1f) {
                rotatePitchFlag = false;
            }
        }
    }

    public void executePendingYaw() {
        if (rotateYawFlag) {
            rotateYawTo(pendingYawGoal, pendingYawSpeed);
            if (Math.abs(getWrappedYaw() - pendingYawGoal) < 1f) {
                rotateYawFlag = false;
            }
        }
    }

    public List<EntityLivingBase> getEntitiesAround(float radius){
        AxisAlignedBB bb = mc.thePlayer.boundingBox.expand(radius, radius, radius);
        List<EntityLivingBase> entities = mc.theWorld.getEntitiesWithinAABB(EntityLivingBase.class, bb,
                entity -> entity != mc.thePlayer
        );
        entities.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
        return entities;
    }


    public enum Desire{
        HEAL,
        EAT,
        WALK,
        BREAK,
        COLLECT_WOOD,
        MINE,
        ATTACK,
        FLEE,
        USE
    }

    public enum Action{

    }

    public enum Direction{
        LEFT,
        FORWARDS,
        RIGHT,
        BACKWARDS
    }

}

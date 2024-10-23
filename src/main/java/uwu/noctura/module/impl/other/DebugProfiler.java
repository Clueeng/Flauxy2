package uwu.noctura.module.impl.other;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.Sys;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.utils.Wrapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

@ModuleInfo(name = "DebugProfiler", displayName = "DebugProfiler", cat = Category.Other, key = -1)
public class DebugProfiler extends Module {

    private static final int MAX_SAMPLES = 2500;
    private int fpsSum = 0;
    private int fpsCount = 0;
    private Deque<Integer> fpsDeque = new ArrayDeque<>(MAX_SAMPLES);
    long started;
    long oldFrame;

    @Override
    public void onEnable() {
        if(mc.thePlayer == null)return;
        started = System.currentTimeMillis();
        oldFrame = System.currentTimeMillis();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventRender2D) {
            if (mc.thePlayer == null) return;
            int currentFPS = Minecraft.getDebugFPS();
            fpsDeque.addLast(currentFPS);
            fpsSum += currentFPS;
            fpsCount++;
            if (fpsCount > MAX_SAMPLES) {
                int oldestFPS = fpsDeque.removeFirst();
                fpsSum -= oldestFPS;
                fpsCount--;
            }
            int fpsAverage = fpsSum / fpsCount;
            long between = System.currentTimeMillis() - oldFrame;
            boolean opt = Noctura.INSTANCE.getModuleManager().getModule(Performance.class).optimizeChunkLayer.isEnabled() && Noctura.INSTANCE.getModuleManager().getModule(Performance.class).isToggled();
            mc.fontRendererObj.drawString("Avg FPS: " + fpsAverage, 4, 4, -1);
            mc.fontRendererObj.drawString("Frame processing: " + between, 4, 16, -1);
            mc.fontRendererObj.drawString("Loaded entities: " + mc.theWorld.loadedEntityList.size(), 4, 28, -1);
            mc.fontRendererObj.drawString("Loaded tile entities: " + mc.theWorld.loadedTileEntityList.size(), 4, 40, -1);
            mc.fontRendererObj.drawString("Layer chunk opt: " + (opt ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + opt, 4, 52, -1);
            if(System.currentTimeMillis() - started > 10_000){
                Wrapper.instance.log("Average fps: " + fpsAverage);
                started = System.currentTimeMillis();
            }
            oldFrame = System.currentTimeMillis();
        }
    }
}

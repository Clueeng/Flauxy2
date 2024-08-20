package uwu.flauxy.module.impl.falses;

import net.minecraft.util.MathHelper;
import optfine.MathUtils;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;

import static net.minecraft.util.MathHelper.SIN_TABLE_FAST;

@ModuleInfo(name = "FastMathHack", displayName = "Fast Math Hack", key = -1, cat = Category.False)
public class FastMathHack extends Module {

    public BooleanSetting spam = new BooleanSetting("On and off spam",false);
    public NumberSetting spamMs = new NumberSetting("time (ms)",50,0,1000,25).setCanShow(m -> spam.isEnabled());
    public static ModeSetting mathHelperSinOptimization = new ModeSetting("Sin Optimization","1.8.8","1.8.8", "1.8.9 L5");
    String oldMode = "";
    public FastMathHack(){
        addSettings(spam, spamMs, mathHelperSinOptimization);
    }
    private Timer timer = new Timer();

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate) e;
            if(!oldMode.equals(mathHelperSinOptimization.getMode())){
                if(mathHelperSinOptimization.getMode().equals("1.8.8")){
                    voidSinTableFast();
                    normalSinTable();
                    Wrapper.instance.log("Loaded default sin table");
                }else{
                    voidSinTableFast();
                    glitchedSinTable();
                    Wrapper.instance.log("Loaded L5 sin table");
                }
            }
            if(ev.isPre()){
                if(timer.hasTimeElapsed(spamMs.getValue(),true) && spam.getValue()){
                    mc.gameSettings.ofFastMath = !mc.gameSettings.ofFastMath;
                    MathHelper.fastMath = !MathHelper.fastMath;
                }
                if(!spam.getValue()){
                    mc.gameSettings.ofFastMath = true;
                    MathHelper.fastMath = true;
                }
                oldMode = mathHelperSinOptimization.getMode();
            }
        }
    }

    @Override
    public void onEnable() {
        mc.gameSettings.ofFastMath = true;
        MathHelper.fastMath = true;
        if(mathHelperSinOptimization.getMode().equals("1.8.8")){
            voidSinTableFast();
            normalSinTable();
            Wrapper.instance.log("Loaded default sin table");
        }else{
            voidSinTableFast();
            glitchedSinTable();
            Wrapper.instance.log("Loaded L5 sin table");
        }
    }

    @Override
    public void onDisable() {
        voidSinTableFast();
        normalSinTable();
        Wrapper.instance.log("Unloaded custom sin table");
    }

    public void voidSinTableFast(){
        for (int j = 0; j < 4096; ++j)
        {
            SIN_TABLE_FAST[j] = 0;
        }

        for (int l = 0; l < 360; l += 90)
        {
            SIN_TABLE_FAST[(int)((float)l * 11.377778F) & 4095] = 0;
        }
    }

    public void glitchedSinTable(){
        for (int j = 0; j < SIN_TABLE_FAST.length; ++j)
        {
            SIN_TABLE_FAST[j] = MathUtils.roundToFloat(Math.sin((double)j * Math.PI * 2.0D / 4096.0D));
        }
    }

    public void normalSinTable(){
        for (int j = 0; j < 4096; ++j)
        {
            SIN_TABLE_FAST[j] = (float)Math.sin((double)(((float)j + 0.5F) / 4096.0F * ((float)Math.PI * 2F)));
        }
        for (int l = 0; l < 360; l += 90)
        {
            SIN_TABLE_FAST[(int)((float)l * 11.377778F) & 4095] = (float)Math.sin((double)((float)l * 0.017453292F));
        }
    }


}

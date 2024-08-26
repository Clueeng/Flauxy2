package uwu.flauxy.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import uwu.flauxy.module.setting.Setting;

import java.util.function.Supplier;

public class GraphSetting extends Setting<float[]> {

    @Getter
    private float x;

    @Getter
    private float y;

    @Getter @Setter
    private float minX;

    @Getter @Setter
    private float maxX;

    @Getter @Setter
    private float minY;

    @Getter @Setter
    private float maxY;

    @Getter @Setter
    private float incrementX;

    @Getter @Setter
    private float incrementY;
    @Getter
    public double[] value;
    @Getter @Setter
    public boolean colorDisplay;

    public NumberSetting hue;

    public GraphSetting(String name, float x, float y, Supplier<Boolean> dependency,
                        float minX, float maxX, float minY, float maxY,
                        float incrementX, float incrementY) {
        super(name, new float[]{x, y}, dependency);
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.incrementX = incrementX;
        this.incrementY = incrementY;
    }
    public GraphSetting(String name, float x, float y, Supplier<Boolean> dependency,
                        float minX, float maxX, float minY, float maxY,
                        float incrementX, float incrementY, NumberSetting hue) {
        super(name, new float[]{x, y}, dependency);
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.incrementX = incrementX;
        this.incrementY = incrementY;
        this.hue = hue;
    }

    public GraphSetting(String name, float x, float y,
                        float minX, float maxX, float minY, float maxY,
                        float incrementX, float incrementY) {
        this(name, x, y, () -> true, minX, maxX, minY, maxY, incrementX, incrementY);
    }
    public GraphSetting(String name, float x, float y,
                        float minX, float maxX, float minY, float maxY,
                        float incrementX, float incrementY, NumberSetting hue) {
        this(name, x, y, () -> true, minX, maxX, minY, maxY, incrementX, incrementY, hue);
    }

    public void setX(float x) {
        float precisionX = 1.0f / this.incrementX;
        this.x = Math.round(Math.max(this.minX, Math.min(this.maxX, x)) * precisionX) / precisionX;
        updateValue();
    }

    public void setY(float y) {
        float precisionY = 1.0f / this.incrementY;
        this.y = Math.round(Math.max(this.minY, Math.min(this.maxY, y)) * precisionY) / precisionY;
        updateValue();
    }

    public void setXY(float x, float y) {
        setX(x);
        setY(y);
    }

    private void updateValue() {
        this.value = new double[]{this.x, this.y};
    }

    public double[] getValue() {
        return this.value == null ? new double[]{0, 0} : this.value;
    }
    public double getX(){
        return getValue()[0];
    }
    public double getY(){
        return getValue()[1];
    }
}
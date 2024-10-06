package uwu.noctura.module.impl.display;

import lombok.Getter;

public class GameInformations {

    @Getter
    String name, customName;
    Object value;
    public float y;

    public GameInformations(String name, String customName, Object value){
        this.name = name;
        this.customName = customName;
        this.value = value;
    }

    public void updateValue(){

    }

    public void draw(){

    }

}

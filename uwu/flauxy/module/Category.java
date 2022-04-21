package uwu.flauxy.module;

import lombok.Getter;

public enum Category {
    COMBAT("Combat"), EXPLOIT("Exploit"), VISUALS("Visuals"), AUXY("Auxy"), MOVEMENT("Movement"), WORLD("World");
    @Getter
    String name;

    Category(String name){
        this.name = name;
    }
}

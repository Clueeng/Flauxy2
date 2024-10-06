package uwu.noctura;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;

public class Changelog {
    @Getter
    @Setter
    String changelog;
    @Getter
    @Setter
    Type type;
    @Getter
    String prefix;

    public static ArrayList<Changelog> changelogs = new ArrayList<>();

    public Changelog(String changelog, Type type){
        this.changelog = changelog;
        this.type = type;
        this.prefix = type.getPrefix();
        addChangeLog();
    }

    public void addChangeLog() {
    }

    public enum Type{
        ADDED(new Color(74, 185, 66), "+"), REMOVED(new Color(155, 33, 33), "-"), EDITED(new Color(181, 131, 38), "~")
        , TITLE(new Color(120, 120, 120), "V");
        public final Color c;
        @Getter
        public String prefix;
        Type(Color c, String prefix){
            this.c = c;
            this.prefix = prefix;
        }

        public Color getColor() {
            return c;
        }
    }

}

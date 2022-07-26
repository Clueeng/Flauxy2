package uwu.flauxy;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Changelog {

    @Getter
    @Setter
    int offset;
    @Getter
    @Setter
    String changelog;
    @Getter
    @Setter
    Type type;

    public static ArrayList<Changelog> changelogs = new ArrayList<>();

    public Changelog(String changelog, int offset, Type type){
        this.changelog = changelog;
        this.offset = offset;
        this.type = type;
        addChangeLog();
    }

    public void addChangeLog() {
    }

    enum Type{
        ADDED, REMOVED, EDITED;
    }

}

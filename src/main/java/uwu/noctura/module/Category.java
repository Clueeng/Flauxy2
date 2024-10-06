package uwu.noctura.module;

import java.awt.*;

public enum Category {
    Combat(new Color(232, 76, 60), 1),
    Movement(new Color(46, 204, 113), 2),
    Player(new Color(141, 68, 173), 3),
    Visuals(new Color(55, 0, 206), 4),
    Exploit(new Color(50, 131, 173), 5),
    Display(new Color(243, 155, 18), 6),
    Other(new Color(148, 133, 59), 7),
    Ghost(new Color(65, 65, 65), 8),
    False(new Color(188, 68, 114), 9),
    Macro(new Color(153, 69, 18), 10);

    Color c;
    public int id;
    Category(Color c, int id){
        this.c = c; this.id = id;
    }

    public Color getCategoryColor(){
        return c;
    }
}

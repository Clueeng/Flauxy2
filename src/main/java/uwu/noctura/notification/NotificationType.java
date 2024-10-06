package uwu.noctura.notification;

import lombok.Getter;

import java.awt.*;

public enum NotificationType {
    INFO(new Color(99, 27, 121));

    @Getter
    final Color color;
    NotificationType(Color c){
        this.color = c;
    }
}

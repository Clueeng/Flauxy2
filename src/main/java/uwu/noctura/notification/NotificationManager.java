package uwu.noctura.notification;

import lombok.Getter;

import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class NotificationManager {

    public CopyOnWriteArrayList<Notification> queuedNotifications = new CopyOnWriteArrayList<>();

    public Notification getCurrent(){
        return this.queuedNotifications.isEmpty() ? null : this.queuedNotifications.get(0);
    }

    public void addToQueue(Notification notification){
        this.queuedNotifications.add(notification);
    }

}

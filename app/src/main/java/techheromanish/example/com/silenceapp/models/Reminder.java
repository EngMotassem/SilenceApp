package techheromanish.example.com.silenceapp.models;

import android.app.PendingIntent;

/**
 * Created by Manish on 3/30/2017.
 */

public class Reminder {

    String time, title, description;
    int id;
//    PendingIntent pendingIntent;

    public Reminder(int id,String time , String title , String description){

        this.id = id;
        this.time = time;
        this.title = title;
        this.description = description;
//        this.pendingIntent = pendingIntent;

    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}

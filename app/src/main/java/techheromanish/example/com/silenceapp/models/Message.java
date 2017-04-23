package techheromanish.example.com.silenceapp.models;

/**
 * Created by Manish on 3/31/2017.
 */

public class Message {

    String message, mobile, time;
    int id;;
    public Message(int id,String message, String mobile, String time) {

        this.id = id;
        this.message = message;
        this.mobile = mobile;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getMobile() {
        return mobile;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }
}

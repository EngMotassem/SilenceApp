package techheromanish.example.com.silenceapp.models;

/**
 * Created by Manish on 4/1/2017.
 */

public class Call {

    String mobile, time;
    int id;

    public Call(String mobile, String time, int id) {
        this.mobile = mobile;
        this.time = time;
        this.id = id;
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

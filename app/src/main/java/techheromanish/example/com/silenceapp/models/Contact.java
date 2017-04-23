package techheromanish.example.com.silenceapp.models;

/**
 * Created by Manish on 12/17/2016.
 */

public class Contact {

    private String contactName, primary_mobile;
    private String phoneno;
    boolean camera, sms, call, silenceexception, videocall,selected;
    boolean toBedeleted;
    int position;
    public Contact(){


    }
public Contact(String contact_name, String phoneNo,boolean selected ,boolean camera, boolean sms, boolean call, boolean silenceexception, boolean videocall){

        this.contactName = contact_name;
        this.phoneno = phoneNo;
        this.selected = selected;
        this.camera = camera;
        this.sms = sms;
        this.call = call;
        this.silenceexception = silenceexception;
        this.videocall = videocall;

    }

    public void setPosition(int position){
        this.position = position;
    }

    public int getPosition(){
        return position;
    }

    public boolean isToBedeleted() {
        return toBedeleted;
    }

    public void setToBedeleted(boolean toBedeleted) {
        this.toBedeleted = toBedeleted;
    }

    public String getPrimary_mobile() {
        return primary_mobile;
    }

    public void setPrimary_mobile(String primary_mobile) {
        this.primary_mobile = primary_mobile;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setCamera(boolean camera) {
        this.camera = camera;
    }

    public void setSmsreminder(boolean smsreminder) {
        this.sms = smsreminder;
    }

    public void setCallreminder(boolean callreminder) {
        this.call = callreminder;
    }

    public void setSilenceexception(boolean silenceexception) {
        this.silenceexception = silenceexception;
    }

    public void setVideocall(boolean videocall) {
        this.videocall = videocall;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }


    public boolean isCamera() {
        return camera;
    }

    public boolean isSmsreminder() {
        return sms;
    }

    public boolean isCallreminder() {
        return call;
    }

    public boolean isSilenceexception() {
        return silenceexception;
    }

    public boolean isVideocall() {
        return videocall;
    }
}

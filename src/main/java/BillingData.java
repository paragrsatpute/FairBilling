public class BillingData {

    private String time;
    private String userName;
    private String action;

    public BillingData() {}

    public BillingData(String time, String userName, String action) {
        this.time = time;
        this.userName = userName;
        this.action = action;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
}

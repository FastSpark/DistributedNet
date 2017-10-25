package cs4262;

import java.sql.Timestamp;

public class Neighbour {

    private String ip;
    private int port;
    private String username;
    private long timeStamp;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public Neighbour(String ip, int port, String username) {
        this.timestamp.getTime();
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public String getIp() {
        return this.ip;
    }

    public String getUsername() {
        return this.username;
    }

    public int getPort() {
        return this.port;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}

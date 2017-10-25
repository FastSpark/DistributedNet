package cs4262;

import java.sql.Timestamp;

public class Node{
	
        private final String ip;
	private final int port;
        private final long timeStamp;
        
        public Node(String ip, int port){
            this.timeStamp =  new Timestamp(System.currentTimeMillis()).getTime();
            this.ip = ip;
            this.port = port;
	}	

	public String getIp(){
		return this.ip;
	}

	public int getPort(){
		return this.port;
	}

        public long getTimeStamp() {
            return timeStamp;
        }
}

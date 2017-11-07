import java.util.concurrent.SynchronousQueue;

public class HelloHandler implements SimpleMessageHandler, Runnable {

	private SynchronousQueue<String> incoming = new SynchronousQueue<String>();
	private MuxDemuxSimple myMuxDemux = null;
	
    public void setMuxDemux(MuxDemuxSimple md){
        myMuxDemux = md;
    }
    
    public void handleMessage(String m){
        try {
        	incoming.put(m);
        } catch (Exception e){
        }
    }
    
    public void run(){
    	while(true){
    		try{
    			String msg = incoming.take();
    			myMuxDemux.send(msg);
    		} catch (Exception e){
    		}
    	}
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

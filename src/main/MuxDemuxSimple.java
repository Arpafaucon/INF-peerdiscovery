import java.util.concurrent.SynchronousQueue;
import java.net.*;
import java.io.*;

public class MuxDemuxSimple implements Runnable{

    private DatagramSocket mySocket = null;
    private BufferedReader in;
    private SimpleMessageHandler[] myMessageHandlers;
    private SynchronousQueue<String> outgoing = new SynchronousQueue<String>();
    
    MuxDemuxSimple (SimpleMessageHandler[] h, DatagramSocket s){
        mySocket = s;
        try{
        	mySocket.setBroadcast(true);
        } catch (Exception e){
        }
        myMessageHandlers = h;
    }
    
    public void run(){
        for (int i=0; i<myMessageHandlers.length; i++){
            myMessageHandlers[i].setMuxDemux(this);
        }
        try{
        	while(true){
        		byte[] buf = new byte[8192];
        		DatagramPacket messageReçu = new DatagramPacket(buf, 8192);
        		mySocket.receive(messageReçu);
        		for (int i=0; i<myMessageHandlers.length; i++){
        			String message = buf.toString();
                    myMessageHandlers[i].handleMessage(message);
                }
        	}
        }catch (IOException e){ }		
        try{
            in.close(); mySocket.close();
        }catch(IOException e){ }
    }
	
    public void send(String s){
    	outgoing.add(s);
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			DatagramSocket s = new DatagramSocket(4242);
			HelloHandler[] handlers = new HelloHandler[1];
			handlers[0]= new HelloHandler();
			MuxDemuxSimple dm = new MuxDemuxSimple(handlers, s);
			new Thread(handlers[0]).start();
			new Thread(dm).start();
		} catch (Exception e){
		}
	}

}

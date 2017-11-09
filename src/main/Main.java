package main;


import handlers.HelloHandler;
import handlers.SimpleMessageHandler;





public class Main{
    
    public static void main(String[] args){
        SimpleMessageHandler[] handlers = new SimpleMessageHandler[3];
        handlers[1] = new HelloHandler();
        //handlers[2] = new LSAHandler();
    }

}
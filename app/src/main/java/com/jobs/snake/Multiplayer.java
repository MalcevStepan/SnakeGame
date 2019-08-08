package com.jobs.snake;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Multiplayer extends Thread {
    private Socket socket;
    private OutputStream out;
    private InputStream in;

    public void run() {
        try {
            socket = new Socket("ip", 1111);
            while (socket.isConnected()){
                sendDirection(Snake.directionNumber);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void sendDirection(byte direction){
        try {
            out.write(direction);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    byte receiveDirection(){
        byte x=0;
        try {
            x=(byte)in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return x;
    }
}

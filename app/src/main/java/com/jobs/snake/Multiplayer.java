package com.jobs.snake;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class Multiplayer extends Thread {
    private static DatagramSocket socket;

    Multiplayer() throws SocketException {
        socket = new DatagramSocket(1);
    }

    static byte[] getApplePos() throws IOException {
        DatagramPacket receivedPosition = new DatagramPacket(new byte[2], 2);
        socket.receive(receivedPosition);
        byte[] position = receivedPosition.getData();
        if (position[1] != 0) {
            return position;
        }
        return null;
    }

    static byte getDirection() throws IOException {
        DatagramPacket receivedDirection = new DatagramPacket(new byte[1], 1);
        socket.receive(receivedDirection);
        byte[] dir = receivedDirection.getData();
        return dir[0];
    }

    static void sendDirection() throws IOException {
        byte[] bytes = Snake.directionNumber;
        DatagramPacket direction = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("94.103.94.112"), 1);
        socket.send(direction);
    }

    static void search() throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[1], 1, InetAddress.getByName("94.103.94.112"), 1);
        socket.send(packet);
    }
    static boolean waitConfirm() throws IOException {
        DatagramPacket packet=new DatagramPacket(new byte[1],1);
        socket.receive(packet);
        byte[] b=packet.getData();
        return b[0] == 1;
    }
}

package com.jobs.snake;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

final class Net {

	static DatagramSocket socket;
	static InetAddress address;

	static int port = 8001;

	static void sendMessage(byte[] message) {
		new Thread(() -> {
			try {
				socket.send(new DatagramPacket(message, message.length, address, port));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	static void sendMessage(byte message) {
		new Thread(() -> {
			try {
				socket.send(new DatagramPacket(new byte[]{ message }, 1, address, port));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	static byte[] getMessage() {
		DatagramPacket res = new DatagramPacket(new byte[20], 20);
		try {
			socket.receive(res);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res.getData();
	}
}
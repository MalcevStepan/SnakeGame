package com.jobs.snake;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

final class Multiplayer {
	private static DatagramSocket socket;

	static {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	static void getData() {
		while (true) {
			try {
				DatagramPacket receivedDirection = new DatagramPacket(new byte[2], 2);
				socket.receive(receivedDirection);
				byte[] dir = receivedDirection.getData();
				if (dir[1] == 0) {
					switch (dir[0]) {
						case 0:
							Memory.snakeEnemy.direction = Direction.Up;
							break;
						case 1:
							Memory.snakeEnemy.direction = Direction.Right;
							break;
						case 2:
							Memory.snakeEnemy.direction = Direction.Down;
							break;
						case 3:
							Memory.snakeEnemy.direction = Direction.Left;
							break;
					}
				} else {
					Memory.apple.position.x = dir[0];
					Memory.apple.position.y = (byte) (dir[1] - 1);
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	static void sendDirection() throws IOException {
		byte[] bytes = Snake.directionNumber;
		DatagramPacket direction = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("94.103.94.112"), 1);
		socket.send(direction);
	}

	static void search() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[]{1}, 1, InetAddress.getByName("94.103.94.112"), 1);
		socket.send(packet);
	}

	static boolean getConfirm() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[1], 1);
		socket.receive(packet);
		byte[] b = packet.getData();
		return b[0] == 1;
	}

	static void sendApplePosition() throws IOException {
		byte[] positionXY = new byte[2];
		positionXY[0] = Memory.apple.position.x;
		positionXY[1] = (byte)(Memory.apple.position.y+1);
		DatagramPacket packet = new DatagramPacket(positionXY, positionXY.length, InetAddress.getByName("94.103.94.112"), 1);
		socket.send(packet);
	}
}
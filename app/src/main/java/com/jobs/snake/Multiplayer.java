package com.jobs.snake;

import android.util.Log;
import android.widget.Toast;

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
				DatagramPacket receivedDirection = new DatagramPacket(new byte[3], 3);
				Log.e("TAG", "PreReceived");
				socket.receive(receivedDirection);
				Log.e("TAG", "received");
				byte[] dir = receivedDirection.getData();
				if (dir[0] == Memory.DIRECTION) {
					Log.e("TAG", "direction: " + dir[0] + " : " + dir[1]);
					switch (dir[1]) {
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
				} else if (dir[0] == Memory.APPLE) {
					Log.e("TAG", "apple: " + dir[0] + " : " + dir[1]);
					Memory.apple.position.x = dir[1];
					Memory.apple.position.y = dir[2];
				} else if (dir[0] == Memory.STATE && dir[1] == State.Exited.getValue()) {
					Log.e("TAG", "exit: " + dir[0] + " : " + dir[1]);
					Memory.viewMode = ViewMode.MultiRoom;
					Toast.makeText(Memory.gameView.getContext(), Memory.gameView.getContext().getResources().getString(R.string.enemy_exited), Toast.LENGTH_SHORT).show();
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	static void sendDirection() {
		try {
			DatagramPacket direction = new DatagramPacket(new byte[]{Memory.DIRECTION, Memory.snake.directionNumber}, 2, InetAddress.getByName("94.103.94.112"), 1);
			socket.send(direction);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static boolean getConfirm() {
		DatagramPacket packet = new DatagramPacket(new byte[3], 3);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] b = packet.getData();
		return b[0] == Memory.STATE && b[1] == State.Ready.getValue();
	}

	static void sendApplePosition() {
		try {
			DatagramPacket packet = new DatagramPacket(new byte[]{Memory.APPLE, Memory.apple.position.x, Memory.apple.position.y}, 3, InetAddress.getByName("94.103.94.112"), 1);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void sendState() {
		try {
			DatagramPacket packet = new DatagramPacket(new byte[]{Memory.STATE, Memory.currentState.getValue()}, 2, InetAddress.getByName("94.103.94.112"), 1);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import model.ClientThread;
import model.UDPClientThread;

public class ServerListener {
	private static ServerListener instance = null;
	public final int PORT = 9562;
	public final int SYNCHRO_PORT = 9561;

	public ServerSocket server = null;
	public ServerSocket synchroServer = null;
	public Socket client = null;
	public Socket synchroClient = null;
	public ClientThread clientThread;

	public ServerListener() {
		new UDPThreadConnector().start();
		new UDPClientThread().start();
		try {
			server = new ServerSocket(PORT);
			synchroServer = new ServerSocket(SYNCHRO_PORT);
		} catch (IOException ex) {
			System.out.println("Port zaj�ty");
			System.exit(-1);
		}
		while (true) {
			try {
				System.out.println("nasluchiwsanie...");
				client = server.accept();
				clientThread = new ClientThread(client);
				clientThread.start();
				synchroClient = synchroServer.accept();
				Synchronizator s = new Synchronizator(synchroClient);
				s.start();
			} catch (IOException ex) {
				System.out.println("Nie mo�na zaakceptowa�");
			}
		}
	}

	public static void main(String[] args) {
		// Window.startWindow(args);
		new ServerListener();
	}

	public static ServerListener getInstance() {
		return instance;
	}
}

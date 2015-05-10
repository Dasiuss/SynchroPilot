package pwr.mobilne.SynchroPilot.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * in Activity, call <code>ConnectionController.getInstance().prepareSocket(getApplicationContext());</code>
 * 
 */
public class ConnectionController {
	private static ConnectionController instance = null;
	public final int PORT = 9562;
	public final int UDPPORT = 9563;
	private Socket socket = null;
	private PrintWriter out;
	@SuppressWarnings("unused")
	private BufferedReader in;
	Boolean socketReady = false;
	public String lastFoundIp = "";

	private Context context;

	public synchronized void sendToSocket(String msg) {
		if (socketReady) out.println(msg);
	}

	public void prepareSocket(Context context) {
		this.context = context;
		Log.i("ConnectionUDP", "preparing...");
		if (!lastFoundIp.equals("")) new TCPConnectionTask(this).execute(lastFoundIp);
		new UDPConnectionTask(this).execute("");
		scanIPsForServer();
	}

	/**
	 * 
	 */
	private void scanIPsForServer() {
		String ip = getWifiIpAddress();
		ip = ip.substring(0, ip.lastIndexOf("."));
		for (int i = 1; i < 255 && socket == null; i++) {
			new TCPConnectionTask(this).execute(ip + "." + i);
			Log.i("ConnectionController", "task " + i);
		}
	}

	protected synchronized void setSocket(Socket socket) {
		if (socket == null) return;
		this.socket = socket;
		lastFoundIp = socket.getInetAddress().getHostAddress();
		try {
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketReady = true;
		} catch (IOException e) {
			e.printStackTrace();
			socket = null;
		}
	}

	public void closeSocket() {
		try {
			if (socket != null) socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	InetAddress getBroadcastAddress() throws IOException {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		if (dhcp == null) return null;
		// TODO handle null somehow

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	protected String getWifiIpAddress() {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		if (dhcp == null) return null;
		// TODO handle null somehow
		String ipAddress = "";
		for (int k = 0; k < 4; k++)
			ipAddress += ((dhcp.ipAddress >> k * 8) & 0xFF) + ".";
		ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
		Log.i("Connection Controller", ipAddress);
		return ipAddress;
	}

	public static ConnectionController getInstance() {
		if (instance == null) instance = new ConnectionController();
		return instance;
	}

}

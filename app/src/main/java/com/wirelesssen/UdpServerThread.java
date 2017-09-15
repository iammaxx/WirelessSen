package com.wirelesssen;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

import static android.content.ContentValues.TAG;


/**
 * Created by SIDDHU on 14/09/2017.
 */

public class UdpServerThread extends Thread{
    int serverPort;
    DatagramSocket socket;

    boolean running;
    public UdpServerThread(int serverPort) {
        super();
        this.serverPort = serverPort;
    }
    @Override
    public void run() {

        running = true;

        try {
            socket = new DatagramSocket(serverPort);
            Log.e(TAG, "UDP Server is running");

            while(running){
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);     //this code block the program flow
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String dString = new Date().toString() + "\n"
                        + "Your address " + address.toString() + ":" + String.valueOf(port);
                buf = dString.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);

            }
            Log.e(TAG, "UDP Server ended");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                socket.close();
                Log.e(TAG, "socket.close()");
            }
        }
    }
}
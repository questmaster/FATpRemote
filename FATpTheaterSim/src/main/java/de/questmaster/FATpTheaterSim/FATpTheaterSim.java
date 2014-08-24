package de.questmaster.FATpTheaterSim;

import java.io.*;
import java.net.*;

/**
 * Created on 24.08.14.
 */
public class FATpTheaterSim {

    public FATpTheaterSim(String[] ports) {
        try {
            // Start Discovery-Service
            UDPServerThread ust = new UDPServerThread(Integer.parseInt(ports[0]));
            ust.start();

            while (true) {
                int port = Integer.parseInt(ports[1]);
                ServerSocket serverSocket = new ServerSocket(port);

                // Auf Verbindung warten
                System.out.println("Open Socket on port " + port + ".");
                Socket s = serverSocket.accept();

                // kommunikation an einen nebenläufigen Thread abgeben
                ServerThread t = new ServerThread(s);
                //t.start();
                t.run(); // do not use thread!

                // und wieder auf neue Verbindungen warten
                s.close();
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FATpTheaterSim(args);
    }

    private void outputLog(char[] buffer, String s) {
        System.out.print("Input: " + String.valueOf(buffer[0]) + "-"
                + String.valueOf(buffer[1]) + "-"
                + String.valueOf(buffer[2]) + "-"
                + String.valueOf(buffer[3])
                + "; " + s + "... ");
    }

    public class ServerThread extends Thread {

        private Socket s;

        public ServerThread(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                char buffer[] = new char[4];

                // lesen
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                int len = in.read(buffer);

                if ((len == 4)
                        && (buffer[0] == 0x42) && (buffer[1] == 0x12)) {
                    // answer 'Venus'
                    if (buffer[2] == 0xfc) {
                        outputLog(buffer, "Output Venus");

                        // schreiben
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

                        out.write("Venus");
                        out.flush();

                        // aufräumen
                        out.close();
                    }
                    // get sqlite
                    else if (buffer[2] == 0xfe) {
                        outputLog(buffer, "Output Sqlite-DB");

                        char outData[] = new char[]{0x00, 0x78, 0x14, 0x00};

                        // schreiben
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

                        out.write(outData);
                        out.write("TODO: Sqlite DB!");
                        out.flush();

                        // aufräumen
                        out.close();
                    }
                }
                in.close();
                System.out.println("done.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class UDPServerThread extends Thread {
        private DatagramSocket serverSocket = null;

        public UDPServerThread(int port) {
            try {
                serverSocket = new DatagramSocket(port);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                byte[] receiveData = new byte[1024];
                while (true) {
                    System.out.println("Open UDP Socket on port " + serverSocket.getLocalPort() + ".");
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    String sentence = new String(receivePacket.getData());
                    System.out.println("UDP Input: " + sentence);
                    if (sentence.equals("Search Venus")) {
                        InetAddress IPAddress = receivePacket.getAddress();
                        int port = receivePacket.getPort();
                        String capitalizedSentence = "TheaterSim";
                        byte[] sendData = capitalizedSentence.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        serverSocket.send(sendPacket);
                        System.out.println("UDP Output sent.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

package IO;

import java.io.*;

public class Server extends Thread {

    public void run() {
        try {
            test();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void test() throws IOException {
        int port = 11111;
        java.net.ServerSocket serverSocket = new java.net.ServerSocket(port);
        java.net.Socket clidecharge = waitOnConnection(serverSocket);
        String message = readMessage(clidecharge);
        System.out.println(message);
        writeMessage(clidecharge, message);
    }

    java.net.Socket waitOnConnection(java.net.ServerSocket serverSocket) throws IOException {
        java.net.Socket socket = serverSocket.accept(); // blockiert, bis sich ein Clidecharge angemeldet hat
        return socket;
    }

    String readMessage(java.net.Socket socket) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        char[] buffer = new char[200];
        int letterCount = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
        String message = new String(buffer, 0, letterCount);
        return message;
    }

    void writeMessage(java.net.Socket socket, String message) throws IOException {
        PrintWriter printWriter =
                new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()));
        printWriter.print(message);
        printWriter.flush();
    }
}
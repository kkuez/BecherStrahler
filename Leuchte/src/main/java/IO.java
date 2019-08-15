

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IO extends Thread {
    Socket socket;
    boolean isConnected = false;
    TikToker tikTok;
    List<String> msgList = new ArrayList<>();
    ServerSocket serverSocket = null;

    public void run() {
        try {
            test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void verarbeiteAnkommendeNachricht(String nachricht){
        if (nachricht.length() > 0) {
            if (nachricht.equals("reset")) {
                PinHub.pinsPruefenUndBeenden();
            } else {
                if(nachricht.equals("tik")){
                    tikTok.setTiktokListe(new TikTok(true, Calendar.getInstance().getTimeInMillis()));
                }else {
                    if(!nachricht.equals("")||!nachricht.equals("\n")){
                        try {
                            int pinnumer = Integer.parseInt(nachricht.substring(0, nachricht.indexOf(";")));
                            int aufladenwert = Integer.parseInt(nachricht.substring(nachricht.indexOf(";") + 1));

                            switch (pinnumer) {

                                case 1:
                                    if(aufladenwert==0){
                                        PinHub.rot01.resetten();
                                    }else {
                                        pinSetzenEins(aufladenwert);
                                        //pinSetzenEins(aufladenwert, 3);
                                    }
                                    break;
                                case 5:
                                    if(aufladenwert==0){
                                        PinHub.blau05.resetten();
                                    }else {
                                        pinSetzenFuenf(aufladenwert);
                                        // pinSetzenFuenf(aufladenwert, 3);
                                    }
                                    break;
                                case 6:
                                    if(aufladenwert==0){
                                        PinHub.gruen06.resetten();
                                    }else {
                                        pinSetzenSechs(aufladenwert);
                                        //  pinSetzenSechs(aufladenwert, 3);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }}
                }
            }
            System.out.println(nachricht);
        }
    }

    void senden( String nachricht) throws IOException {
        PrintWriter printWriter =
                new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()));
        printWriter.print(nachricht);
        printWriter.flush();
    }
    void verbindungAufnehmen(){
        if(tikTok!=null){
            tikTok.interrupt();
            tikTok=null;
        }

        int port = 55551;

        try {
            if(serverSocket!=null){
                serverSocket.close();
            }
            serverSocket = new ServerSocket(port, 50, InetAddress.getLocalHost());
            System.out.println(InetAddress.getLocalHost());
            socket = warteAufAnmeldung(serverSocket);   //Nicht vergessen /etc/hosts einzutragen, die tatsächliche IP bei raspberry
            System.out.println("Verbindung hergestellt.");
            isConnected=true;

            tikTok=new TikToker(this);
            tikTok.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void test() throws IOException {

        verbindungAufnehmen();
        String nachricht;
        while (true) {
            List<String> tempList = getMsgList();
            if(tempList.size()>0){
                for(int i =0;i<tempList.size();i++){
                    senden(tempList.get(i));
                    getMsgList().remove(i);
                }
            }
            if(isConnected){
                nachricht = leseNachricht(socket);
                verarbeiteAnkommendeNachricht(nachricht);
            }else{
                verbindungAufnehmen();
            }
        }}

    public int convertValue(int relativerWertPercent, double maxWertMs){
        double zurück=0;
        double temp=maxWertMs/100;
        zurück = temp*relativerWertPercent;
        return (int)zurück;
    }

    public void pinSetzenEins(int auflade) {
        MessZustand messZustand=PinHub.getMessListeFürRot().get(100-auflade);
        PinHub.rot01.pinSetzen(messZustand.aufladezeit, messZustand.entladezeit);
    }
    public void pinSetzenFuenf(int auflade) {
        MessZustand messZustand=PinHub.getMessListeFuerBlau().get(100-auflade);
        PinHub.blau05.pinSetzen(messZustand.aufladezeit, messZustand.entladezeit);
    }
    public void pinSetzenSechs(int auflade) {
        MessZustand messZustand=PinHub.getMessListeFuerGruen().get(100-auflade);
        PinHub.gruen06.pinSetzen(messZustand.aufladezeit, messZustand.entladezeit);
    }

    public void pinSetzenEins(int auflade, int entlade) {
        PinHub.rot01.setAufladezeit(convertValue(auflade,10));
        PinHub.rot01.setEntladezeit(entlade);
        //   PinHub.rot01.refresh();
        PinHub.rot01.setReset(false);
    }
    public void pinSetzenFuenf(int auflade, int entlade) {
        PinHub.blau05.setAufladezeit(convertValue(auflade,30));
        PinHub.blau05.setEntladezeit(entlade);
        //   PinHub.rot01.refresh();
        PinHub.blau05.setReset(false);
    }
    public void pinSetzenSechs(int auflade, int entlade) {
        PinHub.gruen06.setAufladezeit(convertValue(auflade,30));
        PinHub.gruen06.setEntladezeit(entlade);
        //   PinHub.rot01.refresh();
        PinHub.gruen06.setReset(false);
    }

    java.net.Socket warteAufAnmeldung(java.net.ServerSocket serverSocket) throws IOException {
        java.net.Socket socket = serverSocket.accept(); // blockiert, bis sich ein Client angemeldet hat
        return socket;
    }

    String leseNachricht(java.net.Socket socket) throws IOException {

        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        char[] buffer = new char[200];
        int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
        String nachricht="";
        if(anzahlZeichen>1) {
            nachricht = new String(buffer, 0, anzahlZeichen);
        }
        return nachricht;
    }

    public synchronized List<String> getMsgList() {
        return msgList;
    }

}
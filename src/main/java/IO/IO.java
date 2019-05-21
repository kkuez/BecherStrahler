package IO;

import Hardware.PinHub;
import Messen.MessZustand;

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



    public void processIncomingMessage(String message){
        if (message.length() > 0) {
            if (message.equals("reset")) {
                PinHub.checkPinsAndEnd();
            } else {
                if(message.equals("tik")){
                    tikTok.setTiktokList(new TikTok(true, Calendar.getInstance().getTimeInMillis()));
                }else {
                    if(!message.equals("")||!message.equals("\n")){
                        try {
                            int pinnumber = Integer.parseInt(message.substring(0, message.indexOf(";")));
                            int chargeLoadValue = Integer.parseInt(message.substring(message.indexOf(";") + 1));

                            switch (pinnumber) {

                                case 1:
                                    if(chargeLoadValue==0){
                                        PinHub.red01.reset();
                                    }else {
                                        pinSetOne(chargeLoadValue);
                                        //pinSetOne(chargeLoadValue, 3);
                                    }
                                    break;
                                case 5:
                                    if(chargeLoadValue==0){
                                        PinHub.blue05.reset();
                                    }else {
                                        pinSetFive(chargeLoadValue);
                                        // pinSetFive(chargeLoadValue, 3);
                                    }
                                    break;
                                case 6:
                                    if(chargeLoadValue==0){
                                        PinHub.green06.reset();
                                    }else {
                                        pinSetSix(chargeLoadValue);
                                        //  pinSetSix(chargeLoadValue, 3);
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
            System.out.println(message);
        }
    }

    void send( String message) throws IOException {
        PrintWriter printWriter =
                new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()));
        printWriter.print(message);
        printWriter.flush();
    }
    void connectionChargeTake(){
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
            socket = waitOnConnection(serverSocket);   //Nicht vergessen /etc/hosts einzutragen, die tatsaechliche IP bei raspberry
            System.out.println("Verbindung hergestellt.");
            isConnected=true;

            tikTok=new TikToker(this);
            tikTok.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void test() throws IOException {

        connectionChargeTake();
        String message;


        while (true) {
            List<String> tempList = getMsgList();
            if(tempList.size()>0){
                for(int i =0;i<tempList.size();i++){
                    send(tempList.get(i));
                    getMsgList().remove(i);
                }
            }
            if(isConnected){
                message = readMessage(socket);
                processIncomingMessage(message);
            }else{
                connectionChargeTake();
            }
        }}



    public int convertValue(int relativeValuePercentDecharge, double maxValueMs){
        double returnOut=0;
        double temp=maxValueMs/100;
        returnOut = temp*relativeValuePercentDecharge;
        return (int)returnOut;
    }


    public void pinSetOne(int chargelade) {
        MessZustand messZustand= PinHub.getCalibratedMeassureListForRed().get(100-chargelade);
        PinHub.red01.pinSet(messZustand.chargeTime, messZustand.dechargeTime);
    }
    public void pinSetFive(int chargelade) {
        MessZustand messZustand= PinHub.getCalibratedMeassureListForBlue().get(100-chargelade);
        PinHub.blue05.pinSet(messZustand.chargeTime, messZustand.dechargeTime);
    }
    public void pinSetSix(int chargelade) {
        MessZustand messZustand= PinHub.getCalibratedMeassureListForGreen().get(100-chargelade);
        PinHub.green06.pinSet(messZustand.chargeTime, messZustand.dechargeTime);
    }

    public void pinSetOne(int chargelade, int dechargelade) {


        PinHub.red01.setChargeTime(convertValue(chargelade,10));
        PinHub.red01.setDechargeTime(dechargelade);
        //   PinHub.red01.refresh();
        PinHub.red01.setReset(false);

    }
    public void pinSetFive(int chargelade, int dechargelade) {
        PinHub.blue05.setChargeTime(convertValue(chargelade,30));
        PinHub.blue05.setDechargeTime(dechargelade);
        //   PinHub.red01.refresh();
        PinHub.blue05.setReset(false);
    }
    public void pinSetSix(int chargelade, int dechargelade) {

        PinHub.green06.setChargeTime(convertValue(chargelade,30));
        PinHub.green06.setDechargeTime(dechargelade);
        //   PinHub.red01.refresh();
        PinHub.green06.setReset(false);

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
        String message="";
        if(letterCount>1) {
            message = new String(buffer, 0, letterCount);
        }
        return message;
    }

    public synchronized List<String> getMsgList() {

        return msgList;
    }

}
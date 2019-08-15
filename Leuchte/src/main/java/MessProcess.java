

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MessProcess extends Thread {


    List<String> messLinesFürJedenEingang=new ArrayList<>();
    Object lock=new Object();
    public void run(){
        for(int i =0;i<8;i++){
            getMessLinesFürJedenEingang().add("0");
        }
        if(new File("/home/pi/Dokumente/Waveshare-Board/Raspberry/ADS1256/ads1256_test").exists()) {
            try {
                ProcessBuilder builder = new ProcessBuilder("/home/pi/Dokumente/Waveshare-Board/Raspberry/ADS1256/ads1256_test");

                builder.redirectErrorStream(true);
                String currentLine;

                Process process;
                process = builder.start();

                while (true) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    while (!bufferedReader.ready()) {
                    }
                    System.out.println(bufferedReader.ready());
                    while (bufferedReader.ready()) {
                        // System.out.println(bufferedReader.readLine());
                        int aktuellerEingang;
                        currentLine = bufferedReader.readLine();
                        try {
                            aktuellerEingang = Integer.parseInt(currentLine.substring(0, 1));
                            getMessLinesFürJedenEingang().set(aktuellerEingang, currentLine);
                        } catch (Exception e) {
                        }

                    }

                }
            } catch (Exception e) {
                Logging.warning = "Exception bei Messung:\n" + e.getStackTrace().toString();
                System.out.println(e);
            }
        }else{
            System.out.println("C Programm zum Messen nicht gefunden.");
        }
    }


    //GETTER SETTER
    public List<String> getMessLinesFürJedenEingang() {
        synchronized (lock) {
            return messLinesFürJedenEingang;
        }

    }

    public void setMessLinesFürJedenEingang(List<String> messLinesFürJedenEingang) {
        this.messLinesFürJedenEingang = messLinesFürJedenEingang;
    }
}

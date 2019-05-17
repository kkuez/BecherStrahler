package IO;

import java.util.Calendar;

public class TikTok {
    public TikTok(boolean gueltigrein, long zeitPunktInMillisrein){
        gueltig=gueltigrein;
        zeitPunktInMillis=zeitPunktInMillisrein;
    }
    boolean gueltig;
    Long zeitPunktInMillis = Calendar.getInstance().getTimeInMillis();

}

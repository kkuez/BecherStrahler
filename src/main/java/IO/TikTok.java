package IO;

import java.util.Calendar;

public class TikTok {
    public TikTok(boolean validInto, long pointOfTimeInMillisInto){
        valid=validInto;
        pointOfTimeInMillis=pointOfTimeInMillisInto;
    }
    boolean valid;
    Long pointOfTimeInMillis = Calendar.getInstance().getTimeInMillis();

}

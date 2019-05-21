package Logging;

public class Logging {
    public static String logLabel="";
    public static String warning="";

	public static void refreshLogLabel(String into){
		logLabel =into+"\n\n\n"+warning;
     }
}


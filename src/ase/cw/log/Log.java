package ase.cw.log;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private static Log logger;
    private String logString="";
    private final String logFileName = "log.txt";
    private FileWriter logFileWriter;

    private Log(){

    }

    public static Log getLogger(){
        if (logger == null){
            synchronized (Log.class){
                if (logger == null){
                    logger = new Log();
                }
            }
        }
        return logger;
    }

    public void log (String logLine){

        logLine = "\n\n" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))+ "\n> " + logLine;
        System.out.println(logLine);
        logString += logLine;
    }



    public void writeToLogFile(){

        try{
            logFileWriter = new FileWriter(logFileName);
            logFileWriter.write(logString);
            logFileWriter.close();
        }
        catch (FileNotFoundException fe){
            System.out.println(logFileName + " was not found");
        }

        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

}

import org.apache.log4j.Logger;

public class GenerateLogger {

    public static Logger logger = Logger.getLogger(GenerateLogger.class);
    public static void main(String[] args) throws Exception{

        int i =0;
        while (true){
            logger.info("value: "+i++);
            Thread.sleep(1000);
        }
    }
}

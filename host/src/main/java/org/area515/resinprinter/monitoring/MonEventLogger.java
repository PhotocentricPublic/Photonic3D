package org.area515.resinprinter.monitoring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

enum GCodeCmdType {
    JobStart,
    JobFinish,
    LedOn,
    LedOff,
    NotSet
  }
public class MonEventLogger {
    private static final Logger logger = LogManager.getLogger();
    private static MonEventLogger INSTANCE= null;
    private static MonDataStore MONDATASTORE=MonDataStore.Instance();
    
    private static long s_startTime=0;
	
	public static MonEventLogger Instance() {
        logger.info("MonEventLogger INSTANCE:");
		if (INSTANCE == null) {
			INSTANCE = new MonEventLogger();
		}
		return INSTANCE;
    }
    private MonEventLogger() {

        logger.info("MonEventLogger constructor:");
    }

    public void cancelJob(){
        this.processCancelJob();

    }

    public void addCmdEvt(String cmd)  {
       
        
        GCodeCmdType cmdType=this.getCommandType(cmd);
        this.interpretCommand(cmdType,cmd);
        
        logger.info("MonEventLogger cmdType:{}", cmdType);
		
		
		//logger.debug("Stopped printer:{}", printer);
    }
    public GCodeCmdType getCommandType( String cmd)  {
        GCodeCmdType gcodeType=GCodeCmdType.NotSet;

        logger.info("MonEventLogger getCommandType in:{}", cmd);

        if      (cmd.contains("!Mon##LifeCounterStart")){//";!Mon##LifeCounterStart"))
            gcodeType=GCodeCmdType.JobStart;
            logger.info("fOUND !Mon##LifeCounterStart:", cmd);
        }
        else if (cmd.contains("!Mon##JobEnd")){//";!Mon##JobEnd
            logger.info("MonEventLogger getCommandType is job end");
            gcodeType=GCodeCmdType.JobFinish;
        }
        else if (this.robustGCodeEquals(cmd,"M42 P0 S1")) {
            gcodeType=GCodeCmdType.LedOn;
            logger.info("MonEventLogger gcodeType..LedOn.:{}", gcodeType);
        }
        else if (this.robustGCodeEquals(cmd,"M42 P0 S0")) {
            gcodeType=GCodeCmdType.LedOff;
            logger.info("MonEventLogger gcodeType..LedOff.:{}", gcodeType);
        }
        else{

            logger.info("MonEventLogger else...:{}", cmd);
        }


        //String rtn="dd";
        return gcodeType;
        
    }

    private boolean robustGCodeEquals(String rawGcodeString, String testGcodeString)// M42 P0 S1 ; LED dim to zero=>  M42 P0 S1
    {
        logger.info("MonEventLogger rawGcodeString in:{}", rawGcodeString);
        logger.info("MonEventLogger testGcodeString in:{}", testGcodeString);
        boolean rtn=false;
        //Split test string into g-code elements - with no white space
        String[] matchArray=stringArrayFromString(testGcodeString);

        String[] testeeArray=stringArrayFromString(rawGcodeString);
        testeeArray= removeCharFromFromStringArray(testeeArray, ";");

        // Find each element in matchArray in testeeArray - as long as order matches - accept as equal
        Integer iFoundCount=0;
        Integer testeeIndex=0;
        for (int i = 0; i < matchArray.length; i++){
            String strToTest =matchArray[i];
             logger.info("   strToTest :{}", strToTest);
              logger.info("   testeeIndex :{}", testeeIndex);
            for (int j = testeeIndex; j < testeeArray.length; j++){
                  logger.info("   testeeArray[j] :{}", testeeArray[j]);
                if (strToTest.equals(testeeArray[j]))
                {
                    iFoundCount++;
                    logger.info("   FOUND iFoundCount++; :{}", iFoundCount);
                    
                    testeeIndex=j+1;
                       logger.info("   FOUND testeeIndex; :{}", testeeIndex);
                    break;
                } 
            }
        }
       
        if (iFoundCount==matchArray.length){
              logger.info("  iFoundCountout {} matchArray.length; :{}",iFoundCount,matchArray.length );
            rtn=true;
        }
        logger.info("  ======out  RTN; :{}", rtn);
        return rtn;
    }
    private String[] stringArrayFromString(String iString){
        String[] tempArray;
        String delimiter = " ";
        tempArray = iString.split(delimiter);

        for (int i = 0; i < tempArray.length; i++){
            System.out.println(tempArray[i]);
            tempArray[i]=tempArray[i].trim();
        }

        return tempArray;
    }

     private String[] removeCharFromFromStringArray(String[] strArray, String removeChar){
      
      
        for (int i = 0; i < strArray.length; i++){
            String elem=strArray[i];
            elem = elem.replace(removeChar, "");
            strArray[i] = elem;
        }

        return strArray;
    }
    
    public void interpretCommand(GCodeCmdType cmdType, String cmd)  {

        switch (cmdType)
        {
            case JobStart : {
                this.processJobStart(); break;
            }
            case LedOn : this.processLEDOn(); break;
            case LedOff: this.processLEDOff(); break;
            case JobFinish: this.processJobFinish(); break;
            default:  logger.info("interpretCommand : default ie error ");
        }
        
    }
    private void processJobStart(){
    // write to file - 
        s_startTime = System.currentTimeMillis();
        MONDATASTORE.StartedPrint();
        logger.info("processJobStart {}: ", s_startTime);
    }
    private void processLEDOn(){
  // write to file - 
        s_startTime = System.currentTimeMillis();
        logger.info("processLedOn  s_startTime{}: ", s_startTime);
    }

    private void processLEDOff(){
        logger.info("processLedOff ");
        logger.info("processLEDOff  s_startTime {}",s_startTime);
        long timenow = System.currentTimeMillis();
        logger.info("processLEDOff  timenow {}",timenow);
        long durationMs= timenow-s_startTime;
        logger.info("processLEDOff {}",durationMs);
        long durationS = (long)(durationMs*1.0/1000.0);
        logger.info("processLEDOff secs {}",durationS);

        MONDATASTORE.incrementLifeCounter(durationS);
    }
    private void processJobFinish(){
    // write to file - 
        s_startTime = System.currentTimeMillis();
        logger.info("processJobFinish {}: ", s_startTime);
        MONDATASTORE.writeOutData();
    }

    private void processCancelJob(){
        MONDATASTORE.CancelledPrint();
    }
}
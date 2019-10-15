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
    
    private static long s_startTime;
	
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

        boolean isTrue=(cmd.equals("M42 P0 S1"));

        logger.info("MonEventLogger getCommandType isTrue :{}", isTrue);

        if       (cmd.equals("; -------job start--------")){
            gcodeType=GCodeCmdType.JobStart;
        }
        if       (cmd.equals(";********** Footer End ********")){
            gcodeType=GCodeCmdType.JobFinish;
        }
        else if (cmd.equals("M42 P0 S1")) {
            gcodeType=GCodeCmdType.LedOn;
            logger.info("MonEventLogger gcodeType..LedOn.:{}", gcodeType);
        }
        else if   (cmd.equals("M42 P0 S0")) {
            gcodeType=GCodeCmdType.LedOff;
            logger.info("MonEventLogger gcodeType..LedOff.:{}", gcodeType);
        }
        else{

            logger.info("MonEventLogger else...:{}", cmd);
        }


        //String rtn="dd";
        return gcodeType;
        
    }
    
    public void interpretCommand(GCodeCmdType cmdType, String cmd)  {

        switch (cmdType)
        {
            case JobStart : this.processJobStart(); break;
            case LedOn : this.processLEDOn(); break;
            case LedOff: this.processLEDOff(); break;
            case JobFinish: this.processJobFinish(); break;


        }
        // if (cmdType.equals(GCodeCmdType.JobStart))
        // {
        // }
        // else if (cmdType.equals(GCodeCmdType.LedOn))
        // {
        //     this.processLEDOn();
        // } 
        // else if (cmdType.equals(GCodeCmdType.LedOff)){
        //     this.processLEDOff();
        // }
        // else if (cmdType.equals(GCodeCmdType.JobStart)){

        // }
        // else{
        //     //error
        // }

        
    }
    private void processJobStart(){
    // write to file - 
        s_startTime = System.currentTimeMillis();
        logger.info("processJobStart {}: ", s_startTime);
    }
    private void processLEDOn(){
  // write to file - 
        s_startTime = System.currentTimeMillis();
        logger.info("processLedOn {}: ", s_startTime);
    }

    private void processLEDOff(){
        logger.info("processLedOff ");
        logger.info("processLedOn  s_startTime {}",s_startTime);
        long timenow = System.currentTimeMillis();
        logger.info("processLedOn  timenow {}",timenow);
        long durationMs= timenow-s_startTime;
        logger.info("processLedOn {}",durationMs);
        double durationS = durationMs*1.0/1000.0;
        logger.info("processLedOn secs {}",durationS);

        MonDataStore MonDS=MonDataStore.Instance();

        MonDS.incrementLifeCounter(durationS);
    }
    private void processJobFinish(){
        // write to file - 
            s_startTime = System.currentTimeMillis();
            logger.info("processJobFinish {}: ", s_startTime);
        }

    private void processCancelJob()
    {
        MonDataStore MonDS=MonDataStore.Instance();
        MonDS.incrCancelledCount();
    }
}
package org.area515.resinprinter.monitoring;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//Path stuff

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.area515.resinprinter.server.HostProperties;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import java.io.File;



public class MonDataStore {
    private static final Logger logger = LogManager.getLogger();
    private static MonDataStore INSTANCE;
    public static final String MONDATAFILE="mondata.json";

    private static int completedPrintCount=0;
    private static int startedPrintCount=0;

    private static JSONObject mJsonMonData=null;
    private static long mStartPrintTime=0;
	
	public static MonDataStore Instance() {
		if (INSTANCE == null) {
			INSTANCE = new MonDataStore();
		}
		return INSTANCE;
    }
    private MonDataStore() {
        // Read in data
        JSONObject errObj = new JSONObject();

        mJsonMonData= this.readInData();
        logger.info("MonDataStore: {}", mJsonMonData.toJSONString());

        if (mJsonMonData==null){
            mJsonMonData=new JSONObject();
            mJsonMonData.put("err", "filenotopened");
        }
    } 
    public void StartedPrint(){
        mStartPrintTime=0;
        mStartPrintTime = System.currentTimeMillis();

    }
    public void FinishedPrint(){
        long printTimeEnd = System.currentTimeMillis();
        long totalprinttime=printTimeEnd-mStartPrintTime;
        mStartPrintTime=0;
        //mJsonMonData.put("totalprinttime", newTotalTime);//TODO - how many to keep
    }
    public void CancelledPrint(){
        logger.info("CancelledPrint IN: ");
        this.incrCancelledCount();
        logger.info("CancelledPrint 2: ");
        //Save the monitoring data
        this.writeOutData();
        logger.info("after writeOutData 2: ");
    }

    public void writeOutData(){
        logger.info("MonDataStore::writeOutData  in  ");
        File monFile = new File(HostProperties.Instance().getMonitoringDir(), MONDATAFILE);
      
        try (FileWriter file = new FileWriter(monFile)) {
 
            file.append(mJsonMonData.toJSONString());
            file.flush();
            file.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void incrCancelledCount(){
        if ((String)mJsonMonData.get("err")!="err"){
            long cancelledCount= (long)mJsonMonData.get("cancelled_count");
            ++cancelledCount;
            logger.info("incrCancelledCount -cancelledCount: {}", cancelledCount);

            mJsonMonData.put("cancelled_count",cancelledCount);
            logger.info("cancelledCount 2: {}", cancelledCount);
        }
    }

    public void incrementLifeCounter(long deltaTime){

        logger.info("incrementLifeCounter: mJsonMonData {}",mJsonMonData);
    
        try{
            logger.info("incrementLifeCounter A");
            //int xx=mJsonMonData.get("ledlifespent");
            logger.info("incrementLifeCounter deltaTime: {}",deltaTime);

            logger.info("incrementLifeCounter raw  mJsonMonData: {} ",mJsonMonData);

            long totalTimeUsedSoFar=(long)mJsonMonData.get("ledlifespent");

            logger.info("incrementLifeCounter object: {}",0);
            logger.info("incrementLifeCounter totalTimeUsedSoFar: {}",totalTimeUsedSoFar);
    
            long newTotalTime=totalTimeUsedSoFar+deltaTime;

            mJsonMonData.put("ledlifespent", newTotalTime);
            logger.info("   newTotalTime: {}",newTotalTime);
        }
        catch( Exception e){
            logger.info("Error=============================={}",e.getMessage());
            e.printStackTrace();
        }

    }

    private JSONObject readInData()
    {
        File monFile = new File(HostProperties.Instance().getMonitoringDir(), MONDATAFILE);
        JSONParser jsonParser = new JSONParser();
        JSONObject monDataIn=null;
        try (FileReader reader = new FileReader(monFile))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            monDataIn = (JSONObject) obj;
            reader.close();
 
        } catch (FileNotFoundException e) {
            logger.info("MonDataStore::readInData  FileNotFoundException ");
            //e.printStackTrace();
            monDataIn=this.CreateDataSchmema();// Important - create new schema
        } catch (IOException e) {
            logger.info("MonDataStore::readInData  IOException ");
            e.printStackTrace();
        } catch (ParseException e) {
            logger.info("MonDataStore::readInData  ParseException ");
            e.printStackTrace();
        }
        logger.info("MonDataStore::readInData  successful ");
        return monDataIn;
    }

    

    private JSONObject CreateDataSchmema()
    {
        logger.info("MonDataStore::CreateDataSchmema  in  ");
        JSONObject jsonSchemaobj = new JSONObject();
        long initZero=0;
        jsonSchemaobj.put("ledlifespent", initZero);
         long initZero2=0;
        jsonSchemaobj.put("cancelled_count", initZero2);
        long version=0;
        jsonSchemaobj.put("version", version);
        logger.info("::  successful: {} ",jsonSchemaobj);
        return jsonSchemaobj;
    }
 
}

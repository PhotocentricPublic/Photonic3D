package org.area515.resinprinter.monitoring;

import org.area515.resinprinter.server.HostProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
//Path stuff

//import java.io.File;



public class MonDataStore {
    private static final Logger logger = LogManager.getLogger();
    private static MonDataStore INSTANCE;
    public static final String MONDATAFILE="mondata.json";

    private static int completedPrintCount=0;
    private static int startedPrintCount=0;

    private static JSONObject mJsonMonData=null;
	
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

        if (mJsonMonData==null){
            mJsonMonData=new JSONObject();
            mJsonMonData.put("err", "filenotopened");
        }
    } 
    public void StartedPrint(){


    }
    public void FinishedPrint(){

        
    }
    public void CancelledPrint(){

        this.incrCancelledCount();
        //Save the monitoring data
        this.writeOutData();
    }
    public void incrCancelledCount(){
        if ((String)mJsonMonData.get("err")!="err"){
            int cancelledCount= (int)mJsonMonData.get("cancelled_count");
            cancelledCount++;
            logger.info("cancelledCount: {}", cancelledCount);

            mJsonMonData.put("cancelled_count",cancelledCount);
        }
    }

    public void incrementLifeCounter(double deltaTime){

        File monFile = new File(HostProperties.Instance().getMonitoringDir(), MONDATAFILE);

        logger.info("incrementLifeCounter: pathj {}",monFile);
        
        JSONParser jsonParser = new JSONParser();
        double totalTimeUsedSoFar=-1.0;
        try (FileReader reader = new FileReader(monFile))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject monDataIn = (JSONObject) obj;
            totalTimeUsedSoFar = (double)monDataIn.get("ledlifespent");
            System.out.println(totalTimeUsedSoFar);

            reader.close();
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Write JSON file

        JSONObject monData = new JSONObject();

        double newTotalTime=totalTimeUsedSoFar+deltaTime;

        monData.put("ledlifespent", newTotalTime);

        logger.info("incrementLifeCounter monData :{}", monData);
        
        try (FileWriter file = new FileWriter(monFile)) {
 
            file.append(monData.toJSONString());
            file.flush();
            file.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JSONObject readInData()
    {
        File monFile = new File(HostProperties.Instance().getMonitoringDir(), MONDATAFILE);
        JSONParser jsonParser = new JSONParser();
        double totalTimeUsedSoFar=-1.0;
        JSONObject monDataIn=null;
        try (FileReader reader = new FileReader(monFile))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            monDataIn = (JSONObject) obj;
            //totalTimeUsedSoFar = (double)monDataIn.get("ledlifespent");
           // System.out.println(totalTimeUsedSoFar);

            reader.close();
 
        } catch (FileNotFoundException e) {
            logger.info("MonDataStore::readInData  FileNotFoundException ");
            e.printStackTrace();
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

    private void writeOutData(){
        File monFile = new File(HostProperties.Instance().getMonitoringDir(), MONDATAFILE);

        try (FileWriter file = new FileWriter(monFile)) {
 
            file.append(mJsonMonData.toJSONString());
            file.flush();
            file.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JSONObject CreateDataSchmema()
    {
       
        JSONObject jsonSchemaobj = new JSONObject();
        jsonSchemaobj.put("ledlifespent", -1);
        jsonSchemaobj.put("cancelled_count", -1);
        logger.info("CreateDataSchmema::  successful ");
        return jsonSchemaobj;
    }
 
}

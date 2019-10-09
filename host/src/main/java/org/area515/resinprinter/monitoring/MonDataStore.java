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

import java.io.File;



public class MonDataStore {
    private static final Logger logger = LogManager.getLogger();
    private static MonDataStore INSTANCE;
    public static final String MONDATAFILE="mondata.json";
	
	public static MonDataStore Instance() {
		if (INSTANCE == null) {
			INSTANCE = new MonDataStore();
		}
		return INSTANCE;
    }
    private MonDataStore() {

    } 

    public void incrementLifeCounter(double deltaTime){


        //String path=HostProperties.Instance().getMonitoringDir()+"\\"+MONDATAFILE;
        //Path ppath = FileSystems.getDefault().getPath("path", MONDATAFILE);

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

}

package org.area515.resinprinter.monitoring;

import org.area515.resinprinter.server.HostProperties; 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import org.json.JSONObject;


import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;



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

       

        String path=HostProperties.Instance().getMonitoringDir()+MONDATAFILE;
        logger.info("processLedOn: pathj {}",path);
        
        JSONParser jsonParser = new JSONParser();
        double totalTimeUsedSoFar=-1.0;
        try (FileReader reader = new FileReader(path))
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

        logger.info("incrementLifeCounter monData :{}", monData);

        double newTotalTime=totalTimeUsedSoFar+deltaTime;

        monData.put("ledlifespent", newTotalTime);
        
        try (FileWriter file = new FileWriter(path)) {
 
            file.append(monData.toJSONString());
            file.flush();
            file.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

// public StreamingOutput downloadPrintableFile(@PathParam("filename")String fileName) {
//     return new StreamingOutput() {
//         @Override
//         public void write(OutputStream output) throws IOException, WebApplicationException {
//             InputStream stream = new FileInputStream(new File(HostProperties.Instance().getUploadDir(), fileName));
//             try {
//                 ByteStreams.copy(stream, output);
//             } finally {
//                 try {
//                     stream.close();
//                 } catch (IOException e) {}
//             }
//         }
//     };
// }
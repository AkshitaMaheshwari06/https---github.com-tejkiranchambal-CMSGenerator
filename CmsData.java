package org.openskye;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CmsData {

    public static void main(String[] args) throws Exception {

        Object ConfigPath = new Object();
        ConfigPath = new JSONParser().parse(new FileReader("ui\\ConfigCMSData.json"));
        JSONObject jo = (JSONObject) ConfigPath;

        String Paths = (String) jo.get("FilePath");
        String DestinationPath = (String) jo.get("Destinationpath");
        String NumberOfFiles = (String) jo.get("NumberOfFiles");

        File sf = new File(Paths);

        String str = sf.getName();
        String array[] = str.split("[- _.]+");

        String tcm = array[0];
        String three = array[1];
        String four = array[2];
        String page = array[3];
        String date = array[4];
        String sequence = array[5];

        int s = Integer.parseInt(three);
        int b = Integer.parseInt(four);
        int c = Integer.parseInt(sequence);
        Random objGenerator = new Random();

        for (int iCount = 0; iCount < Integer.parseInt(NumberOfFiles); iCount++) {
            {

                int minRange = 100, maxRange = 999;
                int randomNumber = objGenerator.nextInt(maxRange - minRange) + minRange;
                three = Integer.toString(randomNumber);
            }
            {
                int minRange = 1000, maxRange = 9999;


                int fournumber = objGenerator.nextInt(maxRange - minRange) + minRange;

                four = Integer.toString(fournumber);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date dates = new Date(System.currentTimeMillis());
            {
                int minRange = 100, maxRange = 999;
                int sequencenumber = objGenerator.nextInt(maxRange - minRange) + minRange;

                sequence = Integer.toString(sequencenumber);
            }
            String anc = ("tcm-" + three + "-" + four + "-" + page + "_" + formatter.format(dates) + "_" + sequence + ".cms");
            System.out.println(anc);
            File dest =
                    new File(DestinationPath + anc);

           Files.copy(sf.toPath(), dest.toPath());

                Object obj = new Object();
                obj = new JSONParser().parse(new FileReader(dest));
                JSONObject jon = (JSONObject) obj;

                  String ID = (String) jon.get("Id");

                //System.out.println(ID);
                 String Name = (String) jon.get("Name");
               jon.put("Name", "akshita");
               JSONArray employeeList = new JSONArray();
               employeeList.add(jon);

            FileWriter file = new FileWriter(dest,true);
            file.write(employeeList.toJSONString());


            Map<String, Object> properties = new HashMap<>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);

            JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
            JsonWriter jsonWriter = writerFactory.createWriter(file);
            jsonWriter.writeObject((JsonObject) jon);
            // jsonWriter.writeObject(jsonObject);
            jsonWriter.close();
            file.close();







               // file.flush();
             /*   BufferedReader reader;

                    reader = new BufferedReader(new FileReader(
                            "C:\\Users\\admin\\Desktop\\new6.txt"));
                    String line = reader.readLine();
                    while (line != null) {
                        //System.out.println(line);
                        jon.remove(Name);
                        jon.put("Name", line);
                        line = reader.readLine();
                        // read next line
                    }


                    reader.close();*/
                /*BufferedReader reader;
                reader = new BufferedReader(new FileReader(
                        "C:\\Users\\admin\\Desktop\\new6.txt"));
                String line = reader.readLine();*/
               /* BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
                String id = (String) jon.get("Id");
                jon.replace("Id",id,anc);
                jon.put("Id",anc);*/


                //writer.write(id);
                //System.out.println(a);
                /* jon.remove("Name",Names);
                jon.put("Name", line);
                writer.write(line);*/
                //writer.close();
                //System.out.println(Name);
              /*  File file = new File("C:\\Users\\admin\\Desktop\\new6.txt");
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;

                while ((st = br.readLine()) != null) {
                    Object Names=new Object();
                    //System.out.println(st);
                    jon.remove("Name");
                    jon.put("Name", st);


                }*/

              /*  String FileName = (String) jon.get("FileName");
                JSONObject SystemMetadata = (JSONObject) jon.get("SystemMetadata");
                String Location = (String) SystemMetadata.get("Location");
                //System.out.println(Location);
                String ItemUri = (String) SystemMetadata.get("ItemUri");
                // System.out.println(ItemUri);
                String Version = (String) SystemMetadata.get("Version");
                // System.out.println(Version);
            } catch (IOException ex) {
                System.out.println("Exception = " + ex);
            }
*/

        }
        System.out.println("Success");
    }
}
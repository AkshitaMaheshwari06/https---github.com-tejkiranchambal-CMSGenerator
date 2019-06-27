/*
package org.openskye;


import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.openskye.TemplateJsonReader.JSON_FILE;
import static org.openskye.TemplateJsonReader.Srcrandomlist;

public class TemplateJsonWriter1 {
    private static String  anc1 = null;
    private static String  anc2 = null;
    private static String  anc3 = null;
    private static String  anc4 = null;
    private static String  anc6 = null;

    File OutputSrcFile=null;

    private static final String dest = "C:\\Users\\Admin\\Desktop\\Destinationpath\\";
  public  TemplateJsonWriter1() throws IOException, ParseException, JSONException {
      for(int i=0; i<1;i++){
          File aks=randomlist();
          File aksh=Srcrandomlist();
          String outputs=myfunction();
          String filename = dest + outputs + myfunctions();
          File file = new File(filename);
         // FileUtils.copyFile(aksh, file);
          try {
              file.createNewFile();
          } catch (IOException e) {
              e.printStackTrace();
          }
          File OutputSrcFile = new File("C:\\aks_area\\CMS_DATA_1.28_WF\\Batch1\\Output_files\\" +aks);
          File OutputDestFile =new File ("C:\\Users\\Admin\\Desktop\\output\\" +outputs +setJSONFileNames(OutputSrcFile));
          OutputDestFile.createNewFile();
          FileUtils.copyFile(aks, OutputDestFile);


          JSONParser parser = new JSONParser();
          Object object = parser.parse(new FileReader(JSON_FILE));
          JsonReader jsonReader = Json.createReader((InputStream) object);
         // JsonObject jsonObject = jsonReader.readObject() ;
          JSONObject jsonObject = (JSONObject) object;
          JsonFileTemplate jsonFileTemplate =   createJsonTemplate(filename);
          JsonObjectBuilder empBuilder = Json.createObjectBuilder();
          JsonObjectBuilder SystemMetadataBuilder = Json.createObjectBuilder();
          SystemMetadataBuilder.add("Location", jsonFileTemplate.getSystemMetadata().getLocation()  )
                  //.add("FromPublication", jsonFileTemplate.getSystemMetadata().getFromPublication())
                  .add("ItemUri", jsonFileTemplate.getSystemMetadata().getItemUri())
                  .add("Version", jsonFileTemplate.getSystemMetadata().getVersion());

          empBuilder.add("Id", jsonFileTemplate.getId())
                  .add("Name", jsonFileTemplate.getName())
                  .add("FileName", jsonFileTemplate.getFileName());



          empBuilder.add("SystemMetadata", SystemMetadataBuilder);
          JsonObject empJsonObject = empBuilder.build();
          //JSONObject jsonObj = new JSONObject(jsonObject);
         JSONArray employeeList = new JSONArray();
          employeeList.add(empJsonObject);
          employeeList.add(jsonObject);


          try {
              Map<String, Object> properties = new HashMap<>(1);
              properties.put(JsonGenerator.PRETTY_PRINTING, true);
              FileWriter fw = new FileWriter(file, true);
              fw.write(employeeList.toJSONString());
              JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
              JsonWriter jsonWriter = writerFactory.createWriter(fw);
              jsonWriter.writeObject((JsonObject) employeeList);
              //jsonWriter.writeObject((JsonObject) jsonObject);
             // jsonWriter.writeObject(jsonObject);
              jsonWriter.close();
              fw.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
          System.out.println("Success");
      }
    }

    private File randomlist() {
        Random rand = new Random();
        File[] files = new File("C:\\aks_area\\CMS_DATA_1.28_WF\\Batch1\\Output_files\\" ).listFiles();
        File file = files[ rand.nextInt( files.length ) ];

      return file;
    }
    public static void main(String[] args) throws IOException, ParseException, JSONException {
        TemplateJsonWriter1 templateJsonWriter1 = new TemplateJsonWriter1();

    }
    private String setJSONFileName(String filename) {
        File sf = new File(String.valueOf(filename));
        String str = sf.getName();
        String array[] = str.split("[- _.]+");
        String tcm = array[0];
        String three = array[1];
        String four = array[2];
        String page = array[3];
        String date = array[4];
        String sequence = array[5];
        anc1 = (three + "-" + four + "-" + page);
        return anc1;
    }
    private String setJSONFileNames(File OutputSrcFile) {
        File sf = new File(String.valueOf(OutputSrcFile));

        String str = sf.getName();
        String array[] = str.split("[- _.]+");
        String tcm = array[0];
        String three = array[1];
        String four = array[2];
        String page = array[3];
        String date = array[4];
        String sequence = array[5];
        String type = array[6];
        anc3 = type;
        return anc3;
    }

*/
/*    private String setJSONFileNamess(File OutputDestFile) {
        File sf = new File(String.valueOf(OutputDestFile));
        SimpleDateFormat formatter = null;
        Date dates = null;
        String str = sf.getName();
        String array[] = str.split("[- _.]+");
        {
            formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            dates = new Date(System.currentTimeMillis());
        }
        String tcm = array[0];
        String three = array[1];
        String four = array[2];
        String page = array[3];
        String date = array[4];
        String sequence = array[5];
        String type = array[6];

        //anc4=setJSONFileNames(OutputSrcFile);
        String anc5 = ("tcm-" + three + "-" + four + "-" + page + "_" + formatter.format(dates) + "_" + sequence + setJSONFileNames(OutputSrcFile));
        return anc5;
    }*//*

    private static String myfunctions() {
        File sf = new File(SOURCE_JSON_FILE);

        String str = sf.getName();
        String array[] = str.split("[- _.]+");

        String tcm = array[0];
        String three = array[1];
        String four = array[2];
        String page = array[3];
        String date = array[4];
        String sequence = array[5];
        String type = array[6];
        anc6 = type;
        return anc6;
    }
    private static String myfunction(){
        File sf = new File(SOURCE_JSON_FILE);
        String str = sf.getName();
        String array[] = str.split("[- _.]+");
        String tcm = array[0];
        String three = array[1];
        String four = array[2];
        String page = array[3];
        String date = array[4];
        String sequence = array[5];
        int b = Integer.parseInt(four);
        int c = Integer.parseInt(sequence);
        Random objGenerator = new Random();
        SimpleDateFormat formatter = null;Date dates = null;
        for (int iCount = 0; iCount < 10; iCount++) {
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
            {
                formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                dates = new Date(System.currentTimeMillis());
            }

            {
                int minRange = 100, maxRange = 999;
                int sequencenumber = objGenerator.nextInt(maxRange - minRange) + minRange;

                sequence = Integer.toString(sequencenumber);
            }

        }

        String anc = ("tcm-" + three + "-" + four + "-" + page + "_" + formatter.format(dates) + "_" + sequence +".");
        System.out.println(anc);

        return anc;
    }
    public  JsonFileTemplate createJsonTemplate(String filename) throws IOException, ParseException, JSONException {
        //JSONObject jsonObject = (JSONObject) object;
        JsonFileTemplate jsonFileTemplate = new JsonFileTemplate(Srcrandomlist().getPath());

      String version=  CmsVersion();
        jsonFileTemplate.setId(setJSONFileName(filename) + "-" + version);
        jsonFileTemplate.setName(CmsGetName());
        jsonFileTemplate.setFileName(jsonFileTemplate.getName() );
        SystemMetadata add = new SystemMetadata();
        add.setLocation(CmsLocation() );
       // add.setFromPublication();
        add.setItemUri(anc1);
        add.setVersion(version);
        jsonFileTemplate.setSystemMetadata(add);
        return jsonFileTemplate;


    }
    public static String CmsGetName() throws IOException {
        int totalLines = 0;
        File cmsname = new File("C:\\Users\\Admin\\Desktop\\cmsname.txt");

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(cmsname));

            while ((br.readLine()) != null) {
                totalLines++;
            }
            br.close();

            br = new BufferedReader(new FileReader(cmsname));

            Random random = new Random();
            int randomInt = random.nextInt(totalLines);
            int count=0;
            String icaocode;
            while ( (icaocode = br.readLine()) != null) {
                if (count == randomInt) {
                    br.close();
                    return icaocode;
                }
                count++;
            }
            br.close();


        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + cmsname.toString());
        } catch (IOException e) {
            System.out.println("Unable to read file: " + cmsname.toString());
        }

        return "Exit";

    }
    public static String CmsLocation() throws IOException {
        int totalLines = 0;
        File file = new File("C:\\Users\\Admin\\Desktop\\Locatiom.txt");

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));

            while ((br.readLine()) != null) {
                totalLines++;
            }
            br.close();

            br = new BufferedReader(new FileReader(file));

            Random random = new Random();
            int randomInt = random.nextInt(totalLines);
            int count=0;
            String icaocode;
            while ( (icaocode = br.readLine()) != null) {
                if (count == randomInt) {
                    br.close();
                    return icaocode;
                }
                count++;
            }
            br.close();


        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.toString());
        } catch (IOException e) {
            System.out.println("Unable to read file: " + file.toString());
        }

        return "Exit";

    }


    public static String CmsVersion() throws IOException {
        int totalLines = 0;
        File file = new File("C:\\Users\\Admin\\Desktop\\cmsversion.txt");

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));

            while ((br.readLine()) != null) {
                totalLines++;
            }
            br.close();

            br = new BufferedReader(new FileReader(file));

            Random random = new Random();
            int randomInt = random.nextInt(totalLines);
            int count=0;
            String icaocode;
            while ( (icaocode = br.readLine()) != null) {
                if (count == randomInt) {
                    br.close();
                    anc2=icaocode;
                    return anc2;
                }
                count++;
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.toString());
        } catch (IOException e) {
            System.out.println("Unable to read file: " + file.toString());
        }

        return "Exit";

    }
}
*/

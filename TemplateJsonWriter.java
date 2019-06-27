package org.openskye;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static org.openskye.TemplateJsonReader.randompick_CmsSrcFile;

public class TemplateJsonWriter {
    private static String ItemUri = null;
    private static String Version = null;
    private static String Output_Type = null;
    private static String Cms_Type = null;
    File OutputSrcFile = null;
    static TemplateJsonReader templateJsonReader = new TemplateJsonReader();


    private static final String destination_Path = "C:\\Users\\Admin\\Desktop\\Destinationpath\\";

    public TemplateJsonWriter() throws IOException, ParseException, JSONException, java.text.ParseException {
        Object ConfigPath = new Object();
        ConfigPath = new JSONParser().parse(new FileReader("ui\\ConfigCMSData.json"));
        org.json.simple.JSONObject jo = (org.json.simple.JSONObject) ConfigPath;

        String SourcePaths = (String) jo.get("SourceFilePath");
        String DestinationPath = (String) jo.get("Destinationpath");
        String NumberOfFiles = (String) jo.get("OutputSourceFilepath");
        String OutputSrcFile = (String) jo.get("OutputDestinationFilepath");
        String OutputDestSrcFile = (String) jo.get("OutputDestinationFilepath");

        for (int i = 0; i < 12; i++) {
            templateJsonReader.setSOURCE_JSON_FILE(randompick_CmsSrcFile());
            File random_OutputFile = randompick_Outputfile();
            String FileNames = Cms_FileName();
            String Cms_File_Name = destination_Path + FileNames + cms_Type();
            File file = new File(Cms_File_Name);
            templateJsonReader.setDESTINATION_JSON_FILE(file);
            JsonFileTemplate jsonFileTemplate = templateJsonReader.getSourceFromInputFile();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File OutputSourceFile = new File(OutputSrcFile + random_OutputFile);
            File OutputDestFile = new File(OutputDestSrcFile + FileNames + setJSONFileNames(OutputSourceFile));

            OutputDestFile.createNewFile();
            FileUtils.copyFile(random_OutputFile, OutputDestFile);


            try (FileWriter fw = new FileWriter(file, true);) {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser jp = new JsonParser();
                JsonElement je = jp.parse(jsonFileTemplate.getJsonObject().toString());
                String prettyJsonString = gson.toJson(je);
                fw.write(prettyJsonString);
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Success");
        }
    }

    private File randompick_Outputfile() {
        Random rand = new Random();
        File[] files = new File("C:\\aks_area\\CMS_DATA_1.28_WF\\Batch1\\Output_files\\").listFiles();
        File file = files[rand.nextInt(files.length)];
        return file;
    }

    public static void main(String[] args) throws IOException, ParseException, JSONException, java.text.ParseException {
        TemplateJsonWriter templateJsonWriter = new TemplateJsonWriter();
    }
        /*char operator;
        String result;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter operator (either +, -):");
        operator = scanner.next().charAt(0);
      // number1 = scanner.nextDouble();
        //number2 = scanner.nextDouble();

        switch (operator) {
            case '+':
                result = Cms_FileName_Sequence();
                break;
            case '-':
                result = Cms_FileName();
                break;
            default:
                System.out.println("Invalid operator!");
                break;
        }
    }
*/

       /* Scanner sc = new Scanner(System.in); // object for scanner
        System.out.println("which type of file do you want to create");
        int no = sc.nextInt();
        String sequence=Cms_FileName_Sequence();


        // similiarli Float,Double can be added to it as per the data type.
        System.out.printl("you entered : "+no);*/


    public static String setJSONFileName(String filename) {

        String array[] = filename.split("[- _.]+");
        String tcm = array[0];
        String three = array[1];
        String four = array[2];
        String page = array[3];
        String date = array[4];
        String sequence = array[5];
        ItemUri = (three + "-" + four + "-" + page);
        return ItemUri;
    }

    public static String setcomponent() {
        File sf = templateJsonReader.getSOURCE_JSON_FILE();
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
        SimpleDateFormat formatter = null;
        Date dates = null;
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

        String FileNameCms = ("tcm-" + three + "-" + four);
        System.out.println(FileNameCms);
        return FileNameCms;
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
        Output_Type = type;
        return Output_Type;
    }

    private String cms_Type() {
        File sf = templateJsonReader.getSOURCE_JSON_FILE();
        String str = sf.getName();
        String array[] = str.split("[- _.]+");
        String tcm = array[0];
        String three = array[1];
        String four = array[2];
        String page = array[3];
        String date = array[4];
        String sequence = array[5];
        String type = array[6];
        Cms_Type = type;
        return Cms_Type;
    }

    private static String Cms_FileName() {
        File sf = templateJsonReader.getSOURCE_JSON_FILE();
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
        SimpleDateFormat formatter = null;
        Date dates = null;
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

        String FileNameCms = ("tcm-" + three + "-" + four + "-" + page + "_" + formatter.format(dates) + "_" + sequence + ".");
        System.out.println(FileNameCms);
        return FileNameCms;
    }


    public static String Create_BundleId() {
        File sf = templateJsonReader.getSOURCE_JSON_FILE();
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
        SimpleDateFormat formatter = null;
        Date dates = null;
        {

            int minRange = 10, maxRange = 99;
            int randomNumber = objGenerator.nextInt(maxRange - minRange) + minRange;
            three = Integer.toString(randomNumber);
        }
        {
            int minRange = 100000, maxRange = 999999;
            int fournumber = objGenerator.nextInt(maxRange - minRange) + minRange;
            four = Integer.toString(fournumber);
        }

        {
            int minRange = 1000, maxRange = 9999;
            int sequencenumber = objGenerator.nextInt(maxRange - minRange) + minRange;

            sequence = Integer.toString(sequencenumber);
        }

        String FileNameCms = ("tcm:" + three + "-" + four + "-" + sequence );
        System.out.println(FileNameCms);
        return FileNameCms;
    }

    public static String Create_ApprovalId() {
        Random objGenerator = new Random();
        String three;

            int minRange = 100, maxRange = 9999;
            int randomNumber = objGenerator.nextInt(maxRange - minRange) + minRange;
            three = Integer.toString(randomNumber);
            return three;
        }


    private static String Cms_FileName_Sequence() {
        File sf = templateJsonReader.getSOURCE_JSON_FILE();
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
        SimpleDateFormat formatter = null;
        Date dates = null;
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

        String FileNameCms = ( formatter.format(dates) + "_" + sequence + ".");
        System.out.println(FileNameCms);
        return FileNameCms;
    }



    public static String CmsGetName() throws IOException, ParseException {
        int totalLines = 0;
        Object ConfigPath = new Object();
        ConfigPath = new JSONParser().parse(new FileReader("ui\\ConfigCMSData.json"));
        org.json.simple.JSONObject jo = (org.json.simple.JSONObject) ConfigPath;
        String NameFieldPath = (String) jo.get("NamePath");
        File cmsname = new File(NameFieldPath);
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
            int count = 0;
            String icaocode;
            while ((icaocode = br.readLine()) != null) {
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

    public static String CmsLocation() throws IOException, ParseException {
        int totalLines = 0;
        Object ConfigPath = new Object();
        ConfigPath = new JSONParser().parse(new FileReader("ui\\ConfigCMSData.json"));
        org.json.simple.JSONObject jo = (org.json.simple.JSONObject) ConfigPath;
        String LocationPath = (String) jo.get("LocationPath");
        File file = new File(LocationPath);

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
            int count = 0;
            String icaocode;
            while ((icaocode = br.readLine()) != null) {
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

    public static String CmsCreatedDateTimes() throws IOException {

        StringBuffer stringBuffer = new StringBuffer();
        Date now = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.format(now, stringBuffer, new FieldPosition(0));

        return simpleDateFormat.format(now);
    }

    public static String CmsVersion() throws IOException, ParseException {
        int totalLines = 0;
        Object ConfigPath = new Object();
        ConfigPath = new JSONParser().parse(new FileReader("ui\\ConfigCMSData.json"));
        org.json.simple.JSONObject jo = (org.json.simple.JSONObject) ConfigPath;
        String VersionPath = (String) jo.get("VersionPath");
        File file = new File(VersionPath);

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
            int count = 0;
            String icaocode;
            while ((icaocode = br.readLine()) != null) {
                if (count == randomInt) {
                    br.close();
                    Version = icaocode;
                    return Version;
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
//package org.openskye;
//
//import org.apache.commons.io.FileUtils;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.*;
//import java.io.*;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Random;
//
//public class CMS_DataGeneration extends Random {
//    CMS_DataGeneration() {
//
//    }
//
//    CMS_DataGeneration(int s, int a, int b) {
//    }
//
//
//    public static void main(String[] args) throws Exception {
//        //Getting json file
//        Object ConfigPath = new Object();
//        ConfigPath = new JSONParser().parse(new FileReader("ui\\ConfigCMSData.json"));
//        JSONObject jo = (JSONObject) ConfigPath;
//
//        String Paths = (String) jo.get("FilePath");
//        String DestinationPath = (String) jo.get("Destinationpath");
//
//        System.out.println(DestinationPath);
//
//        //Getting filepath from json
//        Object ConfigPaths = new Object();
//        ConfigPaths = new JSONParser().parse(new FileReader(Paths));
//        JSONObject jon = (JSONObject) ConfigPaths;
//        String ID = (String) jon.get("Id");
//        System.out.println(ID);
//        String Name = (String) jon.get("Name");
//        System.out.println(Name);
//
//        String FileName = (String) jon.get("FileName");
//        System.out.println(FileName);
//
//
//        JSONObject SystemMetadata = (JSONObject) jon.get("SystemMetadata");
//        String Location = (String) SystemMetadata.get("Location");
//        System.out.println(Location);
//        String ItemUri = (String) SystemMetadata.get("ItemUri");
//        System.out.println(ItemUri);
//        String Version = (String) SystemMetadata.get("Version");
//        System.out.println(Version);
//
//        File Sourcefile = new File("C:\\aks_area\\CMS_DATA_1.28_WF\\Batch1\\CMS_files\\tcm-222-7730-64_20190108101637_444934.cms");
//        // String name = Sourcefile.getName();
//
//
//        String str = Sourcefile.getName();
//
//
//        String array[] = str.split("[- _.]+");
//
//        String tcm = array[0];
//        String three = array[1];
//        String four = array[2];
//        String page = array[3];
//        String date = array[4];
//        String sequence = array[5];
//
//        // for (int i = 0; i < array.length; i++) {
//
//
//
//
//           /* Random objGenerator = new Random();
//        for (int iCount = 0; iCount< 10; iCount++){
//            int randomNumber = objGenerator.nextInt(1000);
//            System.out.println("Random No : " + randomNumber);
//
//        }*/
//
//
//
//      /*  for (int i = 0; i < array.length; i++){
//            System.out.println(array[i]);
//        }*/
//
//       /* String[] arrOfStr = str.split("[- _.]+");
//        for (String a : arrOfStr)
//                    System.out.println(a);*/
//        int s = Integer.parseInt(three);
//        int b = Integer.parseInt(four);
//        int c = Integer.parseInt(sequence);
//        Random objGenerator = new Random();
//
//        //   String three = array[1];
//
//        for (int iCount = 0; iCount < 20; iCount++) {
//            {
//                int minRange = 100, maxRange= 999;
//                //for (n = 100; n <= 999; n++) {
//                    int randomNumber = objGenerator.nextInt(maxRange - minRange) + minRange;
//                    //System.out.println("Random No : " + randomNumber);
//                    three = Integer.toString(randomNumber);
//
//                   /* Random rand = new Random();
//
//                    int minRange = 1000, maxRange= 5000;
//                    int value = rand.nextInt(maxRange - minRange) + minRange;*/
//
//                   // System.out.println(value);
//
//                }
//
//                    for (n = 1000; n <= 9999; n++) {
//                        //  Random fours = new Random(Integer.parseInt(four));
//                        //   String three = array[1];
//                        // for (int iCount = 0; iCount< 1; iCount++){
//
//                        int fournumber = objGenerator.nextInt(n);
//                        //System.out.println("Random No : " + randomNumber);
//                        four = Integer.toString(fournumber);
//                    }
//                        //Random paged = new Random(Integer.parseInt(page));
//                        //   String three = array[1];
//                        // for (int iCount = 0; iCount< 1; iCount++){
//                        // int pagenumber = objGenerator.nextInt(64);
//                        //System.out.println("Random No : " + randomNumber);
//                        //page = Integer.toString(pagenumber);
//
//                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//                        Date dates = new Date(System.currentTimeMillis());
//
//                        // Random sequences = new Random(Integer.parseInt(sequence));
//                        //   String three = array[1];
//                        // for (int iCount = 0; iCount< 1; iCount++){
//                        for (n = 100000; n <= 999999; n++) {
//                            int sequencenumber = objGenerator.nextInt(n);
//                            //System.out.println("Random No : " + randomNumber);
//                            sequence = Integer.toString(sequencenumber);
//                        } //System.out.println();
//
//                            System.out.println("tcm-" + three + "-" + four + "-" + page + "_" + formatter.format(dates) + "_" + sequence + ".cms");
//                        }
//
//
//
//
//
///*
//
//        File destFile = new File("DestinationPath");
//        System.out.println("Copying file : " + Sourcefile.getName() +" from Java Program");
//        destFile.createNewFile();
//        FileUtils.copyFile(Sourcefile, destFile);
//        System.out.println("copying of file from Java program is completed");
//
//
//*/
//        }
//    }
//
//  //  }
///* try {
//            is = new FileInputStream(Paths);
//            os = new FileOutputStream(DestinationPath);
//            byte[] buf = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = is.read(buf)) > 0) {
//                os.write(buf,0, bytesRead);
//
//            }
//        } finally {
//
//            is.close();
//            os.close();
//        }
//*/
//        /*Public string getName()
//        {
//
//        }*/
package org.openskye;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

    public class newcms
    {
        private static final String dest = "C:\\Users\\Admin\\Desktop\\Destinationpath\\";

        public static String JSON_FILE=("C:\\aks_area\\CMS_DATA_1.28_WF\\Batch1\\CMS_files\\tcm-222-8260-64_20190110040547_445033.cms");
        @SuppressWarnings("unchecked")
        public static void main(String[] args) throws IOException, ParseException {
            //JSON parser object to parse read file
            // JSONParser jsonParser = new JSONParser();

            Object ConfigPath = new Object();
            ConfigPath = new JSONParser().parse(new FileReader(JSON_FILE));
            JSONObject jo = (JSONObject) ConfigPath;


            //  try (FileReader reader = new FileReader(SOURCE_JSON_FILE))

            //Read JSON file
            // Object obj = jsonParser.parse(reader);
//
            // JSONArray employeeList = (JSONArray) obj;
            System.out.println(jo);


            //File file = new File(filename);

            //Iterate over employee array
            // employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );


       /* private static void parseEmployeeObject(JSONObject employee) {
            //Get employee object within list
            JSONObject employeeObject = (JSONObject) employee.get("Id");

            //Get employee first name
            String Name = (String) employeeObject.get("Name");
            System.out.println(Name);

            //Get employee last name*/
          /*  String lastName = (String) employeeObject.get("lastName");
            System.out.println(lastName);

            //Get employee website name
            String website = (String) employeeObject.get("website");
            System.out.println(website);
        }*/
            //}
            // }
        }

        }

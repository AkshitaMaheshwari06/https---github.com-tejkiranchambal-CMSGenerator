package org.openskye;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.openskye.TemplateJsonWriter.*;


@Slf4j
public class TemplateJsonReader {
    @Getter
    @Setter
    public  File SOURCE_JSON_FILE;
   @Getter
   @Setter
    public File DESTINATION_JSON_FILE;

    public JsonFileTemplate getSourceFromInputFile() throws IOException, ParseException, JSONException, java.text.ParseException {

        JsonFileTemplate jsonFileTemplate = new JsonFileTemplate(SOURCE_JSON_FILE);
        JSONObject multimediaJsonObject = jsonFileTemplate.getJsonObject();
        String CmsGetName = CmsGetName();
        String CmsVersion = CmsVersion();
        multimediaJsonObject.put("Id",TemplateJsonWriter.setJSONFileName(DESTINATION_JSON_FILE.getName()) + "-" + CmsVersion);
        multimediaJsonObject.put("Name", CmsGetName);
        multimediaJsonObject.put("FileName", CmsGetName);

        JSONObject innerJsonObject = multimediaJsonObject.getJSONObject("SystemMetadata");
        SystemMetadata SystemMetadatas = new SystemMetadata();

        innerJsonObject.put("Location", CmsLocation());
        innerJsonObject.put("ItemUri", setJSONFileName(DESTINATION_JSON_FILE.getName()));
        innerJsonObject.put("Version", CmsVersion);
        innerJsonObject.put("CreatedDateTime", CmsCreatedDateTimes());
        multimediaJsonObject.put("SystemMetadata", innerJsonObject);

        String BundleId=Create_BundleId();
        JSONObject innerJsonObjectBundle = multimediaJsonObject.getJSONObject("Bundle");
        innerJsonObjectBundle.put("Id", BundleId);
        innerJsonObjectBundle.put("Name", CmsGetName);

        String ApprovalId=Create_ApprovalId();
        JSONObject Metadatajson = innerJsonObjectBundle.getJSONObject("MetaData");
        Metadatajson.put("Approval_Tracking_Id", ApprovalId);

            JSONObject SystemMetaDataJson = innerJsonObjectBundle.getJSONObject("SystemMetadata");
        SystemMetaDataJson.put("ItemUri", BundleId);
        SystemMetaDataJson.put("Name", CmsGetName);

        JSONObject innerJsonObjects = multimediaJsonObject.getJSONObject("PublishTransaction");
        innerJsonObjects.put("PublishDateTime", CmsCreatedDateTimes());

        List <String> componentTypeList = new ArrayList<>();
        componentTypeList.add("ComponentPresentations");
        componentTypeList.add("Metadata_ReferencedComponents");
        componentTypeList.add("ReferencedComponents");
        for(String componentType : componentTypeList ) {
            try {

                    if (multimediaJsonObject.getJSONArray(componentType) != null) {
                        JSONArray componentJsonArray = multimediaJsonObject.getJSONArray(componentType);
                        ComponentPresentations cp = new ComponentPresentations();
                        JSONArray cmpJsonArry = new JSONArray();
                    for (int i = 0; i < componentJsonArray.length(); i++) {
                        String ItemUri = setcomponent();
                        String Version = CmsVersion();
                        JSONObject componentJsonObject = componentJsonArray.getJSONObject(i);

                        JSONObject compoJson = componentJsonObject.getJSONObject("Component");

                        compoJson.put("Id", ItemUri + "-" + Version);
                        compoJson.put("Name", CmsGetName());
                        JSONObject systemMetadataJson = compoJson.getJSONObject("SystemMetadata");


                        systemMetadataJson.put("ItemUri", ItemUri);
                        systemMetadataJson.put("Version", Version);
                        systemMetadataJson.put("Location", CmsLocation());


                        componentJsonObject.put("Component", compoJson);
                        cmpJsonArry.put(componentJsonObject);
                    }
                    cp.getJsonArray().add(cmpJsonArry);
                }

            } catch (Exception ex) {
                log.info("Embedded components" + ex);
                continue;
            }
        }
        return jsonFileTemplate;

    }

    static File randompick_CmsSrcFile() {
        Random rand = new Random();
        File[] files = new File("C:\\aks_area\\CMS_DATA_1.28_WF\\Batch7\\").listFiles();
        File file = files[rand.nextInt(files.length)];
        return file;
    }
}



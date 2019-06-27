package org.openskye;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
public class JsonFileTemplate {

    private String Id;
    private String Name;
    private String FileName;
    private String PublicationId;
    private String Language;
    private String PageTemplate;
    private JSONObject SystemMetadata;
    private JSONObject Bundle;
    private JSONObject ComponentPresentations;
    @Getter
    @Setter
    private JSONObject jsonObject;
    public String filename;
   // private File file;
   public JsonFileTemplate() {

   }


    public JsonFileTemplate(File file) throws IOException, ParseException {

        try  {
            String jsonString = parseJSONFile(file, "UTF-8");
           // this.jsonObject = new JSONObject(jsonString);
            setJsonObject(new JSONObject(jsonString));
        } catch (Exception ex) {
            log.error("Invalid JSON object found in file : " + file, ex);

        }
    }

    public static String parseJSONFile(File file,String encoding) throws IOException, JSONException {
        try (BOMInputStream inputStream = new BOMInputStream(new FileInputStream(file))) {
            String fileContent;
            if (inputStream.hasBOM()) {
                fileContent = IOUtils.toString(inputStream, inputStream.getBOMCharsetName());
            } else {
                fileContent = IOUtils.toString(inputStream, encoding);
            }
            JSONObject jsonContent = new JSONObject(fileContent);
            return fileContent;
        } catch (Exception e) {
            throw e;
        }
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getId() throws JSONException {
        return (String) jsonObject.get("Id");
    }

    public void setId(String Id) throws JSONException {
        this.jsonObject.put("Id", Id);

    }

    public String getName() throws JSONException {
        return (String) jsonObject.get("Name");

    }

    public void setName(String Name) throws JSONException {
        this.jsonObject.put("Name", Name);
    }

    public String getFileName() throws JSONException {
        return (String) jsonObject.get("FileName");
    }

    public void setFileName(String FileName) throws JSONException {
        this.jsonObject.put("FileName", FileName);
    }
    public String getPublicationId() throws JSONException {
        return (String) jsonObject.get("PublicationId");

    }
    public void setPublicationId(String PublicationId) throws JSONException {
        this.jsonObject.put("PublicationId", PublicationId);

    }
    public String getLanguage() throws JSONException {
        return (String) jsonObject.get("Language");

    }
    public void setLanguage(String Language) throws JSONException {
        this.jsonObject.put("Language", Language);

    }
    public String getPageTemplate() throws JSONException {
        return (String) jsonObject.get("PageTemplate");


    }
    public JSONObject getBundle() {
        return Bundle;
    }

    public void setBundle(JSONObject Bundle) throws JSONException {
        this.jsonObject.put("Bundle", Bundle);

    }

    public JSONObject getSystemMetadata() {
        return SystemMetadata;
    }

    public void setSystemMetadata(JSONObject SystemMetadata) throws JSONException {
        this.jsonObject.put("SystemMetadata", SystemMetadata);

    }
    public JSONObject getComponentPresentations() {
        return ComponentPresentations;
    }
    public void setComponentPresentations(JSONArray ComponentPresentations) throws JSONException {
        this.jsonObject.put("ComponentPresentations", ComponentPresentations);

    }



}

class SystemMetadata extends JSONObject {

    private String Location;
    private String ItemUri;
    private String Version;
    private String CreatedDateTime;

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }



    public String getItemUri() {

        return ItemUri;
    }

    public void setItemUri(String ItemUri) {
        this.ItemUri = ItemUri;
    }

    public String getVersion () {
        return Version;
    }
    public void setVersion (String Version){
        this.Version =Version;
    }
    public String getCreatedDateTime () {
        return CreatedDateTime;
    }
    public void setCreatedDateTime (String CreatedDateTime){
        this.CreatedDateTime =CreatedDateTime;
    }


}


class Bundle extends JSONObject {

    private String Id;
    private String Name;
    private String ItemUri;
    private String Approval_Tracking_Id ;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }



    public String getName() {

        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getApproval_Tracking_Id() {

        return Approval_Tracking_Id;
    }

    public void setApproval_Tracking_Id(String Approval_Tracking_Id) {
        this.Approval_Tracking_Id = Approval_Tracking_Id;
    }


    public String getItemUri() {

        return ItemUri;
    }

    public void setItemUri(String ItemUri) {
        this.ItemUri = ItemUri;
    }

}

class PublishTransaction extends JSONObject {
    private String PublishDateTime;


    public String getPublishDateTime() {
        return PublishDateTime;
    }

    public void setPublishDateTime(String PublishDateTime) {
        this.PublishDateTime = PublishDateTime;
    }

}


class ComponentPresentations {

    @Getter
    @Setter
    JSONArray jsonArray=new JSONArray();

    private String Id;
    private String Name;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id =Id;
    }

    public String getName() {

        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    private String Location;
    private String ItemUri;
    private String Version;
    private String CreatedDateTime;

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }


    public String getItemUri() {

        return ItemUri;
    }

    public void setItemUri(String ItemUri) {
        this.ItemUri = ItemUri;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }
}


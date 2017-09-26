package org.smartregister.facialrecognition.domain;

/**
 * Created by wildan on 9/14/17.
 */

public class ProfileImage {
    private String imageid;
    private String anmId;
    private String entityID;
    private String contenttype;
    private String filepath;
    private String filecategory;
    private String filevector;
    private Object syncStatus;

    public ProfileImage(){

    }

    public ProfileImage(String string, String string1, String string2, String string3, String string4, String string5, String string6, String string7) {

    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public void setAnmId(String anmId) {
        this.anmId = anmId;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setFilecategory(String filecategory) {
        this.filecategory = filecategory;
    }

    public void setFilevector(String filevector) {
        this.filevector = filevector;
    }

    public void setSyncStatus(Object syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getFilevector() {
        return filevector;
    }

    public String getEntityID() {
        return entityID;
    }
}

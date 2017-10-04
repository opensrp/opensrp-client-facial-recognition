package org.smartregister.facialrecognition.domain;

/**
 * Created by wildan on 9/14/17.
 */

public class ProfileImage {
    private Long _id;
    private String anmId;
    private String baseEntityId;
    private String contenttype;
    private String filepath;
    private String filecategory;
    private String faceVector;
    private String syncStatus;
    private Long createdAt;
    private Long updatedAt;



    public ProfileImage(Long _id, String baseEntityId, String faceVector, String syncStatus, long createdAt, long updatedAt) {
        this._id = _id;
        this.baseEntityId = baseEntityId;
        this.faceVector = faceVector;
        this.syncStatus = syncStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ProfileImage() {

    }


    public void set_id(Long _id) {
        this._id = _id;
    }

    public void setAnmId(String anmId) {
        this.anmId = anmId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
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

    public void setFilevector(String faceVector) {
        this.faceVector = faceVector;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getFilevector() {
        return faceVector;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }
}

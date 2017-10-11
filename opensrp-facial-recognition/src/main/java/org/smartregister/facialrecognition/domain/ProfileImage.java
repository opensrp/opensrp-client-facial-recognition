package org.smartregister.facialrecognition.domain;

/**
 * Created by wildan on 9/14/17.
 */

public class ProfileImage {
    private Long _id;
    private String baseEntityId, faceVector, syncStatus;
    private long createdAt, updatedAt;

    private String anmId;
    private String contenttype;
    private String filepath;
    private String filecategory;

//    private static final String ID_COLUMN = "_id";
//    private static final String BASE_ENTITY_ID_COLUMN = "base_entity_id";
//    private static final String FACE_VECTOR_COLUMN = "face_vector";
//    private static final String SYNC_STATUS_COLUMN = "sync_status";
//    private static final String CREATED_AT_COLUMN = "created_at";
//    private static final String UPDATED_AT_COLUMN = "updated_at";

    public ProfileImage(){

    }

    public ProfileImage(Long _id, String baseEntityId, String faceVector, String syncStatus, long createdAt, long updatedAt) {
        this._id = _id;
        this.baseEntityId = baseEntityId;
        this.faceVector = faceVector;
        this.syncStatus = syncStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public Long getId() {
        return _id;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setFaceVector(String faceVector) {
        this.faceVector = faceVector;
    }

    public String getFaceVector() {
        return faceVector;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
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

    public String getFilevector() {
        return faceVector;
    }

}

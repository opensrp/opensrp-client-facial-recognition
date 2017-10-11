package org.smartregister.facialrecognition.domain;

import java.io.Serializable;

/**
 * Created by wildan on 10/9/17.
 */

public class FacialWrapper implements Serializable {

    private Long dbKey;
    private String id;
    private String faceVector;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFaceVector() {
        return faceVector;
    }

    public void setFaceVector(String faceVector) {
        this.faceVector = faceVector;
    }

    public Long getDbKey() {
        return dbKey;
    }

    public void setDbKey(Long dbKey) {
        this.dbKey = dbKey;
    }
}

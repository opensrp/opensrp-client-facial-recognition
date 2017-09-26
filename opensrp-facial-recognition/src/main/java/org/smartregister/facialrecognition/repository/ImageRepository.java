package org.smartregister.facialrecognition.repository;

import org.smartregister.facialrecognition.domain.ProfileImage;

import java.util.List;

/**
 * Created by wildan on 9/14/17.
 */

public class ImageRepository {
    public static Object TYPE_Unsynced;
    private static ImageRepository instance;
    private List<ProfileImage> allVectorImages;

    public static ImageRepository getInstance() {
        return instance;
    }

    public void add(ProfileImage profileImage, String entityId) {

    }

    public void updateByEntityId(String entityId, String faceVector) {

    }

    public List<ProfileImage> getAllVectorImages() {
        return allVectorImages;
    }

    public List<String> findAllUnDownloaded() {
        return null;
    }
}

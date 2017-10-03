package org.smartregister.facialrecognition.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.facialrecognition.domain.ProfileImage;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

/**
 * Created by wildan on 9/14/17.
 */

public class ImageRepository extends BaseRepository {
    private static final String TABLE_PHOTO = "ImageList";
    private static final String PHOTO_SQL = "CREATE TABLE "+ TABLE_PHOTO +" (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREEMENT" +
            "base_entity_id VARCHAR NOT NULL, vector VARCHAR, sync_status VARCHAR, created_at DATETIME, updated_at DATETIME";
    public static Object TYPE_Unsynced;
    private static ImageRepository instance;
    private List<ProfileImage> allVectorImages;

    public ImageRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database){
        database.execSQL(PHOTO_SQL);
    }

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

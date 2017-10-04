package org.smartregister.facialrecognition.repository;

import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.facialrecognition.domain.ProfileImage;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wildan on 9/14/17.
 */

public class ImageRepository extends BaseRepository {
    private static final String PHOTO_TABLE_NAME = "photos";
    private static final String ID_COLUMN = "_id";
    private static final String BASE_ENTITY_ID_COLUMN = "base_entity_id";
    private static final String FACE_VECTOR_COLUMN = "face_vector";
    private static final String SYNC_STATUS_COLUMN = "sync_status";
    private static final String CREATED_AT_COLUMN = "created_at";
    private static final String UPDATED_AT_COLUMN = "updated_at";
    private static final String[] PHOTO_TABLE_COLUMNS = { ID_COLUMN, BASE_ENTITY_ID_COLUMN, FACE_VECTOR_COLUMN, SYNC_STATUS_COLUMN, CREATED_AT_COLUMN, UPDATED_AT_COLUMN};
    private static final String PHOTO_SQL = "CREATE TABLE "+ PHOTO_TABLE_NAME +" (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "base_entity_id VARCHAR NOT NULL, face_vector VARCHAR, sync_status VARCHAR, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
    private static final String TAG = ImageRepository.class.getSimpleName();

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

    public void add(ProfileImage profileImage, String entityId) {}

    public void updateByEntityId(String entityId, String faceVector) {}

    public List<ProfileImage> getAllVectorImages() {
        return allVectorImages;
    }

    public List<String> findAllUnDownloaded() {
        return null;
    }

    public List<ProfileImage> findLast5(String entityid) {
        Cursor cursor = getRepository().getReadableDatabase().query(
                PHOTO_TABLE_NAME, PHOTO_TABLE_COLUMNS, BASE_ENTITY_ID_COLUMN + " = ? " + COLLATE_NOCASE, new String[]{entityid}, null, null, UPDATED_AT_COLUMN + COLLATE_NOCASE + " DESC", null);

        return readAllFacials(cursor);

    }

    private List<ProfileImage> readAllFacials(Cursor cursor) {
        List<ProfileImage> facials = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()){
                while (!cursor.isAfterLast()){
                    facials.add(
                            new ProfileImage(
                                    cursor.getLong(cursor.getColumnIndex(ID_COLUMN)),
                                    cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID_COLUMN)),
                                    cursor.getString(cursor.getColumnIndex(FACE_VECTOR_COLUMN)),
                                    cursor.getString(cursor.getColumnIndex(SYNC_STATUS_COLUMN)),
                                    cursor.getLong(cursor.getColumnIndex(CREATED_AT_COLUMN)),
                                    cursor.getLong(cursor.getColumnIndex(UPDATED_AT_COLUMN))
                            ));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e){
            Log.e(TAG, "readAllFacials: "+ e.getMessage() );
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }

        return facials;
    }
}

package org.smartregister.facialrecognition.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.facialrecognition.domain.ProfileImage;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by wildan on 9/14/17.
 */

public class ImageRepository extends BaseRepository {

    private static final String TAG = ImageRepository.class.getSimpleName();

    private static final String PHOTO_TABLE_NAME = "facials";
    private static final String ID_COLUMN = "_id";
    private static final String BASE_ENTITY_ID_COLUMN = "base_entity_id";
    private static final String FACE_VECTOR_COLUMN = "face_vector";
    private static final String SYNC_STATUS_COLUMN = "sync_status";
    private static final String CREATED_AT_COLUMN = "created_at";
    private static final String UPDATED_AT_COLUMN = "updated_at";
    private static final String[] PHOTO_TABLE_COLUMNS = { ID_COLUMN, BASE_ENTITY_ID_COLUMN, FACE_VECTOR_COLUMN, SYNC_STATUS_COLUMN, CREATED_AT_COLUMN, UPDATED_AT_COLUMN};
    private static final String PHOTO_SQL = "CREATE TABLE "+ PHOTO_TABLE_NAME +" (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "base_entity_id VARCHAR NOT NULL, face_vector VARCHAR, sync_status VARCHAR, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
    private static final String ENTITY_ID_INDEX =
            "CREATE INDEX " + PHOTO_TABLE_NAME + "_" + BASE_ENTITY_ID_COLUMN + "_index ON "
                    + PHOTO_TABLE_NAME + "(" + BASE_ENTITY_ID_COLUMN + " " + "COLLATE" + " NOCASE);";
    public static String TYPE_Unsynced = "Unsynced";
    public static String TYPE_Synced = "Synced";
    private static ImageRepository instance;
    private List<ProfileImage> allVectorImages;

    /**
     * The last ID case
     */
    public static final String LATEST_ID = "last_insert_rowid()";

    public ImageRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database){
        database.execSQL(PHOTO_SQL);
        database.execSQL(ENTITY_ID_INDEX);
    }

    public static ImageRepository getInstance() {
        return instance;
    }

    public void add(ProfileImage profileImage, String entityId) {
        profileImage.setBaseEntityId(entityId);
        add(profileImage);
    }

    public void add(ProfileImage profileImage) {
        try {
            if (profileImage == null) return;

            if (StringUtils.isBlank(profileImage.getSyncStatus())) profileImage.setSyncStatus(TYPE_Unsynced);

            if (profileImage.getUpdatedAt() == null) profileImage.setUpdatedAt(Calendar.getInstance().getTimeInMillis());

            SQLiteDatabase database = getRepository().getWritableDatabase();

            if (profileImage.getBaseEntityId() != null) {
                profileImage.setId(database.insert(PHOTO_TABLE_NAME, null, createValuesFor(profileImage)));
            } else {
                profileImage.setSyncStatus(TYPE_Unsynced);
                update(database, profileImage);
            }

        } catch (Exception e){
            Log.e(TAG, "add: "+ Log.getStackTraceString(e) );
        }

    }

    private void update(SQLiteDatabase database, ProfileImage profileImage) {
        if (profileImage == null || profileImage.getId() != null){
            return;
        }

        try {
            SQLiteDatabase db;
            db = (database == null)? getRepository().getWritableDatabase(): database;

            String idSelection = BASE_ENTITY_ID_COLUMN + " = ?";
            int qr = db.update(PHOTO_TABLE_NAME, createValuesFor(profileImage), idSelection, new String[]{profileImage.getBaseEntityId()});
            Log.e(TAG, "update: "+ qr );
        } catch (Exception e){
            Log.e(TAG, "update: "+ Log.getStackTraceString(e) );
        }
    }

    private ContentValues createValuesFor(ProfileImage profileImage) {
        ContentValues values = new ContentValues();

        long created_at = 0;
        if (profileImage.getCreatedAt() != null) created_at = Calendar.getInstance().getTimeInMillis();

        values.put(ID_COLUMN, profileImage.getId());
        values.put(BASE_ENTITY_ID_COLUMN, profileImage.getBaseEntityId());
        values.put(FACE_VECTOR_COLUMN, profileImage.getFaceVector());
        values.put(SYNC_STATUS_COLUMN, profileImage.getSyncStatus());
        values.put(CREATED_AT_COLUMN, created_at);
        values.put(UPDATED_AT_COLUMN,Calendar.getInstance().getTimeInMillis());
        return values;
    }

    public void updateByEntityId(String entityId, String faceVector) {}

    public List<ProfileImage> getAllVectorImages() {
        Cursor cursor = getRepository().getReadableDatabase().query(
                PHOTO_TABLE_NAME, PHOTO_TABLE_COLUMNS, null, null, null, null, UPDATED_AT_COLUMN + COLLATE_NOCASE + " DESC", null);

        return readAllFacials(cursor);
    }

    public List<String> findAllUnDownloaded() {
        return null;
    }

    public List<ProfileImage> findLast5(String entityid) {
        Cursor cursor = getRepository().getReadableDatabase().query(
                PHOTO_TABLE_NAME, PHOTO_TABLE_COLUMNS, null, null, null, null, UPDATED_AT_COLUMN + COLLATE_NOCASE + " DESC", "5");

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

    public Long findLatestRecordId(){
        Cursor c = null;
        long abc = 0;
        try {
            String sql = "SELECT max("+ID_COLUMN+") FROM "+PHOTO_TABLE_NAME;
            c = getRepository().getReadableDatabase().rawQuery(sql, null);
            if (c !=null){
                c.moveToFirst();
                abc = c.getInt(0);
            }

//            abc = ((c != null && c.getCount() > 0))? c.getLong(c.getColumnIndex(ID_COLUMN)) : 0L ;

        } catch (Exception e){
            Log.e(TAG, "findLatestRecordId: "+ e.getMessage() );
        } finally {
            if (c != null) c.close();
        }
        return abc;
    }

    public ProfileImage find(Long caseId){
        ProfileImage profileImage = null;
        Cursor cursor = null;
        try {
            cursor = getRepository().getReadableDatabase().query(PHOTO_TABLE_NAME, PHOTO_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId.toString()}, null, null, null, null);
            List<ProfileImage> profileImages = readAllFacials(cursor);
            if (!profileImages.isEmpty()) profileImage = profileImages.get(0);

        } catch (Exception e){
            Log.e(TAG, "find: "+Log.getStackTraceString(e) );
        } finally {
            if (cursor != null) cursor.close();
        }
        return profileImage;
    }

}

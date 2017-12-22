package org.smartregister.facialrecognition.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.facialrecognition.FacialRecognitionLibrary;
import org.smartregister.facialrecognition.activities.OpenCameraActivity;
import org.smartregister.facialrecognition.domain.ProfileImage;
import org.smartregister.facialrecognition.repository.ImageRepository;
import org.smartregister.facialrecognition.utils.FaceConstants;
import org.smartregister.facialrecognition.utils.Tools;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by wildan on 10/5/17.
 */

public class BitmapUtil {
    public static String[] photoDirs = new String[]{DrishtiApplication.getAppDir(),
            DrishtiApplication.getAppDir() + File.separator + ".thumbs"};
    public static final String TAG = BitmapUtil.class.getSimpleName();
    private static String[] splitStringArray;

    public void BitmapUtil(){
    }

    public void saveAndClose(Context mContext, String uid, boolean updated, FacialProcessing objFace, int arrayPossition, Bitmap mBitmap, String str_origin_class) {

        if (saveToFile(mBitmap, uid)) {
            Log.e(TAG, "saveAndClose: " + "Saved File Success! uid= " + uid);
            if (saveToDb(updated, uid, objFace)) Log.e(TAG, "saveAndClose: " + "Stored DB Success!");
            if (saveToLocal())Log.e(TAG, "saveAndClose: " + "Stored SharedPrefs Success!");;

        } else {
            Log.e(TAG, "saveAndClose: "+"Failed saved file!" );
        }

    }

    private boolean saveToLocal() {

        return false;
    }

    private static boolean saveToFile(Bitmap mBitmap, String uid) {

        try {
            // Raw image
            File jpegId = new File(String.format("%s%s%s.jpg", photoDirs[0], File.separator, uid));
            FileOutputStream oriFos = new FileOutputStream(jpegId);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, oriFos);
            oriFos.close();
            Log.e(TAG, "Wrote Raw image to " + jpegId.getAbsolutePath());

            // Thumbnail Image
            File thumbsFolder = new File(photoDirs[1]);
            boolean success = true;
            if (!thumbsFolder.exists()) {
                success = thumbsFolder.mkdir();
            }
            if (success) {
                File thumbId = new File(String.format("%s%s%s.jpg", photoDirs[1], File.separator, "th_"+uid));
                FileOutputStream thumbsFos = new FileOutputStream(thumbId);
                final int THUMBSIZE = FaceConstants.THUMBSIZE;
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(jpegId.getAbsolutePath()), THUMBSIZE, THUMBSIZE);
                if(ThumbImage.compress(Bitmap.CompressFormat.PNG, 100, thumbsFos)){
                    thumbsFos.close();
                    Log.e(TAG, "Wrote Thumbs image to " + thumbId.getAbsolutePath());
                } else Log.e(TAG, "saveToFile: Thumbs "+ "Failed" );
            } else {
                Log.e(TAG, "saveToFile: "+"Folder Thumbs failed Created!" );
            }

            return true;

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }


        return false;

    }

    /**
     * Method Save Vector to Database
     * @param updatedMode
     * @param uid
     * @param objFace
     * @return
     */
    private boolean saveToDb(boolean updatedMode, String uid, FacialProcessing objFace) {

        final ImageRepository imageRepo = FacialRecognitionLibrary.getInstance().facialRepository();

        byte[] faceVector;

        if (imageRepo != null) {

            if (!updatedMode){

                int result = objFace.addPerson(0);
                faceVector = objFace.serializeRecogntionAlbum();
                String albumBufferArr = Arrays.toString(faceVector);
                String[] faceVectorContent = albumBufferArr.substring(1, albumBufferArr.length() - 1).split(", ");
                // Get Face Vector Content Only by removing Header
                faceVectorContent = Arrays.copyOfRange(faceVectorContent, faceVector.length - 300, faceVector.length);

                ProfileImage profileImage = new ProfileImage();

                profileImage.setBaseEntityId(uid);
                profileImage.setFaceVector(Arrays.toString(faceVectorContent));
                profileImage.setSyncStatus(String.valueOf(ImageRepository.TYPE_Unsynced));

                imageRepo.add(profileImage, uid);

            } else {
                // TODO: Update existing record
            }

            return true;
        }
        return false;
    }

    // DRAWING
    public static void drawRectFace(Rect rect, Bitmap mBitmap, float pixelDensity) {
        // Extra padding around the faceRects
        rect.set(rect.left -= 20, rect.top -= 20, rect.right += 20, rect.bottom += 20);
        Canvas canvas = new Canvas(mBitmap);

        // Draw rect fill
        Paint paintForRectFill = new Paint();
        paintForRectFill.setStyle(Paint.Style.FILL);
        paintForRectFill.setColor(Color.BLACK);
        paintForRectFill.setAlpha(80);

        // Draw rect strokes
        Paint paintForRectStroke = new Paint();
        paintForRectStroke.setStyle(Paint.Style.STROKE);
        paintForRectStroke.setColor(Color.GREEN);
        paintForRectStroke.setStrokeWidth(3);

        // Draw Face detected Area
        canvas.drawRect(rect, paintForRectFill);
        canvas.drawRect(rect, paintForRectStroke);
    }

    public static void drawInfo(Rect rect, Bitmap mutableBitmap, float pixelDensity, String personName) {

        // Extra padding around the faceRects
        rect.set(rect.left -= 20, rect.top -= 20, rect.right += 20, rect.bottom += 20);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paintForRectFill = new Paint();

        // Draw rect fill
        paintForRectFill.setStyle(Paint.Style.FILL);
        paintForRectFill.setColor(Color.WHITE);
        paintForRectFill.setAlpha(80);

        // Draw rectangular strokes
        Paint paintForRectStroke = new Paint();
        paintForRectStroke.setStyle(Paint.Style.STROKE);
        paintForRectStroke.setColor(Color.GREEN);
        paintForRectStroke.setStrokeWidth(5);
        canvas.drawRect(rect, paintForRectFill);
        canvas.drawRect(rect, paintForRectStroke);

//        float pixelDensity = getResources().getDisplayMetrics().density;
        int textSize = (int) (rect.width() / 25 * pixelDensity);

        Paint paintForText = new Paint();
        Paint paintForTextBackground = new Paint();
        Typeface tp = Typeface.SERIF;
        Rect backgroundRect = new Rect(rect.left, rect.bottom, rect.right, (rect.bottom + textSize));

        paintForText.setColor(Color.WHITE);
        paintForText.setTextSize(textSize);
        paintForTextBackground.setStyle(Paint.Style.FILL);
        paintForTextBackground.setColor(Color.BLACK);
        paintForText.setTypeface(tp);
        paintForTextBackground.setAlpha(80);

        if (personName != null) {
            canvas.drawRect(backgroundRect, paintForTextBackground);
            canvas.drawText(personName, rect.left, rect.bottom + (textSize), paintForText);
        } else {
            canvas.drawRect(backgroundRect, paintForTextBackground);
            canvas.drawText("Not identified", rect.left, rect.bottom + (textSize), paintForText);
        }

//        confirmationView.setImageBitmap(mutableBitmap);

    }

    /**
     * Load Existing Album from Local
     * @param context
     */
    public static void loadAlbum(Context context) {

        SharedPreferences settings = context.getSharedPreferences(FaceConstants.ALBUM_NAME, 0);
        String arrayOfString = settings.getString(FaceConstants.ALBUM_ARRAY, null);
        byte[] albumArray;

        if (arrayOfString != null) {

            splitStringArray = arrayOfString.substring(1, arrayOfString.length() - 1).split(", ");

            albumArray = new byte[splitStringArray.length];


            for (int i = 0; i < splitStringArray.length; i++) {
                albumArray[i] = Byte.parseByte(splitStringArray[i]);
            }

            boolean result = FacialRecognitionLibrary.faceProc.deserializeRecognitionAlbum(albumArray);

            if (result) Log.e(TAG, "loadAlbum: "+"Succes" );

        } else {
            Log.e(TAG, "loadAlbum: " + "is it your first record ? if no, there is problem happen.");
        }
    }

    public static void enableFR(final org.smartregister.Context context, final Activity detailActivity, final CommonPersonObjectClient idClient, ImageView kiview) {

        final HashMap<String, String> hash = Tools.retrieveHash(context.applicationContext());

        kiview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FlurryFacade.logEvent("taking_mother_pictures_on_kohort_ibu_detail_view");
                String entityid = idClient.entityId();

                boolean updateMode = false;
                if (hash.containsValue(entityid)) {
                    updateMode = true;
                }
                Intent takePictureIntent = new Intent(detailActivity, OpenCameraActivity.class);
                takePictureIntent.putExtra("org.smartregister.facialrecognition.OpenCameraActivity.updated", updateMode);
                takePictureIntent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.identify", false);
                takePictureIntent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.id", entityid);
                takePictureIntent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.origin", TAG); // send Class Name

                detailActivity.startActivityForResult(takePictureIntent, 2);
            }
        });
    }

}

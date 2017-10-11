package org.smartregister.facialrecognition.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.renderscript.Element;
import android.util.Log;

import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.smartregister.facialrecognition.FacialRecognitionLibrary;
import org.smartregister.facialrecognition.domain.ProfileImage;
import org.smartregister.facialrecognition.repository.ImageRepository;
import org.smartregister.facialrecognition.utils.FaceConstants;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by wildan on 10/5/17.
 */

public class BitmapUtil {
    public static String[] photoDirs = new String[]{DrishtiApplication.getAppDir(),
            DrishtiApplication.getAppDir() + File.separator + ".thumbs"};
    public static final String TAG = BitmapUtil.class.getSimpleName();
//    private static ImageRepository imageRepo;
//    private static ImageRepository imageRepo = (ImageRepository) org.smartregister.Context.imageRepository();


    public void BitmapUtil(){

//        imageRepo = ImageRepository.getInstance();
//        imageRepo = org.smartregister.Context.getInstance().imageRepository();
//        Log.e(TAG, "BitmapUtil: "+ imageRepo );

    }

    public static void saveAndClose(Context mContext, String uid, boolean updated, FacialProcessing objFace, int arrayPossition, Bitmap mBitmap, String str_origin_class) {

        if (saveToFile(mBitmap, uid)) {
            Log.e(TAG, "saveAndClose: " + "Saved File Success! uid= " + uid);
            if (saveToDb(uid, objFace)) Log.e(TAG, "saveAndClose: " + "Stored DB Success!");
        }


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

    private static boolean saveToDb(String uid, FacialProcessing faceVector) {
        final ImageRepository imageRepo = FacialRecognitionLibrary.getInstance().facialRepository();
        if (imageRepo != null) {

            ProfileImage profileImage = new ProfileImage();

            profileImage.setBaseEntityId(uid);
            profileImage.setSyncStatus(String.valueOf(ImageRepository.TYPE_Unsynced));

            imageRepo.add(profileImage, uid);
            return true;
        }
        return false;
    }

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
//        Log.e(TAG, "drawInfo: rect " + rect);
//        Log.e(TAG, "drawInfo: bitmap" + mutableBitmap);

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

}

package org.smartregister.facialrecognition.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.renderscript.Element;
import android.util.Log;

import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

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

    public static final String TAG = BitmapUtil.class.getSimpleName();
    private static ImageRepository imageRepo;

    public void BitmapUtil(){

        imageRepo = ImageRepository.getInstance();

    }

    public static void saveAndClose(Context mContext, String uid, boolean updated, FacialProcessing objFace, int arrayPossition, Bitmap mBitmap, String str_origin_class) {

        if (saveToFile(mBitmap, uid)) {
            Log.e(TAG, "saveAndClose: " + "Saved File Success!");
            if (saveToDb(uid, objFace)) Log.e(TAG, "saveAndClose: " + "Stored DB Success!");
        }


    }

    private static boolean saveToFile(Bitmap mBitmap, String uid) {

        String[] photoDirs = new String[]{DrishtiApplication.getAppDir(), DrishtiApplication.getAppDir() + File.separator + ".thumbs"};

        try {
            // Raw image
            FileOutputStream oriFos = new FileOutputStream(new File(String.format("%s%s%s.jpg", photoDirs[0], File.separator, uid)));
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, oriFos);
            oriFos.close();
            Log.e(TAG, "Wrote Raw image to " + oriFos.toString());

            // Thumbs Image
            FileOutputStream thumbsFos = new FileOutputStream(new File(String.format("%s%s%s.jpg", photoDirs[1], File.separator, uid)));
            final int THUMBSIZE = FaceConstants.THUMBSIZE;
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(thumbsFos.toString()), THUMBSIZE, THUMBSIZE);
            ThumbImage.compress(Bitmap.CompressFormat.PNG, 100, thumbsFos);
            thumbsFos.close();
            Log.e(TAG, "Wrote Thumbs image to " + thumbsFos.toString());

            return true;

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }


        return false;

    }

    private static boolean saveToDb(String uid, FacialProcessing faceVector) {
        ProfileImage profileImage = new ProfileImage();

        profileImage.setId(Long.valueOf(UUID.randomUUID().toString()));
        profileImage.setBaseEntityId(uid);
        profileImage.setFilevector(Arrays.toString(faceVector.getFaceData()));
        profileImage.setSyncStatus(String.valueOf(ImageRepository.TYPE_Unsynced));

        imageRepo.add(profileImage, uid);
        return false;
    }

}

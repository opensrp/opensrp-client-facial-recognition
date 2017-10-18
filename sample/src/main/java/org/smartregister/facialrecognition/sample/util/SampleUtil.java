package org.smartregister.facialrecognition.sample.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.facialrecognition.domain.FacialWrapper;
import org.smartregister.facialrecognition.sample.MainActivity;
import org.smartregister.facialrecognition.sample.R;
import org.smartregister.facialrecognition.util.BitmapUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wildan on 10/3/17.
 */

public class SampleUtil {
    private static final String TAG = SampleUtil.class.getSimpleName();
    public static final String ENTITY_ID = "1";
    public static final String FACEVECTOR = "0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 9, 8, 7, 6, 5, 4, 3, 2, 1";

    public static void createFacialWidget(MainActivity context, View container, HashMap<Long, Pair<String, String>> last_five_facial_map, ArrayList<View.OnClickListener> listeners, ArrayList<Boolean> editenabled) {
        LinearLayout sampleLayout = (LinearLayout) container.findViewById(R.id.table_list);
        sampleLayout.removeAllViews();

        int i = 0;
        for (Map.Entry<Long, Pair<String, String>> entry : last_five_facial_map.entrySet()){

            Pair<String, String> pair = entry.getValue();
            View view = createTableRowForFacial(context, sampleLayout, entry.getKey(), pair.first, pair.second, editenabled.get(i), listeners.get(i));
            sampleLayout.addView(view);
            i++;
        }
    }

    private static View createTableRowForFacial(Activity context, ViewGroup container, Long uid, String labelString, String valueString, Boolean editEnabled, View.OnClickListener listener) {
        View rows = context.getLayoutInflater().inflate(R.layout.list_single, container, false);
        ImageView profile_pic = (ImageView) rows.findViewById(R.id.profile_pic);
        TextView label = (TextView) rows.findViewById(R.id.full_name);
        TextView value = (TextView) rows.findViewById(R.id.uid);
//        Button edit = (Button) rows.findViewById(R.id.edit);
        if (editEnabled) {
            profile_pic.setOnClickListener(listener);
//            edit.setVisibility(View.VISIBLE);
//            edit.setOnClickListener(listener);
        } else {
//            edit.setVisibility(View.INVISIBLE);
        }
        // Set Text
        try {
            String photoPath = String.format("%s%s%s.jpg", BitmapUtil.photoDirs[1], File.separator, "th_"+uid);
            InputStream is = new File(photoPath).toURI().toURL().openStream();
//            Bitmap photo = BitmapFactory.decodeFile(photoPath);
            Bitmap photo = BitmapFactory.decodeStream(is);
            if (photo != null) profile_pic.setImageBitmap(photo);

        } catch (FileNotFoundException e){
            e.printStackTrace();
//            Log.e(TAG, "createTableRowForFacial: "+ Log.getStackTraceString(e) );
        } catch (IOException e){
            e.printStackTrace();
        }

        label.setText(labelString);
        value.setText(valueString);

        return rows;
    }

    public static void showCameraDialog(Activity context, View view, String dialogTag) {
        FacialWrapper facialWrapper = view.getTag() != null ? (FacialWrapper) view.getTag() : new FacialWrapper();

//        RecordFacialDialogFragment facialDialogFragment =
    }
}

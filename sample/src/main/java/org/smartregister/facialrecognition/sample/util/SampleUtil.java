package org.smartregister.facialrecognition.sample.util;

import android.app.Activity;
import android.media.Image;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.facialrecognition.sample.MainActivity;
import org.smartregister.facialrecognition.sample.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sid on 10/3/17.
 */

public class SampleUtil {
    public static final String ENTITY_ID = "1";
    public static final String FACEVECTOR = "0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 9, 8, 7, 6, 5, 4, 3, 2, 1";

    public static void createFacialWidget(MainActivity context, View container, HashMap<Long, Pair<String, String>> last_five_facial_map, ArrayList<View.OnClickListener> listeners, ArrayList<Boolean> editenabled) {
        LinearLayout sampleLayout = (LinearLayout) container.findViewById(R.id.table_list);
        sampleLayout.removeAllViews();

        int i = 0;
        for (Map.Entry<Long, Pair<String,String>> entry : last_five_facial_map.entrySet()){

            Pair<String, String> pair = entry.getValue();
            View view = createTableRowForFacial(context, sampleLayout, pair.first, pair.second, editenabled.get(i), listeners.get(i));
            sampleLayout.addView(view);
            i++;
        }
    }

    private static View createTableRowForFacial(Activity context, ViewGroup container, String labelString, String valueString, Boolean editEnabled, View.OnClickListener listener) {
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
        label.setText(labelString);
        value.setText(valueString);
        return rows;}
}

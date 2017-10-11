package org.smartregister.facialrecognition.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.facialrecognition.domain.FacialWrapper;
import org.smartregister.facialrecognition.listener.FacialActionListener;
import org.smartregister.util.DateUtil;
import org.smartregister.facialrecognition.FacialRecognitionLibrary;
import org.smartregister.facialrecognition.activities.OpenCameraActivity;
import org.smartregister.facialrecognition.domain.ProfileImage;
import org.smartregister.facialrecognition.repository.ImageRepository;
import org.smartregister.facialrecognition.sample.util.SampleUtil;
import org.smartregister.facialrecognition.utils.Tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by wildan on 9/14/17.
 */

public class MainActivity extends AppCompatActivity implements FacialActionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DIALOG_TAG = "DIALOG_TAG_BLA";
    Long latestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fab_camera = (FloatingActionButton) findViewById(R.id.fab_cam);

        if (!Tools.isSupport()) {
            fab_camera.setVisibility(View.INVISIBLE);
        }



        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use SNAPDRAGON SDK
                getOpenCameraActivity();

//                SampleUtil.showCameraDialog(MainActivity.this, view, DIALOG_TAG);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ImageRepository imgRepo = FacialRecognitionLibrary.getInstance().facialRepository();
        latestId = imgRepo.findLatestRecordId();
        Log.e(TAG, "onResume: latestId "+ latestId );

        refreshEditFacialLayout();
    }

    View.OnClickListener onclicklistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
//                SampleUtil.showEditWeightDialog(MainActivity.this, finalI, DIALOG_TAG);
            // Use SNAPDRAGON SDK
            getOpenCameraActivity();
        }
    };

    private void refreshEditFacialLayout() {
        View facialWidget = findViewById(R.id.cv_listfacial);

        ImageRepository ir = FacialRecognitionLibrary.getInstance().facialRepository();

        ArrayList<View.OnClickListener> listeners = new ArrayList<>();

        LinkedHashMap<Long, Pair<String, String>> facialMap = new LinkedHashMap<>();
        ArrayList<Boolean> editEnabled = new ArrayList<>();

        List<ProfileImage> imageList = ir.findLast5(SampleUtil.ENTITY_ID);

        for (int i = 0; i < imageList.size(); i++) {
            ProfileImage facial = imageList.get(i);

//            facialMap.put(facial.getId(), Pair.create("", facial.getFaceVector() ));
            facialMap.put(facial.getId(), Pair.create(facial.getBaseEntityId(), facial.getSyncStatus() ));
            // Default edit mode = true
            editEnabled.add(true);
            listeners.add(onclicklistener);

        }
        Log.e(TAG, "refreshEditFacialLayout: map Size "+ facialMap.size() );

        if (facialMap.size() < 5 && facialMap.size() > 0){
            facialMap.put(0L, Pair.create(DateUtil.getDuration(0), SampleUtil.FACEVECTOR));
            editEnabled.add(false);
            listeners.add(null);

        }
        SampleUtil.createFacialWidget(MainActivity.this, facialWidget, facialMap, listeners, editEnabled);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0 && resultCode== Activity.RESULT_OK){

        }
    }

    @Override
    public void onFacialTaken(FacialWrapper tag) {

        if (tag != null){
            final ImageRepository facialRepository = FacialRecognitionLibrary.getInstance().facialRepository();
            ProfileImage profileImage = new ProfileImage();
            if (tag.getDbKey() != null){
                profileImage = facialRepository.find(tag.getDbKey());
            }
//            profileImage.setBaseEntityId(SampleUtil.ENTITY_ID);
            profileImage.setFaceVector(tag.getFaceVector());
            profileImage.setCreatedAt(Calendar.getInstance().getTimeInMillis());
//            facialRepository.add(profileImage);
            facialRepository.add(profileImage);
            tag.setDbKey(profileImage.getId());
        } else {
            Log.e(TAG, "onFacialTaken: "+ "tag not NULL " );
        }
    }

    public void getOpenCameraActivity() {
        Intent intent = new Intent(MainActivity.this, OpenCameraActivity.class);
        intent.putExtra("org.smartregister.facialrecognition.OpenCameraActivity.updated", false);
        intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.id", Long.toString(latestId+1L));
        intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.identify", false);
        intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.origin", TAG);

        startActivityForResult(intent, 0);

    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }
}

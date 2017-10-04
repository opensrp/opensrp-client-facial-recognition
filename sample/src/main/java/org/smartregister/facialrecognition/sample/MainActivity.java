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

import org.smartregister.facialrecognition.FacialRecognitionLibrary;
import org.smartregister.facialrecognition.activities.OpenCameraActivity;
import org.smartregister.facialrecognition.domain.ProfileImage;
import org.smartregister.facialrecognition.repository.ImageRepository;
import org.smartregister.facialrecognition.sample.util.SampleUtil;
import org.smartregister.facialrecognition.utils.Tools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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
                Intent intent = new Intent(MainActivity.this, OpenCameraActivity.class);
                intent.putExtra("org.smartregister.facialrecognition.OpenCameraActivity.updated", false);
                intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.id", "");
                intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.identify", false);
                intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.origin", TAG);

                startActivityForResult(intent, 0);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshEditFacialLayout();
    }

    private void refreshEditFacialLayout() {
        View facialWidget = findViewById(R.id.cv_listfacial);

        ImageRepository ir = FacialRecognitionLibrary.getInstance().facialRepository();

        ArrayList<View.OnClickListener> listeners = new ArrayList<>();

        LinkedHashMap<Long, Pair<String, String>> facialMap = new LinkedHashMap<>();

        List<ProfileImage> imageList = ir.findLast5(SampleUtil.ENTITY_ID);
        for (int i = 0; i < imageList.size(); i++) {
            ProfileImage facials = imageList.get(i);
        }

        if (facialMap.size() > 0){
            SampleUtil.createFacialWidget(MainActivity.this, facialWidget, facialMap, listeners);
        }

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
}

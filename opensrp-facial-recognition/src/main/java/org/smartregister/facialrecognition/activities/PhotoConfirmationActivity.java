package org.smartregister.facialrecognition.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.smartregister.facialrecognition.R;
import org.smartregister.facialrecognition.domain.FacialWrapper;
import org.smartregister.facialrecognition.listener.FacialActionListener;
import org.smartregister.facialrecognition.util.BitmapUtil;
import org.smartregister.facialrecognition.utils.FaceConstants;
import org.smartregister.facialrecognition.utils.Tools;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class PhotoConfirmationActivity extends AppCompatActivity {

    private static String TAG = PhotoConfirmationActivity.class.getSimpleName();
    private Bitmap storedBitmap;
    private Bitmap workingBitmap;
    private Bitmap mutableBitmap;
    ImageView confirmationView;
    ImageView confirmButton;
    ImageView trashButton;
    private String entityId;
    private Rect[] rects;
    private boolean faceFlag = false;
    private boolean identifyPerson = false;
    private FacialProcessing objFace;
    private FaceData[] faceDatas;
    private int arrayPossition;
    Tools tools;
    HashMap<String, String> clientList;
    private String selectedPersonName = "";
    private Parcelable[] kiclient;

    String str_origin_class;

    byte[] data;
    int angle;
    boolean switchCamera;
    private byte[] faceVector;
    private boolean updated = false;

//    private DrawerLayout drawerLayout;
    private boolean isDrawerOpened;
    private MaterialMenuDrawable materialMenu;
    private Context context;
    private boolean cameraFront = false;
    private boolean setBitmapFR;
    private int screenWidth, screenHeight;
    private FacialActionListener listener;
    private FacialWrapper tag;

    public static final String WRAPPER_TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fr_confirmation);

        context = getApplicationContext();

        init_gui();

        init_extras();

//        process_img();

        image_proc();

//        Bundle bundle = savedInstanceState.;
//
//        Serializable serializable = bundle.getSerializable(WRAPPER_TAG);
//
//        if (serializable != null && serializable instanceof FacialWrapper) {
//            tag = (FacialWrapper) serializable;
//        }
//
//        if (tag == null) {
//            Log.e(TAG, "onCreate: "+ "Tag NULL" );
//        }

        initListeners();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    private void init_gui() {
        // Display New Photo
        confirmationView = (ImageView) findViewById(R.id.iv_confirmationView);
        trashButton = (ImageView) findViewById(R.id.iv_cancel);
        confirmButton = (ImageView) findViewById(R.id.iv_approve);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                // Handle your drawable state here
////                materialMenu.animateState(newState);
//            }
//        });
//        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
//        toolbar.setNavigationIcon(materialMenu);

    }

    /**
     * Method to get Info from previous Intent
     */
    private void init_extras() {
        Bundle extras = getIntent().getExtras();
        data = getIntent().getByteArrayExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity");
        angle = extras.getInt("org.smartregister.facialrecognition.PhotoConfirmationActivity.orientation");
        cameraFront = extras.getBoolean("org.smartregister.facialrecognition.PhotoConfirmationActivity.switchCamera");
        entityId = extras.getString("org.smartregister.facialrecognition.PhotoConfirmationActivity.id");
        identifyPerson = extras.getBoolean("org.smartregister.facialrecognition.PhotoConfirmationActivity.identify");
        kiclient = extras.getParcelableArray("org.smartregister.facialrecognition.PhotoConfirmationActivity.kiclient");
        str_origin_class = extras.getString("org.smartregister.facialrecognition.PhotoConfirmationActivity.origin");
        updated = extras.getBoolean("org.smartregister.facialrecognition.PhotoConfirmationActivity.updated");

    }

    private void initListeners() {
        // If approved then save the image and close.
        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!identifyPerson) {

//                    Tools.saveAndClose(getApplicationContext(), entityId, updated, objFace, arrayPossition, storedBitmap, str_origin_class);

                    Log.e(TAG, "onClick: "+ entityId);
                    BitmapUtil.saveAndClose(getApplicationContext(), entityId, updated, objFace, arrayPossition, storedBitmap, str_origin_class);

//                    Log.e(TAG, "onClick: listener "+ listener );

//                    tag.setFaceVector("123");
//                    listener.onFacialTaken(tag);

                    // Back To Detail Activity
                    Intent i = new Intent();
                    setResult(2, i);
                    finish();

                } else {
                    Log.e(TAG, "onClick: not identify ");
                    Log.e(TAG, "onClick: " + selectedPersonName);
                }
            }

        });

        confirmButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    confirmButton.setImageResource(R.drawable.ic_confirm_highlighted_24dp);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    confirmButton.setImageResource(R.drawable.ic_confirm_white_24dp);
                }

                return false;
            }
        });

        // Trash the image and return back to the camera preview.
        trashButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent resultIntent = new Intent();
                setResult(RESULT_CANCELED, resultIntent);
                PhotoConfirmationActivity.this.finish();
            }

        });

        trashButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    trashButton.setImageResource(R.drawable.ic_trash_delete_green);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    trashButton.setImageResource(R.drawable.ic_trash_delete);
                }

                return false;
            }
        });

    }

    private void image_proc() {
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        storedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);

        Matrix mat = new Matrix();

        if (!cameraFront) {
            mat.postRotate(angle == 90 ? 270 : (angle == 180 ? 180 : 0));
//            mat.postScale(-1, 1);
            storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
        } else {
            mat.postRotate(angle == 90 ? 90 : (angle == 180 ? 180 : 0));
            storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
        }

        mutableBitmap = storedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap scaled = Bitmap.createScaledBitmap(mutableBitmap, screenWidth/2, screenHeight/2, false);
//        Bitmap scaled = Bitmap.createScaledBitmap(storedBitmap, screenWidth, screenHeight, true);
        confirmationView.setImageBitmap(scaled);
//        confirmationView.setImageBitmap(mutableBitmap);

        useSnapdragonSDK();

    }

    private void useSnapdragonSDK() {
        objFace = OpenCameraActivity.faceProc;
//        setBitmapFR = objFace.setBitmap(storedBitmap);
        setBitmapFR = objFace.setBitmap(mutableBitmap);
        faceDatas = objFace.getFaceData();

        if (setBitmapFR){
            Log.e(TAG, "image_proc: "+"success" );
            if (faceDatas != null){
                rects = new Rect[faceDatas.length];
                for (int i = 0; i < faceDatas.length; i++) {
                    Rect rect = faceDatas[i].rect;
                    rects[i] = rect;

                    int matchRate = faceDatas[i].getRecognitionConfidence();

                    float pixelDensity = getResources().getDisplayMetrics().density; // 2.0

                    // Mode : Create, Read(Identified), Updated
                    if (identifyPerson){
                        String selectedPersonId = Integer.toString(faceDatas[i].getPersonId());
                        Iterator<HashMap.Entry<String, String>> iter = clientList.entrySet().iterator();
                        // Default name is the person is unknown
                        selectedPersonName = "Unknown";
                        while (iter.hasNext()) {
                            Log.e(TAG, "image_proc: "+ "Matching" );
                            HashMap.Entry<String, String> entry = iter.next();
                            if (entry.getValue().equals(selectedPersonId)) {
                                selectedPersonName = entry.getKey();
                            }
                        }

                        Toast.makeText(getApplicationContext(), selectedPersonName, Toast.LENGTH_SHORT).show();

//                        Draw Info on Image
                        BitmapUtil.drawInfo(rect, mutableBitmap, pixelDensity, selectedPersonName);
                        Log.e(TAG, "image_proc: "+i );

                        showDetailUser(selectedPersonName);
                    } else {
                        // Not Identifiying, do new record.
                        // Draw Info on Image
                        Log.e(TAG, "image_proc: rect "+ rect.toString() ); // Rect(125, 409 - 847, 951)
                        BitmapUtil.drawRectFace(rects[i], mutableBitmap, pixelDensity);

                        // Check Detected existing face
                        if(faceDatas[i].getPersonId() < 0){

                            arrayPossition = i;

                        } else {

                            showPersonInfo(matchRate);

                        }

                        // Face only
//                        confirmationView.setImageBitmap(storedBitmap);
                        // Face and Rect
//                        confirmationView.setImageBitmap(mutableBitmap);
                        Bitmap scaled = Bitmap.createScaledBitmap(mutableBitmap, screenWidth, screenHeight, false);
                        confirmationView.setImageBitmap(scaled);
                        Drawable drawable = confirmationView.getDrawable();//you should call after the bitmap drawn
                        Rect bounds = drawable.getBounds();
                    }
                }
            } else {
                Toast.makeText(PhotoConfirmationActivity.this, "No Face Detected", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(RESULT_CANCELED, resultIntent);
                PhotoConfirmationActivity.this.finish();
            }
        } else {
            Log.e(TAG, "onCreate: SetBitmap objFace"+"Failed" );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_confirmation, menu);
        return true;
    }

    public void showDetailUser(String selectedPersonName) {

        Log.e(TAG, "showDetailUser: " );
        Class<?> origin_class = this.getClass();

//        if(str_origin_class.equals(NativeKISmartRegisterFragment.class.getSimpleName())){
//            origin_class = NativeKISmartRegisterActivity.class;
//        } else if(str_origin_class.equals(NativeKBSmartRegisterFragment.class.getSimpleName())){
//            origin_class = NativeKBSmartRegisterActivity.class;
//        } else if(str_origin_class.equals(NativeKIAnakSmartRegisterFragment.class.getSimpleName())){
//            origin_class = NativeKIAnakSmartRegisterActivity.class;
//        } else if(str_origin_class.equals(NativeKIANCSmartRegisterFragment.class.getSimpleName())){
//            origin_class = NativeKIANCSmartRegisterActivity.class;
//        } else if(str_origin_class.equals(NativeKIPNCSmartRegisterFragment.class.getSimpleName())){
//            origin_class = NativeKIPNCSmartRegisterActivity.class;
//        }

        Intent intent = new Intent(PhotoConfirmationActivity.this, origin_class);
        intent.putExtra("org.ei.opensrp.indonesia.face.face_mode", true);
        intent.putExtra("org.ei.opensrp.indonesia.face.base_id", selectedPersonName);

        startActivity(intent);

    }

    private void showPersonInfo(int recognitionConfidence) {
        Log.e(TAG, "showPersonInfo: Similar face found " +
                Integer.toString(recognitionConfidence));

        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setTitle("Are you Sure?");
        builder.setMessage("Similar Face Found! : Confidence "+recognitionConfidence);
        builder.setNegativeButton("CANCEL", null);
        builder.show();
        confirmButton.setVisibility(View.INVISIBLE);

    }

    /*
    Save File and DB
     */
    private void saveAndClose(String entityId) {

        Log.e(TAG, "saveAndClose: updated "+ updated );

        faceVector = objFace.serializeRecogntionAlbum();

        Log.e(TAG, "saveAndClose: " + Arrays.toString(faceVector));

        int result = objFace.addPerson(arrayPossition);
        clientList.put(entityId, Integer.toString(result));

        byte[] albumBuffer = OpenCameraActivity.faceProc.serializeRecogntionAlbum();

        OpenCameraActivity.faceProc.resetAlbum();

//        Tools.WritePictureToFile(PhotoConfirmationActivity.this, storedBitmap, entityId, albumBuffer, updated);
        // TODO : change album buffer to String[]
//        Tools.WritePictureToFile(storedBitmap, entityId, albumBuffer, updated);

        PhotoConfirmationActivity.this.finish();

//        Intent resultIntent = new Intent(this, KIDetailActivity.class);
//        setResult(RESULT_OK, resultIntent);
//        startActivityForResult(resultIntent, 1);

        Log.e(TAG, "saveAndClose: "+"end" );
    }

    public void saveHash(HashMap<String, String> hashMap, android.content.Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.HASH_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        Log.e(TAG, "Hash Save Size = " + hashMap.size());
        for (String s : hashMap.keySet()) {
            editor.putString(s, hashMap.get(s));
        }
        editor.apply();
    }

    public void saveAlbum() {
        byte[] albumBuffer = OpenCameraActivity.faceProc.serializeRecogntionAlbum();
//		saveCloud(albumBuffer);
        Log.e(TAG, "Size of byte Array =" + albumBuffer.length);
        SharedPreferences settings = getSharedPreferences(FaceConstants.ALBUM_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(FaceConstants.ALBUM_ARRAY, Arrays.toString(albumBuffer));
        editor.apply();
    }

}

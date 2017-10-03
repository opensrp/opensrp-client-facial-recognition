package org.smartregister.facialrecognition.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.PREVIEW_ROTATION_ANGLE;

import org.smartregister.facialrecognition.R;
import org.smartregister.facialrecognition.utils.FaceConstants;
import org.smartregister.facialrecognition.utils.Tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OpenCameraActivity extends Activity implements Camera.PreviewCallback {

    private static final String TAG = OpenCameraActivity.class.getSimpleName();
    public static FacialProcessing faceProc;
    private static boolean switchCamera = false;
    private static boolean settingsButtonPress;
    private static boolean faceEyesMouthDetectionPressed;
    private static boolean perfectModeButtonPress;
    private static boolean cameraButtonPress;
    private static boolean animationPress;
    private static String flashButtonPress;
    private static boolean activityStartedOnce;
    private static String entityId;
    Camera cameraObj;
    FrameLayout preview;
    CameraPreview mPreview;
    PaintFaceView drawView;
    FaceData[] faceArray;
    Display display;
    Animation animationFadeOut;
    AnimationDrawable frameAnimation;
    CheckBox smile;
    CheckBox gazeAngle;
    CheckBox eyeBlink;
    HashMap<String, String> hash;
    long t_startCamera = 0;
    double t_stopCamera = 0;
    String str_origin_class;

    private ImageView cameraButton, switchCameraButton, chooseCameraButton;
    private ImageView menu;
    private ImageView settingsButton, faceEyesMouthBtn, perfectPhotoButton, galleryButton, flashButton;

    private boolean isDevCompat;
    private int displayAngle;
    private boolean smileFlag;
    private boolean blinkFlag;
    private boolean horizontalGazeAngleFlag;
    private boolean verticalGazeAngleFlag;
    private int numFaces;
    private boolean identifyPerson = false;
    private ImageView clientListButton;
    private String selectedPersonName;
    private boolean updated;

    // Set Default Facing
    private int FRONT_CAMERA_INDEX = 1;
    private int BACK_CAMERA_INDEX = 0;
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT ;
    public boolean frontFacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initGuiAndAnimation();
        initExtras();
        initializeFlags();
        initListeners();
        initCamera();
        Tools.loadAlbum(getApplicationContext());

        hash = OpenCameraActivity.retrieveHash(getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraObj != null) {
            stopCamera();
        }
        initCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        setFlagsTrue();
        int dRotation = display.getRotation();
        PREVIEW_ROTATION_ANGLE angleEnum = PREVIEW_ROTATION_ANGLE.ROT_0;

        switch (dRotation) {
            case 0:  // Device is not rotated
                displayAngle = 90;
                angleEnum = PREVIEW_ROTATION_ANGLE.ROT_90;
                break;

            case 1:    // Landscape left
                displayAngle = 0;
                angleEnum = PREVIEW_ROTATION_ANGLE.ROT_0;
                break;

            case 2:  // Device upside down
                displayAngle = 270;
                angleEnum = PREVIEW_ROTATION_ANGLE.ROT_270;
                break;

            case 3:    // Landscape right
                displayAngle = 180;
                angleEnum = PREVIEW_ROTATION_ANGLE.ROT_180;
                break;
        }

        cameraObj.setDisplayOrientation(displayAngle);

        if (isDevCompat) {

//            loadAlbum();
            if (faceProc == null) {
                faceProc = FacialProcessing.getInstance();
            }
//            byte[] dataFace = faceProc.serializeRecogntionAlbum();

//            Log.e(TAG, "onCreate: "+ dataFace.length );

//            faceProc.setProcessingMode(FacialProcessing.FP_MODES.FP_MODE_STILL); // Static Image
            faceProc.setProcessingMode(FacialProcessing.FP_MODES.FP_MODE_VIDEO);

            Parameters params = cameraObj.getParameters();
            Size previewSize = params.getPreviewSize();
//            params.set("iso", 400);

//            Log purpose only
//            int previewWidth = params.getPreviewSize().width;
//            int previewHeight = params.getPreviewSize().height;
//            Log.e(TAG, "Preview Size = " + previewWidth + " x " + previewHeight);

            // View Mode : Landscape - Portrait
            // Landscape mode camera : Front , Back
            if (this.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE && !switchCamera) {
                faceProc.setFrame(data, previewSize.width, previewSize.height, true, angleEnum);
            } else if (this.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE && switchCamera) {
                faceProc.setFrame(data, previewSize.width, previewSize.height, false, angleEnum);
            }

            // Portrait mode camera : Front
            else if (this.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_PORTRAIT && !switchCamera) {
                faceProc.setFrame(data, previewSize.width, previewSize.height, true, angleEnum);
            } else {
                faceProc.setFrame(data, previewSize.width, previewSize.height, false, angleEnum);
            }

            // Number of Face in the frame.
            numFaces = faceProc.getNumFaces();

            if (numFaces == 0) {
                // No Face Detected on Frame
//                Log.e(TAG, "No Face Detected");
                smile.setChecked(false);
                eyeBlink.setChecked(false);
                gazeAngle.setChecked(false);

                if (drawView != null) {
                    preview.removeView(drawView);
                    drawView = new PaintFaceView(this, null, false);
                    preview.addView(drawView);
                }

            } else {
//                Log.e(TAG, "Face Detected");
                faceArray = faceProc.getFaceData();

                // Face Detected but not have value
                if (faceArray == null) {

                    Log.e(TAG, "onPreviewFrame: " + "No Face value");

                } else {
                    int surfaceWidth = mPreview.getWidth();
                    int surfaceHeight = mPreview.getHeight();

                    faceProc.normalizeCoordinates(surfaceWidth, surfaceHeight);

                    Log.e(TAG, "onPreviewFrame: personId " + faceArray[0].getPersonId());

                    if (identifyPerson && faceArray[0].getPersonId() != -111) {
//                        Log.e(TAG, "onPreviewFrame: Face Exist" );
                        String selectedPersonId = Integer.toString(faceArray[0].getPersonId());
                        Iterator<HashMap.Entry<String, String>> iter = hash.entrySet().iterator();
                        // Default name is the person is unknown
                        selectedPersonName = "Not Identified";
                        while (iter.hasNext()) {
                            Log.e(TAG, "onPreviewFrame: check Hash");
                            HashMap.Entry<String, String> entry = iter.next();
                            if (entry.getValue().equals(selectedPersonId)) {
                                selectedPersonName = entry.getKey();
                                t_stopCamera = (System.nanoTime() - t_startCamera) / 1000000000.0D;
                            }
                        }

//                        Log.e(TAG, "onPreviewFrame: t_start"+t_startCamera );

                        Class<?> origin_class = this.getClass();

//                        Log.e(TAG, "onPreviewFrame: init " + origin_class.getSimpleName());
//                        Log.e(TAG, "onPreviewFrame: origin " + str_origin_class);

//                        if (str_origin_class.equals(NativeKISmartRegisterFragment.class.getSimpleName())) {
//                            origin_class = NativeKISmartRegisterActivity.class;
//                        } else if (str_origin_class.equals(NativeKBSmartRegisterFragment.class.getSimpleName())) {
//                            origin_class = NativeKBSmartRegisterActivity.class;
//                        } else if (str_origin_class.equals(NativeKIAnakSmartRegisterFragment.class.getSimpleName())) {
//                            origin_class = NativeKIAnakSmartRegisterActivity.class;
//                        } else if (str_origin_class.equals(NativeKIANCSmartRegisterFragment.class.getSimpleName())) {
//                            origin_class = NativeKIANCSmartRegisterActivity.class;
//                        } else if (str_origin_class.equals(NativeKIPNCSmartRegisterFragment.class.getSimpleName())) {
//                            origin_class = NativeKIPNCSmartRegisterActivity.class;
//                        }

//                        Log.e(TAG, "onPreviewFrame: "+ selectedPersonName );
//                        Log.e(TAG, "onPreviewFrame: " + origin_class.getSimpleName());
//                        Intent intent = new Intent(OpenCameraActivity.this, origin_class);

                        Intent intent = new Intent();
                        intent.putExtra("org.ei.opensrp.indonesia.face.face_mode", true);
                        intent.putExtra("org.ei.opensrp.indonesia.face.base_id", selectedPersonName);
                        intent.putExtra("org.ei.opensrp.indonesia.face.proc_time", t_stopCamera);
//                        setResult(RESULT_OK);
//                        startActivity(intent);
                        setResult(2, intent);
                        finish();
//                        startActivity(getIntent());


                    } else {
                        Log.e(TAG, "onPreviewFrame: New Record ");
                    }

//                    Options
                    if (faceEyesMouthDetectionPressed) {
                        // Remove the previously created view to avoid unnecessary stacking of Views.
                        preview.removeView(drawView);
                        drawView = new PaintFaceView(this, faceArray, true);
                        Log.e(TAG, "onPreviewFrame: " + faceArray[0].getPersonId());
                        preview.addView(drawView);

                    } else {

                        preview.removeView(drawView);
                        drawView = new PaintFaceView(this, null, false);
                        preview.addView(drawView);

                    }

                    if (perfectModeButtonPress) {
                        for (int i = 0; i < numFaces; i++) {
                            if (faceArray[i].getSmileValue() < 75) {
                                smileFlag = false;
                                smile.setChecked(false);
                            } else {
                                smile.setChecked(true);
                            }

                            if (faceArray[i].getLeftEyeBlink() > 50 && faceArray[i].getRightEyeBlink() > 50) {
                                blinkFlag = false;
                                eyeBlink.setChecked(false);
                            } else {
                                eyeBlink.setChecked(true);
                            }

                            if (faceArray[i].getEyeHorizontalGazeAngle() < -8 || faceArray[i].getEyeHorizontalGazeAngle() > 8) {
                                horizontalGazeAngleFlag = false;
                                gazeAngle.setChecked(false);
                            } else if (faceArray[i].getEyeVerticalGazeAngle() < -8 || faceArray[i].getEyeVerticalGazeAngle() > 8) {
                                verticalGazeAngleFlag = false;
                                gazeAngle.setChecked(false);
                            } else {
                                gazeAngle.setChecked(true);
                            }

                        }
                        if (smileFlag && blinkFlag && horizontalGazeAngleFlag && verticalGazeAngleFlag && cameraButtonPress) {
                            try {
                                cameraObj.takePicture(shutterCallback, rawCallback, jpegCallback);
                            } catch (Exception e) {

                            }

                            frameAnimation.stop();
                            cameraButton.setBackgroundResource(R.drawable.camera_btn);
                            cameraButton.invalidate();
                            cameraButtonPress = false;
                            animationPress = false;
                            smile.setVisibility(View.INVISIBLE);
                            gazeAngle.setVisibility(View.INVISIBLE);
                            eyeBlink.setVisibility(View.INVISIBLE);
                            smile.setChecked(false);
                            eyeBlink.setChecked(false);
                            gazeAngle.setChecked(false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: request " + requestCode);
        Log.e(TAG, "onActivityResult: result " + resultCode);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    if (requestCode == 0) {
                        Uri selectedImageUri = data.getData();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(selectedImageUri);
                        startActivity(intent);
                    }
                }
                break;

            case 2:
                if (resultCode == 2) {
                    Intent i = new Intent();
                    setResult(2, i);
                    finish();

                }
                break;
            // For the rest don't do anything.
        }
    }

    /**
     *
     */
    private void initGuiAndAnimation() {
        setContentView(R.layout.activity_fr_main);

        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        animationFadeOut = AnimationUtils.loadAnimation(OpenCameraActivity.this, R.anim.fadeout);
        cameraButton = (ImageView) findViewById(R.id.cameraButton);

        settingsButton = (ImageView) findViewById(R.id.settings);
        settingsButton.setVisibility(View.INVISIBLE);

        chooseCameraButton = (ImageView) findViewById(R.id.chooseCamera);
        chooseCameraButton.setImageResource(R.drawable.camera_revert1);
        chooseCameraButton.setVisibility(View.VISIBLE);

        galleryButton = (ImageView) findViewById(R.id.gallery);
        galleryButton.setImageResource(R.drawable.ic_collections_white_24dp);
        galleryButton.setVisibility(View.INVISIBLE);

//        Settings Option Menu
        menu = (ImageView) findViewById(R.id.menu);
        menu.setVisibility(View.INVISIBLE);

        switchCameraButton = (ImageView) findViewById(R.id.switchCamera);
        switchCameraButton.setImageResource(R.drawable.camera_revert2);
        switchCameraButton.setVisibility(View.INVISIBLE);

        perfectPhotoButton = (ImageView) findViewById(R.id.perfectMode);
        perfectPhotoButton.setVisibility(View.INVISIBLE);
        perfectPhotoButton.setImageResource(R.drawable.ic_perfect_mode_off);

        flashButton = (ImageView) findViewById(R.id.flash);
        flashButton.setVisibility(View.INVISIBLE);

        clientListButton = (ImageView) findViewById(R.id.clientList);
        clientListButton.setVisibility(View.INVISIBLE);
        clientListButton.setImageResource(R.drawable.ic_faces);

        // Change the flash image depending on the button that is being pressed.
        if (flashButtonPress == "FLASH_MODE_OFF") {
            flashButton.setImageResource(R.drawable.ic_flash_off);
        } else {
            flashButton.setImageResource(R.drawable.ic_flash_green);
        }

        // Detect Eyes and Mouth.
        if (!faceEyesMouthDetectionPressed) {
            faceEyesMouthBtn = (ImageView) findViewById(R.id.faceDetection);
            faceEyesMouthBtn.setImageResource(R.drawable.fr_face_detection);
        } else {
            faceEyesMouthBtn = (ImageView) findViewById(R.id.faceDetection);
            faceEyesMouthBtn.setImageResource(R.drawable.fr_face_detection_on);
        }
        faceEyesMouthBtn.setVisibility(View.INVISIBLE);


        initializeCheckBoxes();

    }

    private void initListeners() {
        chooseCameraActionListener();
        galleryActionListener();
        cameraActionListener();
//        settingsActionListener();
        faceDetectionActionListener();
        perfectPhotoActionListener();
        flashActionListener();

        clientListActionListener();
    }

    private void initExtras() {
        Bundle extras = getIntent().getExtras();
        updated = extras.getBoolean("org.smartregister.facialrecognition.OpenCameraActivity.updated");
        entityId = extras.getString("org.smartregister.facialrecognition.PhotoConfirmationActivity.id");
        identifyPerson = extras.getBoolean("org.smartregister.facialrecognition.PhotoConfirmationActivity.identify");
        str_origin_class = extras.getString("org.smartregister.facialrecognition.PhotoConfirmationActivity.origin");
    }

    /**
     *
     */
    private void initializeFlags() {
        isDevCompat = false;
        settingsButtonPress = false;
        faceEyesMouthDetectionPressed = false;
        perfectModeButtonPress = false;
        cameraButtonPress = false;
        animationPress = false;
        flashButtonPress = "FLASH_MODE_OFF";
        smileFlag = true;
        blinkFlag = true;
        horizontalGazeAngleFlag = true;
        verticalGazeAngleFlag = true;
        activityStartedOnce = false;
    }

    private ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    private PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    private PictureCallback jpegCallback = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            savePicture(data);
            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontFacing = true;
            }
        }

    };

    // Stop the camera preview. release the camera.
    // Release the FacialActivity Processing object.
    // Make the objects null.
    private void stopCamera() {

        if (cameraObj != null) {
            cameraObj.stopPreview();
            cameraObj.setPreviewCallback(null);
            preview.removeView(mPreview);
            cameraObj.release();
//            if (isDevCompat) {
//                faceProc.release();
//                faceProc = null;
//            }
        }
        cameraObj = null;
    }

    private void initCamera() {

        // Check to see if the FacialProc feature is supported in the device or no.
        isDevCompat = FacialProcessing.isFeatureSupported(FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_PROCESSING);

        if (isDevCompat && faceProc == null) {
            Log.e(TAG, "Feature is supported");
            // Calling the FacialActivity Processing Constructor.
            faceProc = FacialProcessing.getInstance();
            faceProc.setRecognitionConfidence(Tools.CONFIDENCE_VALUE);

        } else if (!isDevCompat && !activityStartedOnce) {
            Log.e(TAG, "Feature is NOT supported");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OpenCameraActivity.this);

            // set title
            alertDialogBuilder.setTitle(FaceConstants.UNSUPPORTED_TITLE);

            // set dialog message
            alertDialogBuilder
                    .setMessage(FaceConstants.UNSUPPORTED_MSG)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            activityStartedOnce = true;
        }

        if (!switchCamera) {
            // Open the Front camera
            cameraObj = Camera.open(FRONT_CAMERA_INDEX);
        } else {
            // Open the back camera
            cameraObj = Camera.open(BACK_CAMERA_INDEX);
        }

        // Create a new surface on which Camera will be displayed.
        mPreview = new CameraPreview(OpenCameraActivity.this, cameraObj);
        preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        cameraObj.setPreviewCallback(OpenCameraActivity.this);

    }

    private void initializeCheckBoxes() {
        smile = (CheckBox) findViewById(R.id.smileCheckBox);
        smile.setVisibility(View.GONE);
        smile.setTextColor(Color.YELLOW);
        gazeAngle = (CheckBox) findViewById(R.id.gazeAngleCheckBox);
        gazeAngle.setVisibility(View.GONE);
        gazeAngle.setTextColor(Color.YELLOW);
        eyeBlink = (CheckBox) findViewById(R.id.blinkCheckBox);
        eyeBlink.setVisibility(View.GONE);
        eyeBlink.setTextColor(Color.YELLOW);
    }

    private void clientListActionListener() {
        clientListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenCameraActivity.this, ClientsListActivity.class);

                startActivity(intent);
            }
        });

    }

    private void chooseCameraActionListener() {
        chooseCameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!switchCamera) {
                    stopCamera();
                    chooseCameraButton.setImageResource(R.drawable.camera_revert1);
                    switchCamera = true;
                    initCamera();
                } else {
                    stopCamera();
                    chooseCameraButton.setImageResource(R.drawable.camera_revert2);
                    switchCamera = false;
                    initCamera();
                }
            }
        });
    }

    /*
     * Function to detect the on click listener for the GALLERY button.
     */
    private void galleryActionListener() {
        galleryButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
            }
        });
    }

    /*
     * Function to detect the on click listener for the camera shutter button.
     */
    private void cameraActionListener() {

        Log.e(TAG, "cameraActionListener: "+ numFaces );
        cameraButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (numFaces != -1) {
                    if (!perfectModeButtonPress) {
                        cameraObj.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {

                                cameraObj.takePicture(shutterCallback, rawCallback, jpegCallback);

                            }
                        });

                    } else {
                        // Play animation
                        cameraButton.setBackgroundResource(R.drawable.fr_spin_animation);
                        frameAnimation = (AnimationDrawable) cameraButton.getBackground();

                        checkBoxVisiblity(true);    // As soon as the shutter button is pressed, make the check boxes visible.

                        // Start the animation (looped playback by default).
                        if (!animationPress) {
                            frameAnimation.start();
                            animationPress = true;
                            cameraButtonPress = true;
                        } else {
                            // If the shutter button is stopped then make the check boxes invisible
                            checkBoxVisiblity(false);
                            // and un-check them.
                            textBoxChecked(false);
                            frameAnimation.stop();
                            cameraButton.setBackgroundResource(R.drawable.camera_revert1);
                            animationPress = false;
                            cameraButtonPress = false;
                        }
                    }
                } else {
                    Toast.makeText(OpenCameraActivity.this, "No Face Detected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
     * Function to detect the on click listener for the switch camera button.
     */
    private void settingsActionListener() {
        settingsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!settingsButtonPress) {
                    // Disable the buttons if the facial processing feature is not supported.
                    if (isDevCompat) {
                        faceEyesMouthBtn.setVisibility(View.VISIBLE);
                        perfectPhotoButton.setVisibility(View.VISIBLE);
                        clientListButton.setVisibility(View.VISIBLE);

                    }
                    menu.setVisibility(View.VISIBLE);
                    switchCameraButton.setVisibility(View.VISIBLE);
                    if (switchCamera)// If facing back camera then only make it visible or else dont.
                        flashButton.setVisibility(View.VISIBLE);
                    settingsButtonPress = true;
                } else {
                    faceEyesMouthBtn.setVisibility(View.INVISIBLE);
                    menu.setVisibility(View.INVISIBLE);
                    switchCameraButton.setVisibility(View.INVISIBLE);
                    perfectPhotoButton.setVisibility(View.INVISIBLE);
                    flashButton.setVisibility(View.INVISIBLE);
                    settingsButtonPress = false;
                    clientListButton.setVisibility(View.INVISIBLE);
                }
            }

        });

        // On touch listener for the settings button to make it highlighted when pressed
        settingsButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    settingsButton.setImageResource(R.drawable.ic_settings_green_24dp);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    settingsButton.setImageResource(R.drawable.ic_settings_white_24dp);
                }
                return false;
            }
        });
    }

    /*
     * Interactive Draw of Eyes and Mouth position.
     */
    private void faceDetectionActionListener() {
        faceEyesMouthBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!faceEyesMouthDetectionPressed) {
                    faceEyesMouthBtn.setImageResource(R.drawable.fr_face_detection_on);
                    fadeOutAnimation();
                    faceEyesMouthDetectionPressed = true;
                    settingsButtonPress = false;
                } else {
                    faceEyesMouthBtn.setImageResource(R.drawable.fr_face_detection);
                    fadeOutAnimation();
                    faceEyesMouthDetectionPressed = false;
                    settingsButtonPress = false;
                }
            }
        });

    }

    /*
    Control Flash Mode
     */
    private void flashActionListener() {
        flashButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Parameters params = cameraObj.getParameters();
                String flashMode = params.getFlashMode();
                if (flashMode == null)
                    return;
                else {
                    // On-Off Flash
                    if (flashButtonPress == "FLASH_MODE_OFF") {
                        params.setFlashMode(Parameters.FLASH_MODE_ON);
                        flashButton.setImageResource(R.drawable.ic_flash_green);
                        cameraObj.setParameters(params);
                        fadeOutAnimation();
                        flashButtonPress = "FLASH_MODE_ON";
                        settingsButtonPress = false;
                        return;
                    } else {
                        params.setFlashMode(Parameters.FLASH_MODE_OFF);
                        flashButton.setImageResource(R.drawable.ic_flash_off);
                        cameraObj.setParameters(params);
                        fadeOutAnimation();
                        flashButtonPress = "FLASH_MODE_OFF";
                        settingsButtonPress = false;
                        return;
                    }
                }
            }
        });

    }

    /*
     * Function to detect the on click listener for the perfect photo mode button.
     */
    private void perfectPhotoActionListener() {
        perfectPhotoButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (perfectModeButtonPress) {
                    perfectPhotoButton.setImageResource(R.drawable.ic_perfect_mode_off);
                    fadeOutAnimation();
                    settingsButtonPress = false;
                    perfectModeButtonPress = false;
                } else {
                    perfectPhotoButton.setImageResource(R.drawable.ic_perfect_mode_on);
                    fadeOutAnimation();
                    settingsButtonPress = false;
                    perfectModeButtonPress = true;
                }
            }
        });
    }

    /* Animation menu display
     *
     */
    private void fadeOutAnimation() {

        // Activated features only Supported Device
        if (isDevCompat) {
            faceEyesMouthBtn.startAnimation(animationFadeOut);
            perfectPhotoButton.startAnimation(animationFadeOut);
        }
        menu.startAnimation(animationFadeOut);
        switchCameraButton.startAnimation(animationFadeOut);
        clientListButton.startAnimation(animationFadeOut);

        if (switchCamera) {
            flashButton.startAnimation(animationFadeOut);
        }
        faceEyesMouthBtn.setVisibility(View.GONE);
        menu.setVisibility(View.GONE);
        switchCameraButton.setVisibility(View.GONE);
        perfectPhotoButton.setVisibility(View.GONE);
        flashButton.setVisibility(View.GONE);

    }

    /*
     * Function to take the raw YUV byte array and do the necessary conversions to save it.
     */
    private void savePicture(byte[] data) {
        cameraObj.startPreview();
        Intent intent = new Intent(this, PhotoConfirmationActivity.class);
        // This is when smart shutter feature is not ON. Take the photo generally.
        if (data != null) {
            intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity", data);
        }
        intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.switchCamera", switchCamera);
        intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.orientation", displayAngle);
        intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.id", entityId);
        intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.identify", identifyPerson);
        intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.origin", str_origin_class);
        intent.putExtra("org.smartregister.facialrecognition.PhotoConfirmationActivity.updated", updated);

        startActivityForResult(intent, 2);
    }

    private void setFlagsTrue() {
        smileFlag = true;
        blinkFlag = true;
        horizontalGazeAngleFlag = true;
        verticalGazeAngleFlag = true;
    }

    /*
     * A helper function to handle the visibility of the check boxes.
     */
    private void checkBoxVisiblity(boolean visible) {

        if (visible) {
            smile.setVisibility(View.VISIBLE);
            gazeAngle.setVisibility(View.VISIBLE);
            eyeBlink.setVisibility(View.VISIBLE);
        } else {
            smile.setVisibility(View.INVISIBLE);
            gazeAngle.setVisibility(View.INVISIBLE);
            eyeBlink.setVisibility(View.INVISIBLE);
        }

    }

    /*
     *  A helper function to handle the CHECK-MARK of the Check-Text Boxes.
     */
    private void textBoxChecked(boolean check) {
        if (check) {
            smile.setChecked(true);
            eyeBlink.setChecked(true);
            gazeAngle.setChecked(true);
        } else {
            smile.setChecked(false);
            eyeBlink.setChecked(false);
            gazeAngle.setChecked(false);
        }

    }

    protected void saveHash(HashMap<String, String> hashMap, Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.HASH_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        Log.e(TAG, "Hash Save Size Clients List= " + hashMap.size());
        for (String s : hashMap.keySet()) {
            editor.putString(s, hashMap.get(s));
        }
        editor.apply();
    }

    /**
     * Get Client List
     *
     * @param context
     * @return
     */
    public static HashMap<String, String> retrieveHash(Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.HASH_NAME, 0);
        HashMap<String, String> hash = new HashMap<>();
        hash.putAll((Map<? extends String, ? extends String>) settings.getAll());
        return hash;
    }


}

package org.smartregister.mylibrary;

import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

/**
 * Created by sid on 9/28/17.
 */

public class MainLib {

    MainLib(){

    }

    boolean isCompat(){
        return (FacialProcessing.isFeatureSupported(FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_PROCESSING));
    }
}

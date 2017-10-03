package org.smartregister.facialrecognition.sample.application;

import org.smartregister.CoreLibrary;
import org.smartregister.facialrecognition.FacialRecognitionLibrary;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by sid on 10/2/17.
 */

public class SampleApplication extends DrishtiApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        context = context.getInstance();

        context.updateApplicationContext(getApplicationContext());

        // Init Module
        CoreLibrary.init(context);

        FacialRecognitionLibrary.init(context, getRepository());
    }

    @Override
    public void logoutCurrentUser() {

    }

    @Override
    public Repository getRepository() {
//        try {
//            if (repository == null)
//        }
        return null;
    }
}

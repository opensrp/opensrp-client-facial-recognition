package org.smartregister.facialrecognition.sample.application;

import android.util.Log;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.facialrecognition.FacialRecognitionLibrary;
import org.smartregister.facialrecognition.sample.repository.SampleRepository;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by sid on 10/2/17.
 */

public class SampleApplication extends DrishtiApplication {

    private static final String TAG = SampleApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());

        // Init Module
        CoreLibrary.init(context);

        FacialRecognitionLibrary.init(context, getRepository());

    }

    @Override
    public void logoutCurrentUser() {

    }

    public static synchronized SampleApplication getInstance(){
        return (SampleApplication) mInstance;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null)
                repository = new SampleRepository(getInstance().getApplicationContext(), context);
        } catch (UnsatisfiedLinkError e){
            Log.e(TAG, "Error on getRepository: "+ e.getMessage() );
        }
        return repository;
    }
}

package org.smartregister.facialrecognition;

import android.util.Log;

import org.smartregister.Context;

import org.smartregister.facialrecognition.repository.ImageRepository;
import org.smartregister.repository.Repository;

/**
 * Created by wildan on 10/2/17.
 */

public class FacialRecognitionLibrary {

    private static final String TAG = FacialRecognitionLibrary.class.getSimpleName();
    private final Repository repository;
    private final Context context;
    private static FacialRecognitionLibrary instance;
    private ImageRepository facialRepository;

    public static void init(Context context, Repository repository){
        if (instance == null) instance = new FacialRecognitionLibrary(context, repository);
    }

    private FacialRecognitionLibrary(Context context, Repository repository){
        this.context = context;
        this.repository = repository;
    }

    public static FacialRecognitionLibrary getInstance(){
        if (instance == null)
            throw new IllegalStateException("Instance does not exist. Call "+ FacialRecognitionLibrary.class.getName()+".init method in the onCreate method of your Application class!.");

        return instance;
    }

    public ImageRepository facialRepository() {
        if (facialRepository == null)
            facialRepository = new ImageRepository(getRepository());

        return facialRepository;
    }

    public Repository getRepository() {
        if (repository == null) Log.e(TAG, "getRepository: "+ null );
        return repository;
    }
}

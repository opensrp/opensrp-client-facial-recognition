package org.smartregister.facialrecognition;

import org.smartregister.Context;

import org.smartregister.repository.Repository;

/**
 * Created by sid on 10/2/17.
 */

public class FacialRecognitionLibrary {

    private final Repository repository;
    private final Context context;
    private static FacialRecognitionLibrary instance;

    public static void init(Context context, Repository repository){
        if (instance == null) instance = new FacialRecognitionLibrary(context, repository);
    }
    private FacialRecognitionLibrary(Context context, Repository repository){
        this.context = context;
        this.repository = repository;
    }

}

package org.smartregister.facialrecognition.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.facialrecognition.domain.FacialWrapper;

/**
 * Created by sid on 10/10/17.
 */

public class RecordFacialDialogFragment extends DialogFragment {

    public static RecordFacialDialogFragment newInstance(FacialWrapper tag){

        RecordFacialDialogFragment recordFacialDialogFragment = new RecordFacialDialogFragment();

        return recordFacialDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}

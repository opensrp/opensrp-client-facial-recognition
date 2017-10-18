package org.smartregister.facialrecognition.sample.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by sid on 10/12/17.
 */

public class FacialRegisterActivityPageAdapter extends PagerAdapter {
    public FacialRegisterActivityPageAdapter(FragmentManager supportFragmentManager, Fragment mBaseFragment, Fragment[] otherFragments) {
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}

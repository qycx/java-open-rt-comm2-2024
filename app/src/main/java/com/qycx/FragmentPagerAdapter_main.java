package com.qycx;

import com.qycx.base.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentPagerAdapter_main extends FragmentPagerAdapter {
    private int PAGER_COUNT = 3;
    //
    public Fragment_msgList mFragment_msg1 = null;
    public Fragment_imObjList mFragment_imObj1 = null;
    public Fragment_me mFragment_me = null;


    public FragmentPagerAdapter_main(FragmentManager fm) {
        super(fm);
        mFragment_msg1 = new Fragment_msgList();
        mFragment_imObj1 = new Fragment_imObjList();
        mFragment_me = new Fragment_me();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        //
        switch (position) {
            case MainActivity.PAGE_ONE:
                fragment = mFragment_msg1;
                break;
            case MainActivity.PAGE_TWO:
                fragment = mFragment_imObj1;
                break;
            case MainActivity.PAGE_THREE:
                fragment = mFragment_me;
                break;

            default:
                break;
        }
        return fragment;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int doClean() {
        Log.d("adapter doClean enter");
        this.mFragment_msg1 = null;
        this.mFragment_imObj1 = null;
        this.mFragment_me = null;
        //
        return 0;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}

package com.qycx;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class FragmentPagerAdapter_dlgTalk extends FragmentPagerAdapter {
    private int PAGER_COUNT = 1;//2;
    public Fragment_dlgTalk_msgList mFragment_dlgTalk_msgList1 = null;

    public FragmentPagerAdapter_dlgTalk(FragmentManager fm)  {
        super(fm);
        mFragment_dlgTalk_msgList1 = new Fragment_dlgTalk_msgList();
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case Activity_dlgTalk.PAGE_ONE:
                fragment = mFragment_dlgTalk_msgList1;
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


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}


package com.br.mtgcardmanager.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.br.mtgcardmanager.View.FragmentHave;
import com.br.mtgcardmanager.View.FragmentSearch;
import com.br.mtgcardmanager.View.FragmentWant;


public class PagerAdapter extends FragmentStatePagerAdapter{
    int mNumOfTabs;


    public PagerAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentHave tabHave = new FragmentHave();
                return tabHave;
            case 1:
                FragmentSearch tabSearch = new FragmentSearch();
                return tabSearch;
            case 2:
                FragmentWant tabWant = new FragmentWant();
                return tabWant;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }



}
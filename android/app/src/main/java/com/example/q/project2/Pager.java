package com.example.q.project2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Map;

public class Pager extends FragmentStatePagerAdapter {
    private int tabCount;
    private Tab1Fragment tab1;
    private Tab2Fragment tab2;
    private Tab3Fragment tab3;

    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
        tab1 = new Tab1Fragment();
        tab2 = new Tab2Fragment();
        tab3 = new Tab3Fragment();
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return tab1;
            case 1:
                return tab2;
            case 2:
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
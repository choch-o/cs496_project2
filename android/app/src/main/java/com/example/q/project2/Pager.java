package com.example.q.project2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class Pager extends FragmentStatePagerAdapter {
    private int tabCount;
    private TabA1Fragment tabA1;
    private TabA2Fragment tabA2;
    private TabBFragment tab2;
    private TabCFragment tab3;

    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
        tabA1 = new TabA1Fragment();
        tabA2 = new TabA2Fragment();
        tab2 = new TabBFragment();
        tab3 = new TabCFragment();
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return tabA1;
            case 1:
                return tabA2;
            case 2:
                return tab2;
            case 3:
                return tab3;
            default:
                return null;
        }
    }

    public void pass(String phone) {
        tab3.onUserEnterPhone(phone);
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
package com.plus.cloudcontacts.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.plus.cloudcontacts.R;
import com.plus.cloudcontacts.http.PostWork;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	private MenuActivity pActivity;
    public SectionsPagerAdapter(FragmentManager fm,MenuActivity pActivity) {
        super(fm);
        this.pActivity = pActivity;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a DummySectionFragment (defined as a static inner class
        // below) with the page number as its lone argument.
    	if (position==0){
    		Fragment fragment = new GroupSectionFragment();
    		Bundle args = new Bundle();
    		ArrayList<String> groupName = new ArrayList<String>();
	        ArrayList<String> groupSize = new ArrayList<String>();
	        ArrayList<String> groupMem = new ArrayList<String>();
	        for(int i = 0; i<pActivity.myGroups.size(); i++){
	        	Map<String,String> item = pActivity.myGroups.get(i);
	        	groupName.add(item.get("name"));
	        	groupSize.add(item.get("size"));
	        	groupMem.add(item.get("mem"));
	        }
	        args.putStringArrayList("groupName",groupName);
	        args.putStringArrayList("groupSize",groupSize);
	        args.putStringArrayList("groupMem",groupMem);
	        args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
	        fragment.setArguments(args);
	        return fragment;
    	}else{
	        Fragment fragment = new DummySectionFragment();
	        Bundle args = new Bundle();
	        args.putString("json",pActivity.getIntent().getExtras().getString("bean"));
	        args.putString("fromGroup",pActivity.getIntent().getExtras().getString("group"));
	        
	        args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
	        fragment.setArguments(args);
	        return fragment;
    	}
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return pActivity.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return pActivity.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }
}
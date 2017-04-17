package com.taozhang.filetransition.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taozhang.filetransition.R;

/**
 * Description:
 * Created by taozhang on 2016/5/10.
 * Company:Geowind,University of South China.
 * ContactQQ:962076337
 *
 * @updateAuthor taozhang
 * @updateDate 2016/5/10
 */
@SuppressLint({ "ValidFragment", "NewApi" })
public class ProfileFragment extends Fragment {
    private View profile;
    private Context context;

    private  static ProfileFragment fragment = null;
    private ProfileFragment(Context context) {
        this.context = context;
    }
    @SuppressLint("ValidFragment")
	public static ProfileFragment getInstance(Context context) {
        if (fragment == null) {
            synchronized (OperateComputerFragment.class) {
                if (fragment == null) {
                    fragment = new ProfileFragment(context);
                }
            }
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profile = inflater.inflate(R.layout.frag_profile,null);
        getComponent();
        return profile;
    }

    private void getComponent() {

    }
}

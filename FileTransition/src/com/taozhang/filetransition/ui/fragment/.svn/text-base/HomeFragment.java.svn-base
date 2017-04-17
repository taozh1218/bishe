package com.taozhang.filetransition.ui.fragment;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.ui.SearchFragment;

/**
 * Description:采用单例模式，构造函数私有化，对象私有，获取对象的方法公有
 * Created by taozhang on 2016/5/9.
 * Company:Geowind,University of South China.
 * ContactQQ:962076337
 *
 * @updateAuthor taozhang
 * @updateDate 2016/5/9
 */
@SuppressLint({ "ValidFragment", "NewApi" })
public class HomeFragment extends Fragment {

    private Context context;
    private View home;
    @SuppressLint("NewApi")
	private Fragment current_frag;
    /**
     * 单例模式 实例
     */
    private static HomeFragment homeFragment_instance = null;

    //私有构造函数
    private HomeFragment(Context context) {
        this.context = context;
    }

    public static HomeFragment getInstance(Context context) {
        if (homeFragment_instance == null) {
            synchronized (HomeFragment.class) {
                if (homeFragment_instance == null) {
                    homeFragment_instance = new HomeFragment(context);
                }
            }
        }
        return homeFragment_instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the view of fragment
        home = inflater.inflate(
                R.layout.frag_home, null);
        getComponent();
        return home;
    }

    private void getComponent() {
        Button btn_send = (Button) home.findViewById(R.id.btn_sendFileHomeFrag);
        Button btn_accept = (Button) home.findViewById(R.id.btn_acceptFileHomeFrag);

        btn_send.setOnClickListener(mOnClickListener);
        btn_accept.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent = null;
            switch (id) {
                case R.id.btn_sendFileHomeFrag://发送
                    intent = new Intent(getActivity(), SearchFragment.class);
                    startActivity(intent);
                    break;
                case R.id.btn_acceptFileHomeFrag://接收

                    break;
            }
        }
    };


}

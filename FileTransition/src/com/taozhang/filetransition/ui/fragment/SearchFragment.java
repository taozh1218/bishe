package com.taozhang.filetransition.ui.fragment;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfServerListView;
import com.taozhang.filetransition.bean.ServiceBean;
import com.taozhang.filetransition.ui.FileListActivity;
import com.taozhang.filetransition.ui.customComponent.SearchDevicesView;
import com.taozhang.filetransition.util.ThreadTask;

public class SearchFragment extends FragmentActivity implements OnItemClickListener {

    public static int PORT = 10088;
    private ListView lv;
    private AdapterOfServerListView adapter;
    private ArrayList<ServiceBean> hosts;
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 30, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(255),
            new ThreadPoolExecutor.CallerRunsPolicy());
    private SearchDevicesView searchView;
    public static String localIp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initComponent();
        new MyAsyncTask().execute();//启动
    }

    private void initComponent() {

        lv = (ListView) findViewById(R.id.lv_searchHostAct);
        searchView = (SearchDevicesView) findViewById(R.id.searchDevice);
        searchView.setWillNotDraw(false);

        hosts = new ArrayList<ServiceBean>();
        ServiceBean bean = new ServiceBean("张三", new InetSocketAddress("192.1.0.0",1000));
        hosts.add(bean);
        adapter = new AdapterOfServerListView(hosts,getApplicationContext());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    Handler handler = new Handler();

    /**
     * 搜索局域网下的所有服务器
     */
    private ArrayList<ServiceBean> searchService() {

        ArrayList<ServiceBean> list = new ArrayList<ServiceBean>();
        // 创建UDP链接对象
        for (int i = 1; i < 255; i++) {
            // 获取每个ip
            String ip = getIP((byte) i);
            threadPool.execute(new ThreadTask(adapter, ip, handler));
        }
        return list;

    }


    /**
     * Description:
     * Created by taozhang on 2016/5/9.
     * Company:Geowind,University of South China.
     * ContactQQ:962076337
     *
     * @updateAuthor taozhang
     * @updateDate 2016/5/9
     */
    class MyAsyncTask extends AsyncTask<Void, Void, ArrayList<ServiceBean>> {
        @Override
        protected void onPreExecute() {
            adapter.cleanData(); //清除数据
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ServiceBean> doInBackground(Void... params) {
            return searchService();
        }

        @Override
        protected void onPostExecute(ArrayList<ServiceBean> result) {
            adapter.addData(result);
            adapter.notifyDataSetChanged();

//            lv.setVisibility(View.VISIBLE);
            super.onPostExecute(result);
        }
    }


    /**
     * 获取当前网段下的指定ip
     *
     * @param i 指定的ip值
     * @return ip
     */
    private String getIP(byte i) {
        // Log.e("步骤","进入了getIP方法");
        InetAddress local = null;
        try {
            // 获取当前的ip
            if (localIp == null) {
                localIp = getLocalIpAddress();
            }
            local = InetAddress.getByName(localIp);
            Log.e("本机IP:-------------", localIp);
            byte[] address = local.getAddress();

            // 只修改最后一个字节
            address[3] = i;
            // 生成新的ip
            local = InetAddress.getByAddress(address);
            Log.e("处理后的ip————————————", local.getHostAddress());
            // 返回
            return local.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        // 返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }



	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		//ArrayList<ServiceBean> data = adapter.getData();
		// InetSocketAddress address = data.get(position).address;
		ServiceBean data = (ServiceBean) parent.getItemAtPosition(position);
		InetSocketAddress address =  data.address;
		 Intent intent = new Intent(SearchFragment.this,FileListActivity.class);
		 Bundle bundle = new Bundle();
		 bundle.putSerializable("IP", address);
		 intent.putExtra("bundle", bundle);
		 startActivity(intent);		
	}


}

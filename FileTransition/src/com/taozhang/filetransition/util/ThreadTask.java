package com.taozhang.filetransition.util;

import android.os.Handler;
import android.util.Log;

import com.taozhang.filetransition.adapter.AdapterOfServerListView;
import com.taozhang.filetransition.bean.ServiceBean;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class ThreadTask implements Runnable {
    private ArrayList<ServiceBean> list = new ArrayList<ServiceBean>();
    private AdapterOfServerListView adapter;
    private String ip;
    private Handler handler;
    public volatile static int count = 0;

    public ThreadTask(AdapterOfServerListView adapter, String ip, Handler handler) {
        super();
        this.adapter = adapter;
        this.ip = ip;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            InetAddress lIp = InetAddress.getByName("localhost");// ����ip
            Log.e("UDP�߳�����", "׼���������������Ϣ");
            DatagramSocket socket = new DatagramSocket();
            byte[] address = lIp.getAddress();
            socket.setSoTimeout(3000);
            // ����������ͱ���ip
            DatagramPacket pack = new DatagramPacket(address, address.length,
                    new InetSocketAddress(ip, 10088));
            socket.send(pack);
            ++count;//������1
            Log.e("UDP�߳�����", "�Ѿ��������������Ϣ����ǰ�߳�λ�ã�" + count);
            // ���ܷ�����������������
            DatagramPacket receivePacket = new DatagramPacket(new byte[1024],
                    1024);
            socket.receive(receivePacket);
            String hostName = new String(receivePacket.getData());
            Log.e("��ӡ���", hostName + "----------------");
            String name = new String(hostName);
            InetSocketAddress socketAddress = (InetSocketAddress) receivePacket.getSocketAddress();
            ServiceBean info = new ServiceBean(name, socketAddress);
            list.add(info);
            socket.close();

            adapter.addData(list);//����listView
            //���̸߳���ui
            handler.post(new Runnable() {

                @Override
                public void run() {
                    //adapter ��notifyDataSetChanged()һ��Ҫ�����߳�
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

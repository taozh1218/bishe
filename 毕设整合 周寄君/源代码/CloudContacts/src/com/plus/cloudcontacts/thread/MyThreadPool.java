package com.plus.cloudcontacts.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {  
    private final static int POOL_SIZE = 4;// �̳߳صĴ�С������ó�ΪCUP������2N  
    private final static int MAX_POOL_SIZE = 8;// �����̳߳ص�����߳���  
    private final static int KEEP_ALIVE_TIME = 4;// �����̵߳Ĵ��ʱ��  
    private final Executor mExecutor;  
    public MyThreadPool() {  
        // �����̳߳ع���  
        ThreadFactory factory = new PriorityThreadFactory("thread-pool", android.os.Process.THREAD_PRIORITY_BACKGROUND);  
        // ������������  
        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();  
        mExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, factory);  
    }  
    // ���̳߳���ִ���߳�  
    public void submit(Runnable command){  
        mExecutor.execute(command);  
    }  
}  

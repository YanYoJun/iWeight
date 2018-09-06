package com.plbear.iweight.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {
    private static ExecutorService sCachedPool = null;

    public static ExecutorService getCachedPool(){
        if(sCachedPool == null){
            synchronized (ThreadUtils.class){
                if(sCachedPool == null){
                    sCachedPool = Executors.newCachedThreadPool();
                }
            }
        }
        return sCachedPool;
    }
}

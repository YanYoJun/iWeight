package com.plbear.iweight.data;

import android.net.Network;
import android.os.AsyncTask;
import android.util.Log;

import com.plbear.iweight.base.App;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.http.Bean.Weight;
import com.plbear.iweight.http.HttpPost;
import com.plbear.iweight.utils.LogInfo;
import com.plbear.iweight.utils.SPUtils;

import java.nio.file.WatchEvent;
import java.util.List;

public class NetworkDataManager {
    private static final String TAG = "NetworkDataManager";

    private static NetworkDataManager sInstance;

    private NetworkDataManager() {

    }

    public static NetworkDataManager getsInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        sInstance = new NetworkDataManager();
        return sInstance;
    }

    public void add(final Data data) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                LogInfo.i(TAG,"add data to network begin");
                boolean status = SPUtils.getSP().getBoolean(Constant.PRE_KEY_LOGIN_STATUS, false);
                if (!status) {
                    return false;
                }
                try {
                    Weight weight = new Weight();
                    weight.setUserid(SPUtils.getSP().getString(Constant.PRE_KEY_USER_ID, ""));
                    weight.setTime(data.getTime() + "");
                    weight.setValue(data.getWeight() + "");
                    HttpPost httpPost = HttpPost.getInstance();
                    return httpPost.addWeight(weight);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    LogInfo.i(TAG,"add data to network end");
                }
            }
        }.execute();
    }

    public void delete(final Data data) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                LogInfo.i(TAG,"delete network data");
                try {
                    boolean status = SPUtils.getSP().getBoolean(Constant.PRE_KEY_LOGIN_STATUS, false);
                    if (!status) {
                        return false;
                    }
                    Weight weight = new Weight();
                    weight.setUserid(SPUtils.getSP().getString(Constant.PRE_KEY_USER_ID, ""));
                    weight.setTime(data.getTime() + "");
                    weight.setValue(data.getWeight() + "");
                    HttpPost httpPost = HttpPost.getInstance();
                    return httpPost.deleteWeight(weight);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    LogInfo.i(TAG,"delete network data end");
                }
            }
        }.execute();
    }

    public void update(final Data data) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean status = SPUtils.getSP().getBoolean(Constant.PRE_KEY_LOGIN_STATUS, false);
                if (!status) {
                    return false;
                }
                LogInfo.i(TAG,"update network data begin");
                try {
                    Weight weight = new Weight();
                    weight.setUserid(SPUtils.getSP().getString(Constant.PRE_KEY_USER_ID, ""));
                    weight.setTime(data.getTime() + "");
                    weight.setValue(data.getWeight() + "");
                    HttpPost httpPost = HttpPost.getInstance();
                    return httpPost.deleteWeight(weight) && httpPost.addWeight(weight);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    LogInfo.i(TAG,"update network data end");
                }
            }
        }.execute();
    }

    public void sync() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean status = SPUtils.getSP().getBoolean(Constant.PRE_KEY_LOGIN_STATUS, false);
                if (!status) {
                    return false;
                }
                try {
                    LogInfo.i(TAG,"sync network data begin");
                    HttpPost httpPost = HttpPost.getInstance();
                    List<Weight> allNetworkData = httpPost.queryAll(SPUtils.getSP().getString(Constant.PRE_KEY_USER_ID, ""));

                    if (allNetworkData == null || allNetworkData.size() == 0) {
                        return false;
                    }

                    DataManager dataManager = DataManager.getInstance();

                    List<Data> allLocalData = dataManager.queryAll();

                    for (int i = 0; i < allNetworkData.size(); i++) {
                        Weight weight = allNetworkData.get(i);
                        boolean find = false;
                        for (int j = 0; j < allLocalData.size(); j++) {
                            Data localData = allLocalData.get(j);
                            if (weight.getTime().equals(localData.getTime() + "")) {
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            try {
                                LogInfo.i(TAG,"add data to local");
                                Data insertData = new Data();
                                insertData.setTime(Long.parseLong(weight.getTime()));
                                insertData.setWeight(Float.parseFloat(weight.getValue()));
                                dataManager.addLocalData(insertData);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    for (int i = 0; i < allLocalData.size(); i++) {
                        Data localData = allLocalData.get(i);
                        boolean find = false;
                        for (int j = 0; j < allNetworkData.size(); j++) {
                            Weight networkData = allNetworkData.get(j);
                            if (networkData.getTime().equals(localData.getTime() + "")) {
                                if (!networkData.getValue().equals(localData.getWeight() + "")) {
                                    LogInfo.i(TAG,"update network data in sync");
                                    httpPost.deleteWeight(networkData);
                                    networkData.setValue(localData.getWeight() + "");
                                    httpPost.addWeight(networkData);
                                }
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            LogInfo.i(TAG,"upload network data in sync");
                            Weight weight = new Weight();
                            weight.setValue(localData.getWeight() + "");
                            weight.setTime(localData.getTime() + "");
                            weight.setUserid(SPUtils.getSP().getString(Constant.PRE_KEY_USER_ID, ""));
                            httpPost.addWeight(weight);
                        }
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    LogInfo.i(TAG,"sync data end");
                }
            }
        }.execute();
    }
}

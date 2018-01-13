package com.plbear.iweight.storage;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import com.plbear.iweight.Data.DataManager;
import com.plbear.iweight.Data.Data;
import com.plbear.iweight.Utils.ILog;
import com.plbear.iweight.Utils.OldUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by HuHu on 2016/11/17.
 */

public class XMLHelper {
    private static final String TAG_IWEIHT = "iweight";
    private static final String TAG_VERSION = "version";
    private static final String TAG_ITEM = "item";
    private static final String ATT_TIME = "time";
    private static final String ATT_DATA = "data";
    private String mDirPath = null;
    private String mFileName = "tizhong.xml";
    private Context mContext = null;
    private final static String TAG = "XMLHelper";

    public interface OnXMLListener {
        public void onReadSuccess();

        public void onSaveSuccess();

        public void onReadFail();

        public void onSaveFail();
    }

    private static final int MSG_ON_READ_SUC = 1;
    private static final int MSG_ON_READ_FAIL = 2;
    private static final int MSG_ON_SAVE_FAIL = 3;
    private static final int MSG_ON_SAVE_SUCC = 4;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            OnXMLListener listener = null;
            switch (msg.what) {
                case MSG_ON_READ_SUC:
                    listener = (OnXMLListener) msg.obj;
                    listener.onReadSuccess();
                    break;
                case MSG_ON_READ_FAIL:
                    listener = (OnXMLListener) msg.obj;
                    listener.onReadFail();
                    break;
                case MSG_ON_SAVE_FAIL:
                    listener = (OnXMLListener) msg.obj;
                    listener.onSaveFail();
                    break;
                case MSG_ON_SAVE_SUCC:
                    listener = (OnXMLListener) msg.obj;
                    listener.onSaveSuccess();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public XMLHelper(Context context) {
        mContext = context;
        mDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "jilutizhong";
        ILog.e(TAG, "savePath:" + mDirPath);
    }

    /**
     * save data to xml file
     * @param listen if save down
     * @return if save success ,return true,else ,return false
     */
    public boolean saveXML(final OnXMLListener listener) {
        ILog.d(TAG, "saveXML");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (saveXMLLocked()) {
                    ILog.d(TAG, "save suceesss");
                    Message msg = mHandler.obtainMessage(MSG_ON_SAVE_SUCC);
                    msg.obj = listener;
                    mHandler.sendMessage(msg);
                } else {
                    ILog.d(TAG, "save fail");
                    Message msg = mHandler.obtainMessage(MSG_ON_SAVE_FAIL);
                    msg.obj = listener;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
        return true;
    }

    private synchronized boolean saveXMLLocked() {
        DataManager db = DataManager.getInstance(mContext);
        ArrayList<Data> list = db.queryAll();
        if (list == null || mDirPath == null || list.size() == 0) {
            return false;
        }
        File dir = new File(mDirPath);
        ILog.d(TAG, "makedir:" + dir.mkdirs());
        File file = new File(dir, mFileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream out = new FileOutputStream(file);
            return saveXMLLocked(list, out);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private synchronized boolean saveXMLLocked(ArrayList<Data> list, OutputStream outStream) {
        try {
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(outStream, "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, TAG_IWEIHT);
            serializer.startTag(null, TAG_VERSION);
            serializer.text(String.valueOf(DBHelper.VERSION));
            serializer.endTag(null, TAG_VERSION);
            for (Data data : list) {
                serializer.startTag(null, TAG_ITEM);
                serializer.attribute(null, ATT_DATA, String.valueOf(data.getWeight()));
                serializer.attribute(null, ATT_TIME, String.valueOf(data.getTime()));
                serializer.endTag(null, TAG_ITEM);
            }
            serializer.endTag(null, TAG_IWEIHT);
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean readXML(final OnXMLListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ILog.d(TAG, "readXML");
                ArrayList<Data> list = readXMLLocked();
                if (list == null) {
                    Message msg = mHandler.obtainMessage(MSG_ON_READ_FAIL);
                    msg.obj = listener;
                    mHandler.sendMessage(msg);
                    return;
                }
                DataManager db = DataManager.getInstance(mContext);
                ArrayList<Data> listNow = db.queryAll();
                ArrayList<Data> listExport = new ArrayList<Data>();
                if (listNow == null || listNow.size() == 0) {
                    db.add(list);
                } else {
                    for (Data data : list) {
                        if (OldUtils.contains(listNow, data)) {
                            continue;
                        }
                        listExport.add(data);
                    }
                    db.add(listExport);
                }
                Message msg = mHandler.obtainMessage(MSG_ON_READ_SUC);
                msg.obj = listener;
                mHandler.sendMessage(msg);
            }
        }).start();
        return true;
    }

    private ArrayList<Data> readXMLLocked() {
        File file = new File(mDirPath, mFileName);
        if (!file.exists()) {
            return null;
        }
        ArrayList list = new ArrayList<Data>();
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(in, "UTF-8");
            int event = pullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = pullParser.getName();
                        ILog.d(TAG, "xmlname:" + name);
                        if (TAG_ITEM.equals(name)) {
                            int count = pullParser.getAttributeCount();
                            Data data = new Data();
                            for (int i = 0; i < count; i++) {
                                String attName = pullParser.getAttributeName(i);
                                if (ATT_DATA.equals(attName)) {
                                    data.setWeight(Float.valueOf(pullParser.getAttributeValue(i)));
                                } else if (ATT_TIME.equals(attName)) {
                                    data.setTime(Long.valueOf(pullParser.getAttributeValue(i)));
                                }
                            }
                            list.add(data);
                        } else if (TAG_VERSION.equals(name)) {

                    }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = pullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}

/* 
 * @Title:  ConfigBoxActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-11-8 下午4:49:33 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xhk.wifibox.R;
import com.xhk.wifibox.XHKApplication;
import com.xhk.wifibox.box.Box;
import com.xhk.wifibox.box.BoxAP;
import com.xhk.wifibox.box.BoxCache;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.utils.UDPHelper;
import com.xhk.wifibox.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author tang
 */
public class ConfigBoxActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();
    private final String PRE_WIFI_BOX_SSID = "WIFIAudio_";
    private final int MSG_OPEN_WIFISETTING = 1;
    private final int MSG_FIND_TARGETAP = 2;
    private final int MSG_CONNECT_AP_TIMEOUT = 3;
    private final int MSG_BOX_NEEDRESTART = 4;
    private final int MSG_NOTFIND_TARGETAP = 5;
    private final int MSG_NO_AP = 6;
    private final int MSG_MORE_AP = 7;
    private final int MSG_TAGRET_BOX = 8;
    private final int MSG_CONNECT_TARGET_TIMEOUT = 9;
    private final int MSG_SHOW_TIPS = 11;
    private WifiManager wManager = null;
    private String defaultBoxIP = "192.168.100.1";
    private int defaultBoxPort = 80;
    private BoxControler mControler = null;
    private BoxCache boxCache = BoxCache.getCache();
    // private final int MSG_FIND_BOX_WIFI = 10;
    private WifiConfiguration wcBox;
    private boolean isRegister = false;
    private Button btnBegin = null;
    private String targetSSID = ""; // 手机配置开始前连接的WIFI热点，最终手机和音响都要连到该WIFI上
    private String currentBoxSSID = "";// 正在操作音响的SSID;
    private boolean targetSSIDReady = false;
    private boolean currentBoxSSIDReady = false;

    private boolean waitBoxRestart = false; // 音响配置好网络后，重新启动标记
    private Box vBox = null; // 用于配置时，使用
    private ProgressDialog msgTips;
    private Box currentBox = null;
    private WifiChangedRceciver receiver;
    private UDPHelper udpHelper;

    private Handler handler = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OPEN_WIFISETTING:
                    setNetworkMethod("当前WIFI网络不可用，是否进行设置?");
                    break;
                case MSG_NO_AP:
                    handleNoAP();
                    break;
                case MSG_MORE_AP:
                    handleMoreAp((List<String>) msg.obj);
                    break;
                case MSG_CONNECT_AP_TIMEOUT:
                    Log.d(TAG, "currentBoxSSIDReady===>" + currentBoxSSIDReady);
                    if (!currentBoxSSIDReady) {
                        setNetworkMethod("没有找到音响WIFI热点，是否手动设置");
                    }
                    break;
                case MSG_CONNECT_TARGET_TIMEOUT:
                    Log.d(TAG, "targetSSIDReady===>" + targetSSIDReady);
                    if (!targetSSIDReady) {
                        setNetworkMethod("网络切换失败，是否手动设置");
                    }
                    break;
                case MSG_NOTFIND_TARGETAP:
                    selectAPSSID((List<String>) msg.obj);
                    break;
                case MSG_BOX_NEEDRESTART:
                    showMsgTips("正在等待音响重启...");
                    connectTargetAP();
                    break;
                case MSG_TAGRET_BOX:
                    ok();
                    break;
                case MSG_FIND_TARGETAP:
                    inputPWD();
                    break;
                case MSG_SHOW_TIPS:

                    if (msgTips.isShowing()) {
                        msgTips.dismiss();
                    }
                    msgTips.setMessage((String) msg.obj);
                    msgTips.show();
                    break;
                // case MSG_FIND_BOX_WIFI:
                // handler.post(new Runnable() {
                //
                // @Override
                // public void run() {
                // findBoxWifiList();
                // }
                // });
                // break;
                default:
                    break;
            }
            return false;
        }
    });

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        XHKApplication.getInstance().addActivity(this);
        vBox = new Box();
        vBox.httpApiPort = defaultBoxPort;
        vBox.deviceIpAddr = defaultBoxIP;
        mControler = BoxControler.getInstance();
        mControler.setContext(this);
        udpHelper = UDPHelper.getHelper(this);
        wManager = (WifiManager) getSystemService(WIFI_SERVICE);

        registerReceiver();

        msgTips = new ProgressDialog(this);
        msgTips.setIndeterminate(true);
        msgTips.setCancelable(false);
        setContentView(R.layout.activity_config);

        findViewById(R.id.btnBegin).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                beginConfig();
            }
        });

    }

    private void registerReceiver() {
        receiver = new WifiChangedRceciver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        iFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        iFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        iFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        iFilter.addAction(UDPHelper.ACTION_FIND_BOX_BY_NAME);
        registerReceiver(receiver, iFilter);
        isRegister = true;
    }

    private void beginConfig() {
        WifiInfo wi = wManager.getConnectionInfo();

        Log.d(TAG, "============" + String.valueOf(wi));
        if (wi == null || wi.getNetworkId() == -1) { // 没找到WIFI信息
            handler.sendEmptyMessage(MSG_OPEN_WIFISETTING);
        } else {
            showMsgTips("正在寻找音响热点...");
            targetSSID = Util.removeTheDoubleQuotationMarks(wi.getSSID());

            Log.d(TAG, "===targetSSID===" + targetSSID);
            findBoxWifiList();
            // handler.sendEmptyMessage(MSG_FIND_BOX_WIFI);
        }
    }

    protected void onResume() {
        super.onResume();

    }

    /**
     *
     */
    private void findBoxWifiList() {
        final List<String> boxWifiList = findBoxWifi();
        Log.d(TAG, "===getWifiList===" + boxWifiList.size());
        if (boxWifiList.size() == 0) { // 没有找到音响AP，提示用户下一步操作，要么退出，要么重试
            handler.sendEmptyMessage(MSG_NO_AP);
        } else if (boxWifiList.size() == 1) { // 找到一个音响AP，直接进行下一步操作
            connectToBoxAp(boxWifiList.get(0));
        } else { // 如果有多个，则让用户选一个
            Message msg = Message.obtain();
            msg.what = MSG_MORE_AP;
            msg.obj = boxWifiList;
            handler.sendMessage(msg);
        }
    }

    /**
     *
     */
    private void handleNoAP() {
        new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.dailog_no_boxap)
                .setMessage(R.string.dailog_no_boxap_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.dailog_btn_self,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                toSystemWifi(); // 用户手动选择WIFI
                            }
                        })
                .setNegativeButton(R.string.dailog_btn_cancle,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                XHKApplication.getInstance().exit(); // 退出APP
                            }
                        }).show();
    }

    /**
     * @param boxList
     */
    private void handleMoreAp(final List<String> boxList) {
        Collections.sort(boxList);
        String arrAPs[] = new String[boxList.size()];
        new AlertDialog.Builder(this)
                .setTitle(R.string.dailog_boxap_select)
                .setCancelable(false)
                .setItems(boxList.toArray(arrAPs),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                connectToBoxAp(boxList.get(which));
                            }
                        }).show();
    }

    private void setNetworkMethod(final String msg) {

        Exception ex = new Exception();
        ex.fillInStackTrace();
        Log.e(TAG, "自定义看谁调用这个方法", ex);
        // 提示对话框
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("音响设置提示").setMessage(msg).setCancelable(false)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (wcBox != null) {
                            wManager.removeNetwork(wcBox.networkId);
                        }
                        toSystemWifi();
                        XHKApplication.getInstance().exit();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XHKApplication.getInstance().exit();
                    }
                }).show();
    }

    private void connectToBoxAp(final String ssid) {
        showMsgTips("正在切换至音响网络...");
        currentBoxSSID = ssid;
        boolean flag = configureBoxWifi(ssid);

        Log.d(TAG, "===connect===" + ssid + "=" + flag);
        if (flag) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    handler.sendEmptyMessageDelayed(MSG_CONNECT_AP_TIMEOUT,
                            40000);
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        if (currentBoxSSIDReady) {
                            configBoxWifi();
                            break;
                        }
                    }
                }
            }).start();

        } else {
            setNetworkMethod("没有找到音响WIFI热点，是否手动设置");
        }
    }

    /**
     *
     */
    private void configBoxWifi() {
        showMsgTips("正在配置音响网络...");
        mControler.setBoxApiAddress(defaultBoxIP, defaultBoxPort);

        List<BoxAP> apList = mControler.getBoxWifiList(vBox);

        List<String> boxAPSSIDList = new ArrayList<String>();
        boolean found = false;
        for (BoxAP ap : apList) {

            boxAPSSIDList.add(ap.SSID);
            if (targetSSID.equals(ap.SSID)) {
                found = true;
            }
        }
        if (found) {
            handler.sendEmptyMessage(MSG_FIND_TARGETAP);
        } else {
            Message msg = Message.obtain();
            msg.obj = boxAPSSIDList;
            msg.what = MSG_NOTFIND_TARGETAP;
            handler.sendMessage(msg);
        }
    }

    private void selectAPSSID(final List<String> ssidList) {
        Collections.sort(ssidList);

        String arrAPs[] = new String[ssidList.size()];
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.dailog_app_list_title)
                .setItems(ssidList.toArray(arrAPs),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                targetSSID = ssidList.get(which);
                                handler.sendEmptyMessage(MSG_FIND_TARGETAP);
                            }
                        }).show();
    }

    /**
     * 配置手机连接音响的路由
     *
     * @return
     */
    private boolean configureBoxWifi(final String ssid) {
        WifiConfiguration oldWC = isExsits(ssid);
        if (oldWC != null) {
            wManager.removeNetwork(oldWC.networkId);
        }
        WifiConfiguration wc = new WifiConfiguration();

        wc.SSID = "\"" + ssid + "\"";
        // wc.wepKeys[0] = "";
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        // wc.wepTxKeyIndex = 0;
        wc.status = WifiConfiguration.Status.ENABLED;
        int netId = wManager.addNetwork(wc);
        boolean result = wManager.enableNetwork(netId, true);
        Log.e(TAG, "result==>" + result + "==WC===>" + wc);
        if (result) {
            wcBox = wc;
        }
        return result;
    }

    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            Log.d(TAG, "SSID==>" + SSID + "==WC===>" + existingConfig);
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private void inputPWD() {
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dailog_pwd_title)
                .setMessage(
                        getBaseContext().getString(R.string.dailog_pwd_msg,
                                targetSSID))
                .setIcon(android.R.drawable.ic_dialog_dialer)
                .setView(inputServer)
                .setNegativeButton("选择其它网络",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ArrayList<ScanResult> list = (ArrayList<ScanResult>) wManager
                                        .getScanResults();
                                List<String> result = new ArrayList<String>();
                                for (ScanResult sr : list) {
                                    result.add(Util
                                            .removeTheDoubleQuotationMarks(sr.SSID));
                                }

                                Message msg = Message.obtain();
                                msg.obj = result;
                                msg.what = MSG_NOTFIND_TARGETAP;
                                handler.sendMessage(msg);
                            }
                        }).setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                Log.d(TAG, "=currentBox==>" + currentBox
                                        + "==vBox=>" + vBox);
                                if (!mControler
                                        .connectAP(targetSSID, inputServer
                                                        .getText().toString().trim(),
                                                currentBox != null ? currentBox
                                                        : vBox)) {
                                    handler.sendEmptyMessage(MSG_FIND_TARGETAP);
                                } else {
                                    mControler
                                            .restart(currentBox != null ? currentBox
                                                    : vBox);

                                    handler.sendEmptyMessage(MSG_BOX_NEEDRESTART);
                                }
                            }
                        }).start();

                        dialog.dismiss();
                    }
                }).show();
    }

    private List<String> findBoxWifi() {
        ArrayList<ScanResult> list = (ArrayList<ScanResult>) wManager
                .getScanResults();
        List<String> result = new ArrayList<String>();
        for (Iterator<ScanResult> iter = list.iterator(); iter.hasNext(); ) {
            ScanResult sr = iter.next();
            if (sr.SSID.startsWith(PRE_WIFI_BOX_SSID)) {
                result.add(sr.SSID);
            }
        }
        return result;
    }

    /**
     * 手机链接到配置前连接的WIFI SSID
     */
    private void connectTargetAP() {
        WifiConfiguration wc = isExsits(targetSSID);
        wManager.enableNetwork(wc.networkId, true);
        handler.sendEmptyMessageDelayed(MSG_CONNECT_TARGET_TIMEOUT, 40000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (targetSSIDReady) {
                        udpHelper.scanBoxByName(currentBoxSSID);
                        break;
                    }
                }
            }
        }).start();
    }

    /**
     *
     */
    private void toSystemWifi() {
        // 判断手机系统的版本 即API大于10 就是3.0或以上版本
        if (android.os.Build.VERSION.SDK_INT > 10) {
            // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        XHKApplication.getInstance().removeActivity(this);
    }

    /**
     *
     */
    private void ok() {
        mControler.setBoxApiAddress(currentBox.deviceIpAddr,
                currentBox.httpApiPort);
        if (wcBox != null) { // 删掉wcBox
            wManager.removeNetwork(wcBox.networkId);
        }
        if (msgTips.isShowing()) {
            msgTips.dismiss();
        }
        Toast.makeText(ConfigBoxActivity.this, "音响配置成功", Toast.LENGTH_SHORT)
                .show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mControler.getCurrentPlayListFromBox(); // 刷新一下播放列表
                mControler.startSyncBoxPlayState(); // 开始监听音响播放状态
            }
        }).start();
        Intent i = new Intent(ConfigBoxActivity.this, MainActivity.class);
        startActivity(i);
        ConfigBoxActivity.this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRegister && receiver != null) {
            unregisterReceiver(receiver);
            isRegister = false;
        }
        if (wcBox != null) {
            wManager.removeNetwork(wcBox.networkId);
        }

    }

    /**
     *
     */
    private void showMsgTips(String msg) {
        Message message = Message.obtain();
        message.what = MSG_SHOW_TIPS;
        message.obj = msg;
        handler.sendMessage(message);
    }

    class WifiChangedRceciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (UDPHelper.ACTION_FIND_BOX_BY_NAME.equals(intent.getAction())) { // 找到指定的音响了
                Box box = intent.getParcelableExtra(UDPHelper.EXTRA_FIND_BOX);
                currentBox = box;
                boxCache.addBox(box);
                handler.sendEmptyMessage(MSG_TAGRET_BOX);
            } else {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    Log.d(TAG, intent.getAction() + "====" + networkInfo);
                    String ssid = Util
                            .removeTheDoubleQuotationMarks(networkInfo
                                    .getExtraInfo());
                    if (NetworkInfo.State.CONNECTED.equals(networkInfo
                            .getState()) && TextUtils.isEmpty(ssid)) { // 适配有些机型
                        WifiInfo wi = wManager.getConnectionInfo();
                        ssid = Util.removeTheDoubleQuotationMarks(wi.getSSID());
                    }

                    WifiInfo wi = wManager.getConnectionInfo();
                    if (!targetSSIDReady) {
                        targetSSIDReady = NetworkInfo.State.CONNECTED
                                .equals(networkInfo.getState())
                                && targetSSID.equals(ssid);
                    }
                    if (!currentBoxSSIDReady) {
                        currentBoxSSIDReady = NetworkInfo.State.CONNECTED
                                .equals(networkInfo.getState())
                                && currentBoxSSID.equals(ssid);
                    }
                    Log.d(TAG,
                            intent.getAction() + "===="
                                    + networkInfo.getState()
                                    + "===targetSSID==" + targetSSID
                                    + "===targetSSIDReady===" + targetSSIDReady
                                    + "===currentBoxSSIDReady="
                                    + currentBoxSSIDReady
                                    + "===currentBoxSSID==" + currentBoxSSID
                                    + "===currentPhoneSSID==" + ssid);
                }
            }
        }

    }
}

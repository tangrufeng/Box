package com.xhk.wifibox.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xhk.wifibox.R;
import com.xhk.wifibox.XHKApplication;
import com.xhk.wifibox.action.PlayerAction;
import com.xhk.wifibox.activity.ttfm.TTFMMainActivity;
import com.xhk.wifibox.activity.xm.XMMainActivity;
import com.xhk.wifibox.adapter.PlayListListAdapter;
import com.xhk.wifibox.model.MediaDatabase;
import com.xhk.wifibox.utils.Contants;

import java.util.List;

public class MainActivity extends BasePlayerActivity implements
        OnClickListener, OnItemClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int DIALOG_TYPE_ADDPLAYLIST = 100;
    private View rlLocalSong, llLove, llMusic, llRadio, llAddPlayList;
    private TextView tvLocalSongTip;
    private ListView lvPlaylistList;
    private int cnt;
    private PlayListListAdapter adapter = null;
    private MediaDatabase mdb = null;
    private List<String> playlist = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView(R.layout.activity_main);
        mdb = MediaDatabase.getInstance(this);
        initView();
        showTitleBtn(false);
        initData();

    }

    private void initData() {
        cnt = mdb.getLocalSongTotal();
        if (cnt > 0) {
            tvLocalSongTip.setText("共发现" + cnt + "首音乐");
        } else {
            tvLocalSongTip.setText("点击添加");
        }
    }

    private void initView() {
        rlLocalSong = findViewById(R.id.rl_localSong);
        llLove = findViewById(R.id.ll_main_love);
        llMusic = findViewById(R.id.ll_main_netmusic);
        llRadio = findViewById(R.id.ll_main_netradio);
        lvPlaylistList = (ListView) findViewById(R.id.lv_playlist);
        rlLocalSong.setOnClickListener(this);
        llLove.setOnClickListener(this);
        llMusic.setOnClickListener(this);
        llRadio.setOnClickListener(this);
        llAddPlayList = findViewById(R.id.ll_add_playlist);
        llAddPlayList.setOnClickListener(this);
        tvLocalSongTip = (TextView) rlLocalSong
                .findViewById(R.id.tv_localSongTip);
        playlist = mdb.getPlaylists();
        adapter = new PlayListListAdapter(this, R.layout.playlistlist_item,
                playlist);
        lvPlaylistList.setAdapter(adapter);
        lvPlaylistList.setOnItemClickListener(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_localSong:
                Intent toLocal;
                if (cnt > 0) {
                    toLocal = new Intent(this, PlayListActivity.class);
                    toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS,
                            PlayerAction.class);
                    toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
                            "getLocalSongs");
                    toLocal.putExtra(Contants.INTENT_EXTRA_LOCAL_SONGS, true);
                    toLocal.putExtra(Contants.INTENT_EXTRA_LIST_ID, "");
                    toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
                    toLocal.putExtra(Contants.INTENT_EXTRA_LIST_NAME, "本地歌曲");
                } else {
                    toLocal = new Intent(this, ScanActivity.class);
                }
                startActivity(toLocal);
                break;
            case R.id.ll_main_love:
                Intent toLove = new Intent(this, PlayListActivity.class);
                toLove.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS,
                        PlayerAction.class);
                toLove.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
                        "getLovePlayList");
                toLove.putExtra(Contants.INTENT_EXTRA_LIST_ID, "");
                toLove.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
                toLove.putExtra(Contants.INTENT_EXTRA_LIST_NAME, "我的最爱");
                startActivity(toLove);
                break;
            case R.id.ll_main_netmusic:
                Intent toXM = new Intent(this, XMMainActivity.class);
                startActivity(toXM);
                break;
            case R.id.ll_main_netradio:
                Intent toXMLY = new Intent(this, TTFMMainActivity.class);
                startActivity(toXMLY);
                break;
            case R.id.ll_add_playlist:
                showDialog(DIALOG_TYPE_ADDPLAYLIST);
            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.xhk.wifibox.activity.BasePlayerActivity#onCreateDialog(int)
     */
    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TYPE_ADDPLAYLIST) {

            final EditText inputServer = new EditText(this);
            inputServer.setHint("请输入歌单名称");
            inputServer.setSingleLine();
            return new AlertDialog.Builder(this)
                    .setTitle("创建新的歌单")
                    .setView(inputServer)
                    .setNegativeButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (!TextUtils.isEmpty(inputServer
                                            .getText())) {
                                        mdb.addPlaylist(
                                                inputServer.getText()
                                                        .toString());
                                        playlist = mdb.getPlaylists();

                                        adapter.notifyDataSetChanged();
                                        MainActivity.this
                                                .getCustomContentView()
                                                .requestLayout();
                                        lvPlaylistList.requestLayout();
                                    }
                                }
                            }).setPositiveButton("取消", null).create();
        }
        return super.onCreateDialog(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.xhk.wifibox.activity.BasePlayerActivity#getActivityTitle()
     */
    @Override
    protected String getActivityTitle() {
        return getString(R.string.app_name);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.xhk.wifibox.activity.BasePlayerActivity#onKeyDown(int,
     * android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this).setTitle("确认退出").setMessage("确认退出应用么?").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    XHKApplication.getInstance().exit();
                }
            }).setNegativeButton("取消", null).show();
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
     * .AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String listName = (String) parent.getItemAtPosition(position);

        Intent toLocal = new Intent(this, PlayListActivity.class);
        toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS,
                PlayerAction.class);
        toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
                "getMyPlayList");
        toLocal.putExtra(Contants.INTENT_EXTRA_LIST_ID, listName);
        toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
        toLocal.putExtra(Contants.INTENT_EXTRA_LIST_NAME, listName);
        startActivity(toLocal);
    }
}

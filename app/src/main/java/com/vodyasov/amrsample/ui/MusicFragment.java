package com.vodyasov.amrsample.ui;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vodyasov.amrsample.R;
import com.vodyasov.amrsample.service.MusicService;
import com.vodyasov.amrsample.service.constant.BroadcastAction;
import com.vodyasov.amrsample.service.constant.BundleArg;
import com.vodyasov.amrsample.service.constant.HandlerAction;

public class MusicFragment extends Fragment implements ServiceConnection, View.OnClickListener
{
    private Context mContext;
    private Messenger mPlayerMessenger;
    private boolean mIsBound = false;

    private EditText et_uri;
    private Button btn_play, btn_stop;
    private TextView tv_streamTitle, tv_br, tv_name, tv_genre, tv_info, tv_desc;


    private BroadcastReceiver mStreamTitleReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String streamTitle = intent.getStringExtra(BundleArg.STREAM_TITLE);
            tv_streamTitle.setText(streamTitle);
        }
    };

    private BroadcastReceiver mHeadersReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            tv_br.setText(intent.getStringExtra(BundleArg.HEADER_BR));
            tv_name.setText(intent.getStringExtra(BundleArg.HEADER_NAME));
            tv_genre.setText(intent.getStringExtra(BundleArg.HEADER_GENRE));
            tv_info.setText(intent.getStringExtra(BundleArg.HEADER_INFO));
            tv_desc.setText(intent.getStringExtra(BundleArg.HEADER_DESCRIPTION));
        }
    };

    public static MusicFragment newInstance()
    {
        MusicFragment f = new MusicFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mContext.bindService(new Intent(mContext, MusicService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view  = inflater.inflate(R.layout.fragment_music, container, false);

        btn_play = (Button) view.findViewById(R.id.btn_play);
        btn_play.setOnClickListener(this);
        btn_stop = (Button) view.findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);

        et_uri = (EditText) view.findViewById(R.id.et_audiostream_uri);

        tv_streamTitle = (TextView) view.findViewById(R.id.tv_streamtitle);
        tv_br = (TextView) view.findViewById(R.id.tv_br);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_genre = (TextView) view.findViewById(R.id.tv_genre);
        tv_desc = (TextView) view.findViewById(R.id.tv_description);
        tv_info = (TextView) view.findViewById(R.id.tv_audio_info);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mContext.registerReceiver(mStreamTitleReceiver, new IntentFilter(BroadcastAction.STREAM_TITLE));
        mContext.registerReceiver(mHeadersReceiver, new IntentFilter(BroadcastAction.HEADERS));
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mContext.unregisterReceiver(mStreamTitleReceiver);
        mContext.unregisterReceiver(mHeadersReceiver);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mContext.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        mPlayerMessenger = new Messenger(service);
        mIsBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        mIsBound = false;
        mPlayerMessenger = null;
    }

    @Override
    public void onClick(View v)
    {
        if (v.equals(btn_play))
        {
            String stringUri = et_uri.getText().toString();
            if (!TextUtils.isEmpty(stringUri))
            {
                Uri uri = Uri.parse(stringUri);
                Message msg = Message.obtain();
                msg.what = HandlerAction.PLAY_URI;
                Bundle data = new Bundle();
                data.putParcelable(BundleArg.PLAY_URI, uri);
                msg.setData(data);
                sendMessage(msg);
                resetTextViews();
            }
            else
            {
                et_uri.setError(getString(R.string.error_input_audiostream_uri));
            }
        }
        else if (v.equals(btn_stop))
        {
            Message msg = Message.obtain();
            msg.what = HandlerAction.STOP;
            sendMessage(msg);
            resetTextViews();
        }
    }

    private void sendMessage(Message msg)
    {
        if (mIsBound && mPlayerMessenger != null)
        {
            try
            {
                mPlayerMessenger.send(msg);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void resetTextViews()
    {
        tv_streamTitle.setText(null);
        tv_br.setText(null);
        tv_name.setText(null);
        tv_desc.setText(null);
        tv_info.setText(null);
        tv_genre.setText(null);
    }
}

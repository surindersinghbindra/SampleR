package com.vodyasov.amrsample.service.util;


import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.vodyasov.amrsample.service.constant.BundleArg;
import com.vodyasov.amrsample.service.constant.HandlerAction;

import java.lang.ref.WeakReference;

public class MusicHandler extends Handler
{
    private WeakReference<IPlayer> ref;

    public MusicHandler(IPlayer player)
    {
        ref = new WeakReference<IPlayer>(player);
    }

    @Override
    public void handleMessage(Message msg)
    {
        IPlayer player = ref.get();
        if (player == null)
        {
            return;
        }
        switch(msg.what)
        {
            case HandlerAction.PLAY:
                player.play();
                break;
            case HandlerAction.PLAY_STRING_URI:
                String stringUri = msg.getData().getString(BundleArg.PLAY_STRING);
                player.play(stringUri);
                break;
            case HandlerAction.PLAY_URI:
                Uri uri = msg.getData().getParcelable(BundleArg.PLAY_URI);
                player.play(uri);
                break;
            case HandlerAction.STOP:
                player.stop();
                break;
            default:
                super.handleMessage(msg);
        }
    }
}

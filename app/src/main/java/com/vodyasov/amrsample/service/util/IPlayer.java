package com.vodyasov.amrsample.service.util;

import android.net.Uri;

public interface IPlayer
{
    void play(String stringUri);
    void play(Uri uri);
    void play();
    void stop();
}

package com.vodyasov.amr;

import java.util.List;

public interface OnNewMetadataListener
{
    void onNewHeaders(String stringUri, List<String> name, List<String> desc, List<String> br,
                      List<String> genre, List<String> info);
    void onNewStreamTitle(String stringUri, String streamTitle);
}

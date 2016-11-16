package com.zz.teaencyclopedia.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/15.
 */
public class NetworkUtils {

    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager=
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null)
            return false;
        switch (networkInfo.getType()){
            case ConnectivityManager.TYPE_WIFI:
                Toast.makeText(context, "当前网络为wifi", Toast.LENGTH_SHORT).show();
                return true;
            case ConnectivityManager.TYPE_MOBILE:
                Toast.makeText(context, "当前为移动网络", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}

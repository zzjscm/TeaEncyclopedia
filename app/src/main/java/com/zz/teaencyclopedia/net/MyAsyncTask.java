package com.zz.teaencyclopedia.net;

import android.os.AsyncTask;

import com.zz.teaencyclopedia.interfaces.DataCallback;
import com.zz.teaencyclopedia.utils.HttpUtils;

/**
 * Created by Administrator on 2016/11/12.
 */
public class MyAsyncTask extends AsyncTask<String, Void, byte[]> {

    private DataCallback mCallback;

    public MyAsyncTask(DataCallback callback) {
        mCallback = callback;
    }

    @Override
    protected byte[] doInBackground(String... params) {
        byte[] bytes = HttpUtils.loadByte(params[0]);

        return bytes;
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
        if (bytes!=null) {
            mCallback.callback(bytes);
        }

    }
}

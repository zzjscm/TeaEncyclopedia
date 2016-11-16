package com.zz.teaencyclopedia.utils;

import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/11/12.
 */
public class HttpUtils {
    public static byte[] loadByte(String path) {
        InputStream is=null;
        ByteArrayOutputStream baos=null;
        try {
            URL url=new URL(path);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode()==200){
                is=conn.getInputStream();
                baos=new ByteArrayOutputStream();
                int len=0;
                byte[] buf=new byte[1024*8];
                while ((len=is.read(buf))!=-1){
                    baos.write(buf,0,len);
                }
                return baos.toByteArray();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void loadByte(final String path, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is=null;
                ByteArrayOutputStream baos=null;
                try {
                    URL url=new URL(path);
                    HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    if (conn.getResponseCode()==200){
                        is=conn.getInputStream();
                        baos=new ByteArrayOutputStream();
                        int len=0;
                        byte[] buf=new byte[1024*8];
                        while ((len=is.read(buf))!=-1){
                            baos.write(buf,0,len);
                        }
                        byte[] bytes = baos.toByteArray();
                        Message msg = Message.obtain(handler, 1, bytes);
                        handler.sendMessage(msg);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

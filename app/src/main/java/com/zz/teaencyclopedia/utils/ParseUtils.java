package com.zz.teaencyclopedia.utils;

import com.zz.teaencyclopedia.beans.TeasMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/12.
 */
public class ParseUtils {
    public static TeasMessage parse(String s) {
        TeasMessage teasMessage=null;
        try {
            JSONObject jsonObject=new JSONObject(s);
            teasMessage.setErrorMessage(jsonObject.optString("errorMessage"));
            JSONArray jsonArray = jsonObject.optJSONArray("data");
            List<TeasMessage.DataBean> dataBeanList=new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.optJSONObject(i);
                TeasMessage.DataBean dataBean = new TeasMessage.DataBean();
                dataBean.setTitle(jsonObject.optString("title"));
                dataBean.setCreate_time(jsonObject.optString("create_time"));
                dataBean.setNickname(jsonObject.optString("nickname"));
                dataBean.setSource(jsonObject.optString("source"));
                dataBeanList.add(dataBean);
            }
            teasMessage.setData(dataBeanList);
            return teasMessage;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

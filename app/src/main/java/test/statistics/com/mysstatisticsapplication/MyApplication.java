package test.statistics.com.mysstatisticsapplication;

import android.app.Application;

import com.jiyoutang.statistics.UmsAgent;

/**
 * Created by tianlong on 2016/6/24.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //数据统计上传的单个接口域名
        String urlDomain = "http://172.16.32.7:8081/actionDetail/collectBatch.do?";
        //数据统计上传的批量接口域名
        String urlDomainForAll = "http://172.16.32.7:8081/actionDetail/collect.do?";
        //产品号
        String productId = "";
        //渠道号
        String channel = "";
        //设置用户id
        UmsAgent.setUserId("");
        //设置区域码
        UmsAgent.setAreaCode("");
        UmsAgent.init(this, urlDomain, urlDomainForAll, productId, channel);
        UmsAgent.onAppStart(this);
    }
}

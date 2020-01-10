package com.kingfeng.flutter_app;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {

    private static final String METHOD_CHANNEL = "com.zhuandian.flutter/android";
    private static final String EVENT_CHANNEL = "com.zhuandian.flutter/android/event"; //事件通道，供原生主动调用flutter端使用
    private static final String METHOD_NATIVE_SEND_MESSAGE_FLUTTER = "nativeSendMessage2Flutter"; //原生主动向flutter发送消息
    private EventChannel.EventSink eventSink;
    private MethodChannel methodChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

        methodChannel = new MethodChannel(getFlutterView(), METHOD_CHANNEL);
        //接受fltuter端传递过来的方法，并做出响应逻辑处理
        methodChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                System.out.println(call.method);
                Toast.makeText(MainActivity.this, call.method, Toast.LENGTH_LONG).show();
                if (call.method.equals(METHOD_NATIVE_SEND_MESSAGE_FLUTTER)) {
                    nativeSendMessage2Flutter();
                }
            }
        });

        new EventChannel(getFlutterView(), EVENT_CHANNEL).setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                MainActivity.this.eventSink = eventSink;
                eventSink.success("事件通道准备就绪");
                //在此不建议做耗时操作，因为当onListen回调被触发后，在此注册当方法需要执行完毕才算结束回调函数
                //的执行，耗时操作可能会导致界面卡死，这里读者需注意！！
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    /**
     * 原生端向flutter主动发送消息；
     */
    private void nativeSendMessage2Flutter() {

        // 设备型号
        String device_model = Build.MODEL;
        eventSink.success(device_model);

        //主动向flutter发送一次更新后的数据
//        eventSink.success("原生端向flutter主动发送消息");

    }
}

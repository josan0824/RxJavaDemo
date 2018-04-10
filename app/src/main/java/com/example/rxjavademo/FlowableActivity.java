package com.example.rxjavademo;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FlowableActivity extends Activity {
    private static final String TAG = "FlowableActivity";
    private Subscription subscription;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowable);
        ButterKnife.bind(this);
    }

    /**
     * 同步
     */
    @OnClick(R.id.btn_sync)
    public void onSync() {
        // 创建观察者
        Flowable flowable = Flowable.create(new FlowableOnSubscribe<Object>() {

            @Override
            public void subscribe(FlowableEmitter<Object> emitter) throws Exception {
                for (int i = 0; i < 20; i++) {
                    // 打印request
                    Log.e(TAG, "request: " + emitter.requested());
                    Log.e(TAG, "emit " + i);
                    emitter.onNext(i);
                }

                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR);

        // 创建被观察者
        Subscriber<Object> subscriber = new Subscriber<Object>() {

            @Override
            public void onSubscribe(Subscription s) {
                // 设置观察者处理事件的个数为10
                subscription = s;
                s.request(10);
            }

            @Override
            public void onNext(Object o) {
                Log.e(TAG, "onNext: " + o);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        };
        // 订阅
        flowable.subscribe(subscriber);
    }

    /**
     * 异步
     */
    @OnClick(R.id.btn_async)
    public void onFlowable() {
        // 创建观察者
        Flowable flowable = Flowable.create(new FlowableOnSubscribe<Object>() {

            @Override
            public void subscribe(FlowableEmitter<Object> emitter) throws Exception {
                for (int i = 0; i < 10; i++) {
                    // 打印request
                    Log.e(TAG, "request: " + emitter.requested());
                    Log.e(TAG, "emit " + i);
                    emitter.onNext(i);
                }

                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR);

        // 创建被观察者
        Subscriber<Object> subscriber = new Subscriber<Object>() {

            @Override
            public void onSubscribe(Subscription s) {
                // 保存Subscription对象，用于再次调用request方法
                subscription = s;
                // 设置观察者处理事件的个数为5
                s.request(5);
            }

            @Override
            public void onNext(Object o) {
                Log.e(TAG, "onNext: " + o);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        };
        // 订阅
        flowable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    @OnClick(R.id.btn_request)
    public void onRequest() {
        subscription.request(1);
    }

}

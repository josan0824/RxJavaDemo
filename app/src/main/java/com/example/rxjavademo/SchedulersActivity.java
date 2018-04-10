package com.example.rxjavademo;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
public class SchedulersActivity extends Activity {
    private static final String TAG = "SchedulersActivity ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedulers);
        ButterKnife.bind(this);
    }

    /**
     * 被观察者和观察者默认都是在订阅的线程中执行的
     */
    @OnClick(R.id.btn_default)
    public void defaultClick() {
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                Log.e(TAG, "被观察者发起事件的线程 thread name:" + Thread.currentThread());
                e.onNext("josan");
            }
        }).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                Log.e(TAG, "观察者处理事件的线程 thread name:" + Thread.currentThread());
            }
        });
    }


    @OnClick(R.id.btn_appoint)
    public void appointClicked() {

        Observable
                .create(new ObservableOnSubscribe<Object>() {

                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        e.onNext("josan");
                        Log.e(TAG, "被观察者: thread name:" + Thread.currentThread());
                    }
                })
                // subscribeOn指定的是被观察者发送事件的线程，多次指定，只有第一次有效
                .subscribeOn(Schedulers.io())
                // observeOn指定的是观察者接受事件的线程，多次指定，每指定一次，就切换一次线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer() {

                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e(TAG, "观察者处理事件的线程 thread name:"  + Thread.currentThread());
                    }
                });
    }

    @OnClick(R.id.btn_multiple_switching)
    public void multipleSwitchingClicked() {
        Observable
                .create(new ObservableOnSubscribe<Object>() {

                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        e.onNext("josan");
                        Log.e(TAG, "被观察者: thread name:" + Thread.currentThread());
                    }
                })
                // 指定被观察者发布事件是在io线程
                .subscribeOn(Schedulers.io())
                // 指定处理事件在主线程
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e(TAG, "accept1: thread name:" + Thread.currentThread());
                    }
                })
                // 再次切换处理线程
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e(TAG, "accept2: thread name:" + Thread.currentThread());
                    }
                })
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e(TAG, "观察者: thread name:" + Thread.currentThread());
                    }
                });

    }
}

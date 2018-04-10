package com.example.rxjavademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class RxJava2xActivity extends Activity {
    private static final String TAG = "RxJava2xActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava2x);
        ButterKnife.bind(this);
        //test();
        //testJust();
        //testDisposable();
        //testSubscribe();
    }

    private void test() {
        Flowable.just("Hello world").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, s);
            }
        });
    }

    /**
     * 一般用法，三步走
     * 只有调用了subscribe方法订阅以后，被观察者，才会发送事件，
     * 即调用ObservableOnSubscribe的subscribe方法
     */
    private void test1() {
        // 1.创建被观察者
        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                emitter.onNext("1 ");
                emitter.onNext("2");
            }
        });
        // 2.创建观察者
        Observer observer = new Observer() {
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe");
            }

            @Override
            public void onNext(Object value) {
                Log.e(TAG, "onNext: " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
            }
        };
        // 3.进行订阅
        observable.subscribe(observer);
    }

    /**
     * 测试Just
     */
    private void testJust() {
//        Observable.range(1, 10)
        Observable.just("Tom", "Josan", "Jim")
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onNext name:" + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        // 相当于如下代码
//        Observable.create(new ObservableOnSubscribe() {
//
//            @Override
//            public void subscribe(ObservableEmitter emitter) throws Exception {
//                emitter.onNext("Tom");
//                emitter.onNext("Josan");
//                emitter.onNext("Jim");
//            }
//        }).subscribe(new Observer<String>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//            }
//
//            @Override
//            public void onNext(String s) {
//                Log.e(TAG, "onNext name:" + s);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//            }
//
//            @Override
//            public void onComplete() {
//            }
//        });
    }

    /**
     * 测试中止观察者处理被观察者发布的事件
     */
    private void testDisposable() {
        Observable.create(new ObservableOnSubscribe<Object>() {

            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                Log.e(TAG, "subscribe: 1");
                emitter.onNext("1");
                Log.e(TAG, "subscribe: 2");
                emitter.onNext("2");
                Log.e(TAG, "subscribe: 3");
                emitter.onNext("3");
                Log.e(TAG, "subscribe: 4");
                emitter.onNext("4");
                Log.e(TAG, "subscribe: 5");
                emitter.onNext("5");
                emitter.onComplete();
            }
        }).subscribe(new Observer<Object>() {
            private Disposable mDisposable;
            private int i;

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe: ");
                mDisposable = d;
            }

            @Override
            public void onNext(Object value) {
                Log.e(TAG, "onNext: " + value);
                i++;
                if (i == 2) {
                    Log.e(TAG, "onNext: dispose");
                    // dispose方法会将被观察者和观察者断开联系，从而导致观察者接受不到被观察者的事件
                    // 注意，该方法并不会组织被观察者发送事件，只是阻止观察者接收事件
                    mDisposable.dispose();
                    Log.e(TAG, "onNext: dispose:" + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
            }
        });
    }

    /**
     * 测试subscribe的其他重载方法
     */
    private void testSubscribe() {
        // 1.无参方法，只发送事件，不接收事件
        Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {

            }
        }).subscribe();

        // 2.只含有一个Consumer参数的方法，表示只关心onNext事件，其他事件不需要关心
        Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                e.onNext("gaga");
                e.onNext("haha");
                e.onComplete();
            }
        }).subscribe(new Consumer<Object>() {

            @Override
            public void accept(Object o) throws Exception {
                Log.e(TAG, "accept:" + o);
            }
        });

        // 3.含有多个Consumer参数的方法，可以关心onNext、onError、onComplete方法
        Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                e.onNext("1");
                e.onNext("2");
                e.onComplete();
            }
        }).subscribe(new Consumer<Object>() {

            @Override
            public void accept(Object o) throws Exception {
                Log.e(TAG, "accept: onNext:" + o);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.e(TAG, "run: onComplete");
            }
        });

    }

    @OnClick(R.id.btn_thread)
    public void onViewClicked() {
        Intent intent = new Intent(this, SchedulersActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_map)
    public void onMapClicked() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_backpressure)
    public void onBackPressure() {
        Intent intent = new Intent(this, BackPressureActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.btn_flowable)
    public void onFlowable() {
        Intent intent = new Intent(this, FlowableActivity.class);
        startActivity(intent);
    }
}

package com.example.pranaybansal.rxandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * https://www.youtube.com/watch?v=vfjgQabgjOg
 * tutplus tutorials
 * http://www.grokkingandroid.com/rxjavas-side-effect-methods/
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "RxAndroidResults";
    private TextView txtApiResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtApiResponse = (TextView) findViewById(R.id.txtApiResponse);

        /**
         * just operator
         */
        Observable<String> myObservable = Observable.just("Hello World!");
        Observer<String> obs = new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG,"inside Onsubscribe");
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG,s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"inside onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"completed");
            }
        };
        myObservable.subscribe(obs);

///////////////////////////////////////////////////////////////////////////////////
        /**
         * from
         * map
         * filter
         */
        Integer[] ints = new Integer[]{1,2,3,4,5,6};

        Observable<Integer> IntegeregerArrayObservable = Observable.fromArray(ints);
        IntegeregerArrayObservable.filter(new Predicate<Integer>() {
            @Override
            public boolean test(@NonNull Integer integers) throws Exception {
                return integers.intValue()%2 == 0;
            }
        }).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(@NonNull Integer integer) throws Exception {
                if (integer.intValue() == 2){
                    return integer.intValue() * 2;
                }
                return integer;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integers) {
                Log.d(TAG, "onNext: length received is :"+integers );
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


/////////////////////////////////////////////////////////////////////////
        /**
         * Create operator
         */
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("Fuck You!!");
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "onNext: "+s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        /////////////////////////////////////////////////////////////////////////
        /**
        * Consumer operator
        */
        Observable.just("You TOO").subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "accept: "+s);
            }
        });


        ///////////////////////////////////////////////////////
        /**
         * Service hit example using rxjava and retrofit
         */
       // retrofitWithoutRX();
        ApiCalls api = RetrofitClient.getClient().create(ApiCalls.class);
        api.getUserDetails("pranay1494").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<GitPojo>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull GitPojo gitPojo) {
                txtApiResponse.setText("login-ID: "+ gitPojo.getLogin());
                Glide.with(MainActivity.this).load(gitPojo.getAvatar_url()).into((ImageView) findViewById(R.id.ivProfile));
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        ////////////////////////////////////////////////////////////////////
        /**
         * doonNext - side effect method
         * Side effect methods do not affect your stream in itself. Instead they are invoked when certain events occur to allow you to react to those events.
         */
        Observable.fromArray(new Integer[]{2, 3, 5, 7, 11})
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.d(TAG, "accept: doOnnext1"+integer);
                    }
                }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(@NonNull Integer integer) throws Exception {
                return integer.intValue()%2 ==0;
            }
        }).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.d(TAG, "accept: doOnnext2"+integer);
            }
        }).count().subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                Log.d(TAG, "answer: "+aLong);
            }
        });
////////////////////////////////////////////////////////////////
        /**
         * flatmap
         * returns an observable
         *
         * toList()coverts diifferent observables into sngle list of objects
         */

        Observable.fromArray(new String[]{"a","b","c"})
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull String s) throws Exception {
                        return Observable.just(s+"x");
                    }
                })
                .doOnNext(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Log.d(TAG, "flatmap: "+(String)o);
            }
        }).toList().subscribe();
    }


//    private void retrofitWithoutRX() {
//        ApiCalls api = RetrofitClient.getClient().create(ApiCalls.class);
//        api.getUserDetails("pranay1494").enqueue(new Callback<GitPojo>() {
//            @Override
//            public void onResponse(Call<GitPojo> call, Response<GitPojo> response) {
//                txtApiResponse.setText("login-ID: "+ response.body().getLogin());
//                Glide.with(MainActivity.this).load(response.body().getAvatar_url()).into((ImageView) findViewById(R.id.ivProfile));
//
//            }
//
//            @Override
//            public void onFailure(Call<GitPojo> call, Throwable t) {
//                Log.d(TAG, "onFailure: error");
//            }
//        });
//    }

}

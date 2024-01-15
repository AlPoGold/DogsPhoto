package com.example.dogsphoto;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    public static final String BASE_URL = "https://dog.ceo/api/breeds/image/random";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_STATUS = "status";

    private MutableLiveData<DogImage> dogImage = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isHavingNet = new MutableLiveData<>();

    public LiveData<Boolean> getIsHavingNet() {
        return isHavingNet;
    }

    private Disposable disposable;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LiveData<DogImage> getDogImage() {
        return dogImage;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
    }



    public void loadImage(){

        disposable = loadDogImageRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        isLoading.setValue(true);
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Throwable {
                        isLoading.setValue(false);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        isHavingNet.setValue(false);
                    }
                })
                .subscribe(new Consumer<DogImage>() {
                    @Override
                    public void accept(DogImage image) throws Throwable {
                        Log.d("mainviewmodel", "accept image");
                        dogImage.setValue(image);
                        isHavingNet.setValue(true);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d("ERROR", throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);

    }

    private Single<DogImage> loadDogImageRx(){
        return ApiFactory.getApiService().loadDogImage();
    }
//    private Single<DogImage> loadDogImageRx(){
//       return Single.fromCallable(new Callable<DogImage>() {
//           @Override
//           public DogImage call() throws Exception {
//                   URL url = new URL(BASE_URL);
//                   HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//                   InputStream inputStream = urlConnection.getInputStream();
//                   InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                   BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//                   String result;
//                   StringBuilder data = new StringBuilder();
//
//                   do {
//                       result = bufferedReader.readLine();
//                       if (result!=null) data.append(result);
//                   } while (result!=null);
//
//                   JSONObject jsonObject = new JSONObject(data.toString());
//                   String message = jsonObject.getString(KEY_MESSAGE);
//                   String status = jsonObject.getString(KEY_STATUS);
//                   Log.d("OpenModelica", message);
//
//                   return new DogImage(message, status);
//           }
//       });
//    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}

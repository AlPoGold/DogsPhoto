package com.example.dogsphoto;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface ApiService {
    @GET("random")

    Single<DogImage> loadDogImage();
}

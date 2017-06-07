package com.example.android.librarywizard;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Android on 6/6/2017.
 */

public interface RetrofitService {

    @GET("api")
    Call<Example> getRandomUser();
}

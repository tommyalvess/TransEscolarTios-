package br.com.transescolar.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static br.com.transescolar.API.URL.BASE_URL;
import static br.com.transescolar.API.URL.BASE_SEARCH;

public class    ApiClient {

    private static ApiClient mInstance;
    private static Retrofit retrofit;

    public ApiClient(){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
    }

    public static synchronized ApiClient getInstance(){
        if (mInstance == null){
            mInstance = new ApiClient();
        }
        return mInstance;
    }

    public static Retrofit getApiClient(){
        if (retrofit==null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_SEARCH)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    //Conectando com Interface
    public ITios getApiT(){
        return retrofit.create(ITios.class);
    }

    public IKids getApiK(){
        return retrofit.create(IKids.class);
    }
    public IRota getApiR(){
        return retrofit.create(IRota.class);
    }

}

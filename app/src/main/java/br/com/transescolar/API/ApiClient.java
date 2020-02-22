package br.com.transescolar.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static br.com.transescolar.API.URL.BASE_URL;
import static br.com.transescolar.API.URL.BASE_SEARCH;

public class    ApiClient {

//    private static final String BASE_URL = "http://apsconsigpromotora.com.br/Slim3RestApi/public/";
//    private static final String BASE_SEARCH = "http://192.168.1.107/retrofit/GET/";

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

    public ITios getApi(){
        return retrofit.create(ITios.class);
    }


    public static Retrofit getApiClient(){
        if (retrofit==null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://apptransescolar.com.br/retrofit/GET/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }


}

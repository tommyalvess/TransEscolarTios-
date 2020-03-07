package br.com.transescolar.API;

import java.util.List;

import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Rota;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static br.com.transescolar.API.URL.BASE_URL;

public interface IRota {
    @GET("getRotas.php")
    Call<List<Rota>> getRotas(
            @Query("item_type") String item_type,
            @Query("key") String keyword,
            @Query("idTios") String idTios
    );

    @GET("getInfRotas.php")
    Call<List<Kids>> getInfRotas(
            @Query("item_type") String item_type,
            @Query("idRota") String idRota
    );

    @FormUrlEncoded
    @POST("createRotas")
    Call<ResponseBody> createRotas (
            @Field("nm_rota") String nome,
            @Field("hora") String data,
            @Field("dias") String end,
            @Field("idTios") String idTios
    );

    @FormUrlEncoded
    @POST("createRotaHasKids")
    Call<ResponseBody> createRotaHasKid (
            @Field("idRota") int idRota,
            @Field("idKids") int idKids
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

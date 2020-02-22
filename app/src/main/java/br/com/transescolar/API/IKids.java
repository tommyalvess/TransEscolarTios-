package br.com.transescolar.API;


import java.util.List;

import br.com.transescolar.model.Crianca;
import br.com.transescolar.model.Kids;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static br.com.transescolar.API.URL.URL_ASP;

public interface IKids {

    @GET("kids")
    Call<List<Crianca>> getKidPorId(@Query("id") int idTios);

    @GET("getPassageiro.php")
    Call<List<Kids>> getAllKids(
            @Query("item_type") String item_type,
            @Query("key") String keyword,
            @Query("idTios") int idTios
            );

    @GET("getPassageiro.php")
    Call<List<Kids>> getKidsById(
            @Query("item_type") String item_type,
            @Query("idTios") int idTios
    );

    @FormUrlEncoded
    @POST("createKids")
    Call<ResponseBody> createkids (
            @Field("nome") String nome,
            @Field("dt_nas") String data,
            @Field("end_principal") String end,
            @Field("periodo") String periodo,
            @Field("embarque") String embarque,
            @Field("desembarque") String desembarque,
            @Field("idTios") int idTios,
            @Field("idEscola") int idEscola,
            @Field("idPais") int idPais
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL_ASP)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

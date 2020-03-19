package br.com.transescolar.API;

import android.widget.EditText;

import java.util.List;

import br.com.transescolar.Classes.LoginResponse;
import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Rota;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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
            @Field("idRota") String idRota,
            @Field("idKids") int idKids
    );

    @DELETE("deletarrota/{id}")
    Call<ResponseBody> deletarRotas(
            @Path("id") String id
    );

    @DELETE("deletakidsrrota")
    Call<ResponseBody> deletarKidsRotas(
            @Query("idRota") String idRota,
            @Query("idKids") String idKids
    );

    @FormUrlEncoded
    @PUT("updaterotainf/{id}")
    Call<ResponseBody> updaterota(
            @Path("id") String id,
            @Field("nm_rota") String nmrota,
            @Field("hora") String hora,
            @Field("dias") String dias
    );

    @FormUrlEncoded
    @PUT("updaterotaembarque/{id}")
    Call<ResponseBody> updatestatusembarque(
            @Path("id") int id,
            @Field("embarque") String embarque
    );

    @FormUrlEncoded
    @PUT("updaterotadesembarque/{id}")
    Call<ResponseBody> updatestatusdesembarque(
            @Path("id") int id,
            @Field("desembarque") String desembarque
    );

    @FormUrlEncoded
    @PUT("updaterotastaus/{id}")
    Call<ResponseBody> updatestatus(
            @Path("id") int id,
            @Field("status") String status
    );

    @FormUrlEncoded
    @PUT("updateusername")
    Call<LoginResponse> updateusername(
            @Path("id") int id,
            @Field("nome") EditText nome
    );
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

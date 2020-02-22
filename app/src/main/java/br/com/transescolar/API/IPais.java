package br.com.transescolar.API;

import java.util.List;

import br.com.transescolar.model.Pais;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface    IPais {

    @GET("pessoa")
    Call<List<Pais>> getPais(@Query("id") int idPais);

    @GET("getPais.php")
    Call<List<Pais>> getPaisBy(
            @Query("item_type") String item_type,
            @Query("key") String keyword,
            @Query("idTios") String idTios
    );

    @GET("getPais.php")
    Call<List<Pais>> getTodosPais(
            @Query("item_type") String item_type,
            @Query("idTios") String idTios
    );

    @FormUrlEncoded
    @POST("createPais")
    Call<ResponseBody> createPais (
            @Field("nm_pai") String nome,
            @Field("email") String email,
            @Field("cpf") String cpf,
            @Field("tell") String tell,
            @Field("senha") String senha
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
            .baseUrl("http://apptransescolar.com.br/retrofit/GET/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}

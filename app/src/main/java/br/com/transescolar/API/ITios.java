package br.com.transescolar.API;

import android.widget.EditText;

import br.com.transescolar.Classes.LoginResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ITios {

    @FormUrlEncoded
    @POST("createUser")
    Call<ResponseBody> createuser (
            @Field("nome") String nome,
            @Field("email") String email,
            @Field("cpf") String cpf,
            @Field("apelido") String apelido,
            @Field("placa") String placa,
            @Field("tell") String tell,
            @Field("senha") String senha
    );

    @FormUrlEncoded
    @POST("createPai")
    Call<ResponseBody> createPais (
            @Field("nm_pai") String nome,
            @Field("email") String email,
            @Field("cpf") String cpf,
            @Field("tell") String tell,
            @Field("senha") String senha,
            @Field("idTios") String idTios
    );

    @FormUrlEncoded
    @PUT("updateusername/{id}")
    Call<LoginResponse> updateusername(
        @Path("id") int id,
        @Field("nome") EditText nome
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
            .baseUrl("http://apptransescolar.com.br/Slim3RestApi/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

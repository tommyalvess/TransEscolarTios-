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

import static br.com.transescolar.API.URL.BASE_URL;

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
    @PUT("updateusername/{id}")
    Call<LoginResponse> updateusername(
        @Path("id") int id,
        @Field("nome") EditText nome
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

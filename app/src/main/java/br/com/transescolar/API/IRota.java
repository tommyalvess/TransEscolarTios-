package br.com.transescolar.API;

import java.util.List;

import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Rota;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

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
}

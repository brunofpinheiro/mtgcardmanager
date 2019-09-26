package com.br.mtgcardmanager.Network;

import com.br.mtgcardmanager.Model.APICards;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {
    @GET("/v1/cards")
    Call<APICards> getAllCards();

    @GET("/v1/cards")
    Call<APICards> getCardsByName(@Query("name") String name, @Query("language") String language, @Query("pageSize") int pageSize);
}

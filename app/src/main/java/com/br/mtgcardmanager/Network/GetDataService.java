package com.br.mtgcardmanager.Network;

import com.br.mtgcardmanager.Model.APICard;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {
    @GET("/v1/cards")
    Call<APICard> getAllCards();

    @GET("/v1/cards")
    Call<APICard> getCardsByName(@Query("name") String name, @Query("language") String language, @Query("pageSize") int pageSize);
}

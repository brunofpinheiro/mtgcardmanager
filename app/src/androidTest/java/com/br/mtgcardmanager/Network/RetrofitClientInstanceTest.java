package com.br.mtgcardmanager.Network;

import org.junit.Test;

import retrofit2.Retrofit;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrofitClientInstanceTest {

    @Test
    public void getRetrofitInstance() {
        Retrofit retrofit;

        retrofit = RetrofitClientInstance.getRetrofitInstance();

        assertThat(retrofit).isNotNull();
    }
}
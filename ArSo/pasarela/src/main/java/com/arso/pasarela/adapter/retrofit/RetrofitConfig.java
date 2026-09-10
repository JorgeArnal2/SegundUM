package com.arso.pasarela.adapter.retrofit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class RetrofitConfig {

    @Value("${microservicios.usuarios.base-url}")
    private String usuariosBaseUrl;

    @Bean
    public UsuariosApiClient usuariosApiClient() {
        ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return new Retrofit.Builder()
            .baseUrl(usuariosBaseUrl)
            .client(new OkHttpClient.Builder().build())
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
            .create(UsuariosApiClient.class);
    }
}

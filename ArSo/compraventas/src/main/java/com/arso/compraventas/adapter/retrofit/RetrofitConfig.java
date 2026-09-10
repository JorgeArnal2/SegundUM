package com.arso.compraventas.adapter.retrofit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class RetrofitConfig {

    @Value("${microservicios.productos.base-url}")
    private String productosBaseUrl;

    @Value("${microservicios.usuarios.base-url}")
    private String usuariosBaseUrl;

    private OkHttpClient buildHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    private ObjectMapper buildObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public ProductosApiClient productosApiClient() {
        return new Retrofit.Builder()
                .baseUrl(productosBaseUrl)
                .client(buildHttpClient())
                .addConverterFactory(JacksonConverterFactory.create(buildObjectMapper()))
                .build()
                .create(ProductosApiClient.class);
    }

    @Bean
    public UsuariosApiClient usuariosApiClient() {
        return new Retrofit.Builder()
                .baseUrl(usuariosBaseUrl)
                .client(buildHttpClient())
                .addConverterFactory(JacksonConverterFactory.create(buildObjectMapper()))
                .build()
                .create(UsuariosApiClient.class);
    }
}

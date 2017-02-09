package com.example.mhewedy.soundclouddownlder;

import com.jakewharton.retrofit.Ok3Client;

import okhttp3.OkHttpClient;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by mhewedy on 2/9/17.
 */
//https://kmangutov.wordpress.com/2015/03/28/android-mvp-consuming-restful-apis/
public class SCService {

    private static final String SC_URL = "http://api.soundcloud.com";
    private static final String CLIENT_ID = "ee7673c7eab7e8dfb40b831344c297b4";
    private SCApi scApi;

    public SCService() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Accept", "application/json");
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.followRedirects(true);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new Ok3Client(builder.build()))
                .setEndpoint(SC_URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        scApi = restAdapter.create(SCApi.class);
    }

    public SCApi getApi() {
        return scApi;
    }

    public interface SCApi {

        @GET("/resolve.json?client_id=" + CLIENT_ID)
        public Observable<Track> getTrack(@Query("url") String url);

        @GET("/i1/tracks/{id}/streams?client_id=" + CLIENT_ID)
        public Observable<Stream> getStream(@Path("id") long trackId);
    }
}

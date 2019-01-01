package com.example.canteenchecker.canteenmanager.proxy;

import android.app.Application;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.domain.Canteen;
import com.example.canteenchecker.canteenmanager.domain.Rating;
import com.example.canteenchecker.canteenmanager.domain.ReviewData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class ServiceProxy {

    private static final String SERVICE_BASE_URL = "https://canteenchecker.azurewebsites.net/";
    private static final long ARTIFICIAL_DELAY = 5;

    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
        .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // if authenticated -> add authorization header
                if (CanteenManagerApplication.getInstance().isAuthenticated()) {
                    Request.Builder requestBuilder = original.newBuilder();
                    requestBuilder.addHeader("Authorization",
                            "Bearer " + CanteenManagerApplication
                                    .getInstance()
                                    .getAuthenticationToken());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
                // otherwise resume with original request
                return chain.proceed(original);
            }
        });


    private final Proxy proxy = new Retrofit.Builder()
            .client(httpClient.build())
            .baseUrl(SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Proxy.class);


    private void causeDelay() {
        try {
            Thread.sleep(ARTIFICIAL_DELAY);
        } catch (InterruptedException ignored) {
        }
    }


    public String Login(String username, String password) throws IOException {
        causeDelay();
        return proxy.postLogin(new ProxyLogin(username, password)).execute().body();
    }

    public Canteen getCanteen() throws IOException {
        causeDelay();

        ProxyCanteen proxyCanteen = proxy.getCanteen().execute().body();
        if (proxyCanteen != null) {
            return proxyCanteen.toCanteen();
        }

        return null;
    }

    public boolean updateCanteen(int canteenId, String name, String meal, float mealPrice, String website, String phone, String address, float averageRating, int averageWaitingTime) throws IOException {
        causeDelay();

        ProxyCanteen proxyCanteen =
                new ProxyCanteen(
                        canteenId,
                        name,
                        meal,
                        mealPrice,
                        website,
                        phone,
                        address,
                        averageRating,
                        averageWaitingTime
                        );

        // check if http status code is in range of 200-300
        retrofit2.Response r = proxy.updateCanteen(proxyCanteen).execute();
        return r.isSuccessful();
    }



    private interface Proxy {

        @POST("/Admin/Login")
        Call<String> postLogin(@Body ProxyLogin login);

        @GET("/Admin/Canteen/")
        Call<ProxyCanteen> getCanteen();

        @PUT("/Admin/Canteen")
        Call<Void> updateCanteen(@Body ProxyCanteen proxyCanteen);
//
//        @GET("/Public/Canteen/{id}/Rating?nrOfRatings=0")
//        Call<ProxyReviewData> getReviewDataForCanteen(@Path("id") String canteenId);

        //@POST("/Admin/Canteen/Rating")
        //Call<ProxyRating> postRating(@Header("Authorization") String authenticationToken, @Body ProxyNewRating rating);

    }

    private static class ProxyCanteen {
        int canteenId;
        String name;
        String meal;
        float mealPrice;
        String website;
        String phone;
        String address;
        float averageRating;
        int averageWaitingTime;
        Collection<ProxyRating> ratings;

        public ProxyCanteen(int canteenId, String name, String meal, float mealPrice, String website, String phone, String address, float averageRating, int averageWaitingTime) {
            this.canteenId = canteenId;
            this.name = name;
            this.meal = meal;
            this.mealPrice = mealPrice;
            this.website = website;
            this.phone = phone;
            this.address = address;
            this.averageRating = averageRating;
            this.averageWaitingTime = averageWaitingTime;
        }

        Canteen toCanteen() {
            // create ratings
            Collection<Rating> domainRatings = new ArrayList<>();
            for (ProxyRating proxyRating : ratings) {
                domainRatings.add(
                        new Rating(proxyRating.ratingId, proxyRating.username, proxyRating.remark, proxyRating.ratingPoints, proxyRating.timestamp));
            }
            // add to canteen
            Canteen canteen = new Canteen(String.valueOf(canteenId), name, phone, website, meal, mealPrice, averageRating, address, averageWaitingTime, domainRatings);
            return canteen;
        }
    }

    private static class ProxyReviewData {
        float average;
        //int count;
        int totalCount;
        //ProxyRating[] ratings;
        int[] countsPerGrade;

        private int getRatingsForGrade(int grade) {
            grade--;
            return countsPerGrade != null && grade >= 0 && grade < countsPerGrade.length ? countsPerGrade[grade] : 0;
        }

        ReviewData toReviewData() {
            return new ReviewData(average, totalCount, getRatingsForGrade(1), getRatingsForGrade(2), getRatingsForGrade(3), getRatingsForGrade(4), getRatingsForGrade(5));
        }
    }

    private static class ProxyRating {
        int ratingId;
        String username;
        String remark;
        int ratingPoints;
        long timestamp;
    }

    private static class ProxyLogin {
        final String username;
        final String password;

        ProxyLogin(String userName, String password) {
            this.username = userName;
            this.password = password;
        }
    }

}

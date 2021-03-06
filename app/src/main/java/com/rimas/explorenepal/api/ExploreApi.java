package com.rimas.explorenepal.api;

import com.rimas.explorenepal.model.PopularList;
import com.rimas.explorenepal.model.PostList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ExploreApi {
    private static final String url= "http://10.0.2.2:80/explore/api/explore/";
    private static final String base_url = "https://jsonplaceholder.typicode.com/";
    private static final String Phone_url= "http://192.168.1.70:80/explore/api/explore/";
//    private static final String Phone_url= "http://192.168.30.150:80/explore/api/explore/";
//private static final String Phone_url= "http://sameeralam.com.np/explore/api/explore/";



    public static ExploreService exploreService=null;

    public static ExploreService getExploreService(){
        if (exploreService==null){
            Retrofit retrofit= new Retrofit.Builder()
                    .baseUrl(Phone_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            exploreService=retrofit.create(ExploreService.class);

        }
        return exploreService;
    }


    public interface ExploreService{

        @GET("read.php")
        Call<List<PostList>> getPostList();
//        @GET("posts")
//        Call<ArrayList<PostList>> getPostList();

        @GET("readPopularData.php")
        Call<ArrayList<PopularList>> getPopularList();

    }
}

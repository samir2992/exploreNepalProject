package com.rimas.explorenepal.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.rimas.explorenepal.R;
import com.rimas.explorenepal.activities.Details;
import com.rimas.explorenepal.activities.Map;
import com.rimas.explorenepal.api.BookmarkApi;
import com.rimas.explorenepal.api.RecommendationApi;
import com.rimas.explorenepal.model.BookmarkList_Data;
import com.rimas.explorenepal.model.RecommendationList;
import com.rimas.explorenepal.model.SinglePost;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> implements LocationListener {

    private Context context;
    ImageButton btnMap;
    CardView cardView;
    RecyclerView recyclerView1;
    private LocationManager locationManager;
    double distance;
    private double latitude, longitude;
    RecommendationApi recommendationApi;
    boolean value=true;

    ImageButton btnBookmark, btnFavourite;


    private ArrayList<RecommendationList> recommendationLists;
    private ArrayList<SinglePost> favouriteLists;
    public RecommendationAdapter( Context context, ArrayList<RecommendationList> recommendationLists){
        this.context=context;
        this.recommendationLists= recommendationLists;

    }
    public void setPostLists(ArrayList<RecommendationList> recommendationLists) {
        this.recommendationLists = recommendationLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view= layoutInflater.inflate(R.layout.recommendation_layout, parent,false);

        btnBookmark=view.findViewById(R.id.btnRecommendationBookmark);



        btnMap=view.findViewById(R.id.btnMapBk);
        cardView= view.findViewById(R.id.recCard);

        setLocationManager();
        return new RecommendationViewHolder(view);

    }

    private void setLocationManager(){

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more Details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);

    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {

        RecommendationList recommendationList = recommendationLists.get(position);
        holder.btnBookmark.setTag("R.drawable.ic_bookmark");

        Call<ArrayList<SinglePost>> selectCall= BookmarkApi.getExploreService().getSingleData();
        selectCall.enqueue(new Callback<ArrayList<SinglePost>>() {
            @Override
            public void onResponse(Call<ArrayList<SinglePost>> call, Response<ArrayList<SinglePost>> response) {
                favouriteLists=response.body();

                for(int i=0; i<favouriteLists.size();i++){

                    if (recommendationList.getId()==favouriteLists.get(i).getId()){

                        holder.btnBookmark.setTag("R.drawable.ic_bookmark_black");
                        holder.btnBookmark.setImageResource(R.drawable.ic_bookmark_black);

                    }

                }






            }

            @Override
            public void onFailure(Call<ArrayList<SinglePost>> call, Throwable t) {

                Toast.makeText(context, "No posts", Toast.LENGTH_SHORT).show();

            }
        });


        LatLng latLngA = new LatLng(latitude, longitude);
        LatLng latLngB = new LatLng(recommendationList.getLat(), recommendationList.getLong());

        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.getLatitude());
        locationA.setLongitude(latLngA.getLongitude());
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.getLatitude());
        locationB.setLongitude(latLngB.getLongitude());

        distance = locationA.distanceTo(locationB);
//        Toast.makeText(context, String.valueOf(distance), Toast.LENGTH_SHORT).show();
        int id = recommendationList.getId();
        if (distance < 85000) {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.RecName.setText(recommendationList.getName());
            holder.RecLocation.setText(recommendationList.getLocation());
            holder.RecDescription.setText(recommendationList.getDescription());

            Glide.with(holder.RecImage.getContext())
                    .load(Uri.parse(recommendationList.getImage()))
                    .placeholder(holder.RecImage.getDrawable())
                    .into(holder.RecImage);

            holder.RecImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Details.class);
                    intent.putExtra("imageName", recommendationList.getName());
                    intent.putExtra("imageDescription", recommendationList.getDescription());
                    intent.putExtra("imageLocation", recommendationList.getLocation());
                    intent.putExtra("image", recommendationList.getImage());
                    context.startActivity(intent);
                }
            });
            holder.RecName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Details.class);
                    intent.putExtra("imageName", recommendationList.getName());
                    intent.putExtra("imageDescription", recommendationList.getDescription());
                    intent.putExtra("imageLocation", recommendationList.getLocation());
                    intent.putExtra("image", recommendationList.getImage());
                    context.startActivity(intent);
                }
            });
            holder.RecDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Details.class);
                    intent.putExtra("imageName", recommendationList.getName());
                    intent.putExtra("imageDescription", recommendationList.getDescription());
                    intent.putExtra("imageLocation", recommendationList.getLocation());
                    intent.putExtra("image", recommendationList.getImage());
                    context.startActivity(intent);
                }
            });
            holder.RecLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Details.class);
                    intent.putExtra("imageName", recommendationList.getName());
                    intent.putExtra("imageDescription", recommendationList.getDescription());
                    intent.putExtra("imageLocation", recommendationList.getLocation());
                    intent.putExtra("image", recommendationList.getImage());
                    context.startActivity(intent);
                }
            });


            holder.btnLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Map.class);

                    intent.putExtra("Lat", recommendationList.getLat());
                    intent.putExtra("Long", recommendationList.getLong());
                    context.startActivity(intent);
                }
            });
            
            holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                   if(holder.btnBookmark.getTag().equals("R.drawable.ic_bookmark")){
                       holder.btnBookmark.setTag("R.drawable.ic_bookmark_black");
                       Toast.makeText(context, "Added to bookmark", Toast.LENGTH_SHORT).show();

                       holder.btnBookmark.setImageResource(R.drawable.ic_bookmark_black);
                       Call<BookmarkList_Data> newCall = RecommendationApi.getExploreService().savePost(recommendationList.getId(),recommendationList.getName(),recommendationList.getLocation()
                               ,recommendationList.getDescription(),recommendationList.getLat(), recommendationList.getLong(), recommendationList.getImage());
                       newCall.enqueue(new Callback<BookmarkList_Data>() {
                           @Override
                           public void onResponse(Call<BookmarkList_Data> call, Response<BookmarkList_Data> response) {
                               Toast.makeText(context, "Success in inserting babe", Toast.LENGTH_SHORT).show();
                           }

                           @Override
                           public void onFailure(Call<BookmarkList_Data> call, Throwable t) {

//                               Toast.makeText(context, "Oopss. sorry", Toast.LENGTH_SHORT).show();
//                               Log.e("failure", String.valueOf(t.getCause()));

                           }
                       });

                   }
                   else{
                       holder.btnBookmark.setTag("R.drawable.ic_bookmark");

                       Toast.makeText(context, "Removed from bookmark", Toast.LENGTH_SHORT).show();

                       holder.btnBookmark.setImageResource(R.drawable.ic_bookmark);

                       Call<BookmarkList_Data> deleteData= RecommendationApi.getExploreService().deletePost(recommendationList.getId());
                       deleteData.enqueue(new Callback<BookmarkList_Data>() {
                           @Override
                           public void onResponse(Call<BookmarkList_Data> call, Response<BookmarkList_Data> response) {

//                               Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();

                           }

                           @Override
                           public void onFailure(Call<BookmarkList_Data> call, Throwable t) {

//                               Toast.makeText(context, "Unable to delete.", Toast.LENGTH_SHORT).show();


                           }
                       });



                   }




                }


            });


        }

        else{
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return recommendationLists == null ? 0 : recommendationLists.size();
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude= location.getLongitude();
        latitude= location.getLatitude();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class RecommendationViewHolder extends RecyclerView.ViewHolder{

        TextView RecName, RecLocation, RecDescription;
        ImageView RecImage;
        ImageButton btnFavourite, btnLocation, btnBookmark;

        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);

            RecName= itemView.findViewById(R.id.txtRecommendationName);
            RecLocation=itemView.findViewById(R.id.txtRecommendationLocation);
            RecDescription= itemView.findViewById(R.id.txtRecommendationDescription);
            RecImage= itemView.findViewById(R.id.recommendationImage);
            btnBookmark=itemView.findViewById(R.id.btnRecommendationBookmark);
            btnLocation=itemView.findViewById(R.id.btnRecommendationLocation);
        }
    }
}

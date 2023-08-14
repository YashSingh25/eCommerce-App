package com.example.ecommercepracticeapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommercepracticeapp.adapters.CategoryAdapter;
import com.example.ecommercepracticeapp.adapters.ProductAdapter;
import com.example.ecommercepracticeapp.databinding.ActivityMainBinding;
import com.example.ecommercepracticeapp.models.CategoryModel;
import com.example.ecommercepracticeapp.models.ProductModel;
import com.example.ecommercepracticeapp.utils.Constants;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<CategoryModel> categoryModels;
    ProductAdapter productAdapter;
    ArrayList<ProductModel> productModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent=new Intent(MainActivity.this , SearchActivity.class);
                intent.putExtra("query" , text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        showCategories();
        showProducts();
        showSliders();

    }

    private void showSliders() {
        getSliders();
    }

    private void getSliders() {
    RequestQueue queue=Volley.newRequestQueue(this);
    StringRequest slidersReq=new StringRequest(Request.Method.GET, Constants.GET_OFFERS_URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject slidersMainObj=new JSONObject(response);
                if(slidersMainObj.getString("status").equals("success")){
                    JSONArray slidersInfoArray=slidersMainObj.getJSONArray("news_infos");
                    for(int i=0;i<slidersInfoArray.length();i++) {
                        JSONObject slidersInfoObj=slidersInfoArray.getJSONObject(i);
                        binding.carousel.addData(new CarouselItem(
                             Constants.NEWS_IMAGE_URL+ slidersInfoObj.getString("image") ,
                             slidersInfoObj.getString("title")
                        ));
                    }
                }else {
                    //Do nothing
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });
    queue.add(slidersReq);
    }

    void showCategories(){
        categoryModels=new ArrayList<>();
        getCategories();
        categoryAdapter=new CategoryAdapter(this , categoryModels);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(this , 4));
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void getCategories() {
        RequestQueue queue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObj=new JSONObject(response);
                    if(mainObj.getString("status").equals("success")){
                        JSONArray categoriesArray= mainObj.getJSONArray("categories");
                        for(int i=0;i<categoriesArray.length();i++) {
                            JSONObject categoriesArrayContentObj = categoriesArray.getJSONObject(i);
                            CategoryModel categoryModel=new CategoryModel(
                                    categoriesArrayContentObj.getString("name"),
                                    Constants.CATEGORIES_IMAGE_URL+categoriesArrayContentObj.getString("icon"),
                                    categoriesArrayContentObj.getString("color"),
                                    categoriesArrayContentObj.getString("brief"),
                                    categoriesArrayContentObj.getInt("id")
                            );
                            categoryModels.add(categoryModel);
                        }
                        categoryAdapter.notifyDataSetChanged();

                    }else {
                        //do nothing
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    void showProducts(){
        productModels=new ArrayList<>();

        getRecentProducts();

        productAdapter=new ProductAdapter(this , productModels);
        binding.rvProducts.setLayoutManager(new GridLayoutManager(this , 2));
        binding.rvProducts.setAdapter(productAdapter);
    }

    private void getRecentProducts() {
        RequestQueue queue=Volley.newRequestQueue(this);
        String countUrl=Constants.GET_PRODUCTS_URL + "?count=8";
        StringRequest productsRequest=new StringRequest(Request.Method.GET, countUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject prdctMainObj=new JSONObject(response);
                    if(prdctMainObj.getString("status").equals("success")){
                        JSONArray productsArray= prdctMainObj.getJSONArray("products");
                        for(int i=0;i<productsArray.length();i++){
                            JSONObject productsArrContentObj=productsArray.getJSONObject(i);
                            ProductModel productModel=new ProductModel(
                                    productsArrContentObj.getString("name") ,
                                    Constants.PRODUCTS_IMAGE_URL + productsArrContentObj.getString("image") ,
                                    productsArrContentObj.getString("status") ,
                                    productsArrContentObj.getDouble("price") ,
                                    productsArrContentObj.getDouble("price_discount") ,
                                    productsArrContentObj.getInt("stock") ,
                                    productsArrContentObj.getInt("id")
                                    );
                            productModels.add(productModel);
                        }
                        productAdapter.notifyDataSetChanged();

                    }else{
                        //do nothing
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(productsRequest);
    }
}
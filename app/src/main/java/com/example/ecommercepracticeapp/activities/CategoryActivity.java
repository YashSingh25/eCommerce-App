package com.example.ecommercepracticeapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommercepracticeapp.adapters.CategoryAdapter;
import com.example.ecommercepracticeapp.adapters.ProductAdapter;
import com.example.ecommercepracticeapp.databinding.ActivityCategoryBinding;
import com.example.ecommercepracticeapp.models.CategoryModel;
import com.example.ecommercepracticeapp.models.ProductModel;
import com.example.ecommercepracticeapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    ActivityCategoryBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<CategoryModel> categoryModels;
    ProductAdapter productAdapter;
    ArrayList<ProductModel> productModels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productModels=new ArrayList<>();
        productAdapter=new ProductAdapter(this , productModels);

        int catId=getIntent().getIntExtra("catId" , 0);
        String categoryName=getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(categoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getProducts(catId);

        binding.rvProductsList.setLayoutManager(new GridLayoutManager(this , 2));
        binding.rvProductsList.setAdapter(productAdapter);

    }

    private void getProducts(int catId) {
        RequestQueue queue= Volley.newRequestQueue(this);
        String countUrl= Constants.GET_PRODUCTS_URL + "?category_id="+catId;
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
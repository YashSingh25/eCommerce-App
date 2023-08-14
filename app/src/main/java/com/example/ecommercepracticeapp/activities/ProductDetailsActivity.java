package com.example.ecommercepracticeapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.ecommercepracticeapp.R;
import com.example.ecommercepracticeapp.databinding.ActivityProductDetailsBinding;
import com.example.ecommercepracticeapp.models.ProductModel;
import com.example.ecommercepracticeapp.utils.Constants;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ProductDetailsActivity extends AppCompatActivity {
  ActivityProductDetailsBinding binding;
  ProductModel currProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name=getIntent().getStringExtra("name");
        String image=getIntent().getStringExtra("image");
        int id=getIntent().getIntExtra("id" , 0);
        double price=getIntent().getDoubleExtra("price" , 0);

        Glide.with(this).load(image).into(binding.imageView);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Cart cart= TinyCartHelper.getCart();

        getProductDetails(id);
        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.addItem(currProduct , 1);
                binding.btnAddToCart.setEnabled(false);
                binding.btnAddToCart.setText("Added To Cart");

            }
        });
    }

    private void getProductDetails(int id) {
        RequestQueue queue= Volley.newRequestQueue(this);
        String url= Constants.GET_PRODUCT_DETAILS_URL + id ;
        StringRequest prdctDetailsReq=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject prdctDetailsMainObj=new JSONObject(response);
                    if(prdctDetailsMainObj.getString("status").equals("success")){
                        JSONObject childObj=prdctDetailsMainObj.getJSONObject("product");
                        binding.textView.setText(Html.fromHtml(childObj.getString("description")));
                        currProduct=new ProductModel(
                                childObj.getString("name") ,
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString("image") ,
                                childObj.getString("status") ,
                                childObj.getDouble("price") ,
                                childObj.getDouble("price_discount") ,
                                childObj.getInt("stock") ,
                                childObj.getInt("id")
                        );


                    }

                    else{
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
        queue.add(prdctDetailsReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.cart){
            Intent intent=new Intent(ProductDetailsActivity.this , CartActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
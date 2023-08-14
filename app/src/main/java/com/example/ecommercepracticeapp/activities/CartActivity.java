package com.example.ecommercepracticeapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ecommercepracticeapp.adapters.CartAdapter;
import com.example.ecommercepracticeapp.databinding.ActivityCartBinding;
import com.example.ecommercepracticeapp.models.ProductModel;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {
ActivityCartBinding binding;
CartAdapter cartAdapter;
ArrayList<ProductModel> productModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Cart cart= TinyCartHelper.getCart();
        productModels=new ArrayList<>();

        for(Map.Entry<Item, Integer> item: cart.getAllItemsWithQty().entrySet())
        {
            ProductModel product= (ProductModel) item.getKey();
            int quantity=item.getValue();
            product.setQuantity(quantity);
            productModels.add(product);
        }

        cartAdapter=new CartAdapter(this, productModels, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.tvSubTotalAmt.setText(String.format("₹ %.2f" , cart.getTotalPrice()));
            }
        });
        LinearLayoutManager manager=new LinearLayoutManager(this );
        DividerItemDecoration decoration=new DividerItemDecoration(this , manager.getOrientation());
        binding.rvCartItems.setLayoutManager(manager);
        binding.rvCartItems.addItemDecoration(decoration);
        binding.rvCartItems.setAdapter(cartAdapter);

        binding.tvSubTotalAmt.setText(String.format("₹ %.2f" , cart.getTotalPrice()));

        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CartActivity.this , CheckoutActivity.class);
                startActivity(intent);
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}

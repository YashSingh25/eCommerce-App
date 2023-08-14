package com.example.ecommercepracticeapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ecommercepracticeapp.databinding.ActivityPaymentBinding;
import com.example.ecommercepracticeapp.utils.Constants;

public class PaymentActivity extends AppCompatActivity {
        ActivityPaymentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String orderNumber=getIntent().getStringExtra("orderNumber");

        binding.webView.setMixedContentAllowed(false);
        binding.webView.loadUrl(Constants.PAYMENT_URL+orderNumber);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
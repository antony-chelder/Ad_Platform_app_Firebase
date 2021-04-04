package com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tony_fire.descorder.R;
import com.tony_fire.descorder.databinding.ActivityFilterBinding;
import com.utils.CountryManager;
import com.utils.DialogCountryHelper;

public class FilterActivity extends AppCompatActivity {
    private ActivityFilterBinding rootElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootElement = ActivityFilterBinding.inflate(getLayoutInflater());
        setContentView(rootElement.getRoot());
        init();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    public void onClickCountry(View view) {
        String city = rootElement.tvCity.getText().toString();
        if(!city.equals(getString(R.string.select_city))) {
            rootElement.tvCity.setText(R.string.select_city);
        }
        DialogCountryHelper.INSTANCE.showDialog(this, CountryManager.INSTANCE.getAllCountries(this), (TextView) view);
    }
    public void onClickCity(View view) {
        String country = rootElement.tvCountry.getText().toString();
        if(!country.equals(getString(R.string.select_country))) {
            DialogCountryHelper.INSTANCE.showDialog(this, CountryManager.INSTANCE.getAllCities(this,country ), (TextView) view);
        }else{
            Toast.makeText(this, "Страна не выбрана", Toast.LENGTH_LONG).show();
        }
    }
}
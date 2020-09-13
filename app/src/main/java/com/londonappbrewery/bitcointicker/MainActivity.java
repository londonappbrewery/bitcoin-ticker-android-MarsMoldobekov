package com.londonappbrewery.bitcointicker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "Bitcoin";

    private TextView mPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = findViewById(R.id.priceLabel);
        Spinner spinner = findViewById(R.id.currency_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.currency_array,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(LOG_TAG, adapterView.getItemAtPosition(i).toString());
                letsDoSomeNetworking();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(LOG_TAG, "Nothing selected");
            }
        });
    }

    private void letsDoSomeNetworking() {
        AsyncHttpClient client = new AsyncHttpClient();
        String URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTCUSD";
        client.get(URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(LOG_TAG, "JSON: " + response.toString());
                String bitcoinRate = fromJSON(response);
                updateUI(bitcoinRate);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d(LOG_TAG, "Request fail! Status code: " + statusCode);
                Log.d(LOG_TAG, "Fail response: " + response);
                Log.e(LOG_TAG, e.toString());
            }
        });
    }

    private String fromJSON(JSONObject response) {
        try {
            return response.getString("ask");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateUI(String bitcoinRate) {
        mPriceTextView.setText(bitcoinRate);
    }
}

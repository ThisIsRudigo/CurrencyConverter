package com.loya.android.currencyconverter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.loya.android.currencyconverter.data.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Dummy Test Class
 */

public class DisplayActivity extends AppCompatActivity {

//    private TextView ethToUsd;
//    private TextView ethToEur;
//    private TextView ethToNgn;

    /**
     * URL for currency  data from the CryptoCompare API
     */
    private static final String REQUEST_URL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=ETH,BTC&tsyms=USD,EUR,NGN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //create an AsyncTask to perform the HTTP request to the given url
        //on a background thread.when the result is received on the main UI thread, then update the UI
        CurrencyAsyncTask task = new CurrencyAsyncTask();
        task.execute(REQUEST_URL);



    }


    /**
     * Update the UI with the given earthquake information.
     */
    private void updateUi(JSONObject jsonObject) {
        try {
            JSONObject ethJsonObject = jsonObject.getJSONObject("ETH");
            double usd = ethJsonObject.getDouble("USD");
            double eur = ethJsonObject.getDouble("EUR");
            double ngn = ethJsonObject.getDouble("NGN");

            JSONObject btcJsonObject = jsonObject.getJSONObject("BTC");
            double usd1 = btcJsonObject.getDouble("USD");
            double eur1 = btcJsonObject.getDouble("EUR");
            double ngn1 = btcJsonObject.getDouble("NGN");








            TextView ethToUsd = (TextView) findViewById(R.id.ethtousd);
            ethToUsd.setText(String.valueOf(usd));

            TextView ethToeur = (TextView) findViewById(R.id.ethtoeur);
            ethToeur.setText(String.valueOf(eur));

            TextView ethTongn = (TextView) findViewById(R.id.ethtongn);
            ethTongn.setText(String.valueOf(ngn));



            TextView btcToUsd = (TextView) findViewById(R.id.btctousd);
            btcToUsd.setText(String.valueOf(usd1));
//
//
            TextView btcToeur = (TextView) findViewById(R.id.btctoeur);
            btcToeur.setText(String.valueOf(eur1));
//
            TextView btcTongn = (TextView) findViewById(R.id.btctongn);
            btcTongn.setText(String.valueOf(ngn1));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first earthquake in the response.
     */
    private class CurrencyAsyncTask extends AsyncTask<String, Void, JSONObject> {

        /**
         * This method is invoked (or called) on a background thread, so we can perform
         * long-running operations like making a network request.
         * <p>
         * It is NOT okay to update the UI from a background thread, so we just return an
         * {@link } object as the result.
         */
        protected JSONObject doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            JSONObject jsonObject = Utils.fetchJsonData(urls[0]);
            return jsonObject;
        }

        /**
         * This method is invoked on the main UI thread after the background work has been
         * completed.
         * <p>
         * It IS okay to modify the UI within this method. We take the {@link Event} object
         * (which was returned from the doInBackground() method) and update the views on the screen.
         */
        protected void onPostExecute(JSONObject result) {
            // If there is no result, do nothing.
            if (result == null) {
                return;
            }

            updateUi(result);
        }
    }


}

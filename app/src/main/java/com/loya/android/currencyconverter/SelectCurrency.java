package com.loya.android.currencyconverter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loya.android.currencyconverter.data.CurrencyContract;
import com.loya.android.currencyconverter.data.CurrencyDbHelper;
import com.loya.android.currencyconverter.data.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Dummy Test Class
 */

public class SelectCurrency extends AppCompatActivity {

    private Spinner mFromSpinner;
    private Spinner mToSpinner;
    private Button insertTable1;
    private Button insertTable2;
    private Button displayBtn;
    private Button displayBtn2;
    //private TextView displayText;


    private String mCryptoUnit = null;
    private String mCurrencyUnit = null;

    private CurrencyDbHelper mDbHelper;

    private static final String REQUEST_URL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=ETH&tsyms=USD,EUR,NGN";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_currency);

        mFromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        mToSpinner = (Spinner) findViewById(R.id.toSpinner);
        insertTable1 = (Button) findViewById(R.id.insertTable1);
        insertTable2 = (Button) findViewById(R.id.insertTable2);
        displayBtn = (Button) findViewById(R.id.displayBtn);
        displayBtn2 = (Button) findViewById(R.id.displayBtn2);


        setUpCryptoSpinner();
        setUpCurrencySpinner();


        mDbHelper = new CurrencyDbHelper(this);


        //create an AsyncTask to perform the HTTP request to the given url
        //on a background thread.when the result is received on the main UI thread, then update the UI
        CurrencyAsyncTask task = new CurrencyAsyncTask();
        task.execute(REQUEST_URL);

//        insertTable1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                insertCurrency();
//
//            }
//        });
//        insertTable2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                insertCurrencyTable2();
//
//            }
//        });

        displayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                displayCurrency();
            }
        });
        displayBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayTable2();
            }
        });




    }


    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setUpCryptoSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter cryptoSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.cryptoCurrencies, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        cryptoSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mFromSpinner.setAdapter(cryptoSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("BTC")) {
                        mCryptoUnit = "btc";
                    } else {
                        mCryptoUnit = "eth";
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCryptoUnit = "BTC"; // Unknown
            }
        });


    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setUpCurrencySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter cryptoSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        cryptoSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mToSpinner.setAdapter(cryptoSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("EUR")) {
                        mCurrencyUnit = "eur";
                    } else if (selection.equals("USD")) {
                        mCurrencyUnit = "usd";
                    } else {
                        mCurrencyUnit = "ngn";
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCryptoUnit = "BTC"; // Unknown
            }
        });

    }

    private void insertCurrency() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CurrencyContract.CurrencyEntry.COLUMN_BTCTOEUR, 3913.93);
        values.put(CurrencyContract.CurrencyEntry.COLUMN_BTCTOUSD, 4616.45);
        values.put(CurrencyContract.CurrencyEntry.COLUMN_BTCTONGN, 1593596.18);

        long rowId = db.insert(CurrencyContract.CurrencyEntry.TABLE1_NAME, null, values);
        Toast.makeText(SelectCurrency.this, "inserted in table 1 with Row id: " + rowId, Toast.LENGTH_LONG).show();


    }

    private void displayCurrency() {
             SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String colName = mCryptoUnit + "to" + mCurrencyUnit;

        if(!colName.startsWith("btc")){
            return;
        }

        String[] projection = {
                CurrencyContract.CurrencyEntry.BTC_ID,
                colName
//                CurrencyContract.CurrencyEntry.COLUMN_BTCTOEUR,
//                CurrencyContract.CurrencyEntry.COLUMN_BTCTOUSD,
//                CurrencyContract.CurrencyEntry.COLUMN_BTCTONGN,
        };
        // Perform  query on pets table
        Cursor cursor = db.query(
                CurrencyContract.CurrencyEntry.TABLE1_NAME, //The table to query
                projection,          //The column to return
                null,                //The column for the WHERE clause
                null,                //The values for the WHERE clause
                null,                //don't group the rows
                null,                //don't filter the row groups
                null);               //The sort order


        try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            TextView displayView = (TextView) findViewById(R.id.displayTv);

            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
            displayView.append(CurrencyContract.CurrencyEntry.BTC_ID + " - " +
                    CurrencyContract.CurrencyEntry.COLUMN_BTCTOEUR + " - " + CurrencyContract.CurrencyEntry.COLUMN_BTCTOUSD + " - " +
                    CurrencyContract.CurrencyEntry.COLUMN_BTCTONGN + "\n");


            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.BTC_ID);
            int nameColumnIndex = cursor.getColumnIndex(colName);
//            int breedColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_BTCTOUSD);
//            int genderColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_BTCTONGN);


            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                double btctoeur = cursor.getDouble(nameColumnIndex);
//                double btctousd = cursor.getDouble(breedColumnIndex);
//                double btctongn = cursor.getDouble(genderColumnIndex);


                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        btctoeur + " - "));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            assert cursor != null;
            cursor.close();
        }


    }

    private void insertCurrencyTable2() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CurrencyContract.CurrencyEntry.COLUMN_ETHTOEUR, 260.03);
        values.put(CurrencyContract.CurrencyEntry.COLUMN_ETHTOUSD, 306.54);
        values.put(CurrencyContract.CurrencyEntry.COLUMN_ETHTONGN, 105974.15);

        long rowId = db.insert(CurrencyContract.CurrencyEntry.TABLE2_NAME, null, values);
        Toast.makeText(SelectCurrency.this, "inserted in table 2 with Row id: " + rowId, Toast.LENGTH_LONG).show();


    }

    private void displayTable2() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        String[] projection = {
                CurrencyContract.CurrencyEntry.ETH_ID,
                CurrencyContract.CurrencyEntry.COLUMN_ETHTOEUR,
                CurrencyContract.CurrencyEntry.COLUMN_ETHTOUSD,
                CurrencyContract.CurrencyEntry.COLUMN_ETHTONGN,
        };
        // Perform  query on pets table
        Cursor cursor = db.query(
                CurrencyContract.CurrencyEntry.TABLE2_NAME, //The table to query
                projection,          //The column to return
                null,                //The column for the WHERE clause
                null,                //The values for the WHERE clause
                null,                //don't group the rows
                null,                //don't filter the row groups
                null);               //The sort order


        try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            TextView displayView = (TextView) findViewById(R.id.displayTv);

            displayView.setText("The second table contains " + cursor.getCount() + " currencies.\n\n");
            displayView.append(CurrencyContract.CurrencyEntry.ETH_ID + " - " +
                    CurrencyContract.CurrencyEntry.COLUMN_ETHTOEUR + " - " + CurrencyContract.CurrencyEntry.COLUMN_ETHTOUSD + " - " +
                    CurrencyContract.CurrencyEntry.COLUMN_ETHTONGN + "\n");


            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.ETH_ID);
            int nameColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_ETHTOEUR);
            int breedColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_ETHTOUSD);
            int genderColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_ETHTONGN);


            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                double ethtoeur = cursor.getDouble(nameColumnIndex);
                double ethtousd = cursor.getDouble(breedColumnIndex);
                double ethtongn = cursor.getDouble(genderColumnIndex);


                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        ethtoeur + " - " + ethtousd + " - " + ethtongn));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            assert cursor != null;
            cursor.close();
        }

    }



    /**
     * Update the UI with the given earthquake information.
     */
    private void updateUi(JSONObject jsonObject) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(CurrencyContract.CurrencyEntry.TABLE1_NAME, null, null);
        try {
            JSONObject ethJsonObject = jsonObject.getJSONObject("ETH");
            double usd = ethJsonObject.getDouble("USD");
            double eur = ethJsonObject.getDouble("EUR");
            double ngn = ethJsonObject.getDouble("NGN");

//            JSONObject btcJsonObject = jsonObject.getJSONObject("BTC");
//            double usd1 = btcJsonObject.getDouble("USD");
//            double eur1 = btcJsonObject.getDouble("EUR");
//            double ngn1 = btcJsonObject.getDouble("NGN");


            ContentValues values = new ContentValues();
            values.put(CurrencyContract.CurrencyEntry.COLUMN_BTCTOEUR, eur);
            values.put(CurrencyContract.CurrencyEntry.COLUMN_BTCTOUSD, usd);
            values.put(CurrencyContract.CurrencyEntry.COLUMN_BTCTONGN, ngn);

            long rowId = db.insert(CurrencyContract.CurrencyEntry.TABLE1_NAME, null, values);
            Toast.makeText(SelectCurrency.this, "inserted in table 1 with Row id: " + rowId, Toast.LENGTH_LONG).show();

            Log.v("USD:", String.valueOf(usd));
            Log.v("EUR:", String.valueOf(eur));
            Log.v("NGN", String.valueOf(ngn));



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

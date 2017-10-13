package com.loya.android.currencyconverter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loya.android.currencyconverter.data.CurrencyContract;
import com.loya.android.currencyconverter.data.CurrencyDbHelper;

public class CustomCurrency extends AppCompatActivity {

    private Spinner mFromSpinner;
    private Spinner mToSpinner;
    private Button okButton;

    private String mCryptoUnit = null;
    private String mCurrencyUnit = null;


    private CurrencyDbHelper mDbHelper;

    private Button displayUserBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_currency);
        mFromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        mToSpinner = (Spinner) findViewById(R.id.toSpinner);
        okButton = (Button) findViewById(R.id.okButton);

        displayUserBtn = (Button) findViewById(R.id.userButton);

        mDbHelper = new CurrencyDbHelper(this);
        setUpCryptoSpinner();
        setUpCurrencySpinner();


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryAndInsert();
                finish();
            }
        });

        displayUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayUserTable();
            }
        });


    }

    private void queryAndInsert() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String colName = mCryptoUnit + "to" + mCurrencyUnit;

        if (!colName.startsWith("btc")) {
            // return;
            String[] projection = {
                    CurrencyContract.CurrencyEntry.ETH_ID,
                    colName
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

                TextView displayView = (TextView) findViewById(R.id.displayTv);

                displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
                displayView.append(CurrencyContract.CurrencyEntry.ETH_ID + " - " +
                        CurrencyContract.CurrencyEntry.COLUMN_ETHTOEUR + " - " + CurrencyContract.CurrencyEntry.COLUMN_ETHTOUSD + " - " +
                        CurrencyContract.CurrencyEntry.COLUMN_ETHTONGN + "\n");


                // Figure out the index of each column
                int idColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry._ID);
                int nameColumnIndex = cursor.getColumnIndex(colName);
//            int breedColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_BTCTOUSD);
//            int genderColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_BTCTONGN);


                // Iterate through all the returned rows in the cursor
                while (cursor.moveToNext()) {
                    // Use that index to extract the String or Int value of the word
                    // at the current row the cursor is on.
                    int currentID = cursor.getInt(idColumnIndex);
                    double btctoeur = cursor.getDouble(nameColumnIndex);


                    insertCurrencyTable2(mCryptoUnit, mCurrencyUnit, btctoeur);

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


        } else {


            String[] projection = {
                    CurrencyContract.CurrencyEntry.BTC_ID,
                    colName
            };
            // Perform  query on BTC table
            Cursor cursor = db.query(
                    CurrencyContract.CurrencyEntry.TABLE1_NAME, //The table to query
                    projection,          //The column to return
                    null,                //The column for the WHERE clause
                    null,                //The values for the WHERE clause
                    null,                //don't group the rows
                    null,                //don't filter the row groups
                    null);               //The sort order


            try {

                TextView displayView = (TextView) findViewById(R.id.displayTv);

                displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
                displayView.append(CurrencyContract.CurrencyEntry.BTC_ID + " - " +
                        CurrencyContract.CurrencyEntry.COLUMN_BTCTOEUR + " - " + CurrencyContract.CurrencyEntry.COLUMN_BTCTOUSD + " - " +
                        CurrencyContract.CurrencyEntry.COLUMN_BTCTONGN + "\n");


                //dummy test data
                // Figure out the index of each column
                int idColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry._ID);
                int nameColumnIndex = cursor.getColumnIndex(colName);


                // Iterate through all the returned rows in the cursor
                while (cursor.moveToNext()) {
                    // Use that index to extract the String or Int value of the word
                    // at the current row the cursor is on.
                    int currentID = cursor.getInt(idColumnIndex);
                    double btctoeur = cursor.getDouble(nameColumnIndex);
//                double btctousd = cursor.getDouble(breedColumnIndex);
//                double btctongn = cursor.getDouble(genderColumnIndex);

                    insertCurrencyTable2(mCryptoUnit, mCurrencyUnit, btctoeur);

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
    }

    /**
     * Setup the dropdown spinner that allows the user to select the crypto currency.
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
                mCryptoUnit = "BTC";
            }
        });


    }

    /**
     * Setup the dropdown spinner that allows the user to select the Currencies.
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
                mCryptoUnit = "BTC";
            }
        });

    }


    private void insertCurrencyTable2(String cyrptoName, String currencyName, double value) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CurrencyContract.CurrencyEntry.COLUMN_CRYPTO_NAME, cyrptoName);
        values.put(CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_NAME, currencyName);
        values.put(CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_VALUE, value);

        long rowId = db.insert(CurrencyContract.CurrencyEntry.TABLE3_NAME, null, values);
        Toast.makeText(CustomCurrency.this, "inserted in table 3 with Row id: " + rowId, Toast.LENGTH_LONG).show();


    }


    private void displayUserTable() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        String[] projection = {
                CurrencyContract.CurrencyEntry.USER_ID,
                CurrencyContract.CurrencyEntry.COLUMN_CRYPTO_NAME,
                CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_NAME,
                CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_VALUE,
        };
        // Perform  query on USER table
        Cursor cursor = db.query(
                CurrencyContract.CurrencyEntry.TABLE3_NAME, //The table to query
                projection,          //The column to return
                null,                //The column for the WHERE clause
                null,                //The values for the WHERE clause
                null,                //don't group the rows
                null,                //don't filter the row groups
                null);               //The sort order


        try {

            TextView displayView = (TextView) findViewById(R.id.displayTv);

            displayView.setText("The user table contains " + cursor.getCount() + " currencies.\n\n");
            displayView.append(CurrencyContract.CurrencyEntry.USER_ID + " - " +
                    CurrencyContract.CurrencyEntry.COLUMN_CRYPTO_NAME + " - " + CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_NAME + " - " +
                    CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_VALUE + "\n");


            //dummy test data
            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.USER_ID);
            int nameColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_CRYPTO_NAME);
            int breedColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_NAME);
            int genderColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_VALUE);


            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String crypto_name = cursor.getString(nameColumnIndex);
                String currency_name = cursor.getString(breedColumnIndex);
                double currency_val = cursor.getDouble(genderColumnIndex);


                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        crypto_name + " - " + currency_name + " - " + currency_val));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            assert cursor != null;
            cursor.close();
        }

    }


}

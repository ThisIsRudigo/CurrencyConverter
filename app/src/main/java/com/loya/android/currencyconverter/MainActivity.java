package com.loya.android.currencyconverter;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.loya.android.currencyconverter.data.CurrencyContract;
import com.loya.android.currencyconverter.data.CurrencyDbHelper;
import com.loya.android.currencyconverter.data.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, CurrencyAdapter.ListItemClickListener {
    private CurrencyDbHelper mDbHelper;

    private String colName;

    private static final String REQUEST_URL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=ETH,BTC&tsyms=USD,EUR,NGN";


    //integer Loader constant for the loader, could be any number
    private static final int CURRENCY_LOADER = 0;

    //adapter to be used for the list view
    private CurrencyAdapter mCurrencyAdapter;

    private RecyclerView mRecycler;

    private List<String> stringList;

    private static final int DB_LOADER_ID = 0;

    private Toast mToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        colName = "btctoeur";
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mRecycler.setLayoutManager(linearLayoutManager);

        mRecycler.setHasFixedSize(true);

        mCurrencyAdapter = new CurrencyAdapter(this, null, this);
        mRecycler.setAdapter(mCurrencyAdapter);


        stringList = new ArrayList<>();


        mDbHelper = new CurrencyDbHelper(this);
        //create an AsyncTask to perform the HTTP request to the given url
        //on a background thread.when the result is received on the main UI thread, then update the UI
        CurrencyAsyncTask task = new CurrencyAsyncTask();
        task.execute(REQUEST_URL);


          /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                int id = (int) viewHolder.itemView.getTag();

                Toast.makeText(MainActivity.this, "" + id, Toast.LENGTH_LONG).show();
                showDeleteConfirmationDialog(id);
                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(DB_LOADER_ID, null, MainActivity.this);

            }
        }).attachToRecyclerView(mRecycler);


          /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(DB_LOADER_ID, null, this);

//
//        mCurrencyAdapter = new CurrencyAdapter(this, stringList);
//
//        mRecycler.setAdapter(mCurrencyAdapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CustomCurrency.class);
                startActivity(intent);
//                DbAsyncTask dbTask = new DbAsyncTask();
//                dbTask.execute(colName);
            }
        });
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog(final int currentItem) {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_this_entry);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteCurrency(currentItem);
                getSupportLoaderManager().restartLoader(DB_LOADER_ID, null, MainActivity.this);

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteCurrency(int id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = CurrencyContract.CurrencyEntry.USER_ID + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(id)};
        // Issue SQL statement.
        db.delete(CurrencyContract.CurrencyEntry.TABLE3_NAME, selection, selectionArgs);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        DbAsyncTask dbTask = new DbAsyncTask();
//        dbTask.execute(colName);
        getSupportLoaderManager().restartLoader(DB_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_data) {
            Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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

            JSONObject btcJsonObject = jsonObject.getJSONObject("BTC");
            double usd1 = btcJsonObject.getDouble("USD");
            double eur1 = btcJsonObject.getDouble("EUR");
            double ngn1 = btcJsonObject.getDouble("NGN");


            ContentValues values = new ContentValues();
            values.put(CurrencyContract.CurrencyEntry.COLUMN_BTCTOEUR, eur);
            values.put(CurrencyContract.CurrencyEntry.COLUMN_BTCTOUSD, usd);
            values.put(CurrencyContract.CurrencyEntry.COLUMN_BTCTONGN, ngn);

            ContentValues values1 = new ContentValues();
            values1.put(CurrencyContract.CurrencyEntry.COLUMN_ETHTOEUR, eur1);
            values1.put(CurrencyContract.CurrencyEntry.COLUMN_ETHTOUSD, usd1);
            values1.put(CurrencyContract.CurrencyEntry.COLUMN_ETHTONGN, ngn1);


            long table1_rowId = db.insert(CurrencyContract.CurrencyEntry.TABLE1_NAME, null, values);
            Toast.makeText(MainActivity.this, "inserted in table 1 with Row id: " + table1_rowId, Toast.LENGTH_LONG).show();

            long table2_rowid = db.insert(CurrencyContract.CurrencyEntry.TABLE2_NAME, null, values1);
            Toast.makeText(MainActivity.this, "inserted in table 2 with Row id: " + table2_rowid, Toast.LENGTH_LONG).show();

            Log.v("USD:", String.valueOf(usd));
            Log.v("EUR:", String.valueOf(eur));
            Log.v("NGN", String.valueOf(ngn));


            Log.v("USD1:", String.valueOf(usd1));
            Log.v("EUR1:", String.valueOf(eur1));
            Log.v("NGN1", String.valueOf(ngn1));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    //force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();


                String[] projection = {
                        CurrencyContract.CurrencyEntry.USER_ID,
                        CurrencyContract.CurrencyEntry.COLUMN_CRYPTO_NAME,
                        CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_NAME,
                        CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_VALUE,
                };
                // Perform  query on us table
                try {
                    return db.query(
                            CurrencyContract.CurrencyEntry.TABLE3_NAME, //The table to query
                            projection,          //The column to return
                            null,                //The column for the WHERE clause
                            null,                //The values for the WHERE clause
                            null,                //don't group the rows
                            null,                //don't filter the row groups
                            null);               //The sort order
                } catch (Exception e) {
                    Log.e("TAG", "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }

        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mCurrencyAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Update the data that the adapter uses to create ViewHolders
        mCurrencyAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(String cryptoName, String currencyName, double currencyValue) {

        if (mToast != null) {
            mToast.cancel();
        }
        String message = "Crypto Name:" + cryptoName + " \nCurrency Name: " + currencyName + "\nCurrency Value: " + currencyValue;
        mToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
        mToast.show();

        Intent convertIntent = new Intent(MainActivity.this, ConvertCurrency.class);
        convertIntent.putExtra("cryptoName", cryptoName);
        convertIntent.putExtra("currencyName", currencyName);
        convertIntent.putExtra("currencyValue", currencyValue);

        startActivity(convertIntent);

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
         * It IS okay to modify the UI within this method. We take the {@link } object
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

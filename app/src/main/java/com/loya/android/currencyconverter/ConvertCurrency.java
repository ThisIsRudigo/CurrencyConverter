package com.loya.android.currencyconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConvertCurrency extends AppCompatActivity {
    private String message;

    private EditText cryptoTextField;
    private EditText currencyTextField;

    private Button convertButton;

    private String cryptoName, currencyName;

    private double currencyValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_currency);


        cryptoTextField = (EditText) findViewById(R.id.cryptoField);
        currencyTextField = (EditText) findViewById(R.id.currencyField);

        convertButton = (Button) findViewById(R.id.convertButton);

        cryptoName = getIntent().getStringExtra("cryptoName");
        currencyName = getIntent().getStringExtra("currencyName");
        currencyValue = getIntent().getDoubleExtra("currencyValue", 0);


        cryptoTextField.setHint(cryptoName.toUpperCase());
        currencyTextField.setHint(currencyName.toUpperCase());


        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convert();
            }
        });


    }

    private void convert() {
        message = cryptoName + " " + currencyName + " " + currencyValue;
        Toast.makeText(ConvertCurrency.this, message, Toast.LENGTH_LONG).show();
        String cryptoVal = cryptoTextField.getText().toString().trim();
        String currencyVal = currencyTextField.getText().toString().trim();

        if (TextUtils.isEmpty(cryptoVal) && TextUtils.isEmpty(currencyVal)) {
            return;
        } else if (TextUtils.isEmpty(currencyVal)) {
            convertToCurrency(Double.parseDouble(cryptoVal));

        } else {
            convertToCrypto(Double.parseDouble(currencyVal));

        }

    }


    private void convertToCurrency(double cryptoVal) {
        double result = cryptoVal * currencyValue;

        currencyTextField.setText(String.valueOf(result));
    }

    private void convertToCrypto(double currencyVal) {
        double result = currencyVal / currencyValue;

        cryptoTextField.setText(String.valueOf(result));

    }
}

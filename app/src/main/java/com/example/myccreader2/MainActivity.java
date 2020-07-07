package com.example.myccreader2;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements CardNfcAsyncTask.CardNfcInterface {
    public NfcAdapter mNfcAdapter;
    public CardNfcAsyncTask mCardNfcAsyncTask;
    public boolean mIntentFromCreate = false;
    public CardNfcUtils mCardNfcUtils;

    public TextView statusTxt, txtCard, txtType, txtDate;

    protected void log(String txt) {
        Log.i("nfc", txt);
        statusTxt = findViewById(R.id.statusTxt);
        statusTxt.setText( txt + "\n" + statusTxt.getText());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtCard = findViewById(R.id.txtCard);
        txtDate = findViewById(R.id.txtDate);
        txtType = findViewById(R.id.txtType);

        log("get NFC adapter");

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null){
            //do something if there are no nfc module on device
            log("no nfc on device");

        } else {
            //do something if there are nfc module on device
            log("nfc detected on device");

            mCardNfcUtils = new CardNfcUtils(this);
//            //next few lines here needed in case you will scan credit card when app is closed
            mIntentFromCreate = true;
            onNewIntent(getIntent());
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mCardNfcUtils.disableDispatch();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            log("New intent");
            //this - interface for callbacks
            //intent = intent :)
            //mIntentFromCreate - boolean flag, for understanding if onNewIntent() was called from onCreate or not
            mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate)
                    .build();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        log("On Resume");
        mIntentFromCreate = false;
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()){
            //show some turn on nfc dialog here. take a look in the samle ;-)
            log("On Resume - nfc disabled");
        } else if (mNfcAdapter != null){
            mCardNfcUtils.enableDispatch();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void startNfcReadCard() {
        //notify user that scannig start
        log(" scanning started");

    }

    @Override
    public void cardIsReadyToRead() {
        log("Card ready to read");

        String card = mCardNfcAsyncTask.getCardNumber();
        String expiredDate = mCardNfcAsyncTask.getCardExpireDate();
        String cardType = mCardNfcAsyncTask.getCardType();

        log("card: " + card);
        log("expiry: " + expiredDate);
        log("type: " + cardType);

        txtCard.setText(card);
        txtDate.setText(expiredDate);
        txtType.setText(cardType);



    }

    @Override
    public void doNotMoveCardSoFast() {
        //notify user do not move the card
        log("stop moving card");

    }

    @Override
    public void unknownEmvCard() {
        //notify user that current card has unnown nfc tag
        log("Unknown nfc tag");

    }

    @Override
    public void cardWithLockedNfc() {
        //notify user that current card has locked nfc tag
        log("locked NFC tag");

    }

    @Override
    public void finishNfcReadCard() {
        //notify user that scannig finished
        log("finished scanning");
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
        }

        return super.onOptionsItemSelected(item);
    }
}
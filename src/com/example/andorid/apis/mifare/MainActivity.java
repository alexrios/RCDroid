package com.example.andorid.apis.mifare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.example.andorid.apis.mifare.task.ConsultaSaldoCartaoTask;
import com.example.andorid.apis.mifare.util.NumberUtil;

import java.io.IOException;

public class MainActivity extends Activity implements OnClickListener {

	// UI Elements
	private static TextView textViewNumeroCartao;
	private static TextView textViewSaldoCartao;

	// NFC parts
	private static NfcAdapter mAdapter;
	private static PendingIntent mPendingIntent;
	private static IntentFilter[] mFilters;
	private static String[][] mTechLists;

	private static final int AUTH = 1;
	private static final int EMPTY_BLOCK_0 = 2;
	private static final int EMPTY_BLOCK_1 = 3;
	private static final int NETWORK = 4;
	private static final String TAG = "purchtagscanact";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        getComponents();
		
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		// Setup an intent filter for all MIME based dispatches
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef, };

		// Setup a tech list for all NfcF tags
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };

		Intent intent = getIntent();
		resolveIntent(intent);
	}

    private void getComponents() {
        textViewNumeroCartao = (TextView) findViewById(R.id.numeroCartao);
        textViewSaldoCartao = (TextView) findViewById(R.id.saldo);
    }

    @Override
   	public void onResume() {
   		super.onResume();
   		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
   	}

   	@Override
   	public void onPause() {
   		super.onPause();
   		mAdapter.disableForegroundDispatch(this);
   	}

    @Override
   	public void onNewIntent(Intent intent) {
   		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
   		resolveIntent(intent);
   	}

	public void onClick(View v) {
		clearFields();
	}

	private void resolveIntent(Intent intent) {
		// Parse the intent
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// status_Data.setText("Discovered tag with intent: " + intent);
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			MifareClassic mfc = MifareClassic.get(tagFromIntent);
			byte[] uid;
			try {
				mfc.connect();
				uid = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
				long numeroChipCartao = NumberUtil.bigToLittleEndian(uid);
				this.textViewNumeroCartao.setText(Long.toString(numeroChipCartao));
				consultarSaldoWebService(textViewSaldoCartao, Long.toString(numeroChipCartao));
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
				showAlert(NETWORK);
			}
		}
	}

    private void consultarSaldoWebService(TextView textViewSaldo, String numeroChip) {
        ConsultaSaldoCartaoTask consultaSaldoCartaoTask = new ConsultaSaldoCartaoTask(this, textViewSaldo, numeroChip);
   		consultaSaldoCartaoTask.execute();
   	}

	private void showAlert(int alertCase) {
        String message = "";
		switch (alertCase) {
            case AUTH:              // Card Authentication Error
                message = "Authentication Failed on Block 0";
                break;
            case EMPTY_BLOCK_0:     // Block 0 Empty
                message = "Failed reading Block 0";
                break;
            case EMPTY_BLOCK_1:     // Block 1 Empty
                message = "Failed reading Block 0";
                break;
            case NETWORK:           // Communication Error
                message = "Tag reading error";
                break;
		}

        new AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    clearFields();
                }
            }).show();
	}

	private static void clearFields() {
		textViewNumeroCartao.setText("");
		textViewSaldoCartao.setText("");
	}

}
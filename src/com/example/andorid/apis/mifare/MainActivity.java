package com.example.andorid.apis.mifare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.example.andorid.apis.mifare.task.ConsultaSaldoCartaoTask;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends Activity implements OnClickListener {
	// UI Elements
	private static TextView numeroCartao;
	private static TextView saldoCartao;
	// NFC parts
	private static NfcAdapter mAdapter;
	private static PendingIntent mPendingIntent;
	private static IntentFilter[] mFilters;
	private static String[][] mTechLists;
	// Hex help
	private static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1',
			(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
			(byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };
	// Just for alerts
	
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	private static final int AUTH = 1;
	private static final int EMPTY_BLOCK_0 = 2;
	private static final int EMPTY_BLOCK_1 = 3;
	private static final int NETWORK = 4;
	private static final String TAG = "purchtagscanact";
	
	private static final String namespace = "http://10.5.34.246/";
	private static final String name = "consultaSaldo";
	private static final String URL = "http://10.5.34.256/ws";
	private static final String ACTION = "http://10.5.34.256/ws/consultaSaldo";
	
	private String saldo;

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		numeroCartao = (TextView) findViewById(R.id.numeroCartao);
		saldoCartao = (TextView) findViewById(R.id.saldo);
		
		numeroCartao.setTextColor(Color.BLACK);
		saldoCartao.setTextColor(Color.BLACK);

		// Capture Purchase button from layout
		//Button scanBut = (Button) findViewById(R.id.clear_but);
		// Register the onClick listener with the implementation above
		//scanBut.setOnClickListener(this);

		// Register the onClick listener with the implementation above
		//scanBut.setOnClickListener(this);

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		// Create a generic PendingIntent that will be deliver to this activity.
		// The NFC stack
		// will fill in the intent with the details of the discovered tag before
		// delivering to
		// this activity.
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
		mTechLists = new String[][] { new String[] { MifareClassic.class
				.getName() } };

		Intent intent = getIntent();
		resolveIntent(intent);

	}
	
	public void consultarSaldoWebService(TextView block_0_Data2, String numeroChip) {
        ConsultaSaldoCartaoTask consultaSaldoCartaoTask = new ConsultaSaldoCartaoTask(this, block_0_Data2, numeroChip);
		consultaSaldoCartaoTask.execute();
	}

	void resolveIntent(Intent intent) {
		// Parse the intent
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// status_Data.setText("Discovered tag with intent: " + intent);
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			MifareClassic mfc = MifareClassic.get(tagFromIntent);
			byte[] data;
			byte[] uid;
			try {
				mfc.connect();
				uid = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
				String uidHex = getHexString(uid, uid.length);
				long numeroChipCartao = bigToLittleEndian(uid);
				this.numeroCartao.setText(Long.toString(numeroChipCartao));
				consultarSaldoWebService(saldoCartao, Long.toString(numeroChipCartao));
			
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
				showAlert(NETWORK);
			}
		} else {
		}
	}

	private void showAlert(int alertCase) {
		// prepare the alert box
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		switch (alertCase) {

		case AUTH:// Card Authentication Error
			alertbox.setMessage("Authentication Failed on Block 0");
			break;
		case EMPTY_BLOCK_0: // Block 0 Empty
			alertbox.setMessage("Failed reading Block 0");
			break;
		case EMPTY_BLOCK_1:// Block 1 Empty
			alertbox.setMessage("Failed reading Block 0");
			break;
		case NETWORK: // Communication Error
			alertbox.setMessage("Tag reading error");
			break;
		}
		// set a positive/yes button and create a listener
		alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			// Save the data from the UI to the database - already done
			public void onClick(DialogInterface arg0, int arg1) {
				clearFields();
			}
		});
		// display box
		alertbox.show();

	}
	
	public static String fromHexString(String hex) {
	    StringBuilder str = new StringBuilder();
	    for (int i = 0; i < hex.length(); i+=2) {
	        str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
	    }
	    return str.toString();
	}
	
	public static long bigToLittleEndian(byte[] bt) {  
	    ByteBuffer buf = ByteBuffer.allocate(8);  
	    
	    buf.order(ByteOrder.BIG_ENDIAN);  
	    buf.put(bt);  
	   
	    buf.order(ByteOrder.LITTLE_ENDIAN);  
	    return buf.getLong(0);  
	}

	public void onClick(View v) {
		clearFields();
	}

	private static void clearFields() {
		numeroCartao.setText("");
		saldoCartao.setText("");
	}

	public static String getHexString(byte[] raw, int len) {
		byte[] hex = new byte[2 * len];
		int index = 0;
		int pos = 0;

		for (byte b : raw) {
			if (pos >= len)
				break;

			pos++;
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}

		return new String(hex);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
				mTechLists);
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		resolveIntent(intent);
		// mText.setText("Discovered tag " + ++mCount + " with intent: " +
		// intent);
	}

	@Override
	public void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}
}
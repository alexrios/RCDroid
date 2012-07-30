package com.riocard.rcdroid;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

public class ConsultaSaldoCartao extends AsyncTask<Object, Object, String> {
	
	private final Context context;

    private ProgressDialog progress;

	private TextView textView;
	
	private String numeroChipCartao;

    public ConsultaSaldoCartao(Context context, TextView block_0_Data2, String numeroChip) {
        this.context = context;
        this.textView = block_0_Data2;
        this.numeroChipCartao = numeroChip;
    }

    protected void onPreExecute() {
        progress = ProgressDialog.show(context, "Aguarde...", "Consultando dados", true, true);
    }

    protected String doInBackground(Object... params) {
    	String saldo = "";
    	try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://10.5.44.62/android-server/cartao/saldo/" + numeroChipCartao);
            get.setHeader("Accept", "application/json");
            get.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(get);
            String jsonDeResposta = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = new JSONObject(jsonDeResposta);
            double saldoCartao = (Double) jsonObject.get("saldo");
            
            NumberFormat formataValor = NumberFormat.getInstance(new Locale("pt","BR"));
    		formataValor.setMinimumFractionDigits(2);
            saldo = "R$ " + formataValor.format(saldoCartao);
            
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException(e);
        }
    	return saldo;
    }

    protected void onPostExecute(String result) {
        progress.dismiss();
        textView.setText(result);
    }

}

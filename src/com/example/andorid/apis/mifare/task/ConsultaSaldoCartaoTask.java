package com.example.andorid.apis.mifare.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import com.example.andorid.apis.mifare.WebClient;
import com.example.andorid.apis.mifare.util.NumberUtil;
import org.json.JSONObject;

public class ConsultaSaldoCartaoTask extends AsyncTask<Object, Object, String> {
	
	private final Context context;
    private ProgressDialog progress;

	private TextView textView;
	private String numeroChipCartao;

    public ConsultaSaldoCartaoTask(Context context, TextView textViewSaldo, String numeroChip) {
        this.context = context;
        this.textView = textViewSaldo;
        this.numeroChipCartao = numeroChip;
    }

    protected void onPreExecute() {
        progress = ProgressDialog.show(context, "Aguarde...", "Efetuando consulta", true, true);
    }

    protected String doInBackground(Object... params) {
    	String saldo = "";
    	try {
            String jsonDeResposta = new WebClient("http://10.5.34.246/server/cartao/saldo/" + numeroChipCartao).get();
            saldo = NumberUtil.formatBrazilianCurrency(new JSONObject(jsonDeResposta).getDouble("saldo"));
        } catch (Exception e) {
            Log.e("ConsultaSaldoCartaoTask", e.getMessage(), e);
        }
    	return saldo;
    }

    protected void onPostExecute(String result) {
        // TODO Informar ao usuario se ocorrer algum problema
        progress.dismiss();
        textView.setText(result);
    }

}

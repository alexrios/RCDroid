package com.example.andorid.apis.mifare;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

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
        progress = ProgressDialog.show(context, "Aguarde...", "Envio de dados para a web", true, true);
    }

    protected String doInBackground(Object... params) {
    	String saldo = "";
    	try {
            String jsonDeResposta = new WebClient("http://10.5.34.246/server/cartao/saldo/" + numeroChipCartao).get();

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

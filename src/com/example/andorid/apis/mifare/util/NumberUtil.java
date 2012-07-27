package com.example.andorid.apis.mifare.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class NumberUtil {

    private NumberUtil() {}

    public static String formatBrazilianCurrency(Double value) {
        NumberFormat formataValor = NumberFormat.getInstance(new Locale("pt","BR"));
		formataValor.setMinimumFractionDigits(2);
        return "R$ " + formataValor.format(value);
    }

}

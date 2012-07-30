package com.riocard.rcdroid.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.NumberFormat;
import java.util.Locale;

public final class NumberUtil {

    private NumberUtil() {}

    public static String formatBrazilianCurrency(Double value) {
        NumberFormat formataValor = NumberFormat.getInstance(new Locale("pt","BR"));
		formataValor.setMinimumFractionDigits(2);
        return "R$ " + formataValor.format(value);
    }

    public static long bigToLittleEndian(byte[] bytes) {
   	    ByteBuffer buf = ByteBuffer.allocate(8);

   	    buf.order(ByteOrder.BIG_ENDIAN);
   	    buf.put(bytes);

   	    buf.order(ByteOrder.LITTLE_ENDIAN);
   	    return buf.getLong(0);
   	}


}

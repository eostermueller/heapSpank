package com.github.eostermueller.heapspank.garbagespank;

import java.text.DecimalFormat;

public class Util {
	private static DecimalFormat df3 = new DecimalFormat("0.000");

	public static String msToSeconds(String strMilliseconds) {
		double rc = Double.parseDouble(strMilliseconds) / 1000;
		return String.valueOf(rc);
	}
	public static String formatDoubleWith3(double d) {
		return df3.format(d);
	}

}

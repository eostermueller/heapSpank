package com.github.eostermueller.heapspank.leakyspank.console;

import java.util.ArrayList;
import java.util.List;

import com.github.eostermueller.heapspank.HeapSpankException;

public class MultiPropertyException extends HeapSpankException {
	
	public int size() {
		return this.exceptions.size();
	}
	public void add(String property, String error, Throwable t) {
		PropertyException pe = new PropertyException();
		pe.propertyName = property;
		pe.errorMessage = error;
		pe.e = t;
		this.exceptions.add(pe);
	}
	private List<PropertyException> exceptions = new ArrayList<PropertyException>();
	
	public PropertyException[] getPropertyExceptions() {
		return this.exceptions.toArray(new PropertyException[]{});
	}
	public static class PropertyException {
		public String propertyName = null;
		public String errorMessage = null;
		public Throwable e = null;
	}
}

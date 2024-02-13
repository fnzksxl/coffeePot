package com.coffeepot.coffeepotspring.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImageUtils {
	
	public static byte[] compressImage(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.setLevel(Deflater.BEST_COMPRESSION);
		deflater.setInput(data);
		deflater.finish();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
		byte[] temp = new byte[4 * 1024];
		while (!deflater.finished()) {
			int size = deflater.deflate(temp);
			baos.write(temp, 0, size);
		}
		
		try {
			baos.close();
		} catch (Exception ignored) {}
		
		return baos.toByteArray();
	}
	
	public static byte[] decompressImage(byte[] data) {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
		byte[] temp = new byte[4 * 1024];
		
		try {
			while (!inflater.finished()) {
				int size = inflater.inflate(temp);
				baos.write(temp, 0, size);
			}
			baos.close();
		} catch (Exception ignored) {}
		
		return baos.toByteArray();
	}

}

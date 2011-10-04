/*
Copyright (c) 2011  <Matthieu Zimmer> <Aymeric Fabian>

Permission is hereby granted, free of charge, to any person obtaining a copy of 
this software and associated documentation files (the "Software"), to deal in 
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
SOFTWARE.
*/

package com.wrathlegacy.librarie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	private static final int	BUFFER	= 2048;
	private static byte			data[]	= new byte[BUFFER];

	public static void compress(File chemin, String[] descrip, String[] files) throws IOException {
		ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(chemin)));
		zip.setMethod(ZipOutputStream.DEFLATED);
		zip.setLevel(Deflater.BEST_COMPRESSION);

		for (int i = 0; i < files.length; i++) {
			ZipEntry entry = new ZipEntry(descrip[i]);
			zip.putNextEntry(entry);

			FileInputStream fi = new FileInputStream(files[i]);
			BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);

			int count;
			while ((count = buffi.read(data, 0, BUFFER)) != -1) {
				zip.write(data, 0, count);
			}

			zip.closeEntry();
			buffi.close();
		}

		zip.close();
	}

	public static ArrayList<File> uncompress(String chemin, String tempPrefix) throws IOException {
		ArrayList<File> result = new ArrayList<File>();

		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(chemin);
		BufferedInputStream buffi = new BufferedInputStream(fis);
		ZipInputStream zis = new ZipInputStream(buffi);

		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {

			File temp = File.createTempFile(tempPrefix, entry.getName());
			result.add(temp);

			FileOutputStream fos = new FileOutputStream(temp);
			dest = new BufferedOutputStream(fos, BUFFER);

			int count;
			while ((count = zis.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();
		}
		zis.close();

		return result;
	}
}

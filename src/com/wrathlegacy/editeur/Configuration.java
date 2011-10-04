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

package com.wrathlegacy.editeur;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.wrathlegacy.librarie.ConfigurationFactory;
import com.wrathlegacy.librarie.Property;

public class Configuration {
	private static boolean		load			= false;

	private static final String	fichierConfig	= "data" + File.separator + "config.properties";

	@Property(key = "preferences.antialiasing", defaultValue = "true")
	public static boolean		ANTIALIASING;

	@Property(key = "preferences.repere", defaultValue = "1")
	public static int			REPERE_VISIBILITE;
	
	@Property(key = "preferences.rendering_quality", defaultValue = "true")
	public static boolean RENDERING_QUALITY;

	public static void load() {
		if (!load) {
			ConfigurationFactory.build(Configuration.class, fichierConfig);
			load = true;
		}
	}

	public static void save(String key, String value) throws IOException {
		Properties p = new Properties();
		p.load(new FileInputStream(fichierConfig));
		p.setProperty(key, value);
		FileOutputStream out = new FileOutputStream(fichierConfig);
		p.store(out, null);
		out.close();
	}
}
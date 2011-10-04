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

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

/**
 * @author Matthieu637
 * 
 */
public final class ConfigurationFactory {

	public static void build(Class<?> clazz, String filename) {

		Properties settings = new Properties();

		try {
			FileInputStream in = new FileInputStream(filename);
			settings.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			for (Field f : clazz.getFields()) {
				Property p = f.getAnnotation(Property.class);
				if (p != null && Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
					if (f.getType() == int.class)
						f.set(null, Integer.parseInt(settings.getProperty(p.key(), p.defaultValue())));
					else if (f.getType() == String.class)
						f.set(null, settings.getProperty(p.key(), p.defaultValue()));
					else if (f.getType() == String[].class) {
						String build = settings.getProperty(p.key(), p.defaultValue());
						f.set(null, build.split(","));
					} else if (f.getType() == boolean.class)
						f.set(null, Boolean.parseBoolean(settings.getProperty(p.key(), p.defaultValue())));
					else
						System.out.println("Cannont use type : " + f.getType());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

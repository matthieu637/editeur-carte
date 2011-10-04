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
import java.util.ArrayList;
import java.util.HashMap;

import com.wrathlegacy.librarie.FiltreExtension;

/**
 * @author Matthieu637
 * 
 */
public class GestionnaireDessins {
	private static final String					CHEMIN_IMAGE	= "data" + File.separator + "images" + File.separator;
	private static final String[]				EXTENSIONS		= { ".png", ".gif" };

	private static boolean						charger			= false;
	private static ArrayList<Dessin>			dessins			= new ArrayList<Dessin>();
	private static HashMap<TypeDessin, Integer>	length			= new HashMap<TypeDessin, Integer>(TypeDessin.values().length);

	public static void charger(int largeurBouton, int hauteurBouton) {
		if (!charger) {

			File[] repertoires = new File(CHEMIN_IMAGE).listFiles();
			FiltreExtension filtre = new FiltreExtension(EXTENSIONS);

	
			for (File rep : repertoires)
				if (rep.isDirectory() && !rep.isHidden()) {

					TypeDessin type = TypeDessin.creerType(rep.getName());
					boolean accessible = rep.getName().toLowerCase().indexOf("inaccessibles") < 0;

					File dossierImage = new File(CHEMIN_IMAGE + rep.getName());
					File[] images = dossierImage.listFiles(filtre);
					

					for (File f : images) {
						if (length.containsKey(type))
							length.put(type, length.get(type) + 1);
						else
							length.put(type, 0);

						dessins.add(Dessin.creerDessinSeulementAvecGestionnaire(f.getPath(), type, accessible, largeurBouton, hauteurBouton));
					}

				}

			charger = true;
		}
	}

	public static ArrayList<Dessin> recupereChaqueDessin() {
		return dessins;
	}

	public static Dessin recupere(int index) {
		return dessins.get(index);
	}

	public static int getNombreDessin(TypeDessin type) {
		if(length.containsKey(type))
			return length.get(type);
		else return 0;
	}
}

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

/**
 * 
 */
package com.wrathlegacy.editeur;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

/**
 * @author Matthieu637
 * @author AymericF
 * 
 */

public final class Dessin{
	private final int			id;
	private final String		chemin;
	private Image				img;
	private ImageIcon			icone;
	private final TypeDessin	type;
	private final boolean		accessible;

	public static Dessin creerDessinSeulementAvecGestionnaire(String chemin, TypeDessin type, boolean accessible, int largeurBouton, int hauteurBouton) {
		return new Dessin(chemin, type, accessible, largeurBouton, hauteurBouton);
	}

	/**
	 * Cr�e une image en m�moire
	 * 
	 * @param chemin
	 *            chemin de l'image
	 * @param type
	 *            type de l'image(tile,objet,batiment)
	 */
	private Dessin(String chemin, TypeDessin type, boolean accessible, int largeurBouton, int hauteurBouton) {
		this.chemin = chemin.replace("\\", "/");
//		System.out.println(chemin);
		this.id = hash(chemin);
		this.img = Toolkit.getDefaultToolkit().getImage(chemin);
		this.icone = new ImageIcon(img);
		this.type = type;
		this.accessible = accessible;
		this.icone = new ImageIcon(creerIcone(largeurBouton, hauteurBouton));
	}

	private int hash(String chemin) {
		return chemin.hashCode();
	}

	public Image creerIcone(int largeurBouton, int hauteurBouton) {
		int largeur = Math.min(largeurBouton, img.getWidth(null));
		double facteur = (double) largeur / img.getWidth(null);
		int hauteur = Math.max((int) (img.getHeight(null) * facteur), 1);
		if(img.getWidth(null) < img.getHeight(null)) {
			hauteur = Math.min(hauteurBouton, img.getHeight(null));
			facteur = (double) hauteur / img.getHeight(null);
			largeur = Math.max((int) (img.getWidth(null) * facteur), 1);
			return img.getScaledInstance(largeur, hauteur, 0);
		}
		return img.getScaledInstance(largeur, hauteur, 0);
	}

	/**
	 * @return Image
	 */
	public Image getImg() {
		return img;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public ImageIcon getIcone() {
		return icone;
	}

	public String getChemin() {
		return chemin;
	}

	public boolean estAccessible() {
		return accessible;
	}

	/**
	 * @return
	 */
	public TypeDessin getType() {
		return type;
	}

	public int getHauteurImage() {
		return getImg().getHeight(null);
	}

	public int getLargeurImage() {
		return getImg().getWidth(null);
	}
}

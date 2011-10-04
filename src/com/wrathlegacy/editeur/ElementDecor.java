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

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author Matthieu637
 * @author AymericF
 */
public class ElementDecor implements Comparable<ElementDecor>, PaintZone {

	private Dessin	dessin;
	private int		x, y, yCompare;
	private Couche	couche;
	private Rectangle rect;

	public ElementDecor(Dessin dessin, Couche couche, int x, int y) {
		this.dessin = dessin;
		this.couche = couche;
		this.x = x;
		this.y = y;
		this.yCompare = y + (y + getHauteur());
		this.rect = new Rectangle(this.getX(), this.getY(), this.getLargeur(), this.getHauteur());
	}
	
	public ElementDecor(ElementDecor e){
		this.dessin = e.getDessin();
		this.couche = e.getCouche();
		this.x = e.getX();
		this.y = e.getY();
		this.yCompare = e.getYCompare();
		this.rect = e.rect;
	}

	@Override
	public Rectangle paintZone() {
		return rect;
	}

	public String toString() {
		return "(" + x + ", " + y + ", " + couche + ")";
	}

	/**
	 * @return Dessin
	 */
	public Dessin getDessin() {
		return dessin;
	}

	/**
	 * @param x
	 * @param y
	 */
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Couche getCouche() {
		return couche;
	}

	public void peindreDecor(Graphics g) {
		g.drawImage(dessin.getImg(), x, y, null);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getYCompare() {
		return yCompare;
	}

	public int getHauteur() {
		return dessin.getHauteurImage();
	}

	public int getLargeur() {
		return dessin.getLargeurImage();
	}

	public int compareTo(ElementDecor a) {
		int comparaisonCouche = this.couche.compareTo(a.couche);

		if (comparaisonCouche == 0) {

			int comparaisonY = ((Integer) (yCompare)).compareTo(a.getYCompare());

			if (comparaisonY == 0) {
				return ((Integer) x).compareTo(a.x);
			} else
				return comparaisonY;
		} else
			return comparaisonCouche;
	}
}

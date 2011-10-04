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
import java.awt.Point;
import java.awt.Polygon;

@SuppressWarnings("serial")
public class Losange extends Polygon {

	private final Point pointLosange;
	private final int largeur;
	private final int hauteur;

	public Losange(Point pointLosange, int largeur, int hauteur) {
		super(creerAbcisses(pointLosange.x, largeur), creerOrdonnees(pointLosange.y, hauteur), 4);

		this.pointLosange = pointLosange;
		this.largeur = largeur;
		this.hauteur = hauteur;
	}

	public Point getPointLosange() {
		return pointLosange;
	}

	public int getLargeur() {
		return largeur;
	}

	public int getHauteur() {
		return hauteur;
	}
	
	public void peindreLosange(Graphics g) {
		g.drawPolygon(this);
	}

	private static int[] creerAbcisses(int xPointDessin, int largeur) {
		int demiLargeur = largeur / 2;

		int xP1 = xPointDessin + demiLargeur;
		int xP2 = xP1 + demiLargeur;
		int xP3 = xP1;
		int xP4 = xPointDessin;

		return new int[] { xP1, xP2, xP3, xP4 };
	}

	private static int[] creerOrdonnees(int yPointDessin, int hauteur) {
		int demiHauteur = hauteur / 2;

		int yP1 = yPointDessin;
		int yP2 = yP1 + demiHauteur;
		int yP3 = yP2 + demiHauteur;
		int yP4 = yP2;

		return new int[] { yP1, yP2, yP3, yP4 };
	}

}

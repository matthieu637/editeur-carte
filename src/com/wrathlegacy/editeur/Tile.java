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
import java.awt.Rectangle;

public class Tile extends Losange implements PaintZone{

	private static final long	serialVersionUID	= 1L;
	private Dessin				dessinTile;
	private Point				paintPoint;
	private final boolean		decale;
	private Rectangle			rect;

	public Tile(Point pointLosange, int largeur, int hauteur, boolean decale) {
		super(pointLosange, largeur, hauteur);
		this.decale = decale;
	}

	public Point getPaintPoint() {
		return paintPoint;
	}

	public Dessin getDessin() {
		return dessinTile;
	}

	public void setDessin(Dessin dessinTile) {
		this.dessinTile = dessinTile;

		if(dessinTile == null)
			return;

		int dessinX = this.getPointLosange().x;
		int dessinY = this.getPointLosange().y + (this.getHauteur() - dessinTile.getHauteurImage());

		this.paintPoint = new Point(dessinX, dessinY);
		this.rect = new Rectangle(getPaintPoint().x, getPaintPoint().y, getDessin().getLargeurImage(), getDessin().getHauteurImage());
	}

	public boolean aUneTile() {
		return dessinTile != null;
	}

	public void peindreTile(Graphics g) {
		g.drawImage(dessinTile.getImg(), paintPoint.x, paintPoint.y, null);
	}

	@Override
	public Rectangle paintZone() {
		return this.rect;
	}

	public boolean estDecale() {
		return decale;
	}
}

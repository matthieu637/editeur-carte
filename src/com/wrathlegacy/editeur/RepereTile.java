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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;

import com.wrathlegacy.librarie.Couple;

/**
 * @author Matthieu637
 * @author AymericF
 * 
 */
public class RepereTile {

	private final int				largeur_repere;
	private final int				hauteur_repere;
	private final int				largeur_losange;
	private final int				hauteur_losange;
	private final int				losanges_en_largeur;
	private final int				losanges_en_hauteur;
	private final int				losanges_en_largeur_couple;
	private final int				demi_largeur_losange;
	private final int				demi_hauteur_losange;

	private AfficheRepere			afficherRepere		= AfficheRepere.values()[Configuration.REPERE_VISIBILITE];
	private boolean					antiAliasing		= Configuration.ANTIALIASING;
	private boolean					renderingQuality	= Configuration.RENDERING_QUALITY;
	private final ArrayList<Tile>	tiles;

	public RepereTile(int tiles_en_largeur, int tiles_en_hauteur, int largeurLosange, int hauteurLosange) {
		this.losanges_en_largeur = tiles_en_largeur;
		this.losanges_en_hauteur = tiles_en_hauteur;
		this.largeur_repere = tiles_en_largeur * largeurLosange + largeurLosange / 2;
		this.hauteur_repere = tiles_en_hauteur * hauteurLosange + hauteurLosange / 2;
		this.largeur_losange = largeurLosange;
		this.hauteur_losange = hauteurLosange;
		this.losanges_en_largeur_couple = 2 * tiles_en_largeur;
		this.demi_largeur_losange = largeur_losange / 2;
		this.demi_hauteur_losange = hauteur_losange / 2;

		this.tiles = creerLosanges();
	}

	public void renderingQuality() {
		renderingQuality = !renderingQuality;
	}

	public boolean isRenderingQuality() {
		return renderingQuality;
	}

	public boolean isAntiAliasing() {
		return antiAliasing;
	}

	public void setAntiAliasing(boolean antiAliasing) {
		this.antiAliasing = antiAliasing;
	}

	public int getLargeur_repere() {
		return largeur_repere;
	}

	public int getHauteur_repere() {
		return hauteur_repere;
	}

	public int hauteurLosange() {
		return hauteur_losange;
	}

	public int largeurLosange() {
		return largeur_losange;
	}

	public int tilesEnLargeur() {
		return losanges_en_largeur;
	}

	public ArrayList<Tile> getAllTiles() {
		return tiles;
	}

	public Tile tileConteneur(int x, int y) {
		// saute colonne de tiles en ignorant les d�cal�s puis ajoute l'indice
		// de l'ordonn�e
		int nbLosangeToX = x / largeur_losange;
		int nbLosangeToY = y / hauteur_losange;

		int indice_estime = losanges_en_largeur_couple * nbLosangeToY + nbLosangeToX;

		if (indice_estime >= 0 && indice_estime < tiles.size() && tiles.get(indice_estime).contains(x, y))
			return tiles.get(indice_estime);

		// saute colonne de tiles en ignorant les non-d�cal�s puis ajoute
		// l'indice de l'ordonn�e en ajoutant le d�calage au d�but

		nbLosangeToX = (x - demi_largeur_losange) / largeur_losange;
		nbLosangeToY = (y - demi_hauteur_losange) / hauteur_losange;

		indice_estime = losanges_en_largeur + losanges_en_largeur_couple * nbLosangeToY + nbLosangeToX;

		if (indice_estime >= 0 && indice_estime < tiles.size() && tiles.get(indice_estime).contains(x, y))
			return tiles.get(indice_estime);

		return null;
	}

	public void peindre(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		if (antiAliasing)
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (renderingQuality)
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		if (afficherRepere == AfficheRepere.NePasAfficher)
			peindreTile(g);
		else if (afficherRepere == AfficheRepere.EnDessous) {
			peindreGrille(g);
			peindreTile(g);
		} else {
			peindreTile(g);
			peindreGrille(g);
		}
	}

	public void peindreSansRepere(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		if (antiAliasing) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		if (renderingQuality)
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		peindreTile(g);
	}

	private void peindreGrille(Graphics g) {
		for (Tile l : tiles)
			l.peindreLosange(g);
	}

	public void peindreTile(Graphics g) {
		for (Tile l : tiles)
			if (l.aUneTile())
				l.peindreTile(g);
	}

	private ArrayList<Tile> creerLosanges() {
		ArrayList<Couple<Point, Boolean>> points = creerPointsDessin();

		ArrayList<Tile> creationLosange = new ArrayList<Tile>(points.size());

		for (Couple<Point, Boolean> c : points)
			creationLosange.add(new Tile(c.first, largeur_losange, hauteur_losange, c.second));

		return creationLosange;
	}

	private ArrayList<Couple<Point, Boolean>> creerPointsDessin() {
		ArrayList<Couple<Point, Boolean>> points = new ArrayList<Couple<Point, Boolean>>((largeur_repere / largeur_losange)
				* (hauteur_repere / hauteur_losange));

		boolean decale = false;
		int demi_largeur = largeur_losange / 2;
		int demi_hauteur = hauteur_losange / 2;

		for (int y = 0; y <= hauteur_repere - hauteur_losange; y += demi_hauteur) {
			if (!decale) {
				for (int x = 0; x <= largeur_repere - largeur_losange; x += largeur_losange)
					points.add(new Couple<Point, Boolean>(new Point(x, y), Boolean.FALSE));
			} else {
				for (int x = demi_largeur; x <= largeur_repere - largeur_losange; x += largeur_losange)
					points.add(new Couple<Point, Boolean>(new Point(x, y), Boolean.TRUE));
			}
			decale = !decale;
		}
		return points;
	}

	public void afficherRepere(AfficheRepere affiche) {
		this.afficherRepere = affiche;
	}

	public void antiAliasing() {
		antiAliasing = !antiAliasing;
	}

	public AfficheRepere getAfficherRepere() {
		return afficherRepere;
	}

	public void setAfficherRepere(AfficheRepere afficherRepere) {
		this.afficherRepere = afficherRepere;
	}

	public int tilesEnHauteur() {
		return losanges_en_hauteur;
	}
}

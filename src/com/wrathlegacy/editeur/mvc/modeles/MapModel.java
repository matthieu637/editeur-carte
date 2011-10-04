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

package com.wrathlegacy.editeur.mvc.modeles;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.TreeSet;

import com.wrathlegacy.editeur.AfficheRepere;
import com.wrathlegacy.editeur.Couche;
import com.wrathlegacy.editeur.Dessin;
import com.wrathlegacy.editeur.ElementDecor;
import com.wrathlegacy.editeur.PaintZone;
import com.wrathlegacy.editeur.RepereTile;
import com.wrathlegacy.editeur.Tile;
import com.wrathlegacy.editeur.mvc.observeurs.ObservableMap;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurMap;
import com.wrathlegacy.editeur.undo.ElementUndo;
import com.wrathlegacy.editeur.undo.TileUndo;
import com.wrathlegacy.editeur.undo.TypeUndo;

/**
 * @author AymericF
 * 
 */
public class MapModel implements ObservableMap {

	private ArrayList<ObserveurMap>	listeObserver;
	private TreeSet<ElementDecor>	decor;
	private Deque<ElementUndo>		redoDeque;
	private Deque<ElementUndo>		undoDeque;
	private ArrayList<Object>		tempUndoListe;
	private ElementDecor			tempElementDecor;
	private Tile					tempTile;
	private Dessin					tempTileDessin;
	private RepereTile				repere;
	private int						maxUndo;
	private TypeUndo				typeUndo;

	public MapModel(int tiles_en_largeur, int tiles_en_hauteur, int largeur_losange, int hauteur_losange, int maxUndo) {
		repere = new RepereTile(tiles_en_largeur, tiles_en_hauteur, largeur_losange, hauteur_losange);
		decor = new TreeSet<ElementDecor>();
		listeObserver = new ArrayList<ObserveurMap>();
		undoDeque = new ArrayDeque<ElementUndo>();
		redoDeque = new ArrayDeque<ElementUndo>();
		this.maxUndo = maxUndo;
	}

	/**************************************************************************************/
	/*************************************** GETTERS **************************************/
	/**************************************************************************************/

	public RepereTile getRepere() {
		return repere;
	}

	public TreeSet<ElementDecor> getDecor() {
		return decor;
	}

	public Tile getTile(int x, int y) {
		return repere.tileConteneur(x, y);
	}

	public boolean aUnUndo() {
		return undoDeque.size() > 0;
	}

	public Tile getTempTile() {
		return tempTile;
	}

	/**************************************************************************************/
	/******************************** PLACEMENT / EFFACEMENT ******************************/
	/**************************************************************************************/

	public void setTile(Dessin dessin, Tile tile) {
		nettoyerTempTile();

		TileUndo tempTile = new TileUndo(tile.getDessin(), tile); // Undo
		tempUndoListe.add(tempTile); // Undo

		tile.setDessin(dessin);
		envoieRepaint(tile);
	}

	public void setElementDecor() {
		ElementDecor tempElt = new ElementDecor(tempElementDecor); // Undo
		tempUndoListe.add(tempElt); // Undo

		tempElementDecor = null;
	}

	public void effacerElementDecor(int x, int y) {
		ElementDecor elt = getElementDecor(x, y);
		if (elt == null)
			return;
		ElementDecor tempElt = new ElementDecor(elt); // Undo
		tempUndoListe.add(tempElt); // Undo
		nettoyerElementDecor(elt);
	}

	public void nettoyerElementDecor(ElementDecor elt) {
		if (elt == null)
			return;
		decor.remove(elt);
		envoieRepaint(elt);
		elt = null;
	}

	public ElementDecor getElementDecor(int x, int y) {
		Point cliquePt = new Point(x, y);
		for (ElementDecor elt : decor)
			if (elt.paintZone().contains(cliquePt))
				return elt;
		return null;
	}

	/**************************************************************************************/
	/**************************************************************************************/
	/**************************************************************************************/

	public Rectangle elementRect(ElementDecor elt) {
		return new Rectangle(elt.getX(), elt.getY(), elt.getLargeur(), elt.getHauteur());
	}

	public void definirFond(Dessin dessin, boolean remplir) {
		nettoyerTempTile();

		setTempUndoListe(TypeUndo.Tile);
		for (Tile t : repere.getAllTiles())
			if (!remplir || t.getDessin() == null)
				setTile(dessin, t);
		this.notifyRepaintAll();
		finTempUndoListe();
	}

	public void afficherRepere(AfficheRepere affiche) {
		repere.afficherRepere(affiche);
		notifyRepaintAll();
	}

	public ArrayList<PaintZone> selectionGroupe(boolean tiles, Rectangle rect) {
		ArrayList<PaintZone> selection = new ArrayList<PaintZone>();
		if (tiles)
			for (Tile t : repere.getAllTiles())
				if (t.paintZone() != null && rect.contains(t.paintZone()))
					selection.add(t);
				else
					for (ElementDecor elt : decor)
						if (rect.contains(elt.paintZone()))
							selection.add(elt);
		return selection;
	}

	public void translationTile(ArrayList<Tile> liste, int x, int y) {
		int tilesEnX = repere.tilesEnLargeur();
		int totalTiles = repere.getAllTiles().size();
		int tx, ty, index;
		boolean cx, cy;

		for (Tile t : liste) {
			index = repere.getAllTiles().indexOf(t);
			tx = ((index % tilesEnX) + x);
			ty = index + y * tilesEnX;
			cx = (tx > 0 && tx < tilesEnX);
			cy = (ty > 0 && ty < totalTiles);
			if (cx && cy)
				t.setDessin(repere.getAllTiles().get(tx + ty).getDessin());
			else
				t.setDessin(null);
		}
	}

	public void translationEltDecor(ArrayList<ElementDecor> liste, int x, int y) {
		int tx, ty;
		boolean cx, cy;

		for (ElementDecor elt : liste) {
			tx = elt.getX() + x;
			ty = elt.getY() + y;
			cx = (tx > 0 && (tx + elt.getLargeur() < repere.getLargeur_repere()));
			cy = (ty > 0 && (ty + elt.getHauteur() < repere.getHauteur_repere()));
			decor.remove(elt);
			if (cx && cy) {
				ElementDecor e = new ElementDecor(elt.getDessin(), elt.getCouche(), tx, ty);
				decor.add(e);
			}
		}
	}

	public void changerRepere(int largeur, int hauteur) {
		RepereTile nouveauRepere = new RepereTile(largeur, hauteur, repere.largeurLosange(), repere.hauteurLosange());
		calquerTiles(repere, nouveauRepere);

		repere = nouveauRepere;
		notifyRedimension(repere.getLargeur_repere(), repere.getHauteur_repere());
	}

	public void calquerTiles(RepereTile base, RepereTile cible) {
		RepereTile petit = base;
		if (cible.getAllTiles().size() < base.getAllTiles().size())
			petit = cible;
		int iterationMax = petit.getAllTiles().size();
		int tilesEnX = petit.tilesEnLargeur();
		int x = 0;
		int y = 0;

		for (int i = 0; i < iterationMax; i++) {
			if (x == tilesEnX) {
				y++;
				x = 0;
			}
			cible.getAllTiles().get(x + y * cible.tilesEnLargeur()).setDessin(base.getAllTiles().get(x + y * base.tilesEnLargeur()).getDessin());
			x++;
		}
	}

	/**************************************************************************************/
	/************************************ TEMP ELEMENT ************************************/
	/**************************************************************************************/

	public void setTempTile(Dessin dessinCourant, int dessinX, int dessinY, Tile tileClique) {

		if (tempTile != null) { // Si on a deja une tileTemp
			tempTile.setDessin(tempTileDessin); // ici tileTemp va changer de place: on remet le dessin d'origine
			envoieRepaint(tempTile); // on l'actualise
		}
		tempTile = tileClique; // on stocke la tile cliquée
		tempTileDessin = tileClique.getDessin(); // on stocke son dessin
		tempTile.setDessin(dessinCourant); // on change son dessin

		envoieRepaint(tileClique);
	}

	public void setTempElementDecor(Dessin dessin, Couche couche, int x, int y) {

		nettoyerTempElement();
		tempElementDecor = new ElementDecor(dessin, couche, x, y); // Une translation ne retriera pas l'élément dans decor même avec remove, translation, add

		if (!decor.contains(tempElementDecor)) { // Ajout dans decor si aucun élément n'a les mêmes coordonées
			decor.add(tempElementDecor);
			envoieRepaint(tempElementDecor);
		} else
			tempElementDecor = null; // Sinon decor.remove(decorTempElement) effacera l'élément de l'arbre
	}

	public void nettoyerTempElement() {
		nettoyerElementDecor(tempElementDecor);
	}

	public void nettoyerTempTile() {
		if (tempTile != null) {
			envoieRepaint(tempTile);
			tempTile.setDessin(tempTileDessin);
			tempTile = null;
		}
	}

	public ElementDecor getTempElementDecor() {
		return tempElementDecor;
	}

	/**************************************************************************************/
	/************************************* UNDO / REDO ************************************/
	/**************************************************************************************/

	public ArrayList<Object> getTempUndoList() {
		return tempUndoListe;
	}

	public void setTempUndoListe(TypeUndo type) {
		tempUndoListe = new ArrayList<Object>();
		typeUndo = type;
	}

	public void finTempUndoListe() {
		creerUndo(new ElementUndo(typeUndo, tempUndoListe));
	}

	public void creerUndo(ElementUndo d) {
		if (undoDeque.size() == maxUndo)
			undoDeque.removeLast();
		undoDeque.addFirst(d);
	}

	public void redo() {
		utiliserUndo(true);
	}

	public void utiliserUndo(boolean redo) {
		Deque<ElementUndo> deque = undoDeque;
		Deque<ElementUndo> autre = redoDeque;
		if (redo) {
			deque = redoDeque;
			autre = undoDeque;
		}
		if (deque.size() == 0)
			return;

		ElementUndo top = deque.getFirst();

		ArrayList<Object> tempList = new ArrayList<Object>(top.getListe().size());

		TypeUndo topType = top.getType();

		if (topType == TypeUndo.Tile) {
			for (Object o : top.getListe()) {

				Tile topTile = ((TileUndo) o).getTile();
				Dessin topDessin = ((TileUndo) o).getDessin();

				tempList.add(new TileUndo(topTile.getDessin(), topTile));

				if (tempTile == topTile)
					tempTileDessin = topDessin;
				else
					topTile.setDessin(topDessin);

				envoieRepaint(topTile);
			}
		} else
			tempList = undoElementDecor(tempList, top, topType);

		if (topType == TypeUndo.Ajout)
			topType = TypeUndo.Effacement;
		else if (topType == TypeUndo.Effacement)
			topType = TypeUndo.Ajout;

		autre.addFirst(new ElementUndo(topType, tempList));

		deque.removeFirst();
	}

	public ArrayList<Object> undoElementDecor(ArrayList<Object> tempList, ElementUndo elt, TypeUndo type) {
		for (Object o : elt.getListe()) {
			ElementDecor topElement = ((ElementDecor) o);
			Rectangle tempRect = elementRect(topElement);

			tempList.add(new ElementDecor(topElement));
			if (type == TypeUndo.Ajout)
				decor.remove(topElement);
			else
				decor.add(topElement);

			envoieRepaint(tempRect);
		}
		return tempList;
	}

	/**************************************************************************************/
	/************************************* OBSERVABLE *************************************/
	/**************************************************************************************/

	public void envoieRepaint(PaintZone elt) {
		notifyRepaint(elt.paintZone());
	}

	public void envoieRepaint(Rectangle rect) {
		notifyRepaint(rect);
	}

	@Override
	public void addObserveur(ObserveurMap obs) {
		listeObserver.add(obs);
	}

	@Override
	public void removeAllObserver() {
		listeObserver = new ArrayList<ObserveurMap>();
	}

	@Override
	public void removeObserver(ObserveurMap obs) {
		listeObserver.remove(obs);
	}

	@Override
	public void notifyRepaint(Rectangle zoneRepeindre) {
		for (ObserveurMap obs : listeObserver)
			obs.redessine(zoneRepeindre);
	}

	@Override
	public void notifyRepaintAll() {
		for (ObserveurMap obs : listeObserver)
			obs.redessineTout();
	}

	@Override
	public void notifyRedimension(int width, int height) {
		for (ObserveurMap obs : listeObserver)
			obs.redimension(width, height);
	}
}

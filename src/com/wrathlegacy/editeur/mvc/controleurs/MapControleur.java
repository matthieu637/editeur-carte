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

package com.wrathlegacy.editeur.mvc.controleurs;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.TreeSet;

import com.wrathlegacy.editeur.AfficheRepere;
import com.wrathlegacy.editeur.Couche;
import com.wrathlegacy.editeur.Dessin;
import com.wrathlegacy.editeur.ElementDecor;
import com.wrathlegacy.editeur.GestionnaireDessins;
import com.wrathlegacy.editeur.PaintZone;
import com.wrathlegacy.editeur.RepereTile;
import com.wrathlegacy.editeur.Tile;
import com.wrathlegacy.editeur.TypeDessin;
import com.wrathlegacy.editeur.mvc.modeles.MapModel;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurMenuToMap;
import com.wrathlegacy.editeur.undo.TypeUndo;

/**
 * @author Matthieu637
 * @author AymericF
 */
public class MapControleur implements ObserveurMenuToMap {

	private MapModel				modele;
	private boolean					modeSelection	= true;
	private Couche					couche			= Couche.Defaut;
	private Dessin					dessinCourant;
	private ArrayList<PaintZone>	selectionDecor;
	private Point					selectionPoint;

	public MapControleur(MapModel modele) {
		this.modele = modele;
	}

	public TreeSet<ElementDecor> getDecor() {
		return modele.getDecor();
	}

	public RepereTile getRepere() {
		return modele.getRepere();
	}

	public void placementDecor(int x, int y, boolean drag) {
		if (dessinCourant() == null || modeSelection)
			return;

		if (isTile()) {
			Tile losange = modele.getTile(x, y);
			if (losange != null && (modele.getTile(x, y).getDessin() != dessinCourant() || modele.getTile(x, y) == modele.getTempTile()))
				modele.setTile(dessinCourant(), losange);
		} else if (!drag && modele.getTempElementDecor() != null) {
			modele.setElementDecor();
		} else
			changementVolatileImage(x, y);
	}

	public void changementVolatileImage(int x, int y) {

		// On arrete si: aucun dessinCourant, mode selection
		if (dessinCourant() == null || modeSelection)
			return;

		int dessinX = x;
		int dessinY = y;

		if (isTile()) {
			Tile tileClique = modele.getTile(x, y);
			// On verif que clique est dans le repere, TempTile change ou est null
			if (tileClique != null && (modele.getTempTile() == null || modele.getTempTile() != tileClique))
				modele.setTempTile(dessinCourant(), dessinX, dessinY, tileClique);
		} else {
			dessinX = x - dessinCourant().getLargeurImage() / 2;
			dessinY = y - dessinCourant().getHauteurImage();
			if (dessinX < 0)
				dessinX = 0;
			if (dessinY < 0)
				dessinY = 0;
			modele.setTempElementDecor(dessinCourant(), couche, dessinX, dessinY);
		}
	}

	public void selectionner(int x, int y) {
		if (!modeSelection)
			return;

		int width = Math.abs(x - selectionPoint.x);
		int height = Math.abs(y - selectionPoint.y);
		int xRect = Math.max(selectionPoint.x, x);
		int yRect = Math.max(selectionPoint.y, y);

		Rectangle rect = new Rectangle(xRect, yRect, width, height);

		selectionDecor = modele.selectionGroupe(isTile(), rect);
	}

	public void nouvelleSelection(int x, int y) {
		selectionPoint = new Point(x, y);
	}

	public void copierDecor(int x, int y) {
		Tile tile = modele.getRepere().tileConteneur(x, y);
		ElementDecor elt = modele.getElementDecor(x, y);

		if (isTile() && (tile == null || tile.getDessin() == null) || !isTile() && elt == null)
			return;

		if (isTile())
			dessinCourant = tile.getDessin();
		else
			dessinCourant = elt.getDessin();
		changementVolatileImage(x, y);
		modeSelection(false);
	}

	public void effacerDecor(int x, int y) {
		if (!modeSelection)
			return;
		Tile tile = modele.getRepere().tileConteneur(x, y);

		if (isTile() && tile != null && tile.getDessin() != null)
			modele.setTile(null, tile);
		else
			modele.effacerElementDecor(x, y);
	}

	public void translation(int x, int y) {
		if (isTile()) {
			ArrayList<Tile> liste = new ArrayList<Tile>();
			for (PaintZone p : selectionDecor)
				liste.add((Tile) p);
			modele.translationTile(liste, x, y);
		} else {
			ArrayList<ElementDecor> liste = new ArrayList<ElementDecor>();
			for (PaintZone p : selectionDecor)
				liste.add((ElementDecor) p);
			modele.translationEltDecor(liste, x, y);
		}
	}

	private boolean isTile() {
		if (dessinCourant() != null)
			return dessinCourant().getType() == TypeDessin.Tile;
		else
			return false;
	}

	public Dessin dessinCourant() {
		return dessinCourant;
	}

	public TypeDessin typeDessinCourant() {
		return dessinCourant().getType();
	}

	@Override
	public void changementDeDessin(int index) {
		this.dessinCourant = GestionnaireDessins.recupere(index);
	}

	@Override
	public void changementDeCouche(Couche couche) {
		this.couche = couche;
	}

	/***************************************** UNDO *****************************************/

	public void debutUndo(boolean cliqueGauche) {
		if (isTile())
			modele.setTempUndoListe(TypeUndo.Tile);
		else if (cliqueGauche)
			modele.setTempUndoListe(TypeUndo.Ajout);
		else
			modele.setTempUndoListe(TypeUndo.Effacement);
	}

	public void finUndo() {
		if (modele.getTempUndoList().size() != 0)
			modele.finTempUndoListe();
	}

	/****************************************************************************************/
	/***************************************** MENU *****************************************/
	/****************************************************************************************/

	@Override
	public void definirFond(boolean remplir) {
		if (isTile() && dessinCourant() != null)
			modele.definirFond(dessinCourant(), remplir);
	}

	@Override
	public void modeSelection(boolean select) {
		modeSelection = select;

		modele.nettoyerTempElement();
		modele.nettoyerTempTile();
	}

	@Override
	public void afficherRepere(AfficheRepere affiche) {
		modele.afficherRepere(affiche);
	}

	@Override
	public void annuler() {
		if (modele.aUnUndo())
			modele.utiliserUndo(false);
	}

	@Override
	public void antiAliasing() {
		modele.getRepere().antiAliasing();
		modele.notifyRepaintAll();
	}

	@Override
	public void renderingQuality() {
		modele.getRepere().renderingQuality();
		modele.notifyRepaintAll();
	}

	@Override
	public void retablir() {
		modele.redo();
	}

	@Override
	public void redimensionnerRepere(Point p) {
		modele.changerRepere(p.x, p.y);
	}
}

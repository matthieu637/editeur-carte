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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import com.wrathlegacy.editeur.AfficheRepere;
import com.wrathlegacy.editeur.Configuration;
import com.wrathlegacy.editeur.Couche;
import com.wrathlegacy.editeur.Dessin;
import com.wrathlegacy.editeur.ElementDecor;
import com.wrathlegacy.editeur.GestionnaireDessins;
import com.wrathlegacy.editeur.RepereTile;
import com.wrathlegacy.editeur.Tile;
import com.wrathlegacy.editeur.mvc.observeurs.ObservableMenu;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurMenu;
import com.wrathlegacy.editeur.mvc.vues.Editeur;
import com.wrathlegacy.librarie.ZipUtils;

/**
 * @author Matthieu637
 * 
 */
public class MenuModele implements ObservableMenu {

	private static final String			prefix			= "HuntersWorldEditor";
	private static final String			dessinSuffix	= "DESSINS";
	private static final String			tileSuffix		= "TILES";
	private static final String			decorSuffix		= "DECORS";

	private ArrayList<ObserveurMenu>	listeObserveur;
	private MapModel					modeleMap;
	private Editeur						editeur;

	public MenuModele(Editeur editeur) {
		listeObserveur = new ArrayList<ObserveurMenu>();
		this.editeur = editeur;
	}

	public void setMapModel(MapModel modeleMap) {
		this.modeleMap = modeleMap;
	}

	public void saveConfiguration() throws IOException {
		boolean antialiasing = modeleMap.getRepere().isAntiAliasing();
		boolean renderingQuality = modeleMap.getRepere().isRenderingQuality();
		AfficheRepere afficherepere = modeleMap.getRepere().getAfficherRepere();
		int affiche = afficherepere == AfficheRepere.NePasAfficher ? 0 : afficherepere == AfficheRepere.EnDessous ? 1 : 0;

		if (antialiasing != Configuration.ANTIALIASING)
			Configuration.save("preferences.antialiasing", String.valueOf(antialiasing));

		if (affiche != Configuration.REPERE_VISIBILITE)
			Configuration.save("preferences.repere", String.valueOf(affiche));

		if (renderingQuality != Configuration.RENDERING_QUALITY)
			Configuration.save("preferences.rendering_quality", String.valueOf(renderingQuality));
	}

	public void enregistrer(File chemin) throws IOException {

		modeleMap.nettoyerTempElement();
		modeleMap.nettoyerTempTile();

		File tempDessin = creerFichierDessins();
		File tempTile = creerFichierTiles();
		File tempDecor = creerFichierDecor();

		ZipUtils.compress(chemin, new String[] { dessinSuffix, tileSuffix, decorSuffix },
				new String[] { tempDessin.getPath(), tempTile.getPath(), tempDecor.getPath() });

		tempTile.delete();
		tempDecor.delete();
		tempDessin.delete();
		editeur.setTitreOnglet(chemin.getName());
	}

	public void exporterImage(String chemin) {

		BufferedImage image = new BufferedImage(modeleMap.getRepere().getLargeur_repere(), modeleMap.getRepere().getHauteur_repere(),
				BufferedImage.TYPE_4BYTE_ABGR_PRE);

		Graphics2D g2d = image.createGraphics();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		modeleMap.getRepere().peindreTile(g2d);

		TreeSet<ElementDecor> decor = modeleMap.getDecor();

		for (ElementDecor e : decor)
			e.peindreDecor(g2d);

		g2d.dispose();

		try {
			ImageIO.write(image, "png", new File(chemin));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void ouvrir(String chemin) throws IOException {

		modeleMap.definirFond(null, false);

		TreeSet<ElementDecor> decor = modeleMap.getDecor();
		decor.clear();

		ArrayList<File> files = ZipUtils.uncompress(chemin, "HWM");

		for (File f : files)
			if (f.getName().endsWith(tileSuffix)) {
				lectureTiles(f);
			} else if (f.getName().endsWith(decorSuffix)) {
				lectureDecors(f);
			}

		for (File f : files)
			f.delete();

		modeleMap.notifyRepaintAll();
	}

	public void desactiveSelection() {
		for (ObserveurMenu obs : listeObserveur)
			obs.desactiveSelection();
	}

	public void nouveau() {
		editeur.nouveau();
	}

	public void fermer() {
		editeur.fermer();
	}

	/**************************************************************************************/
	/*********************************** CREATION FICHIERS ********************************/
	/**************************************************************************************/

	private File creerFichierDessins() throws IOException {
		File tempTile = File.createTempFile(prefix, dessinSuffix);

		HashSet<Dessin> dessinsUtilise = new HashSet<Dessin>();

		ArrayList<Tile> tiles = modeleMap.getRepere().getAllTiles();
		TreeSet<ElementDecor> decor = modeleMap.getDecor();

		for (Tile t : tiles)
			if (t.aUneTile())
				dessinsUtilise.add(t.getDessin());

		for (ElementDecor t : decor)
			dessinsUtilise.add(t.getDessin());

		PrintWriter sortie = new PrintWriter(new BufferedWriter(new FileWriter(tempTile)));

		for (Dessin d : dessinsUtilise) {
			sortie.print(String.valueOf(d.getId()));
			sortie.print(" ");
			sortie.println(d.getChemin());
		}

		sortie.close();

		return tempTile;
	}

	private File creerFichierTiles() throws IOException {
		File tempTile = File.createTempFile(prefix, tileSuffix);
		PrintWriter sortie = new PrintWriter(new BufferedWriter(new FileWriter(tempTile)));

		ArrayList<Tile> tiles = modeleMap.getRepere().getAllTiles();

		int nbLimit = 0;
		for (Tile t : tiles)
			if (t.aUneTile() && !t.estDecale())
				nbLimit++;

		sortie.print(modeleMap.getRepere().tilesEnLargeur());
		sortie.print(" ");
		sortie.print(modeleMap.getRepere().tilesEnHauteur());
		sortie.print(" ");
		sortie.println(String.valueOf(nbLimit));

		for (Tile t : tiles)
			if (t.aUneTile() && !t.estDecale()) {
				sortie.print(String.valueOf((int) (t.getPointLosange().x / Editeur.LARGEUR_LOSANGE)));
				sortie.print(" ");
				sortie.print(String.valueOf((int) (t.getPointLosange().y / Editeur.HAUTEUR_LOSANGE)));
				sortie.print(" ");
				sortie.println(String.valueOf(t.getDessin().getId()));
			}

		for (Tile t : tiles)
			if (t.aUneTile() && t.estDecale()) {
				sortie.print(String.valueOf((int) ((t.getPointLosange().x - Editeur.LARGEUR_LOSANGE / 2) / Editeur.LARGEUR_LOSANGE)));
				sortie.print(" ");
				sortie.print(String.valueOf((int) ((t.getPointLosange().y - Editeur.HAUTEUR_LOSANGE / 2) / Editeur.HAUTEUR_LOSANGE)));
				sortie.print(" ");
				sortie.println(String.valueOf(t.getDessin().getId()));
			}

		sortie.close();

		return tempTile;
	}

	private File creerFichierDecor() throws IOException {
		File tempDecor = File.createTempFile(prefix, decorSuffix);

		TreeSet<ElementDecor> decor = modeleMap.getDecor();

		DataOutputStream sortie = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tempDecor)));
		sortie.writeInt(decor.size());

		for (ElementDecor t : decor) {
			sortie.writeInt(t.getDessin().getId());
			sortie.writeBoolean(t.getDessin().estAccessible());
			sortie.writeInt(t.getX());
			sortie.writeInt(t.getY());
			sortie.writeByte(t.getCouche() == Couche.Defaut ? 0 : 1);
		}

		sortie.close();

		return tempDecor;
	}

	/**************************************************************************************/
	/*********************************** LECTURE FICHIERS *********************************/
	/**************************************************************************************/

	private void lectureTiles(File f) throws IOException {
		RepereTile repere = modeleMap.getRepere();
		DataInputStream entree = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

		entree.readInt();
		entree.readInt();
		int iteration = entree.readInt();

		for (int i = 0; i < iteration; i++) {

			Dessin dessin = GestionnaireDessins.recupere(entree.readInt());

			entree.readBoolean();
			entree.readInt();
			entree.readInt();
			int x = entree.readInt();
			int y = entree.readInt();

			Tile t = repere.tileConteneur(x, y);
			t.setDessin(dessin);
		}

		entree.close();
	}

	private void lectureDecors(File f) throws IOException {
		DataInputStream entree = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

		int iteration = entree.readInt();

		for (int i = 0; i < iteration; i++) {
			Dessin dessin = GestionnaireDessins.recupere(entree.readInt());
			entree.readBoolean();
			int x = entree.readInt();
			int y = entree.readInt();
			Couche couche = entree.readByte() == 0 ? Couche.Defaut : Couche.AvantPlan;

			TreeSet<ElementDecor> decor = modeleMap.getDecor();
			decor.add(new ElementDecor(dessin, couche, x, y));
		}
	}

	/**************************************************************************************/
	/************************************* OBSERVABLE *************************************/
	/**************************************************************************************/

	@Override
	public void addObserveur(ObserveurMenu obs) {
		listeObserveur.add(obs);
	}

	@Override
	public void removeAllObserver() {
		listeObserveur = new ArrayList<ObserveurMenu>();
	}

	@Override
	public void removeObserver(ObserveurMenu obs) {
		listeObserveur.remove(obs);
	}

}

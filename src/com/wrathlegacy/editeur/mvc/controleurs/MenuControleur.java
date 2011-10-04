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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.wrathlegacy.editeur.AfficheRepere;
import com.wrathlegacy.editeur.Couche;
import com.wrathlegacy.editeur.mvc.modeles.MenuModele;
import com.wrathlegacy.editeur.mvc.observeurs.ObservableMenuToMap;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurMenuToMap;

/**
 * @author Matthieu637
 * 
 */
public class MenuControleur implements ObservableMenuToMap {

	private MenuModele						modele;
	private ArrayList<ObserveurMenuToMap>	observeurs;
	private File							nomFichier;

	public MenuControleur(MenuModele modele) {
		this.modele = modele;
		observeurs = new ArrayList<ObserveurMenuToMap>();
	}

	public void enregistrerSous() {

		String chemin = choisirFichier(false,"HWM Hunters' World Map (*.hwm)", "hwm");

		if (chemin == null)
			return;

		File f = new File(chemin + ".hwm");

		try {
			modele.enregistrer(f);
			nomFichier = f;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enregistrer() {
		if (nomFichier == null)
			enregistrerSous();
		else
			try {
				modele.enregistrer(nomFichier);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Impossible d'enregistrer cette map.", "Erreur", JOptionPane.WARNING_MESSAGE);
			}
	}
	
	public void enregistrerTout() {
		//TODO: enregistrerTout
		
	}

	public void ouvrir() {

		String chemin = choisirFichier(true,"HWM Hunters' World Map (*.hwm)", "hwm");

		if (chemin == null || !new File(chemin).exists())
			return;
		
		try {
			modele.ouvrir(chemin);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Impossible de lire cette map.", "Erreur", JOptionPane.WARNING_MESSAGE);
		}
	}

	public void exporterImage() {
		String chemin = choisirFichier(false,"PNG (*.png)", "png");

		if (chemin == null)
			return;

		modele.exporterImage(chemin + ".png");
	}

	public void setCouche(Couche couche) {
		for (ObserveurMenuToMap o : observeurs)
			o.changementDeCouche(couche);
	}

	public void setIndex(int index) {
		modele.desactiveSelection();

		selection(false);

		for (ObserveurMenuToMap o : observeurs)
			o.changementDeDessin(index);
	}

	public void selection(boolean select) {
		for (ObserveurMenuToMap o : observeurs)
			o.modeSelection(select);
	}

	public void afficherRepere(AfficheRepere affiche) {
		for (ObserveurMenuToMap o : observeurs)
			o.afficherRepere(affiche);
	}

	public void annuler() {
		for (ObserveurMenuToMap o : observeurs)
			o.annuler();
	}

	public void antiAliasing() {
		for (ObserveurMenuToMap o : observeurs)
			o.antiAliasing();
	}
	

	public void renderingQuality() {
		for (ObserveurMenuToMap o : observeurs)
			o.renderingQuality();
	}
	
	public void retablir() {
		for (ObserveurMenuToMap o : observeurs)
			o.retablir();
	}

	private String choisirFichier(boolean ouvrir,String description, String extension) {
		FileFilter hwm = new FileNameExtensionFilter(description, extension);

		JFileChooser choix = new JFileChooser();
		choix.setAcceptAllFileFilterUsed(false);
		choix.setFileFilter(hwm);

		int retour;
		if(ouvrir)
			retour = choix.showOpenDialog(new JFrame());
			else retour = choix.showSaveDialog(new JFrame());

		if (retour == JFileChooser.APPROVE_OPTION)
			return choix.getSelectedFile().getPath();
		else
			return null;
	}
	

	public void saveConfigurationAndQuit() {
		try {
			modele.saveConfiguration();
			System.exit(0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Impossible d'enregistrer vos configurations.", "Erreur", JOptionPane.WARNING_MESSAGE);
		}
	}
	

	public void nouveau() {
		modele.nouveau();
	}
	
	public void fermer() {
		modele.fermer();
	}

	/**************************************************************************************/
	/************************************* OBSERVABLE *************************************/
	/**************************************************************************************/

	@Override
	public void addObserveur(ObserveurMenuToMap obs) {
		observeurs.add(obs);
	}

	@Override
	public void removeAllObserver() {
		observeurs = new ArrayList<ObserveurMenuToMap>();
	}

	@Override
	public void removeObserver(ObserveurMenuToMap obs) {
		observeurs.remove(obs);
	}

	public void definirFond(boolean remplir) {
		for (ObserveurMenuToMap o : observeurs)
			o.definirFond(remplir);
	}
	
	public void redimensionner(Point p){
		for (ObserveurMenuToMap o : observeurs)
			o.redimensionnerRepere(p);
	}
}

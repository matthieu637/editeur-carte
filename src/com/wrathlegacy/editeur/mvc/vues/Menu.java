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

package com.wrathlegacy.editeur.mvc.vues;

import java.awt.Checkbox;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import com.wrathlegacy.editeur.AfficheRepere;
import com.wrathlegacy.editeur.Configuration;
import com.wrathlegacy.editeur.Couche;
import com.wrathlegacy.editeur.TypeDessin;
import com.wrathlegacy.editeur.mvc.controleurs.MenuControleur;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurMenu;
import com.wrathlegacy.librarie.GeneralMenu;

/**
 * @author Matthieu637
 * 
 */
@SuppressWarnings("serial")
public class Menu extends GeneralMenu implements ObserveurMenu {

	private MiniMap			minimap;
	private MenuControleur	controleur;
	private Checkbox		selection;
	private Editeur			editeur;

	public Menu(Editeur parrent, MenuControleur controleur, HashMap<TypeDessin, JScrollPane> decorFrame, MiniMap minimap) {
		this.editeur = parrent;
		this.controleur = controleur;
		this.minimap=minimap;

		this.creerMenuFichier();
		this.creerMenuEdition();
		this.creerMenuOutils();
		this.creerMenuPreference();
		this.creerMenuPalette();
		this.creerMenuCouches();

		selection = new Checkbox("Selection");
		selection.setState(true);
		selection.addItemListener(new SelectionListener());
		this.add(selection);
	}

	private void creerMenuFichier() {

		int index = this.construireMenu("Fichiers");
		construireJMenuItem(index,"Nouveau", new NouveauListener(),KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		construireJMenuItem(index, "Ouvrir", new OuvrirListener(), KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		construireJMenuItem(index, "Fermer l'onglet", new FermerListener(), KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
		construireJMenuItem(index, "Enregistrer", new EnregistrerListener(), KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		construireJMenuItem(index, "Enregistrer Sous", new EnresgitrerSousListener(), KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
		construireJMenuItem(index, "Enregistrer Tout", new EnresgitrerToutListener(), null);
		construireJMenuItem(index, "Exporter en format d'image", new ExporterImageListener(), KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		construireJMenuItem(index, "Quitter", new QuitterListener(), KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
	}

	private void creerMenuEdition() {

		int index = this.construireMenu("Edition");
		construireJMenuItem(index, "Annuler", new AnnulerListener(), KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		construireJMenuItem(index, "Rétablir", new RetablirListener(), KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
		construireJMenuItem(index, "Dimensions", new DimensionListener(editeur), KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
	}

	private void creerMenuOutils() {
		int index = this.construireMenu("Outils");
		construireJMenuItem(index, "Définir comme fond", new DefinirFond(false), KeyStroke.getKeyStroke(KeyEvent.VK_F, 0));
		construireJMenuItem(index, "Remplir le fond", new DefinirFond(true), KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
		construireJMenuItem(index, "Minimap", new MinimapListener(), KeyStroke.getKeyStroke(KeyEvent.VK_M, 0));
	}

	private void creerMenuPreference() {
		int index = this.construireMenu("Preferences");
		construireJCheckBoxMenuItem(index, "Anti-aliasing", new AntiAliasingListener(), null, Configuration.ANTIALIASING);
		construireJCheckBoxMenuItem(index, "Rendering Quality", new RenderingQualityListener(), null, Configuration.RENDERING_QUALITY);

		int sous_index = this.constuireSousMenu(index, "Repère");

		String[] reperes = { "Cacher", "Sous les tiles", "Sur les tiles" };
		ActionListener[] listeners = new ActionListener[] { new RepereListener(AfficheRepere.NePasAfficher), new RepereListener(AfficheRepere.EnDessous),
				new RepereListener(AfficheRepere.AuDessus) };

		KeyStroke[] raccourcis = new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), KeyStroke.getKeyStroke(KeyEvent.VK_2, 0),
				KeyStroke.getKeyStroke(KeyEvent.VK_3, 0) };

		constuireJRadioButtonMenuItem(sous_index, reperes, listeners, raccourcis, Configuration.REPERE_VISIBILITE);
	}

	private void creerMenuPalette() {
		int index = this.construireMenu("Palettes");

		KeyStroke[] raccourcis = new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), KeyStroke.getKeyStroke(KeyEvent.VK_D, 0),
				KeyStroke.getKeyStroke(KeyEvent.VK_B, 0) };

		for (int i = 0; i < TypeDessin.values().length; i++) {
			TypeDessin type = TypeDessin.values()[i];
			this.construireJMenuItem(index, String.valueOf(type), new VisibiliteFenetreDessin(type), raccourcis[i]);
		}
	}

	private void creerMenuCouches() {
		int index = this.construireMenu("Couches");

		String[] couches = { "Defaut", "Avant-Plan" };
		ActionListener[] listeners = new ActionListener[] { new CouchesListener(Couche.Defaut), new CouchesListener(Couche.AvantPlan) };
		KeyStroke[] raccourcis = new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK),
				KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_MASK) };

		constuireJRadioButtonMenuItem(index, couches, listeners, raccourcis, 0);
	}

	@Override
	public void desactiveSelection() {
		selection.setState(false);
	}

	/**************************************************************************************/
	/**************************************************************************************/
	/**************************************************************************************/
	/************************************* LISTENERS *************************************/
	/**************************************************************************************/
	/**************************************************************************************/
	/**************************************************************************************/

	// TODO:review
	class DimensionListener implements ActionListener {
		private DimensionRepereDialog	dimensionDialog;

		public DimensionListener(Editeur editeur) {
			this.dimensionDialog = new DimensionRepereDialog(editeur);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Point dimensions = dimensionDialog.lanceDialogue();
			controleur.redimensionner(dimensions);
		}
	}

	private class VisibiliteFenetreDessin implements ActionListener {
		private TypeDessin	type;

		public VisibiliteFenetreDessin(TypeDessin type) {
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			editeur.changePalette(type);
		}
	}

	private class DefinirFond implements ActionListener {

		private boolean	b;

		private DefinirFond(boolean b) {
			this.b = b;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			controleur.definirFond(b);
		}
	}

	private class CouchesListener implements ActionListener {
		private Couche	couche;

		public CouchesListener(Couche couche) {
			this.couche = couche;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			controleur.setCouche(couche);
		}
	}

	private class EnregistrerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.enregistrer();
		}
	}

	private class EnresgitrerSousListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.enregistrerSous();
		}
	}
	
	private class EnresgitrerToutListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.enregistrerTout();
		}
	}

	private class ExporterImageListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.exporterImage();
		}
	}

	private class SelectionListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			controleur.selection(((Checkbox) e.getSource()).getState());
		}

	}

	private class RepereListener implements ActionListener {
		private AfficheRepere	affiche;

		public RepereListener(AfficheRepere affiche) {
			this.affiche = affiche;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.afficherRepere(affiche);
		}
	}

	private class AnnulerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controleur.annuler();
		}
	}

	private class AntiAliasingListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controleur.antiAliasing();
		}
	}

	private class RenderingQualityListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controleur.renderingQuality();
		}
	}

	private class OuvrirListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.ouvrir();
		}
	}
	
	private class FermerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.fermer();
		}
	}
	
	private class NouveauListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.nouveau();
		}
	}

	private class QuitterListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.saveConfigurationAndQuit();
		}
	}

	private class MinimapListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			minimap.setVisible(!minimap.isVisible());
		}
	}

	private class RetablirListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.retablir();
		}
	}
}

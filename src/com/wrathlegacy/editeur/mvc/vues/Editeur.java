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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.wrathlegacy.editeur.Configuration;
import com.wrathlegacy.editeur.GestionnaireDessins;
import com.wrathlegacy.editeur.TypeDessin;
import com.wrathlegacy.editeur.mvc.controleurs.MapControleur;
import com.wrathlegacy.editeur.mvc.controleurs.MenuControleur;
import com.wrathlegacy.editeur.mvc.modeles.MapModel;
import com.wrathlegacy.editeur.mvc.modeles.MenuModele;
import com.wrathlegacy.editeur.mvc.observeurs.ObservableZoneVisibleEditeur;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurMinimapChangeVue;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurZoneVisibleEditeur;

/**
 * @author Matthieu637
 * 
 */
@SuppressWarnings("serial")
public class Editeur extends JFrame implements ObserveurMinimapChangeVue, ObservableZoneVisibleEditeur {
	public final static int							LARGEUR_LOSANGE			= 158;
	public final static int							HAUTEUR_LOSANGE			= 88;
	private final static int						LOSANGES_EN_LARGEUR		= 30;
	private final static int						LOSANGES_EN_HAUTEUR		= 30;
	private final static int						LARGEUR					= LOSANGES_EN_LARGEUR * LARGEUR_LOSANGE + LARGEUR_LOSANGE / 2;
	private final static int						HAUTEUR					= LOSANGES_EN_HAUTEUR * HAUTEUR_LOSANGE + HAUTEUR_LOSANGE / 2;
	private final static int						MAX_UNDO				= 100;

	public final static int							largeurButtonPalette	= 100;

	private ArrayList<JScrollPane>					scroll					= new ArrayList<JScrollPane>();
	private ArrayList<Map>							map						= new ArrayList<Map>();
	private ArrayList<MapModel>						modeleMap				= new ArrayList<MapModel>();
	private ArrayList<MapControleur>				controleurMap			= new ArrayList<MapControleur>();

	private ArrayList<ObserveurZoneVisibleEditeur>	observeur;
	private final MiniMap							minimap					= new MiniMap();
	private MenuModele								modeleMenu;
	private MenuControleur							controleurMenu;
	private JSplitPane								splitPane;
	private HashMap<TypeDessin, JScrollPane>		palette;
	private JTabbedPane								systemeOnglet;
	private int										ongletSelectionne		= 0;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Configuration.load();

		GestionnaireDessins.charger(largeurButtonPalette, largeurButtonPalette);

		GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle dimEcran = graphics.getMaximumWindowBounds();

		Editeur editeur = new Editeur(graphics.getDefaultScreenDevice().getDefaultConfiguration());
		editeur.setTitle("Editeur de map Wrath Legacy Team");
		editeur.setBounds(dimEcran);

		System.gc();
		editeur.setVisible(true);
	}

	private void creerMapComplete() {
		// creation du modele
		MapModel modeleMap = new MapModel(LOSANGES_EN_LARGEUR, LOSANGES_EN_HAUTEUR, LARGEUR_LOSANGE, HAUTEUR_LOSANGE, MAX_UNDO);
		this.modeleMap.add(modeleMap);

		// creation du controleur
		MapControleur controleurMap = new MapControleur(modeleMap);
		this.controleurMap.add(controleurMap);
		controleurMenu.addObserveur(controleurMap);

		// creation de la vue
		Map map = new Map(controleurMap, LARGEUR, HAUTEUR);
		modeleMap.addObserveur(map);
		modeleMap.addObserveur(minimap);
		this.map.add(map);

		// creation du scroll et de ces listeners
		JScrollPane scroll = new JScrollPane(map);
		scroll.setMinimumSize(new Dimension(largeurButtonPalette * 2 + 17, 200));
		scroll.getHorizontalScrollBar().setUnitIncrement(25);
		scroll.getVerticalScrollBar().setUnitIncrement(25);

		MouseAdapter scrollListener = new ScrollChangePosition();
		scroll.addMouseWheelListener(scrollListener);
		scroll.getHorizontalScrollBar().addMouseListener(scrollListener);
		scroll.getVerticalScrollBar().addMouseListener(scrollListener);
		scroll.getHorizontalScrollBar().addMouseMotionListener(scrollListener);
		scroll.getVerticalScrollBar().addMouseMotionListener(scrollListener);
		scroll.addComponentListener(new ComposantResizeListener());
		this.scroll.add(scroll);

		systemeOnglet.addTab("Map " + this.map.size(), scroll);
	}

	public Editeur(GraphicsConfiguration gc) {
		super(gc);

		observeur = new ArrayList<ObserveurZoneVisibleEditeur>();

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// creation du système d'onglet
		systemeOnglet = new JTabbedPane();

		// creation du menu
		modeleMenu = new MenuModele(this);
		controleurMenu = new MenuControleur(modeleMenu);
		creerPalettes(controleurMenu);
		Menu menu = new Menu(this, controleurMenu, palette, minimap);
		modeleMenu.addObserveur(menu);

		// construction de la map
		creerMapComplete();

		// construction du splitpane
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, palette.get(TypeDessin.Tile), systemeOnglet);

		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(false);
		splitPane.setDividerLocation(largeurButtonPalette * 2 + 17);

		// observeurs
		systemeOnglet.addChangeListener(new OngletChange());
		minimap.addObserveur(this);

		this.addWindowListener(new QuitterWindowListener());

		ComposantResizeListener composantResize = new ComposantResizeListener();
		this.addComponentListener(composantResize);
		this.addObserveur(minimap);

		// ajout des composant à la fenetre
		this.getContentPane().add(splitPane);
		this.setJMenuBar(menu);
		this.add(new IconeMenu(controleurMenu), BorderLayout.PAGE_START);

		// injection
		modeleMenu.setMapModel(modeleMap.get(ongletSelectionne));
		minimap.setDecor(controleurMap.get(ongletSelectionne));
	}

	public void nouveau() {
		creerMapComplete();
		systemeOnglet.setSelectedIndex(map.size() - 1);
	}

	public void fermer() {
		if (map.size() > 0) {
			controleurMenu.removeObserver(controleurMap.get(ongletSelectionne));
			modeleMap.get(ongletSelectionne).removeAllObserver();

			scroll.remove(ongletSelectionne);
			map.remove(ongletSelectionne);
			modeleMap.remove(ongletSelectionne);
			controleurMap.remove(ongletSelectionne);
			systemeOnglet.remove(ongletSelectionne);

			System.gc();
		}
	}

	public void setTitreOnglet(String name) {
		systemeOnglet.setTitleAt(ongletSelectionne, name);
	}

	@Override
	public void positionScroll(int horizontal, int vertical) {
		scroll.get(ongletSelectionne).getHorizontalScrollBar().setValue(horizontal - getWidth() / 2);
		scroll.get(ongletSelectionne).getVerticalScrollBar().setValue(vertical - getHeight() / 2);
	}

	private void creerPalettes(MenuControleur controleurMenu) {
		palette = new HashMap<TypeDessin, JScrollPane>(TypeDessin.values().length);

		for (int i = 0; i < TypeDessin.values().length; i++) {
			TypeDessin type = TypeDessin.values()[i];

			JScrollPane leftRight = new JScrollPane(new Palette(controleurMenu, type));
			leftRight.setMinimumSize(new Dimension(largeurButtonPalette * 2 + 17, 200));
			palette.put(type, leftRight);
		}
	}

	public void changePalette(TypeDessin type) {
		if (splitPane.getLeftComponent() == palette.get(type))
			splitPane.setLeftComponent(null);
		else
			splitPane.setLeftComponent(palette.get(type));
	}

	/**************************************************************************************/
	/************************************ LISTENERS ***************************************/
	/**************************************************************************************/

	private class OngletChange implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			ongletSelectionne = systemeOnglet.getSelectedIndex();

			if (ongletSelectionne != -1) {
				modeleMenu.setMapModel(modeleMap.get(ongletSelectionne));
				minimap.setDecor(controleurMap.get(ongletSelectionne));
				minimap.redessineTout();
			}
		}

	}

	private class ComposantResizeListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			notifyZoneVisibleChange();
		}
	}

	private class ScrollChangePosition extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			notifyZoneVisibleChange();
		}

		public void mouseDragged(MouseEvent e) {
			notifyZoneVisibleChange();
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			notifyZoneVisibleChange();
		}
	}

	private class QuitterWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			controleurMenu.saveConfigurationAndQuit();
		}
	}

	/**************************************************************************************/
	/************************************ OBSERVABLE ***************************************/
	/**************************************************************************************/

	@Override
	public void addObserveur(ObserveurZoneVisibleEditeur obs) {
		observeur.add(obs);
	}

	@Override
	public void removeAllObserver() {
		observeur.clear();
	}

	@Override
	public void removeObserver(ObserveurZoneVisibleEditeur obs) {
		observeur.remove(obs);
	}

	@Override
	public void notifyZoneVisibleChange() {
		for (ObserveurZoneVisibleEditeur obs : observeur)
			obs.zoneVisibleChange(scroll.get(ongletSelectionne).getHorizontalScrollBar().getValue(), scroll.get(ongletSelectionne).getVerticalScrollBar()
					.getValue(), map.get(ongletSelectionne).getVisibleRect().width, map.get(ongletSelectionne).getVisibleRect().height);
	}
}

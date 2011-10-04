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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.wrathlegacy.editeur.ElementDecor;
import com.wrathlegacy.editeur.RepereTile;
import com.wrathlegacy.editeur.mvc.controleurs.MapControleur;
import com.wrathlegacy.editeur.mvc.observeurs.ObservableMinimapChangeVue;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurMap;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurMinimapChangeVue;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurZoneVisibleEditeur;

/**
 * @author Matthieu637
 * 
 */
@SuppressWarnings("serial")
public class MiniMap extends JFrame implements ObserveurMap, ObserveurZoneVisibleEditeur, ObservableMinimapChangeVue {

	private MiniMapPanel							minimapPanel;
	private double									scaleRapport;
	private double									rapportLargeurHauteur;
	private int										derniereHauteur;
	private int										derniereLargeur;
	private int										largeur_repere;
	private MapControleur							controleur;
	private ArrayList<ObserveurMinimapChangeVue>	observeurs;

	private TreeSet<ElementDecor>					decor;
	private RepereTile								repere;

	public MiniMap() {

		observeurs = new ArrayList<ObserveurMinimapChangeVue>();

		this.addComponentListener(new MinimapRedimensionne());
		this.setTitle("Minimap");
		this.setAlwaysOnTop(true);

		minimapPanel = new MiniMapPanel();
		this.getContentPane().add(minimapPanel);
	}

	public void setVisible(boolean b)
	{
		super.setVisible(b);
		minimapPanel.repaint();
	}
	
	public void setDecor(MapControleur controleur) {

		this.controleur = controleur;
		largeur_repere = controleur.getRepere().getLargeur_repere();
		int hauteur_repere = controleur.getRepere().getHauteur_repere();

		rapportLargeurHauteur = (double) largeur_repere / hauteur_repere;

		this.decor = controleur.getDecor();
		this.repere = controleur.getRepere();

		calculerScaleRapport(300);
	}

	public void dimensionFenetreChange() {
		double width;
		int height;

		if (getWidth() != derniereLargeur) {
			width = getWidth();
			height = (int) (width / rapportLargeurHauteur);
		} else if (getHeight() != derniereHauteur) {
			height = getHeight();
			width = height * rapportLargeurHauteur;
		} else
			return;

		if (width < 300) {
			width = 300;
			height = (int) (width / rapportLargeurHauteur);
		}

		minimapPanel.setPreferredSize(new Dimension((int) width, height));

		pack();
		captureDimension();
		replacerFenetre();

		calculerScaleRapport(width);
	}

	public void calculerScaleRapport(double width) {
		scaleRapport = (double) (1 / (largeur_repere / width));
	}

	public void replacerFenetre() {
		Dimension dimEcran = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dimEcran.width - getWidth(), 20);
	}

	public void captureDimension() {
		derniereLargeur = getWidth();
		derniereHauteur = getHeight();
	}

	/**************************************************************************************/
	/**************************************** PANEL ***************************************/
	/**************************************************************************************/

	class MiniMapPanel extends JPanel {

		private int	xZoneVisible;
		private int	yZoneVisible;
		private int	largeurZoneVisible;
		private int	hauteurZoneVisible;

		public MiniMapPanel() {
			MouseAdapter listener = new ListenerPanelMinimap();
			this.addMouseListener(listener);
			this.addMouseMotionListener(listener);
		}

		public void calculerRectangleZoneVisible(int x, int y, int largeur, int hauteur) {
			xZoneVisible = (int) (x * scaleRapport);
			yZoneVisible = (int) (y * scaleRapport);
			largeurZoneVisible = (int) (largeur * scaleRapport);
			hauteurZoneVisible = (int) (hauteur * scaleRapport);
		}

		public void clicRectangleZoneVisible(int x, int y) {
			xZoneVisible = x - largeurZoneVisible / 2;
			yZoneVisible = y - hauteurZoneVisible / 2;
		}
		
		public void calculRectangleZoneVisible(double rapport)
		{
			if(rapport==0)
				return;
			
			xZoneVisible /= rapport;
			yZoneVisible /= rapport;
			largeurZoneVisible /= rapport;
			hauteurZoneVisible /= rapport;
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;
			g2d.scale(scaleRapport, scaleRapport);

			repere.peindreSansRepere(g);

			for (ElementDecor a : decor)
				a.peindreDecor(g);

			g2d.scale(1 / scaleRapport, 1 / scaleRapport);

			g.setColor(Color.white);

			g.drawRect(xZoneVisible, yZoneVisible, largeurZoneVisible, hauteurZoneVisible);
		}
	}

	/**************************************************************************************/
	/************************************* LISTENERS *************************************/
	/**************************************************************************************/

	@Override
	public void redessine(Rectangle zoneRepeindre) {
		int x = (int) (zoneRepeindre.x * scaleRapport) - 5;
		int y = (int) (zoneRepeindre.y * scaleRapport) - 5;

		int largeur = (int) (zoneRepeindre.getWidth() * scaleRapport) + 10;
		int hauteur = (int) (zoneRepeindre.getHeight() * scaleRapport) + 10;

		minimapPanel.repaint(new Rectangle(x, y, largeur, hauteur));
	}

	@Override
	public void zoneVisibleChange(int x, int y, int largeur, int hauteur) {
		minimapPanel.calculerRectangleZoneVisible(x, y, largeur, hauteur);
		minimapPanel.repaint();
	}

	@Override
	public void redimension(int width, int height) {
		setDecor(controleur);
		derniereHauteur--;
		dimensionFenetreChange();
		redessineTout();
	}

	@Override
	public void redessineTout() {
		minimapPanel.repaint();
	}

	private class MinimapRedimensionne extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			int largeur = derniereLargeur;
			dimensionFenetreChange();
			minimapPanel.calculRectangleZoneVisible((double)largeur/derniereLargeur);
			redessineTout();
		}
	}

	private class ListenerPanelMinimap extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			notifyPositionScroll((int) (e.getX() * 1 / scaleRapport), (int) (e.getY() * 1 / scaleRapport));
			minimapPanel.clicRectangleZoneVisible(e.getX(), e.getY());
			minimapPanel.repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			notifyPositionScroll((int) (e.getX() * 1 / scaleRapport), (int) (e.getY() * 1 / scaleRapport));
			minimapPanel.clicRectangleZoneVisible(e.getX(), e.getY());
			minimapPanel.repaint();
		}
	}

	/**************************************************************************************/
	/************************************ OBSERVABLE ***************************************/
	/**************************************************************************************/

	@Override
	public void notifyPositionScroll(int horizontal, int vertical) {
		for (ObserveurMinimapChangeVue obs : observeurs)
			obs.positionScroll(horizontal, vertical);
	}

	@Override
	public void addObserveur(ObserveurMinimapChangeVue obs) {
		observeurs.add(obs);
	}

	@Override
	public void removeAllObserver() {
		observeurs.clear();
	}

	@Override
	public void removeObserver(ObserveurMinimapChangeVue obs) {
		observeurs.remove(obs);
	}
}

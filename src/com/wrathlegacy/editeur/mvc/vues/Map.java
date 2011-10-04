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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.TreeSet;

import javax.swing.JPanel;

import com.wrathlegacy.editeur.ElementDecor;
import com.wrathlegacy.editeur.RepereTile;
import com.wrathlegacy.editeur.mvc.controleurs.MapControleur;
import com.wrathlegacy.editeur.mvc.observeurs.ObserveurMap;

/**
 * @author Matthieu637
 * 
 */
@SuppressWarnings("serial")
public class Map extends JPanel implements ObserveurMap {

	private MapControleur			controleur;
	private TreeSet<ElementDecor>	decor;

	public Map(MapControleur controleur, int largeur, int hauteur) {
		this.controleur = controleur;
		this.decor = controleur.getDecor();

		this.addMouseListener(new ClickSouris());
		this.addMouseMotionListener(new DeplacementSouris());

		Dimension d = new Dimension(largeur, hauteur);
		this.setPreferredSize(d);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		getRepere().peindre(g);

		for (ElementDecor a : decor)
			a.peindreDecor(g);
	}

	public void redessine(Rectangle zoneRepeindre) {
		this.repaint(zoneRepeindre);
	}

	public RepereTile getRepere() {
		return controleur.getRepere();
	}

	/***************************************************************************************************************************
	 ******************************************* CLASSES LISTENERS **************************************************************
	 ****************************************************************************************************************************/

	class ClickSouris implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
				controleur.debutUndo(false);
			else {
				controleur.debutUndo(true);
				controleur.nouvelleSelection(e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
				controleur.effacerDecor(e.getX(), e.getY());
			else {
				controleur.placementDecor(e.getX(), e.getY(), false);
				controleur.selectionner(e.getX(), e.getY());
			}
			controleur.finUndo();
		}
	}

	class DeplacementSouris implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
			if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
				controleur.effacerDecor(e.getX(), e.getY());
			else
				controleur.placementDecor(e.getX(), e.getY(), true);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			controleur.changementVolatileImage(e.getX(), e.getY());
		}
	}

	@Override
	public void redessineTout() {
		this.repaint();
	}

	@Override
	public void redimension(int width, int height) {
		Dimension d = new Dimension(width, height);
		this.setPreferredSize(d);
		this.setSize(width, height);
		this.repaint();
	}
}

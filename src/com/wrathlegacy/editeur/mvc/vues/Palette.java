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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.wrathlegacy.editeur.GestionnaireDessins;
import com.wrathlegacy.editeur.TypeDessin;
import com.wrathlegacy.editeur.mvc.controleurs.MenuControleur;

@SuppressWarnings("serial")
public class Palette extends JPanel {

	public Palette(MenuControleur controleur, TypeDessin type) {
		this.setVisible(true);

		TitledBorder bord = BorderFactory.createTitledBorder("Palette de " + type + "s");
		bord.setTitleJustification(TitledBorder.CENTER);
		this.setBorder(bord);

		int nbCase = GestionnaireDessins.getNombreDessin(type);

		this.setPreferredSize(new Dimension(Editeur.largeurButtonPalette * 2, (nbCase / 3 + 1) * 45));
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));

		for (int i = 0; i < GestionnaireDessins.recupereChaqueDessin().size(); i++)
			if (GestionnaireDessins.recupereChaqueDessin().get(i).getType() == type) {
				JButton bouton = new JButton(GestionnaireDessins.recupere(i).getIcone());
				bouton.setPreferredSize(new Dimension(Editeur.largeurButtonPalette, 100));
				bouton.addActionListener(new IndexListener(controleur, i));
				this.add(bouton);
			}
	}

	private class IndexListener implements ActionListener {

		private MenuControleur	controleur;
		private int				index;

		public IndexListener(MenuControleur controleur, int index) {
			this.index = index;
			this.controleur = controleur;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			controleur.setIndex(index);
		}

	}
}
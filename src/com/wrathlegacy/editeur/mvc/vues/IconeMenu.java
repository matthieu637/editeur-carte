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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.wrathlegacy.editeur.mvc.controleurs.MenuControleur;

@SuppressWarnings("serial")
public class IconeMenu extends JToolBar {

	private static final String	CHEMIN	= "data" + File.separator + "interface" + File.separator;

	private MenuControleur		controleur;

	/**
	 * 
	 */

	public IconeMenu(MenuControleur controleur) {
		this.controleur = controleur;

		JButton nouveau = new JButton(new ImageIcon(CHEMIN + "TreeLeaf.gif"));
		nouveau.addActionListener(new NouveauListener());
		this.add(nouveau);
		
		
		JButton ouvrir = new JButton(new ImageIcon(CHEMIN + "NewFolder.gif"));
		ouvrir.addActionListener(new OuvrirListener());
		this.add(ouvrir);
		
		JButton enregistrer = new JButton(new ImageIcon(CHEMIN + "HardDrive.gif"));
		enregistrer.addActionListener(new EnregistrerListener());
		this.add(enregistrer);
		
		this.addSeparator();
	}
	

	/**************************************************************************************/
	/**************************************************************************************/
	/**************************************************************************************/
	/************************************* LISTENERS *************************************/
	/**************************************************************************************/
	/**************************************************************************************/
	/**************************************************************************************/

	private class NouveauListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.nouveau();
		}
	}
	
	private class OuvrirListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.ouvrir();
		}
	}
	
	private class EnregistrerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controleur.enregistrer();
		}
	}
}

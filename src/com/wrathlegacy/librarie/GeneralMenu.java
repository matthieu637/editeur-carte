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

package com.wrathlegacy.librarie;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

/**
 * @author Matthieu637
 * 
 */
@SuppressWarnings("serial")
public class GeneralMenu extends JMenuBar {

	/**
	 * Contient tous les menu
	 */
	private ArrayList<JMenu>	menu;

	/**
	 * 
	 */
	public GeneralMenu() {
		menu = new ArrayList<JMenu>();
	}

	/**
	 * Construit menu
	 * 
	 * @param nomMenu
	 *            nom du menu
	 * @return index du menu
	 */
	public int construireMenu(String nomMenu) {
		assert (nomMenu != null) : "nomMenu nulle";

		JMenu nouveauJMenu = new JMenu(nomMenu);
		this.add(nouveauJMenu);
		this.menu.add(nouveauJMenu);
		return menu.size()-1;
	}
	
	/**
	 * @param index du menu
	 * @param nomSousMenu
	 * @return index du sous-menu
	 */
	public int constuireSousMenu(int index,String nomSousMenu){
		assert (nomSousMenu != null) : "nomMenu nulle";
		assert (index > 0 && index < menu.size()) : "index incorrect";
		
		JMenu nouveauJMenu = new JMenu(nomSousMenu);
		this.menu.get(index).add(nouveauJMenu);
		this.menu.add(nouveauJMenu);
		return menu.size()-1;
	}

	/**
	 * Construit un JMenuItem dans un menu avec un actionlistener et un raccourci
	 * 
	 * @param index
	 *            du menu
	 * @param nomJMenuItem
	 *            nom du JMenuItem
	 * @param listener
	 *            sur le JMenuItem
	 * @param raccourci
	 *            pour le JMenuItem
	 */
	public void construireJMenuItem(int index, String nomJMenuItem, ActionListener listener, KeyStroke raccourci) {
		assert (index > 0 && index < menu.size()) : "index incorrect";

		JMenuItem jMenuItem = new JMenuItem(nomJMenuItem);
		jMenuItem.addActionListener(listener);
		jMenuItem.setAccelerator(raccourci);
		menu.get(index).add(jMenuItem);
	}

	/**
	 * Construire un JCheckBoxMenuItem
	 * 
	 * @param index
	 *            du menu
	 * @param nomJCheckBoxMenuItem
	 *            nom du JCheckBoxMenuItem
	 * @param listener
	 *            sur le JCheckBoxMenuItem
	 * @param raccourci
	 *            sur le JCheckBoxMenuItem
	 * @param selected
	 *            activation ou non du JCheckBox
	 */
	public void construireJCheckBoxMenuItem(int index, String nomJCheckBoxMenuItem, ActionListener listener, KeyStroke raccourci, boolean selected) {
		assert (index > 0 && index < menu.size()) : "index incorrect";

		JCheckBoxMenuItem jMenuItem = new JCheckBoxMenuItem(nomJCheckBoxMenuItem);
		jMenuItem.addActionListener(listener);
		jMenuItem.setAccelerator(raccourci);
		jMenuItem.setSelected(selected);
		menu.get(index).add(jMenuItem);
	}

	/**
	 * Construit un groupe de JRadioButtomMenuItem
	 * 
	 * @param index du menu
	 * @param nomJRadio
	 * @param listeners
	 * @param raccourcis
	 * @param selected_index
	 */
	public void constuireJRadioButtonMenuItem(int index, String[] nomJRadio, ActionListener listeners[], KeyStroke raccourcis[], int selected_index) {
		assert (nomJRadio.length == listeners.length && listeners.length == raccourcis.length) : "erreur taille tableau";
		ButtonGroup groupe = new ButtonGroup();

		for(int i=0;i<nomJRadio.length;i++)
		{
			JRadioButtonMenuItem radio = new JRadioButtonMenuItem(nomJRadio[i]);
			radio.setAccelerator(raccourcis[i]);
			radio.addActionListener(listeners[i]);
			groupe.add(radio);
			menu.get(index).add(radio);
			
			if(i==selected_index)
				radio.setSelected(true);
		}
	}
}

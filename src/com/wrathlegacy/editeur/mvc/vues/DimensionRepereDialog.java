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

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DimensionRepereDialog extends JDialog implements ActionListener {

	private boolean				ok					= false;
	private JButton				okBouton;
	private JTextField			txtLargeur, txtHauteur;
	
	public DimensionRepereDialog(Editeur edit) {
		super(edit, "Dimensions en tiles", true);
		setResizable(false);
		setSize(250, 100);

		Container contenu = getContentPane();
		contenu.setLayout(new FlowLayout());
		txtLargeur = new JTextField(5);
		contenu.add(txtLargeur);
		txtHauteur = new JTextField(5);
		contenu.add(txtHauteur);

		okBouton = new JButton("OK");
		okBouton.addActionListener(this);
		contenu.add(okBouton);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okBouton) {
			ok = true;
			setVisible(false);
		}
	}

	public Point lanceDialogue() {
		ok = false;
		setVisible(true);
		if(ok)
			return new Point(Integer.parseInt(txtLargeur.getText()), Integer.parseInt(txtHauteur.getText()));
		else
			return null;
	}
}

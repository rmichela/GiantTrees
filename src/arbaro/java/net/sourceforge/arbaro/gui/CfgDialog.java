/*
 * This copy of Arbaro is redistributed to you under GPLv3 or (at your option)
 * any later version. The original copyright notice is retained below.
 */
//  #**************************************************************************
//  #
//  #    Copyright (C) 2003-2006  Wolfram Diestel
//  #
//  #    This program is free software; you can redistribute it and/or modify
//  #    it under the terms of the GNU General Public License as published by
//  #    the Free Software Foundation; either version 2 of the License, or
//  #    (at your option) any later version.
//  #
//  #    This program is distributed in the hope that it will be useful,
//  #    but WITHOUT ANY WARRANTY; without even the implied warranty of
//  #    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  #    GNU General Public License for more details.
//  #
//  #    You should have received a copy of the GNU General Public License
//  #    along with this program; if not, write to the Free Software
//  #    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//  #
//  #    Send comments and bug fixes to diestel@steloj.de
//  #
//  #**************************************************************************/

package net.sourceforge.arbaro.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.sourceforge.arbaro.tree.Tree;

public class CfgDialog {
	JFrame frame;
	JPanel mainPanel;
	JComboBox formatBox;
	JFileChooser fileChooser;
	JTextField fileField;
	JTextField pathField;
	JTextField widthField;
	JTextField heightField;
	JTextField seedField;

	String fileSep = System.getProperty("file.separator");

	Config config;
	Workplace workplace;
	
	public CfgDialog(Workplace wplace, JFrame parent, Config cfg) {
		
		config = cfg;
		workplace = wplace;
		
		frame = new JFrame("Arbaro setup");
		frame.setIconImage(parent.getIconImage());
		
		fileChooser = new JFileChooser();
		// fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")"));
		
		createGUI();
		frame.setVisible(true);
	}
	
	void createGUI() {
		mainPanel = new JPanel();
		//panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		GridBagLayout grid = new GridBagLayout();
		mainPanel.setLayout(grid);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		
		// label constraints
		GridBagConstraints clabel = new GridBagConstraints();
		clabel.gridx = 0;
		clabel.anchor = GridBagConstraints.WEST;
		
		// text field constraints
		GridBagConstraints ctext = new GridBagConstraints();
		ctext.gridx = 1;
		ctext.ipady = 4;
		ctext.anchor = GridBagConstraints.WEST;
		ctext.insets = new Insets(1,5,1,5);
		
		// button constraints
		GridBagConstraints cbutton = new GridBagConstraints();
		cbutton.gridx = 2;
		cbutton.anchor = GridBagConstraints.WEST;
		
		int line=-1;
		JLabel label;
		
		// default export format
		clabel.gridy = ++line;
		label = new JLabel("Default export format:");
		grid.setConstraints(label,clabel);
		mainPanel.add(label);
		
		ctext.gridy = line;
		formatBox = new JComboBox(Tree.getOutputTypes());
		formatBox.setEditable(false);
		formatBox.setSelectedIndex(
				Integer.parseInt(config.getProperty("export.format","0")));
		grid.setConstraints(formatBox,ctext);
		mainPanel.add(formatBox);
		
		// default output path
		clabel.gridy = ++line;
		label = new JLabel("Output path:");
		grid.setConstraints(label,clabel);
		mainPanel.add(label);
		
		ctext.gridy = line;
		pathField = new JTextField(30);
		pathField.setText(config.getProperty("export.path",
				System.getProperty("user.dir")+fileSep+"pov"));
		grid.setConstraints(pathField,ctext);
		mainPanel.add(pathField);
		
		cbutton.gridy = line;
		JButton selectFile = new JButton("Choose...");
		selectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnVal = fileChooser.showSaveDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					pathField.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		});
		grid.setConstraints(selectFile,cbutton);
		mainPanel.add(selectFile);

		// povray executable  
		clabel.gridy = ++line;
		label = new JLabel("POVRay executable:");
		grid.setConstraints(label,clabel);
		mainPanel.add(label);
		
		ctext.gridy = line;
		fileField = new JTextField(30);
		fileField.setText(config.getProperty("povray.executable",
			Config.defaultPovrayExe()));
		grid.setConstraints(fileField,ctext);
		mainPanel.add(fileField);
		
		cbutton.gridy = line;
		selectFile = new JButton("Choose...");
		selectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnVal = fileChooser.showSaveDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					fileField.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		});
		grid.setConstraints(selectFile,cbutton);
		mainPanel.add(selectFile);

		// render width 
		clabel.gridy = ++line;
		label = new JLabel("Render width:");
		grid.setConstraints(label,clabel);
		mainPanel.add(label);
		
		ctext.gridy = line;
		widthField = new JTextField(10);
		widthField.setAlignmentX(JTextField.RIGHT_ALIGNMENT);
		widthField.setText(config.getProperty("povray.width","400"));
		grid.setConstraints(widthField,ctext);
		mainPanel.add(widthField);

		// render height
		clabel.gridy = ++line;
		label = new JLabel("Render height:");
		grid.setConstraints(label,clabel);
		mainPanel.add(label);
		
		ctext.gridy = line;
		heightField = new JTextField(10);
		heightField.setAlignmentX(JTextField.RIGHT_ALIGNMENT);
		heightField.setText(config.getProperty("povray.height","600"));
		grid.setConstraints(heightField,ctext);
		mainPanel.add(heightField);

		// random seed
		clabel.gridy = ++line;
		label = new JLabel("Default seed:");
		grid.setConstraints(label,clabel);
		mainPanel.add(label);
		
		ctext.gridy = line;
		seedField = new JTextField(10);
		seedField.setAlignmentX(JTextField.RIGHT_ALIGNMENT);
		seedField.setText(config.getProperty("tree.seed","13"));
		grid.setConstraints(seedField,ctext);
		mainPanel.add(seedField);
		
		
		// buttons
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new OKButtonListener());
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		
		JPanel buttons = new JPanel();
		buttons.add(okButton);
		buttons.add(cancelButton);
		
		cbutton.gridx = 1;
		cbutton.gridy = 7;
		cbutton.anchor = GridBagConstraints.CENTER;
		grid.setConstraints(buttons,cbutton);
		mainPanel.add(buttons);
		
		frame.getContentPane().add(mainPanel);
		frame.pack();
	}
	
	class OKButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			config.setProperty("export.format", Integer.toString(formatBox.getSelectedIndex()));
			config.setProperty("export.path", pathField.getText());
			config.setProperty("povray.executable",fileField.getText());
			config.setProperty("povray.width",widthField.getText());
			config.setProperty("povray.height",heightField.getText());
			config.setProperty("tree.seed",seedField.getText());
			frame.dispose();
			try {
				config.store();
				workplace.applyConfig();
			} catch (Exception err) {
				JOptionPane.showMessageDialog(frame,err.getMessage(),
						"Setup Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
};






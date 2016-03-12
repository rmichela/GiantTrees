/*
 * This copy of Arbaro is redistributed to you under GPLv3 or (at your option)
 * any later version. The original copyright notice is retained below.
 */
/*
 * Portions of this file are
 * Copyright (C) 2016 Ronald Jack Jenkins Jr.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import net.sourceforge.arbaro.tree.Tree;

public class ExportDialog {
	final static int INTERVAL = 500; // 0.5 sec
	// static Tree tree;
	JFrame frame;
	Config config;
	boolean render;

	JPanel mainPanel;
	Progressbar progressbar;
	
	Tree tree;

	File treefile = null;
	JFileChooser fileChooser;
	JFileChooser sceneFileChooser;
	JFileChooser renderFileChooser;
	JTabbedPane tabbedPane;
	JComboBox formatBox;
	JCheckBox sceneCheckbox;
	JCheckBox renderCheckbox;
	JCheckBox uvStemsCheckbox;
	JCheckBox uvLeavesCheckbox;
	JTextField seedField = new JTextField(6);
	JTextField smoothField = new JTextField(6);
	JTextField fileField;
	JTextField sceneFileField;
	JTextField renderFileField;
	JTextField widthField=new JTextField(6);
	JTextField heightField=new JTextField(6);
	JButton selectSceneFile;
	JButton selectRenderFile;
	Timer timer;
	TreeCreationTask treeCreationTask;
	JButton startButton;
	JButton cancelButton;
	
	String fileSep = System.getProperty("file.separator");
	
	public ExportDialog(JFrame parent, Tree tr, Config cfg, boolean rend) {
		
		tree = tr;
		config = cfg;
		render = rend;
		
		frame = new JFrame("Create and export tree");
		frame.setIconImage(parent.getIconImage());
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// FIXME: make this filechoosers static?
		fileChooser = new JFileChooser();

		// FIXME use path from preferences
		fileChooser.setCurrentDirectory(new File(tree.getOutputPath()));
//		System.getProperty("user.dir")+fileSep+"pov"));
		sceneFileChooser = new JFileChooser();
		sceneFileChooser.setCurrentDirectory(new File(tree.getOutputPath()));
//				System.getProperty("user.dir")+fileSep+"pov"));
		renderFileChooser = new JFileChooser();
		renderFileChooser.setCurrentDirectory(new File(tree.getOutputPath()));
//		System.getProperty("user.dir")+fileSep+"pov"));
		
		timer = new Timer(INTERVAL, new TimerListener());
		treeCreationTask = new TreeCreationTask(config);
		
		createGUI();
		frame.setVisible(true);
	}
	
	void createGUI() {
		tabbedPane = new JTabbedPane();

		JComponent exportPanel = createExportPanel();
		tabbedPane.addTab("Export", null, exportPanel,
		                  "Export options");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent renderPanel = createRenderPanel();
		tabbedPane.addTab("Render", null, renderPanel,
        "Render options");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);

		if (render) {
			tabbedPane.setSelectedIndex(1);
		}
		formatSettings(formatBox.getSelectedIndex());
		
		// buttons
		startButton = new JButton("Start");
		startButton.addActionListener(new StartButtonListener());
		
		cancelButton = new JButton("Close");
		cancelButton.addActionListener(new CancelButtonListener());
		
		JPanel buttons = new JPanel();
		buttons.add(startButton);
		buttons.add(cancelButton);
		
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.setLayout(new BorderLayout());
		panel.add(tabbedPane,BorderLayout.CENTER);
		panel.add(buttons,BorderLayout.SOUTH);
		
		// add panel to content pane
		Container contentPane = frame.getContentPane(); 
		contentPane.add(panel,BorderLayout.CENTER);
		
		// add status line
		progressbar = new Progressbar();
		progressbar.setVisible(false);
		contentPane.add(progressbar,BorderLayout.PAGE_END);
		
		frame.pack();
	}

	private void formatSettings(int outputFormat) {
		switch (outputFormat) {
		case Tree.MESH: 
			fileField.setText(fileChooser.getCurrentDirectory().getPath()
					+fileSep+tree.getParam("Species")+".inc");
		sceneCheckbox.setEnabled(true);
		renderCheckbox.setEnabled(true);
		smoothField.setEnabled(true);
		tabbedPane.setEnabledAt(1,true);
		uvStemsCheckbox.setEnabled(true);
		uvLeavesCheckbox.setEnabled(true);
		break;
		
		case Tree.CONES: 
			fileField.setText(fileChooser.getCurrentDirectory().getPath()
					+fileSep+tree.getParam("Species")+".inc");
		sceneCheckbox.setEnabled(true);
		renderCheckbox.setEnabled(true);
		smoothField.setEnabled(false);
		tabbedPane.setEnabledAt(1,true);
		uvStemsCheckbox.setEnabled(false);
		uvLeavesCheckbox.setEnabled(false);
		break;
		
		case Tree.DXF: 
			fileField.setText(fileChooser.getCurrentDirectory().getPath()
					+fileSep+tree.getParam("Species")+".dxf");
		sceneCheckbox.setSelected(false);
		sceneCheckbox.setEnabled(false);
		renderCheckbox.setSelected(false);
		renderCheckbox.setEnabled(false);
		smoothField.setEnabled(true);
		tabbedPane.setEnabledAt(1,false);
		uvStemsCheckbox.setEnabled(false);
		uvLeavesCheckbox.setEnabled(false);
		break;
		
		case Tree.OBJ: 
			fileField.setText(fileChooser.getCurrentDirectory().getPath()
					+fileSep+tree.getParam("Species")+".obj");
		sceneCheckbox.setSelected(false);
		sceneCheckbox.setEnabled(false);
		renderCheckbox.setSelected(false);
		renderCheckbox.setEnabled(false);
		smoothField.setEnabled(true);
		tabbedPane.setEnabledAt(1,false);
		uvStemsCheckbox.setEnabled(true);
		uvLeavesCheckbox.setEnabled(true);
		break;
		
		}
	}

	
	JPanel createExportPanel() {
		
		JPanel panel = new JPanel();
		
		// create GridBagLayout
		GridBagLayout grid = new GridBagLayout();
		panel.setLayout(grid);
		
		panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
//		panel.setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createRaisedBevelBorder(),
//				BorderFactory.createEmptyBorder(30,30,30,30)));
		
		// Constraints for the left labels
		GridBagConstraints clabel = new GridBagConstraints();
		clabel.gridx = 0;
		clabel.anchor = GridBagConstraints.WEST;
		
		// Constraint for the input fields
		GridBagConstraints ctext = new GridBagConstraints();
		ctext.gridx = 1;
		ctext.ipady = 4;
		ctext.anchor = GridBagConstraints.WEST;
		ctext.insets = new Insets(1,5,1,5);
		
		// Constraint for the choose buttons
		GridBagConstraints cbutton = new GridBagConstraints();
		cbutton.gridx = 2;
		cbutton.anchor = GridBagConstraints.WEST;
		
		JLabel label;
		int line=-1;
		
		// export format
		clabel.gridy = ++line;
		label = new JLabel("Export format:");
		grid.setConstraints(label,clabel);
		panel.add(label);
		
		ctext.gridy = line;
		formatBox = new JComboBox(Tree.getOutputTypes());
		int format = Integer.parseInt(config.getProperty("export.format","0")); 
		if (render) {
			for (int i=formatBox.getItemCount()-1; i>=Tree.DXF; i--) {
				formatBox.removeItemAt(i);
			}
			if (format>=Tree.DXF) format=Tree.MESH;
		}
		formatBox.setEditable(false);
		formatBox.setSelectedIndex(format);
		
		formatBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				formatSettings(formatBox.getSelectedIndex());
			}
		});
		grid.setConstraints(formatBox,ctext);
		panel.add(formatBox);
		
		
		// export file path and name 
		clabel.gridy = ++line;
		label = new JLabel("Export to file:");
		grid.setConstraints(label,clabel);
		panel.add(label);
		
		ctext.gridy = line;
		fileField = new JTextField(30);
		fileField.setText(fileChooser.getCurrentDirectory().getPath()
				+fileSep+tree.getParam("Species")+".inc");
		fileField.setMinimumSize(new Dimension(250,19));
		grid.setConstraints(fileField,ctext);
		panel.add(fileField);
		
		cbutton.gridy = line;
		JButton selectFile = new JButton("Choose...");
		selectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnVal = fileChooser.showSaveDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					fileField.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		});
		grid.setConstraints(selectFile,cbutton);
		panel.add(selectFile);
		
		// UV-coordinates
		clabel.gridy = ++line;
		label = new JLabel("UV-coordinates:");
		grid.setConstraints(label,clabel);
		panel.add(label);
		
		ctext.gridy = line;
		uvStemsCheckbox = new JCheckBox("for Stems");
		uvStemsCheckbox.setSelected(tree.getOutputStemUVs());
		uvLeavesCheckbox = new JCheckBox("for Leaves");
		uvLeavesCheckbox.setSelected(tree.getOutputLeafUVs());
		JPanel uv = new JPanel();
		uv.add(uvStemsCheckbox);
		uv.add(uvLeavesCheckbox);
		grid.setConstraints(uv,ctext);
		panel.add(uv);
		
		// POV scene file 
		clabel.gridy = ++line;
		sceneCheckbox = new JCheckBox("POV Scene file:");
		sceneCheckbox.setSelected(render);
		sceneCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				sceneFileField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				selectSceneFile.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				renderCheckbox.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		grid.setConstraints(sceneCheckbox,clabel);
		panel.add(sceneCheckbox);
		
		ctext.gridy = line;
		sceneFileField = new JTextField(30);
		sceneFileField.setEnabled(sceneCheckbox.isSelected());
		sceneFileField.setText(sceneFileChooser.getCurrentDirectory().getPath()
				+fileSep+tree.getParam("Species")+".pov");
		sceneFileField.setMinimumSize(new Dimension(250,19));
		grid.setConstraints(sceneFileField,ctext);
		panel.add(sceneFileField);
		
		cbutton.gridy = line;
		selectSceneFile = new JButton("Choose...");
		selectSceneFile.setEnabled(sceneCheckbox.isSelected());
		selectSceneFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnVal = sceneFileChooser.showSaveDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					sceneFileField.setText(sceneFileChooser.getSelectedFile().getPath());
				}
			}
		});
		grid.setConstraints(selectSceneFile,cbutton);
		panel.add(selectSceneFile);
		
		// random seed
		clabel.gridy = ++line;
		label = new JLabel("Seed:");
		grid.setConstraints(label,clabel);
		panel.add(label);
		
		ctext.gridy = line;
		seedField.setText(""+tree.getSeed()); 
		seedField.setMinimumSize(new Dimension(80,19));
		grid.setConstraints(seedField,ctext);
		panel.add(seedField);
		
		// smooth value
		clabel.gridy = ++line;
		label = new JLabel("Smooth value:");
		grid.setConstraints(label,clabel);
		panel.add(label);
		
		ctext.gridy = line;
		smoothField.setText(tree.getParam("Smooth").toString());
		smoothField.setMinimumSize(new Dimension(80,19));
		grid.setConstraints(smoothField,ctext);
		panel.add(smoothField);

		return panel;
	}
	

	JPanel createRenderPanel() {
		JPanel panel = new JPanel();
		
		// create GridBagLayout
		GridBagLayout grid = new GridBagLayout();
		panel.setLayout(grid);
		panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
//		panel.setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createRaisedBevelBorder(),
//				BorderFactory.createEmptyBorder(30,30,30,30)));
		
		// Constraints for the left labels
		GridBagConstraints clabel = new GridBagConstraints();
		clabel.gridx = 0;
		clabel.anchor = GridBagConstraints.WEST;
		
		// Constraint for the input fields
		GridBagConstraints ctext = new GridBagConstraints();
		ctext.gridx = 1;
		ctext.ipady = 4;
		ctext.anchor = GridBagConstraints.WEST;
		ctext.insets = new Insets(1,5,1,5);
		
		// Constraint for the choose buttons
		GridBagConstraints cbutton = new GridBagConstraints();
		cbutton.gridx = 2;
		cbutton.anchor = GridBagConstraints.WEST;

		JLabel label;
		int line=-1;
		
		// path and filename of rendered image
		clabel.gridy = ++line;
		renderCheckbox = new JCheckBox("Render scene to:");
		renderCheckbox.setSelected(render);
		renderCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				renderFileField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				selectRenderFile.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		grid.setConstraints(renderCheckbox,clabel);
		panel.add(renderCheckbox);
		
		ctext.gridy = line;
		renderFileField = new JTextField(30);
		renderFileField.setEnabled(renderCheckbox.isSelected());
		renderFileField.setText(renderFileChooser.getCurrentDirectory().getPath()
				+fileSep+tree.getParam("Species")+".png");
		renderFileField.setMinimumSize(new Dimension(250,19));
		grid.setConstraints(renderFileField,ctext);
		panel.add(renderFileField);
		
		cbutton.gridy = line;
		selectRenderFile = new JButton("Choose...");
		selectRenderFile.setEnabled(renderCheckbox.isSelected());
		selectRenderFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnVal = renderFileChooser.showSaveDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					renderFileField.setText(renderFileChooser.getSelectedFile().getPath());
				}
			}
		});
		grid.setConstraints(selectRenderFile,cbutton);
		panel.add(selectRenderFile);
		
		// render width
		clabel.gridy = ++line;
		label = new JLabel("Image width:");
		grid.setConstraints(label,clabel);
		panel.add(label);
		
		ctext.gridy = line;
		widthField.setText(""+tree.getRenderW());
		widthField.setMinimumSize(new Dimension(80,19));
		grid.setConstraints(widthField,ctext);
		panel.add(widthField);
		
		// render height
		clabel.gridy = ++line;
		label = new JLabel("Image height:");
		grid.setConstraints(label,clabel);
		panel.add(label);
		
		ctext.gridy = line;
		heightField.setText(""+tree.getRenderH());
		heightField.setMinimumSize(new Dimension(80,19));
		grid.setConstraints(heightField,ctext);
		panel.add(heightField);
		
		return panel;
	}
	
	class StartButtonListener implements ActionListener {
		// creates the tree and writes to file when button pressed
		public void actionPerformed(ActionEvent e) {
			// System.err.println("seed "+seedField.getText());
			// System.err.println("smooth "+smoothField.getText());
			// System.err.println("0Branches "+tree.params.getParam("0Branches").getValue());
			
			// get seed, output parameters
			
			try{
				tree.setSeed(Integer.parseInt(seedField.getText()));
				tree.setParam("Smooth",smoothField.getText());
				tree.setRenderW(Integer.parseInt(widthField.getText()));
				tree.setRenderH(Integer.parseInt(heightField.getText()));
				tree.setOutputType(formatBox.getSelectedIndex());
				tree.setOutputStemUVs(uvStemsCheckbox.isSelected());
				tree.setOutputLeafUVs(uvLeavesCheckbox.isSelected());
				//FIXME fileChooser.getPath() ???
				//tree.setOutputPath(fileField.getText());
			} catch (Exception err) {
				err.printStackTrace();
			}
			// setup progress dialog
//			progressMonitor = new ProgressMonitor(frame,"","",0,100);
//			progressMonitor.setProgress(0);
//			progressMonitor.setMillisToDecideToPopup(1*INTERVAL);
			
			progressbar.setVisible(true);
			
			startButton.setEnabled(false);
			cancelButton.setText("Cancel");
			
			// start tree creation
			System.err.println("start creating tree and writing to "+fileField.getText());
			
			File incfile = new File(fileField.getText());
			File povfile = null;
			if (sceneCheckbox.isSelected()) povfile = new File(sceneFileField.getText());
			String imgFilename = null;
			if (renderCheckbox.isSelected()) imgFilename = renderFileField.getText();
			treeCreationTask.start(tree,incfile,povfile,imgFilename); //fileChooser.getSelectedFile());
			timer.start();
		}
	}
	
	class CancelButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			if (treeCreationTask.notActive())
				frame.dispose();
			else {
				treeCreationTask.stop();
//				progressMonitor.close();
				Toolkit.getDefaultToolkit().beep();
				timer.stop();
				startButton.setEnabled(true);
				startButton.setText("Restart");
				cancelButton.setText("Close");
				progressbar.setVisible(false);
			}
		}
	}
	
	class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			// System.err.println("timer event "+createTreeTask.getProgress());
			if (/*progressMonitor.isCanceled() ||*/ treeCreationTask.notActive()) 
			{
				treeCreationTask.stop();
//				progressMonitor.close();
				progressbar.setProgress(100);
				progressbar.setNote("Ready");
				Toolkit.getDefaultToolkit().beep();
				timer.stop();
				startButton.setEnabled(true);
				startButton.setText("Restart");
				cancelButton.setText("Close");
			} else {
//				progressMonitor.setProgress((int)(100*createTreeTask.getProgress()));
//				progressMonitor.setNote(createTreeTask.getProgressMsg());
				
				progressbar.setProgress(treeCreationTask.getProgress());
				progressbar.setNote(treeCreationTask.getProgressMsg());
			}
		}
	}
}

class Progressbar extends JPanel {
	JLabel label;
	JProgressBar progressbar;
	
	public Progressbar() {
		super(new BorderLayout());
		((BorderLayout)getLayout()).setHgap(20);
		
		setBorder(BorderFactory.createEmptyBorder(10,5,5,5));
		
		label = new JLabel("Creating tree structure");
    	Font font = label.getFont().deriveFont(Font.PLAIN,12);
    	label.setFont(font);
		add(label,BorderLayout.WEST);
    	
		progressbar = new JProgressBar(0,100);
		progressbar.setValue(-1);
		progressbar.setStringPainted(true);
		progressbar.setString("");
		progressbar.setIndeterminate(true);
		add(progressbar,BorderLayout.CENTER);
	}
	
	public void setProgress(int val) {
//		System.err.println("setProg "+val);
		if (val<=0) {
			progressbar.setIndeterminate(true);
			progressbar.setString("");
		} else {
			progressbar.setIndeterminate(false);
			progressbar.setValue(val);
			progressbar.setString(""+val+"%");
		}
	}
	
	public void setNote(String note) {
		label.setText(note);
	}
}

/* this class actually creates the tree and saves it to a POV file
 */
class TreeCreationTask {
	Tree tmptree;
	PrintWriter writer;
	File scene_file = null;
	PrintWriter scenewriter = null;
	String renderFilename = null;
	boolean isNotActive;
	String povrayexe;
	
	final class TreeWorker extends SwingWorker<DoTask, Void> {
	  @Override
		protected DoTask doInBackground() {
			return new DoTask();
		}
		
		@Override
		protected void done() {
			// may be this should be done in DoTask instead?
			isNotActive = true;
		}
	};
	
	TreeWorker worker;
	
	public TreeCreationTask(Config config) {
		povrayexe = config.getProperty("povray.executable");
		if (povrayexe == null) {
			System.err.println("Warning: Povray executable not set up, trying \""+
			Config.defaultPovrayExe()+"\" without path");
			povrayexe = Config.defaultPovrayExe();
		}
		isNotActive=true;
	};
	
	public void start(Tree tree, File outFile, File sceneFile, String imgFilename) {
		// create new Tree copying the parameters of tree
		try {
			tmptree = new Tree(tree);
			tmptree.params.verbose=false;
			
			writer = new PrintWriter(new FileWriter(outFile)); 
			if (sceneFile != null) {
				scene_file = sceneFile;
				scenewriter = new PrintWriter(new FileWriter(sceneFile)); 
			}
			renderFilename = imgFilename;
			
			isNotActive = false;
			
			worker = new TreeWorker();
			worker.execute();
			
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace(System.err);
		}
	}
	
	int getProgress() {
		return tmptree.getProgress().getPercent();
	}
	
	String getProgressMsg() {
		return tmptree.getProgress().getPhase();
	}
	
	void stop() {
		if (worker != null) {
			System.err.println("stop tree creation...");
			worker.cancel(true);
			isNotActive = true;
			worker = null;
		}
	}
	
	boolean notActive() {
		return isNotActive;
	}
	
	class DoTask {
		void render() {
			try {
				
				String [] povcmd = { povrayexe,
						"+L"+scene_file.getParent(),
						"+w"+tmptree.getRenderW(),
						"+h"+tmptree.getRenderH(),
						"+o"+renderFilename,
						scene_file.getPath()};
				System.err.println(povcmd);
				System.err.println("rendering...");
				Process povProc = Runtime.getRuntime().exec(povcmd);
				BufferedReader pov_in = new BufferedReader(
						new InputStreamReader(povProc.getErrorStream()));
				
				String str;
				while ((str = pov_in.readLine()) != null) {
					System.err.println(str);
				}
				
			} catch (Exception e) {
				System.err.println(e);
				e.printStackTrace(System.err);
			}
		}
		DoTask() {
			try {
				tmptree.make();
				tmptree.output(writer);
				if (scenewriter != null) tmptree.outputScene(scenewriter);
				if (renderFilename != null && renderFilename.length()>0) {
					tmptree.getProgress().beginPhase("Rendering tree",-1);
					render();
				}
			} catch (Exception err) {
				System.err.println(err);
				err.printStackTrace(System.err);
			}
		}
	};
}










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

import java.awt.Color;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import net.sourceforge.arbaro.params.AbstractParam;


class GroupNode extends DefaultMutableTreeNode {
	String groupName;
	String groupLabel;
	int groupLevel;
	
	public GroupNode(String name, String label, int level) {
		super(label);
		groupName=name;
		groupLabel=label;
		groupLevel = level;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public int getGroupLevel() {
		return groupLevel;
	}

}

/**
 * @author wdiestel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class ParamGroupsView extends JTree {
	
	
	
//	private final static String[] levels = {"general parameters",
//			"first level", "second level", "third level", "fourth level"};
	
	static DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
	
	static final Color bgClr = new Color(250,250,240);
	
	// ParamValueTable paramValueEditor;
	ChangeEvent changeEvent;
	
	/**
	 * 
	 */
	public ParamGroupsView() {
		super(root);
		createNodes();
		// setMinimumSize(new Dimension(100,100));
		setBackground(bgClr);
		
		setRootVisible(false);
		setShowsRootHandles(true);
		setExpandsSelectedPaths(true);
		getSelectionModel().setSelectionMode
	            (TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		//ImageIcon leafIcon = createImageIcon("images/middle.gif");
		// show no icons in the groups tree
		DefaultTreeCellRenderer renderer = 
			new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		renderer.setBackground(bgClr);
		renderer.setBackgroundNonSelectionColor(bgClr);
		// renderer.setBackgroundSelectionColor(bgClr);
		setCellRenderer(renderer);
		//}
		
		addTreeSelectionListener(
				new TreeSelectionListener() {
					public void valueChanged(TreeSelectionEvent e) {
						DefaultMutableTreeNode node = 
							(DefaultMutableTreeNode)
							getLastSelectedPathComponent();
						
						if (node == null) return;
						
//						if (node.isLeaf()) {
							fireStateChanged();
//						}
					}
				}
				
		);
	}
	
	public String getGroupName() throws Exception {
		DefaultMutableTreeNode node = 
			(DefaultMutableTreeNode)
			getLastSelectedPathComponent();
		if (!node.isRoot()) {
			return ((GroupNode)node).getGroupName();
		}
		// no group selected
		throw new Exception("no group selected");
	}
	
	public int getGroupLevel() throws Exception {
		// FIXME: higher nodes could return the level too
		DefaultMutableTreeNode node = 
			(DefaultMutableTreeNode)
			getLastSelectedPathComponent();
		if (!node.isRoot()) {
			return ((GroupNode)node).getGroupLevel();
		}
		// no group selected
		throw new Exception("no group selected");
	}	
	
	private void createNodes() {
		
		GroupNode general = new GroupNode("","General",AbstractParam.GENERAL);
		root.add(general);
		
		GroupNode firstGroup =
			addGroup(general,"SHAPE","Tree shape",AbstractParam.GENERAL);
		
		addGroup(general,"TRUNK","Trunk radius",AbstractParam.GENERAL);
		addGroup(general,"LEAVES","Leaves",AbstractParam.GENERAL);
		//addGroup(general,"LEAVESADD","Leaf details",AbstractParam.GENERAL);
		addGroup(general,"PRUNING","Pruning/Envelope",AbstractParam.GENERAL);
		//addGroup(general,"MISC","Miscellaneous",AbstractParam.GENERAL);
		addGroup(general,"QUALITY","Quality",AbstractParam.GENERAL);
		
		for (int i=0; i<4; i++) {
			String lName = "Level "+i;
			if (i==0) lName += " (trunk)";
			GroupNode level = new GroupNode("",lName,i);
			root.add(level);
			
			addGroup(level,"LENTAPER","Length and taper",i);
			addGroup(level,"CURVATURE","Curvature",i);
			addGroup(level,"SPLITTING","Splitting",i);
			addGroup(level,"BRANCHING","Branching",i);
		}
		
		//scrollPathToVisible(new TreePath(firstGroup.getPath()));
		setSelectionPath(new TreePath(firstGroup.getPath()));
//		expandPath(root.getPath());
	}
	
	private GroupNode addGroup(DefaultMutableTreeNode parent,String groupName, String groupLabel,
			int groupLevel) {
		GroupNode groupNode = new GroupNode(groupName,groupLabel,groupLevel);
		// FIXME: add more code here
		parent.add(groupNode);
		return groupNode;
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	
	protected void fireStateChanged() {
		Object [] listeners = listenerList.getListenerList();
		for (int i = listeners.length -2; i>=0; i-=2) {
			if (listeners[i] == ChangeListener.class) {
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
			}
		}
	}
	
}

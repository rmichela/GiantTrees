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

import net.sourceforge.arbaro.params.*;
import net.sourceforge.arbaro.tree.Tree;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.TreeMap;




public final class ParamValueTable extends JPanel {
	
	JTable table;
	HelpInfo helpInfo;
	
	Tree tree;
	
	String groupName;
	int groupLevel;
	ParamTableModel tableModel;
	
	static final Color bgClr = new Color(250,250,240);
	
	ChangeEvent changeEvent;
	
	//	class CellRenderer extends DefaultTableCellRenderer {
	//	    public CellRenderer() { 
	//	    	super(); 
	//	    }
	//
	//	    public void setValue(Object value) {
	//	        setText((value == null) ? "" : 
	//	        	((AbstractParam)value).getValue());
	//	    }
	//	}

	
	/********************** LeafShapeBox *****************************/

	class LeafShapeBox extends JComboBox {
		
		public LeafShapeBox() {
			super();
			setEditable(true);
			
			//LeafShapeParam param = (LeafShapeParam)tree.getParam("LeafShape");
			
			//fill
			String[] items = LeafShapeParam.values();
			
			for (int i=0; i<items.length; i++) {
				addItem(items[i]);
			}
		}
		
		public void setValue(AbstractParam p) {
			// select item
			for (int i=0; i<getItemCount(); i++) {
				if (getItemAt(i).equals(p.getValue())) {
					setSelectedIndex(i);
					return;
				}
			}
		}
		
		public String getValue() {
			return (String)getSelectedItem();
		}

	}

    /********************** LeafShapeBox *****************************/

    class WoodTypeBox extends JComboBox {

        public WoodTypeBox() {
            super();
            setEditable(true);

            //LeafShapeParam param = (LeafShapeParam)tree.getParam("LeafShape");

            //fill
            String[] items = WoodTypeParam.values();

            for (int i=0; i<items.length; i++) {
                addItem(items[i]);
            }
        }

        public void setValue(AbstractParam p) {
            // select item
            for (int i=0; i<getItemCount(); i++) {
                if (getItemAt(i).equals(p.getValue())) {
                    setSelectedIndex(i);
                    return;
                }
            }
        }

        public String getValue() {
            return (String)getSelectedItem();
        }

    }
	
	/********************** ShapeBox *****************************/

	class ShapeBox extends JComboBox {
		
		//	ParamFrame parent;
//		ParamValueTable parent;
		
		//Integer [] values;
//		final String[] items = { "conical", "spherical", "hemispherical", "cylindrical", 
//				"tapered cylindrical","flame","inverse conical","tend flame",
//		"envelope" };
//		
//		final String[] values = {"0","1","2","3","4","5","6","7","8"};

		ImageIcon [] shapeIcons;
		
		/** Returns an ImageIcon, or null if the path was invalid. */
		protected ImageIcon createImageIcon(String path,
				String description) {
			java.net.URL imgURL = ShapeBox.class.getResource(path);
			if (imgURL != null) {
				return new ImageIcon(imgURL, description);
			} else {
				System.err.println("Couldn't find file: " + path);
				return null;
			}
		}
		
		public ShapeBox(ParamValueTable pnt) {
			super();
//			parent = pnt;
			ShapeRenderer renderer= new ShapeRenderer();
			//renderer.setPreferredSize(new Dimension(200, 130));
			setRenderer(renderer);
			//ShapeParam param = (ShapeParam)tree.getParam("Shape");
			
			//fill
			String[] items = ShapeParam.values();
			shapeIcons = new ImageIcon[items.length];
			for (int i=0; i<items.length; i++) {
				// values[i] = new Integer(i);
				shapeIcons[i] = createImageIcon("images/shape"+i+".png",items[i]);
				addItem(""+i);
			}
		}
		
		public void setValue(AbstractParam p) {
			// select item
			setSelectedIndex(((IntParam)p).intValue());
		}
		
		public String getValue() {
			return ""+getSelectedIndex();
		}
	    
		class ShapeRenderer extends JLabel implements ListCellRenderer {
			public ShapeRenderer() {
				setOpaque(true);
			}
			public Component getListCellRendererComponent(
					JList list,
					Object value,
					int index,
					boolean isSelected,
					boolean cellHasFocus)
			{
				int myIndex = Integer.parseInt(value.toString());
				
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				
				//Set the icon and text.  If icon was null, say so.
				ImageIcon icon;
//				if (myIndex>=0 && myIndex<9) {
					icon = shapeIcons[myIndex];
					setIcon(icon);
					setText(icon.getDescription());
//				}; 
				
				return this;
			}
		};
		
	};
	
	
	class CellEditor extends AbstractCellEditor implements TableCellEditor {
		AbstractParam param;
		JTextField paramField;
		ShapeBox shapeBox;
        WoodTypeBox woodTypeBox;
		LeafShapeBox leafShapeBox;
		JComponent editor;
		
		public CellEditor(ParamValueTable parent) {
			super();
			paramField = new JTextField();
			// paramField.setHorizontalAlignment(JTextField.RIGHT);
			
			shapeBox = new ShapeBox(parent);
			shapeBox.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			fireEditingStopped();
	    		}
	    	});

            woodTypeBox = new WoodTypeBox();
            woodTypeBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
			
			leafShapeBox = new LeafShapeBox();
			leafShapeBox.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			fireEditingStopped();
	    		}
	    	});
			
		}
		
		public Object getCellEditorValue() {
			try {
				if (editor == shapeBox) {
					param.setValue(shapeBox.getValue());
				} else if (editor == leafShapeBox) {
					param.setValue(leafShapeBox.getValue());
                } else if (editor == woodTypeBox) {
                    param.setValue(woodTypeBox.getValue());
				} else { 
					param.setValue(paramField.getText());
				}
			} catch (Exception err) {
				System.err.println(err);
				showError(err);
			}

			return param;
		}
		
		public Component getTableCellEditorComponent(JTable table,
				Object value,
				boolean isSelected,
				int row,
				int column) {
			//			currentColor = (Color)value;
			param = (AbstractParam)value;

			if (param.getName().equals("Shape")) {
				shapeBox.setValue(param);
				editor = shapeBox;
			} else if (param.getName().equals("LeafShape")) {
				leafShapeBox.setValue(param);
				editor = leafShapeBox;
            } else if (param.getName().equals("WoodType")) {
                woodTypeBox.setValue(param);
                editor = woodTypeBox;
			} else {
				paramField.setText(param.toString());
				paramField.selectAll();
				editor = paramField;
			}
			return editor;
		}
	}
	
	class CellRenderer extends DefaultTableCellRenderer {
	    public CellRenderer() { super(); }

	    public void setValue(Object value) {
	    	// alignment for parameter type
	    	if (value.getClass() == ShapeParam.class) {
	    		setHorizontalAlignment(LEFT);
	    		setText(""
	    				+((ShapeParam)value).intValue()
						+ " - "+value.toString());
	    	} else if (value.getClass() == LeafShapeParam.class) {
	    		setHorizontalAlignment(LEFT);
	    		setText(value.toString());
            } else if (value.getClass() == WoodTypeParam.class) {
                setHorizontalAlignment(LEFT);
                setText(value.toString());
	    	} else if (value.getClass() == StringParam.class) {
	    		setHorizontalAlignment(LEFT);
	    		setText(value.toString());
	    	} else {
	    		setHorizontalAlignment(RIGHT);
	    		setText(value.toString());
	    	}
	    	
	    	this.setEnabled(((AbstractParam)value).getEnabled());
	    }
	}

	
	class ParamTableModel extends AbstractTableModel {
		public int getColumnCount() { return 2; }
		public int getRowCount() { 
			TreeMap params = tree.getParamGroup(groupLevel,groupName);
			return params.size();
		}
		public Object getValueAt(int row, int col) {
			// FIXME: maybe the params should be stored directly in the model
			
			TreeMap params = tree.getParamGroup(groupLevel,groupName);
			int r = 0;
			for (Iterator e=params.values().iterator(); e.hasNext();) {
				AbstractParam p = (AbstractParam)e.next();
				if (row==r++) { 
					if (col==0) return p.getName();
					else return p;
					
					//					panl.add(Box.createHorizontalGlue());
					//					if (p.getName().equals("Shape")) {
					//						// create combo box for Shape param
					//						panl.add(Box.createRigidArea(new Dimension(7,0)));
					//						ShapeBox sh = new ShapeBox(parent,p); 
					//						sh.addFocusListener(groupListener);
					//						panl.add(sh);
					
					
				}
			}
			return ""; // if not found
			
			//			} else {
			//				// create text field
			//				ParamField pfield = new ParamField(parent,6,p);
			//				pfield.addFocusListener(groupListener);
			//				panl.add(pfield);
			//			}
			//			add(panl);
		}
		
		public void setValueAt(Object value, int row, int col) {
			noError();
			
			// FIXME: maybe the params should be stored directly in the model

//			if (col==1) {
//				TreeMap params = new TreeMap(tree.getParamGroup(groupLevel,groupName));
//				int r = 0;
//				for (Iterator e=params.values().iterator(); e.hasNext();) {
//					AbstractParam p = (AbstractParam)e.next();
//					if (row==r++) try { 
//						p.setValue(value.toString());
//					} catch (Exception err) {
//						System.err.println(err);
//						showError(err);
//					}
//				}
//			}
			try {
				// enable/disable
				tableModel.fireTableDataChanged();
				// propagate change to other components, e.g. the preview
				fireStateChanged();
			} catch (Exception e) {
				if (e.getClass()==ParamError.class) {
					System.err.println(e);
					showError(e);
				} else {
					System.err.println(e);
					e.printStackTrace();
				}
			}
		}
		
		public boolean isCellEditable(int row, int col) { 
			return (col==1) && ((AbstractParam)getValueAt(row,col)).getEnabled(); 
		}	
		
		//		public Class getColumnClass(int c) {
		//	        return getValueAt(0, c).getClass();
		//	    }
		
	};
	
	
	public ParamValueTable(Tree theTree) {
		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder());
		setBackground(bgClr);
		
		tree = theTree;
		
		tableModel = new ParamTableModel();
		table = new JTable(tableModel);
		table.setBackground(bgClr);
		table.setRowHeight((int)(table.getRowHeight()*1.3));
		
		TableColumn paramColumn = table.getColumnModel().getColumn(0);
		paramColumn.setHeaderValue("Parameter");
		
		TableColumn valueColumn = table.getColumnModel().getColumn(1);
		valueColumn.setHeaderValue("Value");
		
		valueColumn.setCellEditor(new CellEditor(this));
		valueColumn.setCellRenderer(new CellRenderer());
		//		valueColumn.setCellRenderer(new CellRenderer());
		
		//		FIXME: das gibt einen Fehler im debug-Modus
		//		wenn ScrollPane nach seinen Headern fragt,
		// 		weiss nicht, was genau das Problem ist		
		
		//		JScrollPane scrollPane = new JScrollPane(table);
		//		scrollPane.setBackground(bgClr);
		//		add(scrollPane,BorderLayout.CENTER);
		add(table,BorderLayout.NORTH);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Ask to be notified of selection changes.
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				if (e.getValueIsAdjusting()) return;
				
				ListSelectionModel lsm =
					(ListSelectionModel)e.getSource();
				if (lsm.isSelectionEmpty()) {
					// no rows are selected
					helpInfo.setText("");
					helpInfo.setLongText("");
				} else {
					int selectedRow = lsm.getMinSelectionIndex();
					//selectedRow is selected
					AbstractParam param=(AbstractParam)tableModel.getValueAt(selectedRow,1);
					helpInfo.setText("<html><a href=\"longDesc\">"
							+param.getName()+"</a>: "
							+param.getShortDesc()
							+"</html>");
					helpInfo.setLongText("<html>"+param.getLongDesc()+"</html>");
				}
			}
		});
		
		
		// add label for parameter info
		helpInfo = new HelpInfo();
		helpInfo.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				((HelpInfo)e.getSource()).showLongText();
			}
		});
		
		add(helpInfo,BorderLayout.CENTER);
		
	}
	
	public void showGroup(String group, int level) {
		if (table.isEditing())
			table.getCellEditor().stopCellEditing();
		
		groupName = group;
		groupLevel = level;
		
		tableModel.fireTableDataChanged();
//		if (tableModel.getRowCount()>0)
//			table.setRowSelectionInterval(0,0);
	}
	
	public void stopEditing() {
		if (table.isEditing())
			table.getCellEditor().stopCellEditing();
	}
	
	public void showError(Exception e) {
		helpInfo.showError(e.getMessage());
	}
	
	public void noError() {
		helpInfo.noError();
	}
	
	//		FocusListener groupListener = new FocusAdapter() {
	//			public void focusGained(FocusEvent e) {
	////				image.setIcon(imageicon);
	////				((TitledBorder)image.getBorder()).setTitle(imageicon.getDescription());
	//				
	//				// FIXME: remake preview Tree only at parameter changes
	//				((TreePreview)image).remakeTree();
	//				((TreePreview)image).repaint();
	//			}
	//		};
	
	
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
	
	
	class HelpInfo extends JLabel {
		String longText;
		boolean errorShowing=false;
		
		public HelpInfo () {
			super();
			setFont(getFont().deriveFont(Font.PLAIN,12));
			setBorder(BorderFactory.createEmptyBorder(10,5,5,5));
		}
		
		public void setLongText(String text) {
			if(! errorShowing) longText = text;
		}
		
		public void showLongText() {
			if (! errorShowing) {
				JLabel msg = new JLabel(longText.replace('\n',' '));
				//    		Dimension dim = msg.getMaximumSize();
				//    		dim.setSize(100,dim.getHeight());
				//    		msg.setMaximumSize(dim);
				JOptionPane.showMessageDialog(this,msg,
						"Parameter description",JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		public void showError(String err) {
			setText("<html><font color='red'>"+err+"</font></html>");
			errorShowing=true;
		}
		
		public void noError() {
			errorShowing=false;
		}
		
		public void setText(String str) {
			if (! errorShowing) super.setText(str);
		}
	}

	
	
}

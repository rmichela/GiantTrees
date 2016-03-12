/*
 * This copy of Arbaro is redistributed to you under GPLv3 or (at your option)
 * any later version. The original copyright notice is retained below.
 */
//#**************************************************************************
//#
//#    Copyright (C) 2003-2006  Wolfram Diestel
//#
//#    This program is free software; you can redistribute it and/or modify
//#    it under the terms of the GNU General Public License as published by
//#    the Free Software Foundation; either version 2 of the License, or
//#    (at your option) any later version.
//#
//#    This program is distributed in the hope that it will be useful,
//#    but WITHOUT ANY WARRANTY; without even the implied warranty of
//#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//#    GNU General Public License for more details.
//#
//#    You should have received a copy of the GNU General Public License
//#    along with this program; if not, write to the Free Software
//#    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//#
//#    Send comments and bug fixes to diestel@steloj.de
//#
//#**************************************************************************/

package net.sourceforge.arbaro.gui;

import javax.swing.JComponent;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.Enumeration;
import java.awt.geom.AffineTransform;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Color;

import net.sourceforge.arbaro.tree.*;
import net.sourceforge.arbaro.transformation.*;
import net.sourceforge.arbaro.mesh.*;

/**
 * An image showing parts of the edited tree
 */
public class TreePreview extends JComponent {
	
	PreviewTree previewTree;
	int perspective;
	boolean draft=false;
	final static int PERSPECTIVE_FRONT=0;
	final static int PERSPECTIVE_TOP=90;

	static final Color thisLevelColor = new Color(0.3f,0.2f,0.2f);
	static final Color otherLevelColor = new Color(0.6f,0.6f,0.6f);
	static final Color leafColor = new Color(0.1f,0.6f,0.1f);
	static final Color bgClr = new Color(250,250,245);
	
	AffineTransform transform;
	Transformation rotation; // viewing perspective
	Vector origin = new Vector(0,0,0);
	
	public TreePreview(PreviewTree prvTree, int perspect) {
		super();
		setMinimumSize(new Dimension(100,100));
		setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		setOpaque(true);
		setBackground(Color.WHITE);
		
		previewTree = prvTree;
		perspective = perspect;

		initRotation();
		
		previewTree.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				repaint();
			}
		});
	}
	
	public void paint(Graphics g) {
		if (previewTree.getMesh() == null) {
			try {
				previewTree.remake();
			} catch (Exception e) {
				System.err.println(e);
				e.printStackTrace();
			}
			return;
		}
		
		Graphics2D g2 = (Graphics2D)g;
		
		// turn antialiasing on
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.addRenderingHints(rh);
		
		try {
			g.clearRect(0,0,getWidth(),getHeight());
			g.setColor(bgClr);
			if (perspective==PERSPECTIVE_FRONT) 
				g.fillRect(0,0,getWidth()-1,getHeight());
			else
				g.fillRect(0,0,getWidth(),getHeight()-1);
			//g.drawRect(10,10,getWidth()-20,getHeight()-20);
			initTransform(g);
			if (draft) previewTree.traverseTree(new StemDrawer(g)); //drawStems(g);
			else {
				drawMesh(g);
				//drawLeaves(g);
				previewTree.traverseTree(new LeafDrawer(g));
			}

			// DEBUG
//			Enumeration e = previewTree.allStems(previewTree.getShowLevel()-1);
//			Stem stem = (Stem)e.nextElement();
//			Vector diag = stem.getMaxPoint().sub(stem.getMinPoint());
//			Vector orig = stem.getTransformation().getT();
//			setOrigin(new Vector(orig.getX(),orig.getY(),0));
//			Vector max = stem.getMaxPoint();
//			Vector min = stem.getMinPoint();
//			
//			// get greatest distance from orig
//			double x1 = Math.abs(min.getX())-Math.abs(orig.getX());
//			double x2 = Math.abs(max.getX())-Math.abs(orig.getX());
//			double x = Math.max(Math.abs(x1),Math.abs(x2));
//			double y1 = Math.abs(min.getY())-Math.abs(orig.getY());
//			double y2 = Math.abs(max.getY())-Math.abs(orig.getY());
//			double y = Math.max(Math.abs(y1),Math.abs(y2));
//			
//			Vector av = max.sub(orig).add(min.sub(orig));
//			//av=av.mul(0.5);
//
////			double dw = Math.sqrt(x*x+y*y)*2;
//			double dw = Math.sqrt(av.getX()*av.getX()+av.getY()*av.getY())*2;
//			double minw = -dw/2;
//			
//			double dh = diag.getZ();
//			double minh = min.getZ();
//			
//			g.setColor(Color.RED);
//			drawLine(g,orig,min);
//			drawLine(g,orig,max);
//			
//			g.setColor(Color.GREEN);
//			drawLine(g,orig,orig.add(new Vector(dw/2,dw/2,0)));
//			
//			g.setColor(Color.BLUE);
//			drawLine(g,orig,av.add(orig));
////			drawLine(g,orig,new Vector(dw/4,dw/4,orig.getZ()));
//			
////			g.setColor(Color.BLUE);
////			drawLine(g,stem.getTransformation().getT(),stem.getMaxPoint().sub(stem.getMinPoint()));

		} catch (Exception e) {
			// do nothing, don't draw
		}
	}
	
	public void setDraft(boolean d) {
		draft=d;
	}
	
	protected void initRotation() {
		rotation = new Transformation();
		if (perspective == PERSPECTIVE_TOP) 
			rotation = rotation.rotx(90);
	}
	
	public void setRotation(double zangle) {
		initRotation();
		rotation = rotation.rotz(zangle);
		repaint();
	}
	
	public void setOrigin(Vector orig) {
		origin=orig;
	}
	
	private void initTransform(Graphics g) throws Exception {
		// Perform transformation
		transform = new AffineTransform();
		double dw=1;
		double minw=0;
		double dh=0;
		double minh=0;
		double scale;
		double x;
		double y;
		//double abs;
		final int margin=5;
	
		int showLevel = previewTree.getShowLevel();
		
		class FindAStem extends DefaultTreeTraversal {
			Stem found = null;
			int level;
			
			public FindAStem(int level) {
				this.level=level;
			}
			public Stem getFound() { return found; }
			public boolean enterStem(Stem stem) {
				if (found == null && stem.stemlevel < level)
					return true; // look further
				else if (found != null || stem.stemlevel > level)
					return false; // found a stem or too deep
				else if (stem.stemlevel == level)
					found = stem;
				
				return true;
			}
			public boolean leaveTree(Tree tree) {
				return (found != null);
			}
		}
		
		if (showLevel < 1) {
			setOrigin(new Vector());

			//////////// FRONT view
			if (perspective==PERSPECTIVE_FRONT) {
				// get width and height of the tree
				dw = previewTree.getWidth()*2;
				dh = previewTree.getHeight();
				minh = 0;
				minw = -dw/2;
			///////////////// TOP view
			} else {
				// get width of the tree
				dw = previewTree.getWidth()*2;
				minw = -dw/2;
			}
			
		} else {
				
			// find stem which to show
/*			Enumeration e = previewTree.allStems(showLevel-1);
			if (! e.hasMoreElements()) throw new Exception("Stem not found");
			Stem stem = (Stem)e.nextElement();
			*/
			Stem aStem = null;
			FindAStem stemFinder = new FindAStem(showLevel-1);
			if (previewTree.traverseTree(stemFinder)) {
				aStem = stemFinder.getFound();
			}

			if (aStem != null) {
				Vector diag = aStem.getMaxPoint().sub(aStem.getMinPoint());
				Vector orig = aStem.getTransformation().getT();
				setOrigin(new Vector(orig.getX(),orig.getY(),0));
				Vector max = aStem.getMaxPoint();
				Vector min = aStem.getMinPoint();
				
				// get greatest distance from orig
				x = Math.max(Math.abs(min.getX()-orig.getX()),
							Math.abs(max.getX()-orig.getX()));
				y = Math.max(Math.abs(min.getY()-orig.getY()),
							Math.abs(max.getY()-orig.getY()));
	
				dw = Math.sqrt(x*x+y*y)*2;
				minw = -dw/2;
				
				dh = diag.getZ();
				minh = min.getZ();
			}
			//DEBUG
//			System.err.println("O: "+ orig +" min: "+min+" max: "+max);
//			System.err.println("maxX: "+x+" maxY: "+y);
//			System.err.println("dg: "+diag+" dw: "+dw+" minw: "+minw+" dh: "+dh+" minh: "+minh);
		}

		//////////// FRONT view
		if (perspective==PERSPECTIVE_FRONT) {
			
			// how much to scale for fitting into view?
			scale = Math.min((getHeight()-2*margin)/dh,(getWidth()-2*margin)/dw);
			
			if (previewTree.params.debug)
				System.err.println("scale: "+scale);

			// shift to mid point of the view
			transform.translate(getWidth()/2,getHeight()/2);					
			// scale to image height
			transform.scale(scale,-scale);
			// shift mid of the tree to the origin of the image
			transform.translate(-minw-dw/2,-minh-dh/2);
			
	    ///////////////// TOP view
		} else {
			
			// how much to scale for fitting into view?
			scale = Math.min((getHeight()-2*margin)/dw,(getWidth()-2*margin)/dw);

			// shift to mid point of the view
			transform.translate(getWidth()/2,getHeight()/2);					
			// scale to image height
			transform.scale(scale,-scale);
			// shift mid of the stem to the origin of the image
			transform.translate(-minw-dw/2,-minw-dw/2);
		}
		
		// DEBUG
		Point p = new Point();
		transform.transform(new Point2D.Double(0.0,0.0),p);
		if (previewTree.params.debug) {
			System.err.println("width: "+minw+"+"+dw);
			System.err.println("height: "+minh+"+"+dh);
			System.err.println("Origin at: "+p);
			System.err.println("view: "+getWidth()+"x"+getHeight());
		}
	}
	
	
	private void drawMesh(Graphics g) {
		try {
			for (Enumeration parts=previewTree.getMesh().elements(); parts.hasMoreElements();) 
			{
				MeshPart m = (MeshPart)parts.nextElement();
				if (m.getLevel()==previewTree.getShowLevel()) {
					g.setColor(thisLevelColor);
				} else {
					g.setColor(otherLevelColor);
				}
				drawMeshPart(g,m);
			}
		} catch (Exception e) {
			//System.err.println("nummer "+i);
			e.printStackTrace();
		}
	}
	
	
	private void drawMeshPart(Graphics g, MeshPart m) {
		if (m.size()>0) {
			MeshSection s = (MeshSection)m.elementAt(1);
			//			c=0;
			
			while (s.next != null) {
				//				g.setColor(clr[c]);
				if (s.size()>=s.next.size()) {
					for (int i=0; i<s.size(); i++) {
						drawLine(g,s.here(i),s.up(i));
						drawLine(g,s.here(i),s.right(i));
					} 
					s=s.next;
					//					c=1-c;
				} else {
					s=s.next; 
					//					c=1-c;
					for (int i=0; i<s.size(); i++) {
						drawLine(g,s.here(i),s.down(i));
						drawLine(g,s.here(i),s.right(i));
					} 
				}
			}
		}
	}
	
	private class LeafDrawer implements TreeTraversal {
		LeafMesh m;
		Graphics g;
		
		public LeafDrawer(Graphics g) {
			this.g = g;
			g.setColor(leafColor);
		}
		public boolean enterTree(Tree tree) {
			m = tree.createLeafMesh(false);
			return true;
		}
		public boolean leaveTree(Tree tree) {
			return true;
		}
		public boolean enterStem(Stem stem) {
			return true;
		}
		public boolean leaveStem(Stem stem) {
			return true;
		}
		public boolean visitLeaf(Leaf l) {
			if (m.isFlat()) {
				Vector p = l.transf.apply(m.shapeVertexAt(m.getShapeVertexCount()-1).point);
			
				for (int i=0; i<m.getShapeVertexCount(); i++) {
					Vector q = l.transf.apply(m.shapeVertexAt(i).point);
					drawLine(g,p,q);
					p=q;
				}
			} else {
				for (int i=0; i<m.getShapeFaceCount(); i++) {
					Face f = m.shapeFaceAt(i);
					Vector p = l.transf.apply(m.shapeVertexAt((int)f.points[0]).point);
					Vector q = l.transf.apply(m.shapeVertexAt((int)f.points[1]).point);
					Vector r = l.transf.apply(m.shapeVertexAt((int)f.points[2]).point);
					drawLine(g,p,q);
					drawLine(g,p,r);
					drawLine(g,r,q);
				}
				
			}
			return true;
		}
	}
	
/*
 	private void drawLeaves(Graphics g) {
		LeafMesh m = previewTree.createLeafMesh(false);
		Enumeration leaves = previewTree.allLeaves();
		
		g.setColor(leafColor);
		while (leaves.hasMoreElements()) {
			Leaf l = (Leaf)leaves.nextElement();
			
			if (m.isFlat()) {
				Vector p = l.transf.apply(m.shapeVertexAt(m.getShapeVertexCount()-1).point);
			
				for (int i=0; i<m.getShapeVertexCount(); i++) {
					Vector q = l.transf.apply(m.shapeVertexAt(i).point);
					drawLine(g,p,q);
					p=q;
				}
			} else {
				for (int i=0; i<m.getShapeFaceCount(); i++) {
					Face f = m.shapeFaceAt(i);
					Vector p = l.transf.apply(m.shapeVertexAt((int)f.points[0]).point);
					Vector q = l.transf.apply(m.shapeVertexAt((int)f.points[1]).point);
					Vector r = l.transf.apply(m.shapeVertexAt((int)f.points[2]).point);
					drawLine(g,p,q);
					drawLine(g,p,r);
					drawLine(g,r,q);
				}
				
			}
		}
	}
*/
	
	private void drawLine(Graphics g,Vector p, Vector q) {
		// FIXME: maybe eliminate class instantiations
		// from this method for performance reasons:
		// use static point arrays for transformations
		
		Vector u = rotation.apply(p.sub(origin));
		Vector v = rotation.apply(q.sub(origin));

		Point2D.Double from = new Point2D.Double(u.getX(),u.getZ());
		Point2D.Double to = new Point2D.Double(v.getX(),v.getZ());
		
		Point ifrom = new Point();
		Point ito = new Point();
		
		transform.transform(from,ifrom);
		transform.transform(to,ito);
		
//		g.drawLine(xInt(u.getX()),yInt(u.getZ()),
//				xInt(v.getX()),yInt(v.getZ()));

		g.drawLine(ifrom.x,ifrom.y,ito.x,ito.y);
	}
	
	private class StemDrawer implements TreeTraversal {
		Graphics g;

		public StemDrawer(Graphics g) {
			this.g = g;
			g.setColor(otherLevelColor);
		}
		public boolean enterTree(Tree tree) {
			return true;
		}
		public boolean leaveTree(Tree tree) {
			return true;
		}
		public boolean enterStem(Stem stem) throws TraversalException {
			stem.traverseStem(
					new StemTraversal() {
						public boolean enterStem(Stem stem) { return true; }
						public boolean leaveStem(Stem stem) { return true; }
						public boolean enterSegment(Segment seg) throws TraversalException {
							// FIXME: maybe draw rectangles instead of thin lines
							//drawStripe(g,seg.posFrom(),seg.rad1,seg.postTo(),seg.rad2());
							drawLine(g,seg.posFrom(),seg.posTo());
							return true; 
						}
						public boolean leaveSegment(Segment seg) { return true; }
						public boolean visitSubsegment(Subsegment ss) { return true; }
					}
					);

			return true;
		}
		public boolean leaveStem(Stem stem) {
			return true;
		}
		public boolean visitLeaf(Leaf l) {
			return true;
		}
	}
	
	/*
	private void drawStems(Graphics g) {
		g.setColor(otherLevelColor);
		for (Enumeration stems=previewTree.allStems(-1); stems.hasMoreElements();) {
			drawSegments(g, (Stem)stems.nextElement());
		}
	}
	
	private void drawSegments(Graphics g, Stem s) {
		for (Enumeration segments=s.stemSegments(); segments.hasMoreElements();) {
			Segment seg = (Segment)segments.nextElement();
			// FIXME: maybe draw rectangles instead of thin lines
			//drawStripe(g,seg.posFrom(),seg.rad1,seg.postTo(),seg.rad2());
			drawLine(g,seg.posFrom(),seg.posTo());
		}
	}
	*/
	
//	int xInt(double x) {
//		return (int)Math.round(xoff+xscale*x);
//	}
//	
//	int yInt(double y) {
//		return (int)Math.round(yoff+yscale*y);
//	}
}

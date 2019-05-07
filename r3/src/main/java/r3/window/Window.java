package r3.window;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Window extends JFrame implements KeyListener, MouseListener{
	
	private DrawComp dc;
	
	public Window() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		dc = new DrawComp();
		dc.setPreferredSize(new Dimension(500,500));
		this.add(dc);
		
		
		this.addKeyListener(this);
		this.addMouseListener(this);
		
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
//	public void draw() {
//		this.dc.repaint();
//	}
	
	
	
	
	
	////// INPUTS - KEYBOARD
	
	private boolean[] keyRegister = new boolean[KeyEvent.RESERVED_ID_MAX+1];
	
	public void keyPressed(KeyEvent e) {
		keyRegister[e.getKeyCode()] = true;
		//System.out.println("registered key: " + e.getKeyChar());
		}
	
	public void keyReleased(KeyEvent e) {
		this.keyRegister[e.getKeyCode()] = false;
	}
	
	/**
	 * @returns if the key with ID >> KeyCode.VK_{...} is being pressed
	 */
	public boolean isKeyBeingPressed(int keyCode) {
		return this.keyRegister[keyCode];
	}
	
	/**
	 * @returns the KeyRegister of this Window.<br>
	 * Indices are KeyEvent.VK_{...} constants for each key
	 */
	public boolean[] getKeyRegister() {
		return this.keyRegister;
	}
	
	public void keyTyped(KeyEvent e) {}
	
	//////INPUTS - MOUSE
	
	private final int[] mouseMovementPixelCache = new int[2];
	private final int[] ARRAY_EMPTY_2 = new int[] {0,0};
	private Point mousePositionOnLastInvoke = null;
	private Point mousePositionCurrentCache;
	public int[] getMouseMovementPixelSinceLastInvoke() {
		if(!this.mouseBeingClicked) {
			mousePositionOnLastInvoke = null;
			return ARRAY_EMPTY_2;
		}
		
		mousePositionCurrentCache = this.getMousePosition();
		if(mousePositionCurrentCache == null)
			return ARRAY_EMPTY_2;
		
		
		if(mousePositionOnLastInvoke == null) {
			mousePositionOnLastInvoke = mousePositionCurrentCache;
			return ARRAY_EMPTY_2;
		}
		
		//calc delta of mouse movement and write into cache that will be returned
		mouseMovementPixelCache[0] = mousePositionCurrentCache.x - mousePositionOnLastInvoke.x;
		mouseMovementPixelCache[1] = mousePositionCurrentCache.y - mousePositionOnLastInvoke.y;
		
		mousePositionOnLastInvoke = mousePositionCurrentCache;
		return mouseMovementPixelCache;
	}	
	
	private boolean mouseBeingClicked = false;
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {		
		this.mouseBeingClicked = true;
	}
	public void mouseReleased(MouseEvent e) {
		this.mouseBeingClicked = false;
	}
	
	//////
	
	public DrawComp getDrawComp() {
		return this.dc;
	}
	
}

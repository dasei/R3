package r3.window;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import game.Game;
import r3.main.Main;

public class Window extends JFrame implements KeyListener, MouseListener{
	
	private static final long serialVersionUID = 1L;
	private DrawComp dc;
	
	public void init() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		
		dc = new DrawComp();
		dc.setPreferredSize(new Dimension(1280,720));
		this.add(dc);
		
		
		this.addKeyListener(this);
		this.addMouseListener(this);
		
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	
	////// INPUTS - KEYBOARD
	
	private boolean[] keyRegister = new boolean[KeyEvent.RESERVED_ID_MAX+1];
	
	public void keyPressed(KeyEvent e) {
		keyRegister[e.getKeyCode()] = true;
		
		switch(e.getKeyCode()) {
		case KeyEvent.VK_G:
			Game.GRAVITY ^= true;
			System.out.println("GRAVITY: " + (Game.GRAVITY ? "on" : "off"));
			break;
		case KeyEvent.VK_P:
			Main.lowMode++;
			System.out.println("lowMode: " + Main.lowMode);
			break;
		case KeyEvent.VK_L:
			Main.lowMode--;
			System.out.println("lowMode: " + Main.lowMode);
			break;
		case KeyEvent.VK_F1:
			Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN ^= true;
			System.out.println("SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN: " + Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN);
			break;
		case KeyEvent.VK_F2:
			Game.ANTIALIAZING ^= true;
			System.out.println("ANTIALIAZING: " + (Game.ANTIALIAZING ? "on" : "off"));
			break;
		case KeyEvent.VK_I:
			DrawComp.ANTIALIAZING_RADIUS += 0.5;
			System.out.println("ANTIALIAZING_RADIUS: " + DrawComp.ANTIALIAZING_RADIUS);
			break;
		case KeyEvent.VK_J:
			DrawComp.ANTIALIAZING_RADIUS -= 0.5;
			System.out.println("ANTIALIAZING_RADIUS: " + DrawComp.ANTIALIAZING_RADIUS);
			break;
		}
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

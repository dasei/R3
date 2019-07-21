package r3.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

import game.Game;
import game.gameobjects.GameObject;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import r3.main.Main;
import r3.mathstuff.Camera;
import r3.mathstuff.Mathstuff;
import r3.multithreading.ThreadProcessor;

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
		case KeyEvent.VK_F3:
			DrawComp.rgbMode ^=true;
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
//		System.out.println(e.getButton());
		if(e.getButton()==1)
		{
			this.mouseBeingClicked = true;
		}
		else if(e.getButton()==2)
		{
			Game.machineGun ^= true;
		}
		else 
		{
			Window.playSound1();
			if(!Game.modification)
			{
				GameObject cube = Mathstuff.generateCube(new double[] {Camera.forward[0]+Camera.pos[0],Camera.forward[1]+Camera.pos[1],Camera.forward[2]+Camera.pos[2]}, 0.1, Main.storeColor(Color.BLACK.getRGB()),true);
	//			GameObject cube = Mathstuff.generateCube(Camera.pos, 0.1, -1);
				cube.setSpeedPerSecond(new double[] {Camera.forward[0]*100,Camera.forward[1]*100,Camera.forward[2]*100});
				Game.getGame().addGameObject(cube);
				Game.getGame().gameObjectsCache.add(cube);
				ThreadProcessor.addGameObjects(Game.getGame().gameObjectsCache, true);
				Game.getGame().gameObjectsCache.clear();
			}
		}
		
	}
	public void mouseReleased(MouseEvent e) {
		if(e.getButton()==1)
		{
			this.mouseBeingClicked = false;
		}
//		else if(e.getButton()==2)
//		{
//			Game.machineGun = false;
//		}
	}
	
	//////
	
	public DrawComp getDrawComp() {
		return this.dc;
	}
	public static void playSound1()
	{
		Thread dt = new Thread()
		{
			public void run()
			{
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(new File("res/Nachtigall Sound.mp3"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Player player = null;
				try {
					player = new Player(fileInputStream);
				} catch (JavaLayerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					player.play();
				} catch (JavaLayerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		dt.start();
	}
	public static void playSound2()
	{
		Thread dt = new Thread()
		{
			public void run()
			{
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(new File("res/Explosion.mp3"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Player player = null;
				try {
					player = new Player(fileInputStream);
				} catch (JavaLayerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					player.play();
				} catch (JavaLayerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		dt.start();
	}
}

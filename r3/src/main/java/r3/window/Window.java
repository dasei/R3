package r3.window;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JFrame;

import r3.main.Main;

public class Window extends JFrame implements KeyListener, MouseListener{
	
	private DrawComp dc;
	
	public Window() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		dc = new DrawComp();
		dc.setPreferredSize(new Dimension(500,500));
		this.add(dc);
		
		this.setLocationRelativeTo(null);
		this.pack();
		this.setVisible(true);
		
		this.addKeyListener(this);
	}
	
//	public void draw() {
//		this.dc.repaint();
//	}
	
	public void repaint(double[][][] coords){
		this.dc.setCoords(coords);
		this.dc.repaint();
	}
	
	public void keyPressed(KeyEvent e) {
		System.out.println("pressed: " + e.getKeyChar());
		
		dc.coords = new double[][][] {
			{{5, 5, 5}, {5, 10, 5}, {5, 5, 10}}
		};
		
		switch(e.getKeyCode()){
		case KeyEvent.VK_A:
			Main.getCamera().pos[1]++;
			break;
		case KeyEvent.VK_D:
			Main.getCamera().pos[1]--;
			break;
			
		case KeyEvent.VK_W:
			Main.getCamera().pos[0]++;
			break;
		case KeyEvent.VK_S:
			Main.getCamera().pos[0]--;
			break;
			
		case KeyEvent.VK_SPACE:
			Main.getCamera().pos[2]++;
			break;
		case KeyEvent.VK_SHIFT:
			Main.getCamera().pos[2]--;
			break;
		}
		
		this.dc.repaint();
		
		System.out.println(Arrays.toString(Main.getCamera().pos));
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
	}
	
	
	private int mouseXLast = -1;
	private int mouseYLast = -1;
	public void mouseClicked(MouseEvent e) {
		
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
		Main.getCamera().beta += (e.getX()-mouseXLast/200);
		Main.getCamera().alpha += (e.getY()-mouseXLast/200);
		mouseXLast = e.getX();
		mouseYLast = e.getY();
	}
	
	public void mouseReleased(MouseEvent e) {
	}
	
}

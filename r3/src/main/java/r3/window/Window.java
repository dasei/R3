package r3.window;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Window extends JFrame {
	
	private DrawComp dc;
	
	public Window() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		dc = new DrawComp();
		dc.setPreferredSize(new Dimension(500,500));
		this.add(dc);
		
		this.setLocationRelativeTo(null);
		this.pack();
		this.setVisible(true);
	}
	
	public void draw() {
		this.dc.repaint();
	}
	
}

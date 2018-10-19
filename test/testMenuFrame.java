package test;

import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;

import view.menu.MenuFrame;

public class testMenuFrame{
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		MenuFrame menu = new MenuFrame();
		menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menu.setVisible(true);
	}
}

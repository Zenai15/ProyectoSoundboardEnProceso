package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import soundboard.AudioPlayer;

public class MainFrame {
	// Archivo de texto donde guardaremos la última ruta configurada
	private static final Path CONFIG_FILE = Paths.get("config.txt");
	private static Path path = Paths.get("src/soundboard/mp3.wav");

	public static void main(String[] args) {
		// AL INICIAR: Intentamos cargar la última ruta guardada
		if (Files.exists(CONFIG_FILE)) {
			try {
				String savedPath = Files.readString(CONFIG_FILE).trim();
				if (!savedPath.isEmpty() && Files.exists(Paths.get(savedPath))) {
					path = Paths.get(savedPath);
				}
			} catch (IOException e) {
				System.err.println("No se pudo cargar la configuración: " + e.getMessage());
			}
		}

		JFrame ventana = new JFrame("Sorpresa");
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setLayout(new BorderLayout());
		ventana.setSize(1000, 700);

		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("Options");
		menu.add(fileMenu);

		JMenuItem inputItem = new JMenuItem("Change sound path");
		fileMenu.add(inputItem);

		inputItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(ventana, "Enter the path:");
				if (input == null || input.trim().isEmpty()) return;

				Path source = Paths.get(input);
				// Guardamos en una carpeta externa a 'src' para que no dependa de la compilación de Eclipse
				Path destination = Paths.get("soundsWAV/" + source.getFileName()).toAbsolutePath();
				
				try {
					if (Files.notExists(destination.getParent())) {
						Files.createDirectories(destination.getParent());
					}

					Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
		           
					path = destination;
					
					// GUARDAR EN DISCO: Escribimos la ruta en el config.txt para el próximo inicio
					Files.writeString(CONFIG_FILE, path.toString());
					System.out.println("Guardado permanentemente en: " + path);
					
		        } catch (Exception ex) {
		            ex.printStackTrace();
		            JOptionPane.showMessageDialog(ventana, "Error: " + ex.getMessage());
		        }
			}
		});

		ventana.setJMenuBar(menu);

		JButton botonSonido = new JButton("Play sound");
		botonSonido.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.playSound(path.toString());
			}
		});

		ventana.add(botonSonido, BorderLayout.CENTER);
		ventana.setVisible(true);
	}
}

package soundboard;

import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class AudioPlayer {
	public static void playSound(String filePath) {
		try {
			File soundFile = new File(filePath);
			AudioInputStream audioStream;
			
			// 1. Intentar cargar el archivo desde el disco duro o desde el Classpath
			if (!soundFile.exists()) {
				java.net.URL soundURL = AudioPlayer.class.getResource("/" + filePath.replace("src/", ""));
				if (soundURL != null) {
					audioStream = AudioSystem.getAudioInputStream(soundURL);
				} else {
					System.out.println("ERR: No file found at - " + filePath);
					return;
				}
			} else {
				audioStream = AudioSystem.getAudioInputStream(soundFile);
			}

			// 2. OBTENER EL FORMATO ORIGINAL
			AudioFormat baseFormat = audioStream.getFormat();
			AudioFormat targetFormat = baseFormat;

			// 3. SI ES DE 24 BITS (O 32 BITS), FORZAMOS LA CONVERSIÓN A 16 BITS PCM
			if (baseFormat.getSampleSizeInBits() > 16 || baseFormat.getSampleSizeInBits() == -1) {
				targetFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(),       // Mantiene los 48000.0 Hz originales
					16,                               // Forzamos bajada segura a 16 bits para compatibilidad
					baseFormat.getChannels(),         // Mantiene estéreo o mono
					baseFormat.getChannels() * 2,     // 2 bytes por frame en 16-bit
					baseFormat.getSampleRate(),
					false                             // Little-endian
				);
				// Transforma el stream original incompatible en el nuevo stream de 16 bits
				audioStream = AudioSystem.getAudioInputStream(targetFormat, audioStream);
			}

			// 4. REPRODUCIR EL STREAM CONVERTIDO
			DataLine.Info info = new DataLine.Info(Clip.class, targetFormat);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioStream);
			clip.start();
			
		} catch (Exception e) {
			System.err.println("Error al reproducir el archivo de audio:");
			e.printStackTrace();
		}
	}
}

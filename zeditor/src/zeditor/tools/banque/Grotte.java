package zeditor.tools.banque;

import java.awt.Point;
import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

public class Grotte extends Banque {

	// Grotte.dec = Interia3 + Interia2 (203 tiles)
	public Grotte() {
		coords = new Point[] {
		/* Murs étage 2 */
		new Point(0, 0), new Point(16, 0), new Point(64, 0), new Point(0, 16),
				new Point(16, 16), new Point(32, 16), new Point(48, 16),
				new Point(64, 16), new Point(16, 32), new Point(32, 32),
				new Point(48, 32), new Point(16, 48), new Point(32, 48),
				new Point(48, 48), new Point(0, 64), new Point(16, 64),
				new Point(64, 64),

				/* Coins étage 2 */
				new Point(160, 32), new Point(176, 32), new Point(160, 48),
				new Point(176, 48),

				/* Tour étage 2 */
				new Point(64, 112), new Point(80, 112), new Point(96, 112),
				new Point(64, 128), new Point(96, 128), new Point(64, 144),
				new Point(80, 144), new Point(96, 144),

				/* Virage étage2 */
				new Point(112, 112), new Point(128, 112), new Point(112, 128),
				new Point(128, 128),

				/* Murs étage 1 */
				new Point(80, 0), new Point(96, 0), new Point(144, 0),
				new Point(80, 16), new Point(96, 16), new Point(112, 16),
				new Point(128, 16), new Point(144, 16), new Point(96, 32),
				new Point(112, 32), new Point(128, 32), new Point(96, 48),
				new Point(112, 48), new Point(128, 48), new Point(80, 64),
				new Point(96, 64), new Point(144, 64),

				/* Coins étage 1 */
				new Point(192, 32), new Point(208, 32), new Point(192, 48),
				new Point(208, 48),

				/* Virages */
				new Point(0, 112), new Point(16, 112), new Point(32, 112),
				new Point(48, 112), new Point(0, 128), new Point(16, 128),
				new Point(32, 128), new Point(48, 128), new Point(0, 144),
				new Point(16, 144), new Point(32, 144), new Point(48, 144),
				new Point(0, 160), new Point(16, 160), new Point(32, 160),
				new Point(48, 160),

				/* S‚paration */
				new Point(112, 144), new Point(128, 144), new Point(144, 144),
				new Point(112, 160), new Point(144, 160), new Point(112, 176),
				new Point(128, 176), new Point(144, 176),

				/* Eau */
				new Point(64, 80), new Point(80, 80), new Point(96, 80),

				/* Sortie */
				new Point(0, 80), new Point(16, 80), new Point(0, 96),
				new Point(16, 96),

				/* Statue d'abeille */
				new Point(112, 80), new Point(128, 80), new Point(112, 96),
				new Point(128, 96),

				/* Escaliers */
				new Point(160, 0), new Point(176, 0), new Point(160, 16),
				new Point(176, 16), new Point(192, 0), new Point(208, 0),
				new Point(192, 16), new Point(208, 16),

				/* Porte ouverte */
				new Point(160, 64), new Point(176, 64), new Point(160, 80),
				new Point(176, 80), new Point(192, 64), new Point(208, 64),
				new Point(192, 80), new Point(208, 80),
				new Point(224, 64),
				new Point(240, 64),
				new Point(224, 80),
				new Point(240, 80),
				new Point(256, 64),
				new Point(272, 64),
				new Point(256, 80),
				new Point(272, 80),

				/* Porte … casser */
				new Point(64, 160),
				new Point(80, 160),
				new Point(64, 176),
				new Point(80, 176),

				/* Grosse pierre */
				new Point(224, 0),
				new Point(240, 0),
				new Point(137, 288), // Lava animation B1
				new Point(224, 16),
				new Point(240, 16),
				new Point(153, 288), // Lava animation B2
				new Point(224, 32),
				new Point(240, 32),
				new Point(256, 32),

				/* Caves */
				/* Vide */
				new Point(64, 64), // 127
				/* Tour1 */
				new Point(16, 32), new Point(32, 32), new Point(64, 0),
				new Point(96, 32), new Point(112, 32), new Point(112, 48),
				new Point(96, 64), new Point(112, 64), new Point(64, 96),
				new Point(16, 64), new Point(32, 64), new Point(16, 48),
				new Point(144, 48), new Point(160, 48), new Point(144, 64),
				new Point(160, 64),
				/* Tour2 */
				new Point(48, 32), new Point(64, 16), new Point(80, 32),
				new Point(96, 48), new Point(80, 64), new Point(64, 80),
				new Point(48, 64), new Point(32, 48), new Point(144, 80),
				new Point(160, 80), new Point(144, 96), new Point(160, 96),
				/* Sol */
				new Point(64, 48), new Point(64, 32),
				/* Corner enhancer */
				new Point(192, 80), new Point(192, 96),
				//new Point(112, 80),	new Point(128, 80), 
				new Point(112, 96), new Point(128, 96),
				/* Fenˆtre */
				new Point(80, 0), new Point(96, 0), new Point(80, 16),
				new Point(96, 16), new Point(80, 80), new Point(96, 80),
				new Point(80, 96), new Point(96, 96),
				/* Table */
				new Point(0, 0), new Point(16, 0), new Point(32, 0),
				new Point(0, 16), new Point(16, 16), new Point(32, 16),
				/* Portes */
				new Point(0, 80), new Point(16, 80), new Point(0, 96),
				new Point(16, 96), /* Angle0 */
				new Point(32, 80), new Point(48, 80), new Point(32, 96),
				new Point(48, 96),/* Angle2 */
				/* Escalier en haut */
				new Point(0, 112), new Point(16, 112), new Point(0, 128),
				new Point(16, 128), /* Descend */
				new Point(32, 112), new Point(48, 112), new Point(32, 128),
				new Point(48, 128), /* Monte */
				
				/* Porte gauche */
				new Point(64, 112), new Point(80, 112),
				new Point(64, 128), new Point(80, 128),
				new Point(64, 144),
				/* Porte droite */
				new Point(96, 112), new Point(112, 112),
				new Point(96, 128), new Point(112, 128),
				new Point(80, 144),
				/* Dessus de porte */
				new Point(32, 144), new Point(48, 144),
				
				/* Coin en pierre */
				new Point(112, 0), new Point(128, 0),
				new Point(112, 16), new Point(128, 16),
				
				/* Lit de prison */
				new Point(144,0), new Point(137, 304),
				new Point(144, 16), new Point(153, 304),
				new Point(144, 32), new Point(80, 96),	// Pushed button
				
				/* Bouton */
				new Point(64, 96),
				
				new Point(144, 96), new Point(160, 96), new Point(176, 96),
				new Point(272, 32), new Point(288, 32), new Point(304, 32),
				new Point(160, 144), new Point(160, 160), new Point(160, 176),
				new Point(144, 80), 
				new Point(160, 112), new Point(176, 112),
				new Point(144, 112), new Point(144, 128), new Point(160, 128), new Point(176, 128),
				/* Torch */
				new Point(288, 80), new Point(304, 80), new Point(304, 96),
				/* Additional walls */
				new Point(192, 96), new Point(208, 96), new Point(192, 112), new Point(208, 112),
				/* Side hill */
				new Point(192, 128), new Point(208, 128), new Point(192, 144), new Point(208, 144),
				new Point(176, 144), new Point(176, 160),
				new Point(192, 160), new Point(208, 160), new Point(304, 0), new Point(304, 16),
				new Point(176, 176), new Point(192, 176),
				/* Skull and bones */
				new Point(224, 96), new Point(240, 96),
				new Point(192, 0), new Point(208, 16),
				new Point(112, 80),
				new Point(80,48),
				/* Lava wall animation */
				//new Point(137, 288), new Point(153, 288),
				//new Point(137, 304), new Point(153, 304)
				// Alternative floor
		};
				
		pkmChanges = Arrays.asList(new GraphChange("interia3", 0, 0),
				new GraphChange("interia2", 126, 192),
				new GraphChange("interia3", 212, -192),
				new GraphChange("interia2", 251, 192)
				);
	}
}

/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zeditor.core.prefetch.complex;

import zeditor.tools.AreaWrapper;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 * 
 */
public class Hill extends TraceDelegateDraw {

	final int[] conv_etage_to_map = { 0, 3, 21, 54, 0, 5, 22, 0, // 0--7}
			0, 9, 25, 0, 0, 10, 26, 0, // 8--15}
			0, 31, 32, 0, 0, 14, 34, 0, // 16--23}
			0, 15, 17, 0, 0, 0, 20, // 24--31}
			0, 0, 30, 0, 0, 0, 33, 0, 0, // 32--39}
			0, 2, 0, 0, 0, 4, 0, 0 }; // 40--47}

	// Les angles codés ( * 32 )}
	final int angle0 = 0;
	final int angle1 = 4;
	final int angle2 = 8;
	final int angle3 = 12;
	final int angle4 = 16;
	final int angle5 = 20;
	final int angle6 = 24;
	final int angle7 = 28;
	final int angle8 = 32;
	final int angle9 = 36;
	final int anglea = 40;
	final int angleb = 44;

	// Collines
	final int[][] tab_raise = { { 0, 1 + anglea, 1 + angle0, 1 + angleb, 0 },
			{ 1 + angle7, 2 + angle7, 2 + angle0, 2 + angle1, 1 + angle1 },
			{ 1 + angle6, 2 + angle6, 3, 2 + angle2, 1 + angle2 },
			{ 1 + angle5, 2 + angle5, 2 + angle4, 2 + angle3, 1 + angle3 },
			{ 0, 1 + angle9, 1 + angle4, 1 + angle8, 0 } };

	final int[] movx = { 0, 1, 1, 1, 0, -1, -1, -1, 1, -1, -1, 1 };
	final int[] movy = { -1, -1, 0, 1, 1, 1, 0, -1, 1, 1, -1, -1 };

	int[][] mapetage;

	AreaWrapper map;

	public Hill() {
		mapetage = new int[64][64];
		for (int i = 0; i < 64 * 64; i++) {
			mapetage[i / 64][i % 64] = 0;
		}
	}

	@Override
	public void draw(AreaWrapper p_map, Point p_start) {
		map = p_map;
		int xx = p_start.x;
		int yy = p_start.y;
		int cur_etage;
		int angle_to_compare;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				// On utilise plusieurs variables pour un code simple}
				int x = xx + j - 2;
				int y = yy + i - 2;
				int value = mapetage[yy + i - 2][xx + j - 2];
				int angle = value & (4 + 8 + 16 + 32);
				int etage = value & 3;

				if (tab_raise[i][j] != 0 && etage <= (tab_raise[i][j] & 3)) {
					// On ne remplit cette case que si c'est pour rehausser le
					// terrain}
					mapetage[y][x] = tab_raise[i][j] & 3;
					mapetage[y][x] = mapetage[y][x] | 128;

					angle_to_compare = tab_raise[i][j] & (4 + 8 + 16 + 32);

					if ((value == 0 && etage != 3) || // rien avant?}
							angle == angle_to_compare) { // Memes angles ?}
						mapetage[y][x] += angle_to_compare;
					} else {
						// On doit choisir le bon angle sur les 2}
						// Pour ça, on repère une case adjacente de même
						// hauteur}
						int cur_angle = -2;
						while (true) {
							cur_angle = cur_angle + 2;
							value = mapetage[y + movy[cur_angle]][x
									+ movx[cur_angle]];
							cur_etage = value & 3;
							if (cur_etage == etage || cur_angle == 6) {
								break;
							}
						}
						if (cur_etage != etage) { // ProblŠme, ‡a ne doit pas
													// arriver !}
							mapetage[y][x] += angle_to_compare;
						} else {
							// On a trouv‚ cette case de mˆme hauteur, on
							// v‚rifie que les
							// angles sont compatibles}
							if (angle_to_compare < angle8) {
								value = (angle_to_compare / 4) % 2;
							} else {
								value = 1;
							}

							if (cur_angle % 2 == value) {
								mapetage[y][x] += angle_to_compare;
							} else {
								if (angle > angle7 && (mapetage[y][x] & 3) == 2) {
									// On a un angle spécial
									if (angle == angle8) {
										angle = angle3;
									} else if (angle == angle9) {
										angle = angle5;
									} else if (angle == anglea) {
										angle = angle7;
									} else if (angle == angleb) {
										angle = angle1;
									}
								}
								mapetage[y][x] += angle;
							}
						}
					}
				}
			}

			// On dessine à titre indicatif sur la vraie map
			p_map.writemap(xx, yy, 56);
		}
	}

	@Override
	public void finalizeDraw() {
		int dx = map.getDim_x();
		int dy = map.getDim_y();
		for (int i = 0; i < dy - 1; i++) {
			for (int j = 0; j < dx - 1; j++) {
				if (mapetage[i][j] != 0) {
					// On est au 1er etage, alors on coordonne les décors
					int temp = mapetage[i][j];
					if ((temp & 128) != 0) {
						temp = temp & 127;
						temp = conv_etage_to_map[temp];

						int temp2 = 0; // Le bas des collines prend un motif de
										// plus que les autres côtés
						if (temp == 10 || temp == 28 || temp == 30) {
							temp2 = 11;
						} else if (temp == 31) {
							temp2 = 12;
						} else if (temp == 33 || temp == 36 || temp == 14) {
							temp2 = 13;
						}
						if (temp2 != 0) {
							map.writemap(j, i + 1, temp2);
						}
						temp2 = 0; // Un motif de plus sur le côté haut des
									// collines
						if (temp == 0) {
							temp2 = 1;
						} else if (temp == 8 || temp == 5) {
							temp2 = 6;
						}
						if (temp2 != 0) {
							map.writemap(j, i - 1, temp2);
						}
						map.writemap(j, i, temp);

					}
				}
			}
		}
	}
}
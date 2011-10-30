/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

package zildo.fwk.gfx.effect;

import java.nio.ByteBuffer;

/**
 * Cloud generator, highly inspired from this web page :<br/>
 * <a href=
 * "http://www.gamedev.net/page/resources/_/reference/programming/sweet-snippets/simple-clouds-part-1-r2085"
 * > Simple clouds</a>
 * 
 * @author Tchegito
 * 
 */
public class CloudGenerator {

	ByteBuffer texture;

	private static final int lineSize = 256 * 3;

	float map32[][] = new float[32][32];
	float map256[][] = new float[256][256];

	int colorMin = 200;
	int colorMax = 200 + 42 << 16; // + 255 << 8;

	public CloudGenerator(ByteBuffer p_scratch) {
		texture = p_scratch;
	}

	private void pset(int x, int y, int col) {
		int r = (col >> 16) & 0xff;
		int g = (col >> 8) & 0xff;
		int b = (col) & 0xff;

		texture.put(y * lineSize + x * 3, (byte) r);
		texture.put(y * lineSize + x * 3 + 1, (byte) g);
		texture.put(y * lineSize + x * 3 + 2, (byte) b);
	}

	public void generate() {

		setNoise();

		OverlapOctaves();
		ExpFilter(map256);

		for (int j = 0; j < 256; j++) {
			int c = 0;
			for (int i = 0; i < 256; i++) {
				float f = map256[i][j];
				// int c=(int) (colorMin + (f / 256.f) * (colorMax - colorMin));
				c = (1 + 256 + 256 * 256) * (int) f;
				// c+=1+256+256*256;
				pset(i, j, c);
			}
		}
	}

	private float noise(int x, int y, int random)
	{
		int n = x + y * 57 + random * 131;
		n = (n << 13) ^ n;
		return (1.0f - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) * 0.000000000931322574615478515625f);
	}

	private void setNoise() { // Temp is [34][34]
		int random = (int) (Math.random() * 5000);

		float[][] temp = new float[34][34];

		// 1)
		for (int y = 1; y < 33; y++) {
			for (int x = 1; x < 33; x++) {
				temp[x][y] = 128.0f + noise(x, y, random) * 128.0f;
			}
		}

		// 2)
		for (int x = 1; x < 33; x++) {
			temp[0][x] = temp[32][x];
			temp[33][x] = temp[1][x];
			temp[x][0] = temp[x][32];
			temp[x][33] = temp[x][1];
		}
		temp[0][0] = temp[32][32];
		temp[33][33] = temp[1][1];
		temp[0][33] = temp[32][1];
		temp[33][0] = temp[1][32];

		// 3) smooth
		for (int y = 1; y < 33; y++) {
			for (int x = 1; x < 33; x++)
			{
				float center = temp[x][y] / 4.0f;
				float sides = (temp[x + 1][y] + temp[x - 1][y] + temp[x][y + 1] + temp[x][y - 1]) / 8.0f;
				float corners = (temp[x + 1][y + 1] + temp[x + 1][y - 1] + temp[x - 1][y + 1] + temp[x - 1][y - 1]) / 16.0f;

				map32[x - 1][y - 1] = center + sides + corners;
			}
		}
	}

	private float interpolate(float x, float y, float[][] map)
	{
		int Xint = (int) x;
		int Yint = (int) y;

		float Xfrac = x - Xint;
		float Yfrac = y - Yint;

		int X0 = Xint % 32;
		int Y0 = Yint % 32;
		int X1 = (Xint + 1) % 32;
		int Y1 = (Yint + 1) % 32;

		float bot = map[X0][Y0] + Xfrac * (map[X1][Y0] - map[X0][Y0]);
		float top = map[X0][Y1] + Xfrac * (map[X1][Y1] - map[X0][Y1]);

		return (bot + Yfrac * (top - bot));
	}

	void OverlapOctaves()
	{
		for (int x = 0; x < 256 * 256; x++)
		{
			map256[x & 255][x / 256] = 0;
		}
		for (int octave = 0; octave < 4; octave++) {
			for (int x = 0; x < 256; x++) {
				for (int y = 0; y < 256; y++) {
					float scale = (float) (1 / Math.pow(2, 3 - octave));
					float noise = interpolate(x * scale, y * scale, map32);

					map256[x][y] += (float) (noise / Math.pow(2, octave));
				}
			}
		}
	}

	void ExpFilter(float[][] map)
	{
		float cover = 20.0f;
		float sharpness = 0.95f;

		for (int y = 0; y < 256; y++) {
			for (int x = 0; x < 256; x++)
			{
				float c = map[x][y] - (255.0f - cover);
				if (c < 0) {
					c = 0;
				}
				map[x][y] = 255.0f - ((float) (Math.pow(sharpness, c)) * 255.0f);
			}
		}
	}

}

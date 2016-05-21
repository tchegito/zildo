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

package zildo.platform.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.stage.GameStage;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.opengl.OpenGLGestion;
import zildo.platform.input.LwjglKeyboardHandler;
import zildo.server.EngineZildo;

public class LwjglOpenGLGestion extends OpenGLGestion {

	final static String title = "Zildo OpenGL";

	private DisplayMode displayMode;

	KeyboardHandler kbHandler = Zildo.pdPlugin.kbHandler;
	
	public static final byte[] icon = { 92, -109, 121, -1, 99, -101, -128, -1, 114, -81, -114, -1, 106, -91, -122, -1, 101, -98, -121, -1, 75, 127, 91, -1, 76, -105, 72, -1, 76, -110, 73, -1, 
		66, 121, 63, -1, 61, 116, 60, -1, 63, 126, 63, -1, 80, -122, 62, -1, 117, -101, 64, -1, -127, -76, 71, -1, 67, -108, 75, -1, 68, -124, 68, -1, 
		68, -128, 72, -1, 76, -109, 84, -1, 75, -124, 89, -1, 67, 118, 77, -1, 78, -119, 95, -1, 55, 117, 61, -1, 36, 80, 37, -1, 36, 77, 38, -1, 
		48, 110, 56, -1, 64, -117, 68, -1, 61, 118, 61, -1, 62, 127, 63, -1, 60, 122, 62, -1, -111, -83, 65, -1, -62, -34, 73, -1, 73, -104, 74, -1, 
		66, -126, 66, -1, 67, -124, 63, -1, 73, -127, 88, -1, 87, -112, 115, -1, 60, 110, 81, -1, 83, 28, 19, -1, -125, 7, 14, -1, -126, 16, 15, -1, 
		81, 34, 15, -1, 48, 70, 46, -1, 58, 115, 63, -1, 61, 116, 58, -1, 58, 113, 59, -1, 57, 123, 65, -1, -85, -59, 68, -1, -57, -43, 67, -1, 
		69, -119, 71, -1, 72, -114, 79, -1, 62, 96, 75, -1, 64, 86, 63, -1, 84, 34, 31, -1, -90, 22, 17, -1, -73, 39, 22, -1, -71, 44, 21, -1, 
		-63, 44, 18, -1, 116, 20, 9, -1, 34, 63, 32, -1, 62, 127, 61, -1, 62, 120, 59, -1, 53, 117, 59, -1, 78, 126, 51, -1, -42, -39, 57, -1, 
		72, -112, 75, -1, 70, -121, 84, -1, 98, -111, 112, -1, 118, 63, 28, -1, 127, 3, 6, -1, -128, 19, 14, -1, 122, 16, 10, -1, 117, 12, 6, -1, 
		-105, 31, 12, -1, -73, 40, 14, -1, 42, 34, 19, -1, 53, 112, 57, -1, 52, 110, 51, -1, 62, 92, 52, -1, 91, 117, 91, -1, -80, -76, 93, -1, 
		63, 127, 66, -1, 66, 122, 70, -1, 56, 101, 86, -1, 78, 22, 22, -1, -120, 24, 14, -1, -124, 45, 17, -1, -93, 86, 40, -1, -96, 89, 55, -1, 
		-97, 91, 46, -1, -107, 106, 39, -1, 63, 39, 30, -1, 95, 89, 75, -1, 126, -118, 124, -1, -110, -111, -117, -1, -112, -103, -114, -1, 90, -126, 94, -1, 
		64, 125, 65, -1, 72, -124, 74, -1, 70, 117, 93, -1, 62, 29, 12, -1, -104, 83, 31, -1, -88, 119, 41, -1, -75, -112, 78, -1, -69, -74, -100, -1, 
		123, -123, 105, -1, 91, 78, 43, -1, 86, 43, 41, -1, 112, 84, 73, -1, 100, 104, 96, -1, 119, -108, 120, -1, 67, 119, 66, -1, 46, 102, 45, -1, 
		70, -116, 73, -1, 73, -117, 80, -1, -120, -69, -107, -1, 105, -123, 102, -1, 65, 62, 46, -1, -115, 107, 43, -1, -70, -115, 69, -1, -55, -86, 115, -1, 
		-60, -86, 107, -1, -128, 103, 56, -1, 66, 55, 46, -1, 85, 56, 51, -1, 105, 99, 91, -1, -88, -81, -89, -1, -88, -83, -92, -1, 98, -123, 91, -1, 
		67, -123, 70, -1, 70, -123, 76, -1, -127, -78, -113, -1, -109, -57, -102, -1, 88, 120, 97, -1, 18, 15, 63, -1, 93, 64, 53, -1, 122, 81, 41, -1, 
		91, 57, 44, -1, 58, 71, 79, -1, 81, -118, 92, -1, 54, 101, 55, -1, 125, -120, 115, -1, -67, -66, -71, -1, -60, -63, -60, -1, -94, -99, -112, -1, 
		65, -126, 67, -1, 72, -120, 79, -1, -125, -74, -115, -1, 77, 104, 91, -1, 46, 41, 69, -1, 35, 23, 74, -1, 62, 40, 21, -1, 64, 41, 8, -1, 
		33, 39, 36, -1, 84, 110, 106, -1, 91, -108, 99, -1, 54, 113, 54, -1, 57, 90, 55, -1, 86, 105, 82, -1, 117, -121, 104, -1, 86, 109, 78, -1, 
		70, -122, 72, -1, 69, -122, 79, -1, 63, 93, 78, -1, 29, 28, 94, -1, 53, 57, -73, -1, 37, 38, -125, -1, 46, 30, 31, -1, 39, 28, 66, -1, 
		59, 79, 88, -1, 121, -85, -125, -1, 95, -102, 109, -1, 59, 122, 57, -1, 54, 102, 55, -1, 47, 90, 46, -1, 66, 118, 55, -1, 58, 118, 57, -1, 
		122, -79, -119, -1, 50, 47, 30, -1, 42, 21, 75, -1, 33, 36, -83, -1, 43, 45, -73, -1, 39, 40, -71, -1, 26, 29, -93, -1, 15, 21, -100, -1, 
		25, 38, 72, -1, -122, -73, -118, -1, -114, -65, -104, -1, 79, -116, 82, -1, 64, 114, 60, -1, 107, -120, 105, -1, 100, -122, 99, -1, 60, 119, 59, -1, 
		-94, -33, -79, -1, 100, 115, 92, -1, 55, 3, 7, -1, 36, 34, 99, -1, 40, 45, -101, -1, 27, 28, -113, -1, 37, 36, 122, -1, 45, 14, 48, -1, 
		37, 20, 18, -1, 96, -128, 99, -1, -111, -58, -99, -1, 86, -116, 86, -1, -113, -102, -125, -1, -48, -53, -49, -1, -60, -63, -63, -1, -120, -114, 121, -1, 
		-80, -25, -73, -1, -111, -62, -102, -1, 35, 47, 36, -1, 2, 1, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 72, 0, 6, -1, -86, 36, 1, -1, 
		-121, 48, 0, -1, 83, 109, 86, -1, -99, -48, -91, -1, 84, -117, 84, -1, 107, -128, 97, -1, -85, -80, -89, -1, -73, -71, -72, -1, -101, -103, -118, -1, 
		-95, -42, -87, -1, -92, -40, -85, -1, -102, -52, -93, -1, 95, 126, 100, -1, 49, 66, 53, -1, 31, 54, 35, -1, 44, 50, 29, -1, 72, 73, 50, -1, 
		89, 101, 69, -1, -121, -77, -114, -1, -109, -62, -102, -1, 78, -127, 80, -1, 47, 85, 46, -1, 72, 96, 69, -1, 102, 124, 87, -1, 75, 112, 69, -1, 
		120, -85, -116, -1, 117, -91, -121, -1, 122, -83, -115, -1, -127, -75, -109, -1, -116, -61, -97, -1, 95, -92, 108, -1, 64, -110, 77, -1, 117, -75, -109, -1, 
		117, -84, -112, -1, 108, -104, 124, -1, 105, -108, 122, -1, 70, 123, 73, -1, 51, 96, 51, -1, 56, 99, 55, -1, 75, 118, 60, -1, 52, 109, 51, -1, 
 };

	public static final byte[] bigIcon = { 107, -90, -122, -1, 93, -109, 123, -1, 95, -106, 125, -1, 118, -74, -112, -1, 121, -70, -110, -1, 107, -90, -122, -1, 101, -98, -126, -1, 118, -74, -112, -1, 
		107, -90, -122, -1, 89, -114, 120, -1, 79, -127, 108, -1, 66, 122, 69, -1, 69, -119, 69, -1, 71, -112, 71, -1, 70, -116, 70, -1, 67, -126, 67, -1, 
		66, -128, 66, -1, 64, 123, 64, -1, 63, 117, 63, -1, 63, 122, 63, -1, 63, 122, 63, -1, 65, -124, 65, -1, 81, -112, 66, -1, 108, -110, 63, -1, 
		89, -118, 64, -1, 104, -104, 67, -1, -75, -53, 69, -1, 82, -106, 70, -1, 75, -106, 75, -1, 76, -103, 76, -1, 74, -110, 74, -1, 67, -126, 67, -1, 
		87, -114, 109, -1, 74, -126, 90, -1, 79, -113, 96, -1, 99, -100, 126, -1, 101, -98, -127, -1, 87, -114, 109, -1, 81, -121, 101, -1, 99, -101, -128, -1, 
		91, -112, 121, -1, 81, -121, 108, -1, 79, -111, 94, -1, 66, 124, 68, -1, 71, -112, 71, -1, 73, -102, 73, -1, 71, -112, 71, -1, 65, 120, 65, -1, 
		63, 118, 63, -1, 63, 119, 63, -1, 65, -126, 65, -1, 63, 126, 63, -1, 61, 115, 61, -1, 61, 118, 61, -1, 62, 125, 62, -1, 62, 124, 62, -1, 
		62, 120, 62, -1, 90, -121, 64, -1, -102, -74, 67, -1, -95, -56, 72, -1, -91, -49, 76, -1, 121, -72, 77, -1, 76, -101, 76, -1, 72, -115, 72, -1, 
		72, -127, 83, -1, 68, -124, 68, -1, 76, -100, 78, -1, 78, -117, 99, -1, 77, -126, 99, -1, 71, 124, 82, -1, 68, 123, 75, -1, 76, 127, 99, -1, 
		76, 127, 99, -1, 77, -118, 93, -1, 76, -101, 76, -1, 49, 92, 49, -1, 50, 101, 50, -1, 51, 108, 51, -1, 50, 101, 50, -1, 47, 89, 47, -1, 
		55, 105, 55, -1, 65, -127, 65, -1, 67, -119, 67, -1, 66, -117, 66, -1, 63, 124, 63, -1, 60, 113, 60, -1, 61, 117, 61, -1, 67, -117, 67, -1, 
		65, -125, 65, -1, 62, 119, 62, -1, 73, 122, 62, -1, -73, -54, 69, -1, -23, -18, 72, -1, -69, -40, 73, -1, 112, -80, 75, -1, 78, -97, 78, -1, 
		69, -120, 69, -1, 69, -123, 69, -1, 79, -95, 79, -1, 73, -104, 73, -1, 68, -126, 70, -1, 66, 122, 69, -1, 66, 122, 69, -1, 66, 122, 69, -1, 
		66, 122, 69, -1, 66, 127, 68, -1, 61, 127, 60, -1, 21, 15, 8, -1, 16, 2, 2, -1, 16, 2, 2, -1, 16, 2, 2, -1, 20, 15, 8, -1, 
		40, 70, 33, -1, 62, -128, 62, -1, 59, 110, 59, -1, 66, -122, 68, -1, 66, -120, 66, -1, 61, 121, 61, -1, 62, 118, 62, -1, 70, -113, 70, -1, 
		68, -116, 68, -1, 64, -126, 64, -1, 60, 111, 60, -1, 63, 119, 63, -1, -77, -56, 69, -1, -19, -15, 72, -1, -56, -34, 72, -1, 86, -97, 72, -1, 
		65, 123, 65, -1, 65, 122, 65, -1, 66, 125, 66, -1, 66, 124, 66, -1, 75, -128, 96, -1, 79, -127, 108, -1, 79, -127, 108, -1, 79, -127, 108, -1, 
		79, -127, 108, -1, 58, 95, 78, -1, 21, 15, 8, -1, -116, 21, 20, -1, -102, 22, 22, -1, -102, 22, 22, -1, -102, 22, 22, -1, -116, 21, 20, -1, 
		80, 18, 14, -1, 6, 13, 6, -1, 12, 21, 14, -1, 64, 106, 88, -1, 62, 112, 69, -1, 57, 108, 57, -1, 57, 106, 57, -1, 58, 109, 58, -1, 
		58, 109, 58, -1, 58, 108, 58, -1, 59, 110, 59, -1, 70, -114, 70, -1, 83, -102, 72, -1, -97, -62, 70, -1, -30, -28, 68, -1, -55, -42, 68, -1, 
		68, -123, 68, -1, 71, -112, 71, -1, 70, -114, 70, -1, 66, 123, 66, -1, 76, 127, 99, -1, 95, -106, 125, -1, 109, -87, -120, -1, 101, -99, 127, -1, 
		67, 104, 85, -1, 52, 32, 29, -1, 124, 17, 17, -1, -94, 23, 23, -1, -90, 23, 23, -1, -93, 23, 23, -1, -87, 29, 23, -1, -67, 42, 22, -1, 
		-89, 39, 19, -1, -117, 34, 15, -1, -128, 34, 16, -1, 32, 34, 28, -1, 46, 84, 48, -1, 58, 114, 58, -1, 60, 123, 60, -1, 60, 121, 60, -1, 
		58, 114, 58, -1, 59, 112, 59, -1, 65, -124, 65, -1, 66, -120, 66, -1, 62, 123, 62, -1, -115, -87, 62, -1, -35, -35, 64, -1, -50, -44, 64, -1, 
		71, -125, 78, -1, 70, -114, 70, -1, 73, -105, 74, -1, 73, -128, 87, -1, 50, 83, 66, -1, 50, 79, 65, -1, 60, 93, 73, -1, 63, 85, 67, -1, 
		72, 52, 42, -1, 108, 15, 15, -1, -86, 25, 23, -1, -72, 36, 23, -1, -71, 37, 22, -1, -75, 36, 22, -1, -72, 40, 21, -1, -55, 50, 21, -1, 
		-57, 49, 21, -1, -60, 48, 20, -1, -74, 45, 19, -1, 62, 11, 5, -1, 34, 39, 21, -1, 43, 84, 43, -1, 60, 121, 60, -1, 61, -128, 61, -1, 
		58, 117, 58, -1, 58, 113, 58, -1, 65, -121, 65, -1, 60, 119, 60, -1, 57, 108, 57, -1, 98, -117, 59, -1, -93, -69, 63, -1, -46, -44, 63, -1, 
		73, -127, 89, -1, 69, -118, 69, -1, 74, -104, 77, -1, 80, -124, 109, -1, 49, 77, 64, -1, 49, 57, 31, -1, 54, 42, 14, -1, 44, 6, 4, -1, 
		91, 13, 10, -1, -108, 21, 18, -1, -105, 22, 18, -1, -84, 38, 18, -1, -82, 40, 18, -1, -85, 39, 17, -1, -88, 39, 17, -1, -88, 39, 17, -1, 
		-89, 38, 16, -1, -83, 41, 17, -1, -69, 45, 19, -1, -125, 26, 12, -1, 37, 7, 3, -1, 29, 55, 29, -1, 59, 117, 59, -1, 62, -127, 62, -1, 
		60, 121, 60, -1, 58, 114, 58, -1, 59, 121, 59, -1, 55, 105, 55, -1, 55, 103, 55, -1, 58, 113, 58, -1, 106, -106, 61, -1, -41, -41, 63, -1, 
		75, -116, 91, -1, 72, -103, 72, -1, 73, -108, 76, -1, 80, -125, 108, -1, 108, -88, -121, -1, -98, -73, 104, -1, -84, -124, 47, -1, 116, 16, 9, -1, 
		114, 16, 9, -1, 110, 16, 8, -1, 110, 15, 8, -1, 103, 15, 8, -1, 102, 15, 8, -1, 101, 15, 8, -1, 100, 15, 8, -1, 101, 15, 8, -1, 
		102, 15, 8, -1, -126, 25, 12, -1, -65, 47, 20, -1, -65, 47, 20, -1, 57, 14, 6, -1, 30, 61, 30, -1, 61, 124, 61, -1, 61, -128, 61, -1, 
		61, -128, 61, -1, 59, 120, 59, -1, 55, 102, 55, -1, 58, 100, 55, -1, 62, 104, 61, -1, 63, 106, 63, -1, 106, -120, 65, -1, -49, -49, 69, -1, 
		74, -120, 89, -1, 68, -116, 68, -1, 64, 120, 67, -1, 74, 119, 100, -1, 100, -101, 125, -1, 85, 114, 90, -1, 86, 47, 38, -1, -107, 21, 21, -1, 
		-108, 21, 20, -1, -108, 20, 20, -1, -109, 20, 20, -1, -114, 20, 20, -1, -115, 20, 20, -1, -116, 20, 19, -1, -117, 19, 19, -1, -113, 22, 19, -1, 
		-95, 32, 19, -1, -71, 45, 19, -1, -65, 47, 20, -1, -64, 47, 20, -1, 57, 14, 6, -1, 25, 28, 25, -1, 51, 70, 51, -1, 56, 105, 56, -1, 
		56, 105, 56, -1, 55, 104, 55, -1, 58, 100, 55, -1, 88, 80, 54, -1, 122, 121, 111, -1, -121, -117, -121, -1, -117, -114, -121, -1, -100, -100, -127, -1, 
		71, -125, 86, -1, 66, -118, 66, -1, 65, -126, 67, -1, 71, 116, 96, -1, 79, 125, 106, -1, 47, 71, 61, -1, 53, 15, 15, -1, -111, 21, 20, -1, 
		-125, 19, 16, -1, 113, 16, 11, -1, 117, 21, 12, -1, -105, 64, 24, -1, -102, 69, 25, -1, -102, 69, 25, -1, -103, 69, 25, -1, -102, 69, 25, -1, 
		-97, 72, 25, -1, -86, 91, 31, -1, -75, -127, 44, -1, -75, -127, 44, -1, 54, 38, 13, -1, 51, 31, 31, -1, 96, 69, 60, -1, 87, 89, 61, -1, 
		101, 108, 87, -1, 118, -124, 118, -1, 119, -125, 118, -1, -128, 124, 117, -1, -117, -119, -121, -1, -89, -89, -89, -1, -97, -82, -97, -1, 87, -123, 83, -1, 
		64, 119, 71, -1, 61, 123, 61, -1, 62, 122, 63, -1, 63, 110, 76, -1, 68, 112, 92, -1, 35, 56, 49, -1, 36, 5, 4, -1, 122, 21, 14, -1, 
		124, 34, 15, -1, -128, 51, 17, -1, -126, 55, 18, -1, -102, 85, 26, -1, -85, 117, 56, -1, -80, -127, 67, -1, -78, -122, 82, -1, -82, -115, 110, -1, 
		-116, 106, 75, -1, 123, 94, 47, -1, -81, -99, 75, -1, 97, 89, 32, -1, 68, 48, 32, -1, 81, 49, 42, -1, 107, 78, 65, -1, 121, 112, 103, -1, 
		-125, 126, 121, -1, -103, -103, -103, -1, -79, -79, -79, -1, -80, -80, -80, -1, 124, -109, 124, -1, 117, -110, 117, -1, 110, -112, 110, -1, 57, 114, 57, -1, 
		67, 122, 71, -1, 60, 116, 60, -1, 61, 116, 61, -1, 70, 121, 76, -1, 78, 125, 97, -1, 40, 63, 53, -1, 34, 11, 4, -1, 120, 42, 15, -1, 
		-115, 67, 26, -1, -90, 102, 38, -1, -93, 109, 34, -1, -100, 90, 27, -1, -72, -111, 84, -1, -61, -88, 108, -1, -59, -77, -120, -1, -65, -64, -65, -1, 
		122, -128, 122, -1, 83, 84, 58, -1, -82, -105, 97, -1, 18, 16, 10, -1, 84, 47, 47, -1, 106, 65, 55, -1, 102, 79, 63, -1, 122, 117, 110, -1, 
		118, 116, 112, -1, -128, -124, -128, -1, -93, -80, -93, -1, -94, -80, -94, -1, 89, -120, 89, -1, 57, 114, 57, -1, 55, 107, 55, -1, 54, 101, 54, -1, 
		87, -103, 99, -1, 64, -121, 64, -1, 68, -119, 70, -1, 99, -100, 119, -1, 106, -95, 127, -1, 61, 90, 71, -1, 52, 37, 17, -1, -111, 83, 28, -1, 
		-97, 97, 45, -1, -79, -128, 62, -1, -80, -103, 53, -1, -98, 100, 31, -1, -76, -115, 78, -1, -65, -93, 103, -1, -61, -80, -123, -1, -67, -66, -75, -1, 
		126, -115, 118, -1, 90, 106, 59, -1, -82, -106, 96, -1, 31, 23, 12, -1, 80, 44, 43, -1, 114, 63, 63, -1, 109, 64, 57, -1, 79, 59, 37, -1, 
		64, 53, 41, -1, 51, 63, 51, -1, 63, 105, 62, -1, 66, 108, 66, -1, 66, 109, 66, -1, 68, 120, 67, -1, 65, 122, 64, -1, 54, 102, 54, -1, 
		87, -105, 98, -1, 63, -123, 63, -1, 67, -121, 69, -1, 98, -102, 118, -1, -123, -73, -112, -1, -116, -70, -109, -1, 99, 127, 100, -1, 18, 12, 6, -1, 
		34, 28, 23, -1, 94, 71, 49, -1, -70, -128, 64, -1, -82, -105, 53, -1, -95, 112, 39, -1, -82, -126, 70, -1, -64, -89, 109, -1, -64, -88, 114, -1, 
		-71, -93, 107, -1, -75, -97, 101, -1, -70, -99, 98, -1, -114, 85, 31, -1, 48, 27, 11, -1, 65, 36, 36, -1, 118, 66, 65, -1, 109, 64, 61, -1, 
		81, 56, 54, -1, 77, 77, 72, -1, -102, -103, -118, -1, -79, -75, -80, -1, -76, -72, -76, -1, -89, -87, -98, -1, 126, -111, 113, -1, 59, 122, 59, -1, 
		85, -108, 96, -1, 62, -124, 62, -1, 66, -122, 68, -1, 98, -102, 117, -1, -121, -71, -110, -1, -107, -60, -101, -1, -121, -78, -115, -1, 95, 125, 99, -1, 
		58, 74, 60, -1, 33, 27, 23, -1, 93, 70, 49, -1, -83, 126, 61, -1, -75, -117, 76, -1, -70, -102, 94, -1, -64, -90, 106, -1, -64, -90, 106, -1, 
		-65, -91, 106, -1, -65, -91, 106, -1, -79, -104, 97, -1, 59, 38, 17, -1, 62, 82, 61, -1, 68, 79, 67, -1, 67, 51, 51, -1, 73, 58, 57, -1, 
		97, 87, 82, -1, -112, -117, -124, -1, -73, -75, -78, -1, -65, -65, -65, -1, -64, -64, -64, -1, -68, -69, -71, -1, -88, -85, -96, -1, 125, -113, 112, -1, 
		83, -112, 93, -1, 61, -127, 61, -1, 65, -124, 67, -1, 97, -103, 115, -1, -122, -73, -111, -1, -108, -61, -101, -1, -108, -61, -102, -1, -109, -63, -103, -1, 
		-116, -71, -110, -1, 94, 124, 99, -1, 5, 4, 4, -1, 16, 11, 6, -1, -120, 90, 51, -1, -70, 123, 70, -1, -70, 123, 70, -1, -70, 123, 70, -1, 
		-71, 123, 70, -1, -121, 91, 52, -1, 29, 30, 22, -1, 121, -98, 125, -1, 108, -98, 124, -1, 78, -122, 88, -1, 56, 110, 56, -1, 63, 99, 61, -1, 
		107, 124, 97, -1, -85, -90, -100, -1, -65, -65, -65, -1, -64, -64, -64, -1, -64, -64, -64, -1, -64, -64, -64, -1, -72, -74, -78, -1, -103, -103, -121, -1, 
		82, -113, 93, -1, 61, -128, 61, -1, 65, -125, 67, -1, 97, -104, 115, -1, -123, -74, -112, -1, -125, -76, -113, -1, 124, -81, -118, -1, -118, -75, -112, -1, 
		95, 126, 100, -1, 37, 47, 67, -1, 23, 23, 117, -1, 23, 23, 116, -1, 45, 32, 48, -1, 55, 35, 20, -1, 55, 35, 20, -1, 55, 35, 20, -1, 
		55, 35, 20, -1, 51, 39, 50, -1, 42, 47, 114, -1, 43, 56, 52, -1, 82, 123, 97, -1, 79, -117, 90, -1, 58, 118, 58, -1, 56, 99, 56, -1, 
		72, 103, 69, -1, 103, 119, 93, -1, -118, -102, 120, -1, -69, -67, -71, -1, -63, -63, -63, -1, -63, -63, -63, -1, -72, -74, -78, -1, -103, -105, -121, -1, 
		82, -113, 92, -1, 61, -128, 61, -1, 65, -126, 67, -1, 96, -104, 115, -1, -123, -74, -112, -1, -121, -72, -111, -1, 109, -105, 119, -1, 69, 90, 71, -1, 
		52, 60, 47, -1, 39, 28, 43, -1, 55, 37, 104, -1, 21, 19, 85, -1, 44, 31, 36, -1, 56, 36, 16, -1, 56, 36, 16, -1, 51, 33, 15, -1, 
		28, 18, 8, -1, 9, 10, 26, -1, 34, 39, 85, -1, 70, 91, 78, -1, 92, -119, 108, -1, 80, -115, 90, -1, 59, 124, 59, -1, 57, 111, 57, -1, 
		59, 100, 59, -1, 67, 93, 63, -1, 88, 113, 76, -1, 123, -119, 121, -1, 126, -117, 126, -1, -118, -105, -125, -1, -117, -105, 126, -1, 106, 119, 97, -1, 
		81, -114, 92, -1, 61, -128, 61, -1, 64, -126, 66, -1, 96, -105, 115, -1, -123, -74, -112, -1, 125, -91, -125, -1, 78, 102, 91, -1, 22, 23, 54, -1, 
		39, 36, 63, -1, 63, 49, 77, -1, 64, 38, 74, -1, 7, 4, 8, -1, 79, 51, 23, -1, 113, 73, 33, -1, 103, 67, 30, -1, 76, 52, 25, -1, 
		62, 54, 34, -1, 44, 58, 46, -1, 53, 70, 56, -1, -118, -74, -111, -1, 115, -89, -125, -1, 81, -114, 91, -1, 61, 127, 61, -1, 59, 118, 59, -1, 
		59, 106, 59, -1, 60, 90, 60, -1, 61, 87, 61, -1, 60, 86, 60, -1, 70, 96, 64, -1, 90, 117, 73, -1, 93, 120, 74, -1, 58, 91, 58, -1, 
		81, -113, 92, -1, 60, -128, 60, -1, 64, -125, 66, -1, 96, -105, 114, -1, 123, -87, -123, -1, 68, 89, 71, -1, 19, 21, 54, -1, 58, 64, -83, -1, 
		59, 65, -83, -1, 53, 58, -84, -1, 33, 33, -102, -1, 6, 6, 30, -1, 73, 47, 26, -1, 102, 66, 30, -1, 73, 47, 26, -1, 15, 19, 27, -1, 
		68, 89, 78, -1, -121, -77, -114, -1, -116, -69, -107, -1, -112, -64, -104, -1, 114, -90, -125, -1, 81, -113, 92, -1, 60, 122, 60, -1, 55, 104, 55, -1, 
		55, 103, 55, -1, 57, 98, 57, -1, 60, 89, 60, -1, 60, 88, 60, -1, 90, 120, 73, -1, 103, -123, 78, -1, 89, 125, 71, -1, 55, 103, 55, -1, 
		82, -112, 93, -1, 61, -126, 61, -1, 65, -124, 67, -1, 97, -104, 115, -1, 40, 59, 46, -1, 22, 24, 22, -1, 32, 32, 71, -1, 38, 39, -86, -1, 
		48, 52, -83, -1, 60, 67, -80, -1, 56, 61, -83, -1, 33, 33, -102, -1, 16, 14, 47, -1, 11, 7, 3, -1, 16, 14, 48, -1, 28, 29, -116, -1, 
		22, 24, 84, -1, 32, 47, 40, -1, 78, 120, 102, -1, 103, -100, 123, -1, 102, -99, 123, -1, 81, -113, 92, -1, 61, -127, 61, -1, 60, 125, 60, -1, 
		58, 115, 58, -1, 55, 102, 55, -1, 55, 101, 55, -1, 55, 100, 55, -1, 62, 121, 60, -1, 65, -127, 63, -1, 65, -126, 64, -1, 65, -125, 65, -1, 
		-125, -72, -117, -1, 93, -118, 97, -1, 19, 39, 20, -1, 29, 45, 34, -1, 26, 30, 96, -1, 29, 29, 126, -1, 34, 34, -113, -1, 34, 35, -85, -1, 
		38, 39, -84, -1, 42, 44, -83, -1, 41, 43, -83, -1, 34, 35, -86, -1, 27, 27, -121, -1, 24, 24, 120, -1, 27, 27, -121, -1, 31, 31, -102, -1, 
		17, 17, 85, -1, 37, 51, 41, -1, 126, -83, -118, -1, -122, -72, -111, -1, -121, -71, -110, -1, -127, -75, -119, -1, 106, -91, 109, -1, 65, -121, 65, -1, 
		63, -127, 63, -1, 58, 116, 58, -1, 55, 102, 55, -1, 55, 102, 55, -1, 56, 108, 56, -1, 56, 110, 56, -1, 58, 116, 58, -1, 62, -126, 62, -1, 
		-103, -53, -95, -1, 108, -113, 113, -1, 7, 2, 2, -1, 71, 16, 16, -1, 48, 29, 78, -1, 34, 34, -117, -1, 34, 34, -81, -1, 36, 36, -81, -1, 
		41, 43, -80, -1, 44, 47, -80, -1, 34, 34, -83, -1, 34, 34, -84, -1, 34, 34, -84, -1, 34, 34, -85, -1, 34, 34, -85, -1, 31, 31, -101, -1, 
		17, 17, 85, -1, 44, 58, 46, -1, -107, -59, -100, -1, -107, -59, -100, -1, -107, -59, -100, -1, -107, -59, -100, -1, 123, -81, -128, -1, 61, 120, 61, -1, 
		60, 118, 60, -1, 74, 121, 71, -1, 113, -123, 103, -1, 125, -108, 124, -1, 126, -106, 126, -1, 118, -116, 113, -1, 94, 125, 87, -1, 58, 116, 58, -1, 
		-106, -53, -96, -1, 125, -91, -125, -1, 54, 60, 48, -1, 105, 28, 27, -1, 84, 32, 32, -1, 49, 30, 80, -1, 30, 31, -114, -1, 45, 48, -73, -1, 
		50, 53, -74, -1, 49, 52, -76, -1, 34, 34, -80, -1, 34, 34, -82, -1, 40, 41, -81, -1, 33, 34, -108, -1, 24, 24, 120, -1, 22, 22, 109, -1, 
		12, 12, 60, -1, 31, 41, 32, -1, 109, -113, 113, -1, -111, -64, -104, -1, -116, -67, -107, -1, -113, -64, -104, -1, 123, -84, -128, -1, 60, 111, 59, -1, 
		73, 115, 70, -1, 115, -121, 106, -1, -77, -82, -91, -1, -61, -61, -62, -1, -59, -59, -59, -1, -71, -73, -78, -1, -108, -103, -120, -1, 84, 117, 78, -1, 
		-115, -58, -100, -1, -93, -41, -86, -1, -108, -61, -102, -1, 15, 20, 16, -1, 104, 24, 24, -1, 74, 17, 17, -1, 18, 20, 52, -1, 58, 65, -81, -1, 
		46, 50, -87, -1, 32, 32, -94, -1, 32, 32, -94, -1, 31, 31, -98, -1, 49, 54, -94, -1, 36, 33, 83, -1, 15, 2, 3, -1, 16, 3, 2, -1, 
		18, 6, 1, -1, 14, 7, 0, -1, 14, 19, 15, -1, -119, -75, -113, -1, 120, -83, -120, -1, -127, -75, -114, -1, 124, -77, -127, -1, 72, -123, 70, -1, 
		114, -111, 104, -1, -80, -85, -95, -1, -59, -59, -59, -1, -61, -61, -61, -1, -61, -61, -61, -1, -62, -62, -62, -1, -72, -74, -78, -1, -102, -105, -121, -1, 
		-91, -37, -83, -1, -91, -39, -84, -1, -107, -60, -100, -1, 15, 20, 16, -1, 11, 2, 2, -1, 8, 2, 2, -1, 2, 2, 5, -1, 6, 7, 18, -1, 
		5, 5, 17, -1, 3, 3, 17, -1, 3, 3, 17, -1, 3, 3, 16, -1, 5, 6, 17, -1, 78, 12, 21, -1, -106, 18, 24, -1, -102, 25, 22, -1, 
		-85, 56, 12, -1, -120, 66, 0, -1, 14, 19, 15, -1, -119, -76, -113, -1, -108, -60, -100, -1, -108, -60, -101, -1, 123, -80, 127, -1, 71, -124, 69, -1, 
		113, -113, 102, -1, -83, -88, -98, -1, -62, -62, -62, -1, -64, -64, -64, -1, -64, -64, -64, -1, -64, -64, -64, -1, -73, -75, -79, -1, -102, -103, -121, -1, 
		-86, -33, -79, -1, -91, -39, -84, -1, -96, -45, -89, -1, 118, -101, 123, -1, 34, 44, 35, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 
		0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 84, 10, 14, -1, -114, 17, 23, -1, -82, 37, 21, -1, -57, 80, 7, -1, 
		-52, 90, 4, -1, -108, 71, 0, -1, 14, 18, 15, -1, -120, -78, -115, -1, -106, -59, -100, -1, -107, -60, -101, -1, 122, -81, 126, -1, 63, 126, 63, -1, 
		76, 117, 72, -1, 103, 120, 93, -1, -118, -101, 120, -1, -70, -68, -72, -1, -65, -65, -65, -1, -64, -64, -64, -1, -73, -75, -79, -1, -103, -103, -122, -1, 
		-86, -32, -78, -1, -92, -39, -84, -1, -92, -39, -84, -1, -94, -42, -86, -1, 105, -117, 110, -1, 81, 107, 84, -1, 56, 74, 59, -1, 0, 0, 0, -1, 
		0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 59, 7, 10, -1, 83, 10, 14, -1, 89, 22, 9, -1, 105, 51, 0, -1, 
		105, 51, 0, -1, 95, 64, 23, -1, 80, 106, 84, -1, -115, -70, -109, -1, -108, -61, -102, -1, -109, -62, -102, -1, 120, -86, 125, -1, 57, 110, 57, -1, 
		58, 99, 58, -1, 66, 93, 62, -1, 86, 112, 74, -1, 121, -121, 120, -1, 125, -118, 125, -1, -119, -106, -126, -1, -118, -106, 125, -1, 105, 120, 96, -1, 
		-87, -32, -79, -1, -92, -40, -85, -1, -92, -40, -85, -1, -93, -40, -85, -1, -93, -40, -85, -1, -95, -43, -87, -1, 126, -89, -124, -1, 47, 62, 49, -1, 
		46, 61, 48, -1, 41, 56, 44, -1, 29, 46, 35, -1, 19, 39, 20, -1, 18, 39, 18, -1, 24, 42, 27, -1, 34, 49, 39, -1, 44, 58, 46, -1, 
		44, 58, 46, -1, 74, 98, 77, -1, -110, -63, -103, -1, -109, -63, -103, -1, -109, -63, -103, -1, -110, -63, -103, -1, 119, -89, 124, -1, 55, 105, 55, -1, 
		56, 98, 56, -1, 58, 88, 58, -1, 59, 86, 59, -1, 59, 85, 59, -1, 69, 95, 63, -1, 89, 115, 72, -1, 92, 122, 73, -1, 58, 98, 58, -1, 
		-97, -43, -87, -1, -101, -50, -92, -1, -101, -50, -92, -1, -100, -48, -90, -1, -100, -48, -90, -1, -103, -51, -93, -1, -105, -55, -96, -1, -105, -55, -96, -1, 
		-107, -57, -98, -1, -122, -70, -109, -1, 99, -100, 119, -1, 65, -123, 67, -1, 61, -126, 61, -1, 81, -114, 91, -1, 113, -91, -126, -1, -113, -65, -105, -1, 
		-115, -68, -107, -1, -117, -71, -109, -1, -117, -71, -109, -1, -117, -72, -109, -1, -117, -72, -110, -1, -118, -72, -110, -1, 114, -90, 121, -1, 59, 121, 59, -1, 
		56, 111, 56, -1, 55, 95, 55, -1, 58, 87, 58, -1, 59, 89, 59, -1, 89, 118, 72, -1, 101, -126, 77, -1, 88, 127, 71, -1, 58, 120, 58, -1, 
		87, -121, 116, -1, 84, -125, 112, -1, 84, -125, 112, -1, 83, -126, 111, -1, 83, -126, 111, -1, 82, -128, 109, -1, 81, 126, 107, -1, 84, -127, 109, -1, 
		95, -111, 118, -1, 108, -92, -128, -1, 101, -98, 120, -1, 66, -122, 68, -1, 62, -125, 62, -1, 82, -113, 92, -1, 103, -99, 124, -1, 103, -100, 123, -1, 
		90, -118, 113, -1, 74, 116, 100, -1, 74, 116, 99, -1, 74, 115, 99, -1, 74, 115, 99, -1, 74, 115, 99, -1, 68, 111, 85, -1, 54, 101, 54, -1, 
		53, 100, 53, -1, 53, 99, 53, -1, 54, 100, 54, -1, 58, 119, 58, -1, 62, 124, 60, -1, 61, 114, 58, -1, 57, 102, 55, -1, 53, 101, 53, -1, 
	};

	public LwjglOpenGLGestion() {
		super(title);
	}

	public LwjglOpenGLGestion(boolean fullscreen) {
		super(title, fullscreen);

		z = 0.0f;
	}

	@Override
	protected void mainloopExt() {

		EngineZildo.extraSpeed = 1;
		if (kbHandler.isKeyDown(LwjglKeyboardHandler.KEY_LSHIFT)) {
			EngineZildo.extraSpeed = 2;
		}
	}

	@Override
	public void render(boolean p_clientReady) {

		// Clear the screen and the depth buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); 

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity(); // Reset The Projection Matrix

		// invert the y axis, down is positive
		float zz = z * 5.0f;
		if (zz != 0.0f) {
			GL11.glTranslatef(-zoomPosition.getX() * zz, zoomPosition.getY() * zz, 0.0f);
		}
		GL11.glScalef(1 + zz, -1 - zz, 1);
		if (ClientEngineZildo.filterCommand != null) {
			ClientEngineZildo.filterCommand.doPreFilter();
		}

		clientEngineZildo.renderFrame(awt);
		/*
		if (!p_clientReady && !awt) {
			clientEngineZildo.renderMenu();
		}
*/
		for (GameStage stage : ClientEngineZildo.getClientForGame().getCurrentStages()) {
			stage.renderGame();
		}
		
		if (ClientEngineZildo.filterCommand != null) {
			ClientEngineZildo.filterCommand.doFilter();
			ClientEngineZildo.filterCommand.doPostFilter();
		}

		if (framerate != 0) {
			Display.sync(framerate);
		}

		if (!awt) {
			Display.update();
		}
	}

	@Override
	protected void cleanUpExt() {
		ClientEngineZildo.cleanUp();
	}


	/**
     * Switch display with given fullscreen mode (TRUE or FALSE=windowed)
     * @param p_fullscreen
     */
	@Override
    public void switchFullscreen(boolean p_fullscreen) {
            fullscreen = p_fullscreen;
            try {
                    if (fullscreen) {       // Hide mouse in fullscreen
                            Mouse.setGrabbed(true);
                    } else {
                            Mouse.setGrabbed(false);
                            Display.setDisplayMode(displayMode);                    
                    }
                    Display.setFullscreen(p_fullscreen);
            } catch (LWJGLException e) {
                    throw new RuntimeException("Failed to switch full/windowed mode");
            }
	}

	private void showDisplayMode(DisplayMode d) {
         System.out.println("mode: " + d.getWidth() + "x" + d.getHeight() + " "
                         + d.getBitsPerPixel() + "bpp " + d.getFrequency() + "Hz");
	}

	private void createWindow() throws Exception {
		Display.setFullscreen(fullscreen);
		DisplayMode d[] = Display.getAvailableDisplayModes();
		List<DisplayMode> selecteds = new ArrayList<DisplayMode>();
		for (DisplayMode element : d) {
			if (element.getWidth() == Zildo.screenX && element.getHeight() == Zildo.screenY
			// && d[i].getBitsPerPixel() == 32
			) {
				selecteds.add(element);
			}
		}

		// Sort display modes from best to worse
		Collections.sort(selecteds, new DisplayModeComparator());

		boolean success = false;
		for (DisplayMode dm : selecteds) {
			displayMode = dm;
			showDisplayMode(dm);
			success = initDisplayMode(dm);
			if (success) {
				break;
			}
		}
		if (!success) {
			throw new RuntimeException("Unable to set up screen !");
		}
	}

	public boolean initDisplayMode(DisplayMode d) {

		try {
			Display.setDisplayMode(displayMode);
			Display.setTitle(windowTitle);
			framerate = Display.getDisplayMode().getFrequency();
			Display.create();
		} catch (LWJGLException e) {
			Display.destroy();
			return false;
		}
		return true;
	}

	@Override
	public void initDisplay() throws Exception {
        createWindow();
	}

	@Override
	public void init() {
        initAppIcon();
        initGL();
	}

	private void initAppIcon() {
        ByteBuffer iconBuf = ByteBuffer.allocate(16 * 16 * 4);
        iconBuf.put(LwjglOpenGLGestion.icon);
        iconBuf.flip();
        ByteBuffer bigIconBuf = ByteBuffer.allocate(32 * 32 * 4);
        bigIconBuf.put(LwjglOpenGLGestion.bigIcon);
        bigIconBuf.flip();
        Display.setIcon(new ByteBuffer[] { iconBuf, bigIconBuf });
	}

	private void initGL() {
        GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
        GL11.glClearDepth(1.0f); // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

        // initProjectionScene();

        GL11.glEnable(GL11.GL_CULL_FACE);

        ByteBuffer temp = ByteBuffer.allocateDirect(16);
        temp.order(ByteOrder.nativeOrder());
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, (FloatBuffer) temp
                        .asFloatBuffer().put(lightAmbient).flip()); // Setup The Ambient
                                                                                                                // Light
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, (FloatBuffer) temp
                        .asFloatBuffer().put(lightDiffuse).flip()); // Setup The Diffuse
                                                                                                                // Light
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, (FloatBuffer) temp
                        .asFloatBuffer().put(lightPosition).flip()); // Position The
                                                                                                                        // Light
        GL11.glEnable(GL11.GL_LIGHT1); // Enable Light One

        // GL11.glEnable(GL11.GL_LIGHTING);

        Display.setVSyncEnabled(true);

	}

	@Override
	public void cleanUp() {
		cleanUpExt();
        Display.destroy();
        Mouse.destroy();
	}

	@Override
	public boolean mainloop() {
		boolean done = false;
        if (!awt) {
                if (Display.isCloseRequested()) { // Exit if window is closed
                        done = true;
                }

                mainloopExt();
        }

        return done;
	}

	@Override
	public double getTimeInSeconds() {
		if (ticksPerSecond == 0) { // initialize ticksPerSecond
            ticksPerSecond = Sys.getTimerResolution();
		}
		return (((double) Sys.getTime()) / (double) ticksPerSecond);
	}
	
	@Override
	public ByteBuffer capture() {
		GL11.glReadBuffer(GL11.GL_FRONT);
		int width = Zildo.screenX;
		int height= Zildo.screenY;
		int bpp = 3; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer );
		
		return buffer;
	}

}

# Sprite storage specifics #

Sprite are stored in home made bank file, associated with a 256x256 texture. In the bank file we find usual data but there's some specifics. Here is what we store for each sprite
 * width and height
 * location on associated texture (x,y)
 * offsetY
 * offsetX1 and offsetX2
 
Everything is stored as unsigned bytes. Nothing fancy about the 2 first lines, but this page will focus on the latest 2: offsets.

Some sprite may have blank spaces in order to be displayed correctly ingame. One solution would be to save the sprite with blank part and display it without concern. But to minimize the number of texture files, we have to save some space in the 256x256 texture. So offsets are here for that purpose.

Each X offset are about the blank on the left and on the right of the sprite. Because in the rendering process, every sprites is located with a formula involving width and height:
``` 
    screenX = x + width / 2
    screenY = y - height
```

Note that these formulas are different depending on the nature of the sprite (Element or Perso). Anyway, with the offset, we can complete this formula recreating the blank spaces:
``` 
    screenX = x - (width + offsetX1 + offsetX2) / 2
    screenY = y - height + offsetY
```
We need to separate offsetX1 and offsetX2 because of reverse possibility. If sprite must be displayed with an horizontal symetry, the x formula becomes:
``` 
    screenX = x - (width + offsetX1 - offsetX2) / 2
```
** Y particularity **

Here is an example of a sprite with an offsetY:

The offsetY field can be negative.

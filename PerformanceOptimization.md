

# Goal of this document #

Going on with Android development, speed isn't the same at all than in standard platforms (PC, Mac). A real effort need to be done in the engine to drastically optimize performances.

And this concerns several sections of the game :
  * graphic engine : tile and sprite rendering
  * collision : sprite and characters
  * common : unrelated with specific part, but getting performance down either

# Graphic Engine #

## Tiles ##

There was 2 problems at the tiling engine, as it was in the 1.095 release.
  1. We were iterating over the whole map, even if current tile isn't displayed at screen.
  1. We were updating every tile in the graphic buffers, even if it hasn't changed.

So these 2 points need to be addressed. Each one could be easily done separately. But the implementation of both was problematic. We need to manage a buffer of tiles actually rendered at screen, and update only those which changed during the frame.

To get rid of this, I choose a system of combining buffers :
  * one representing the complete grid :

## Sprites ##

# Collision #

## Sprites ##

## Characters ##

# Common #

## Map ##

### HashMap --> Array ###

The area player is visiting was stored as an HashMap. With an Integer key, we got a Case object, containing all informations about a specific tile. This has to be replaced by an array, faster than a map, because we avoid to compare keys.

### Tile Translations ###

No need to translate tile coordinates when player moves around. We can do this by 'glTranslate'.
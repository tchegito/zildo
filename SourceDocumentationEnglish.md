# Technical Documentation #



Project is divided into 3 parts:
  * **fwk** : framework
  * **gui** : user interface
  * **monde** : "business", or model

This is a kind of a MVC, where [fwk](http://code.google.com/p/zildo/source/browse/trunk/zildo/src/zildo/#zildo/fwk) represents the controler layer, but it is mainly the technical container for Zildo, first of all.

The GUI part, still short, stands for everything surrounding game visual, in addition of tiles and sprites.

All the RPG side, qualified as "business", is in the ["monde"](http://code.google.com/p/zildo/source/browse/trunk/zildo/src/zildo/monde/) package.

## zildo.fwk ##

Coming soon...

## zildo.gui ##

Coming soon...

## zildo.monde ##

### 1.1) The tiles ###

Tiles are the 16x16 sections that are composing the map for every living creatures in Zildo's world.

We distinguish 2 kinds of tiles : foreground and background. Foreground tiles can hide some people approaching, like trees, house's doors, or village entrance.

Tiles can be animated, like flowers, or water on the seashore.

The class that modelizing these tiles is [Case](http://code.google.com/p/zildo/source/browse/trunk/zildo/src/zildo/monde/map/Case.java).

### 1.2) The sprites ###

The class that defines every sprite, from the most static to the most evoluate is `SpriteEntity`. This class represents the minimal informations to modelize any entity on a map.

From this class, we derive to complexify the entity concept and we can observe the following class hierarchy:
```
SpriteEntity
|
+->Element
   |
   +->Perso
```
We got here 3 main classes to design every entity that can be found on a map.

> #### a/ `SpriteEntity` [(SVN)](http://code.google.com/p/zildo/source/browse/trunk/zildo/src/zildo/monde/sprites/SpriteEntity.java) ####

The first one stand for the static elements, that can't move, and doesn't block anybody. We have for example : the bushes leafs that are vanishing, the smoking pipes on house's roofs, ...

This class contains coordinates (X, Y) on the screen, graphic informations, and various flags (visible, fore/back ground, visual effects ...)

> #### b/ `Element` [(SVN)](http://code.google.com/p/zildo/source/browse/trunk/zildo/src/zildo/monde/sprites/elements/Element.java) ####

This is a more particular class, that represents a moving entity in the world. It's under the physic laws. Furthermore, it can block characters or elements coming right away.

It has coordinates (X, Y, Z) on the map, speed, acceleration, and friction forces. It can be linked to another entity.

> #### c/ `Perso` [(SVN)](http://code.google.com/p/zildo/source/browse/trunk/zildo/src/zildo/monde/sprites/persos/Perso.java) ####

The most evoluated of the basic classes represents a character on a map. We got here informations about behavior, action area, friendly/enemy attitude, or dialogs position.

> #### d/ More complete hierarchy ####

These basic classes can be filled out by another ones, for more precise elementst, or characters, about their behavior or graphic display. Here is a more complete hierarchy:

http://lh4.ggpht.com/_q_5wHG9LsPk/SfRjrW3Cf_I/AAAAAAAABJE/Q02fzQO9vtk/s800/spriteClassesHierarchy.JPG

We can see that characters are divided into 2 categories :
  * the unplayables (`PersoNJ`)
  * the playable (`PersoZildo`) : there is just one for now
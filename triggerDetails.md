# TriggerElement details #

## location ##

 When hero reach a certain area: map, specific location on the map, platform, button, precise tile value...
 
 * **name** : map name
 * **mover** : name of the mover (optional)
 * **radius** : radius in tile units, can be negative (optional)
 * **gear** : gear
 * **pos** : location in pixel coordinates
 * **tilePos** : 
 * **tileValue** : value of a tile
 
 Example:
  ```xml
             <location name="promenade2" pos="422,230" radius="2"/>
             <location name="voleursg5" tilePos="52,26" gear="TIMED_BUTTON"/>
             <location name="voleursg5" mover="platform2"/>
 ```
 
 About radius:
 - a positive one means "match if hero is inside a X-tiles circle"
 - a negative one means "match if hero is away at least from a abs(X)-tiles circle"           
 
## dialog ##

 When a character speak one of its planned sentence.
 
 * **name** : character's name
 * **num** : index of his sentence
 
## inventory ##

 When hero has a kind of item.
 
 * **name** : item name (from ItemKind enum), possibly preceded by "!" to negate the condition
  
## questdone ##

 When some quests state meet the expression.
 
 * **name** : ZSSwitch expression (example: "backCoucou-!retour_trion")
 
## dead ## 

 When a character gets killed.

 * **name** : character's name separated by commas (example: "s1,s2")
 
## push ##
 
 When an element is being pushed by hero.
 
 * **name** : name of the pushed element
 * **angle** : push direction

## lift ##

 When hero picks up some thing (jar, bushes ...).

 * **name** : map name
 * **tilePos** : location on the map
 
## use ##

 When hero uses a specific item.
 
 * **name** : kind of item (member of ItemKind)
 
## fall ##

 When an element falls on a tile.
 
 * **type** : member of ElementDescription
 * **nature** : member of TileNature (optional)
 * **name** : name of fallen element
 
## chainingpoint ##

 When hero reach a map's chaining point.
 
## tileattack ##

 When hero hits something on a tile, or something falls on a tile (except tiny rocks).
 
 * **tilePos** : location in tile coordinates
 
 

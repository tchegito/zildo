

# Advanced Scripting #

## Contextual actions ##

There is two kinds of context which can be assigned to an execution script:
  * sprite : referring to an actor (character) or a simple entity
  * tile : referring to a tile position

In both context, special variables are allowed:
  * **x** and **y** : coordinates of the sprite/tile, each one on its referential
  * **z** : z-coordinate (only for sprite context)
  * **attente** : value of character's 'attente' field (only for character context)
  * **self** : name of the character
  * **zildo.x** and **zildo.y** : hero's coordinates
  * **zildo.z** : hero's *z* coordinate
  * **zildo.scrX** and **zildo.scrY** : hero's screen coordinates
  * **zildo.money** : hero's gold pieces amount
  * **zildo.angle.x** and **zildo.angle.y** : hero's angle coordinate (example: [0, -1] for north, [1, 0] for east)

## Local variables ##

<p>We can use local variables in scripts, defined with a name starting with "<code>loc:</code>".</p>
A local variable is visible in its own scope, and sub sets, as in any classic language. For example:
```xml
    <tileAction id="fireflies">
        <spawn what="loc:firefly" type="PURPLE_FIREFLY" pos="x*16, y*16"
               z="4" alpha="180" foreground="true" />
       	<timer each="80+random*15">
            <action>
		<moveTo what="loc:firefly" pos="x*16+random*40,y*16+random*30" way="circular"
                        zoom="128+bell*128" unblock="true"/>
            </action>
        </timer>
    </tileAction>
```
Here, variable `loc:firefly` is visible everywhere inside this `tileAction` because it's defined at its top level. So inside the `timer` scope, we can access it, as we do in the `moveTo` action.

## Conditions ##

The "<code>if</code>" tag is available to check some conditions about two sides:
  * variables value
  * quests state

On each one, we can combine using operators, for example with variables:
```xml
    <if exp="loc:duration=7 | loc:duration=14">
        actions
    </if> 
```

And with quests state:
```xml
    <if expQuest="!igor_promise_sword">
        actions
    </if> 
```
It is possible to use both attributes <code>exp</code> and <code>expQuest</code>. In this case, a "OR" operator is implicit between them.
```xml
    <if expQuest="dragonShouldMove1" exp="loc:dragonPos!">
        actions
    </if>
```
## Built-in functions ##

<p>There is several useful built-in functions which an be used in any scripts:</p>

  * **dice10** : returns a integer x as followed: 0 <= x < 10
  * **random** : returns a float f as followed: 0 <= f < 1

<p>There's also functions with parameters, for example:</p>

 * **mapFloor(x, y)** : returns the floor at given location (in pixel coordinates)
 
 To use such functions, you have to prefix it by "fun:" keyword. Here's an usage:
 ```xml
    <if exp="fun:mapFloor(zildo.x,zildo.y)=zildo.floor">
```
### Special functions ###

In a circular `moveTo` action (see tileAction 'fireflies' just above) we can use **bell** keyword. It provides a smooth function for going from one value to another one, describing a kind of bell, with a simple sinus function.

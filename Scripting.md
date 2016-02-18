


# Model Description #

The scripting module in Zildo is divided into two essentials sections :
  * scenes
  * quests

## Main elements ##

All is described in a set of XML files, each one containing an ADVENTURE root. The division of files is only intended to separate adventure in episodes, to enhance understanding.

But let's see how works the 2 sections.

### Scenes ###

A scene is a cinematic sequence, where player can't act. He just has to show events happening like in a movie.

Basically, it's a list of mini-actions which are launched sequentially. Some can be launched simultaneously. (see the 'Misc' section)

### Quests ###

A quest is an objective for the player. It has 3 parts:
  * **trigger** : what the player has to do in order to accomplish the quest
  * **action** : direct actions from the accomplishment
  * **history** : consequences in the history (map replacements ...)

A quest has a name and a state during the game, which can be 'done' or 'undone'.

_Note_ : when loading a saved game, every accomplished quest launches the execution of each 'history' part, but not 'action' part.

#### Attributes ####

  * repeat : 'false' (by default) means only one execution of this quest is possible
  * locked : 'true' by default => it means that the triggering of this quest freeze the game

### Mapscript ###

This is a set of "condition" happening when player arrives on a new map. So it's about a map name, and a combination of quests being accomplished or not.

**Example** :
```
    <condition name="prisonext" exp="retour_trion-!hector_call3">
        <!-- Repeat Pssst if player hasn't seen Hector yet -->
        <markQuest name="hector_call1" value="false"/>
        <markQuest name="hector_call2" value="false"/>
    </condition>
```

That means : " **When** `retour_trion` is accomplished and `hector_call3` is not, **then** mark the following quests as 'not done' "

But it can trigger too when no map name is specified at all. It's useful for event happening whatever location player is at, but concerning a particular situation, like night time, or people following hero.

#### All attributes ####

  * **name** : map (optional)
  * **exp** : quests combination
  * **scroll** : true/false (optional, means whatever transition)

## Trigger & Action ##

### Trigger ###

A trigger can be of 10 different kinds : (corresponding to [QuestEvent](https://github.com/tchegito/zildo/blob/master/zildo/src/zildo/monde/quest/QuestEvent.java) )

#### Types ####

  * **location** : when a character reach a given location
    * name: map name
    * pos: location, in pixel coordinates
    * radius: radius around the point indicated by 'pos' (in tile coordinates)
    * tilePos: location, in tile coordinates
    * gear: mechanism type (BUTTON, TIMED\_BUTTON)
    * mover: name of a moving platform
    * tileValue: a value for a given tile (bank\*256 + index)
  * **dialog** : when a specific character say a given sentence
  * **inventory** : does Zildo have a given item ?
  * **questDone** : has Zildo achieved a given quest ?
  * **dead** : does a set of creatures died ?
  * **push** : is someone pushing an element ?
  * **lift** : is hero taking some jar/bush ?
  * **use** : does hero use one of his items ?
  * **fall** : does an element/character falls on the floor ? (water, lava, or any ground)
  * **chainingPoint** : means that this quest is a chaining point acceptance (can accept/reject current point)

#### Particular cases ####

To handle some automatic events, like "hero opens a chest", or "hero unlocks a door", we need to remember that players did it. So it's represented as a quest in the savegame.

To name such quest, we use a simple mechanism:
```
questName = "<mapName><location>"```

For example, the following is the name of a quest happening on map "voleursm2u" at location (4,3) :
```
"voleursm2u(4, 3)"```

It means that we can run actions when this particular event is triggered, just with declaring such quest with this name in the XML file. Then, this quest won't need any trigger. Here is the quest corresponding to the previous example:
```
    <quest name="voleursm2u(4, 3)">
    	<action>
    	    <speak who="zildo" text="voleurs.m2u.money"/>
    	</action>
    </quest>
```

### Action ###

Every action is a parametered command. Here is an exhaustive list of all existing actions :

| **Command** | **Type** | **Who** | **What** | **Unblock** | **Meaning** |
|:------------|:---------|:--------|:---------|:------------|:------------|
| activate    | text     | -       | V        | V           | Activate a gear (door, button, ...) with boolean 'value' to turn on/off the gear (according to TRUE/FALSE value) |
| angle       | int      | V       | -        | -           | Set character's angle |
| animation   | text     | -       | -        | -           | Spawn an animation from the enum [SpriteAnimation](https://github.com/tchegito/zildo/tree/master/zildo/src/zildo/monde/sprites/desc/SpriteAnimation.java). _what_ attribute permits to name the future animation element. _who_ attribute allow to localize animation on a specific character. |
| attack      | text     | V       | -        | -           | Make a given character attacking in front of him |
| end         | text     | -       | -        | -           | For game over, depending on type attribute : 0=player wins / 1=player dies |
| exec        | text     | -       | -        | -           | Execute a named script |
| fadeIn      | int      | -       | -        | V           | Start a fade in, with given effect |
| fadeOut     | int      | -       | -        | V           | Start a fade out, with given effect |
| filter      | text     | -       | -        | -           | Add a color filter : 0=normal / 1=night / 2=evening / 3=red / 4=lightning |
| focus       | text     | V       | -        | -           | Focus on given character (if delta is TRUE, camera moves smoothly) |
| herospecial | text     | -       | -        | -           | Special actions about hero, set accordingly with 'value'.<ul><li>0 => Hero gains 1 blue drop, and loose 2 moon fragments.</li><li>1 => Blocks everyone except hero.</li><li>2 => Unblocks everyone except hero.</li><li>3 => back up current game</li><li>4 => reload last backed up game, updated with current hero's HP. </li><li>5 => inflict damage to hero, given by 'arg' attribute. Default is 1.</li></ul> |
| if          | text     | -       | -        | -           | Check the 'exp' expression (based on context variables), and if it's true, then nesting actions will be executed. Check for 'expQuest' too, based on accomplished quests.|
| impact      | text     | -       | -        | -           | Spawn an animation from the enum [ImpactKind](https://github.com/tchegito/zildo/tree/master/zildo/monde/sprites/elements/ElementImpact.java) |
| lookFor     | text     | -       | -        | -           | Look for some characters around another, with a desired radius. The first discovered one become the new context for nested action elements. Field 'info' precise a PersoInfo enum, and 'negative' could be used to detect the opposite: character away from another. |
| map         | text     | -       | -        | -           | Load the given map |
| mapReplace  | text     | -       | V        | -           | Replace map indicated by 'what' by the one named by 'name'  |
| markQuest   | text     | -       | -        | -           | Accomplish/reset a quest identified by 'name' attribute, according to the 'value' attribute (0=cancel / 1=accomplish) |
| moveTo      | position | V       | V        | V           | Move character or camera's location, progressively. Can indicate a way of moving among "physic", "arc", and "circular". Default is rectilinear movement. |
| music       | text     | V       | V        | V           | Play a given music|
| perso       | text     | -       | -        | -           | Change some character's attributes : type, info, fx. 'action' attribute is available to set a PersoAction. |
| pos         | position | V       | V        | -           | Set character or camera's location |
| putDown     | text     | -       | -        | -           | Remove an item from Zildo's inventory |
| remove      | text     | V       | V        | -           | Remove an element (what), a character (who) or a chaining point (chaining) from the current map. Without parameters, this command removes all sprites except Zildo. |
| respawn     | text     | -       | -        | -           | Respawn player at his starting location in the current area. |
| script      | int      | V       | -        | -           | Set character's script. _value_ attribute points to an index in [MouvementPerso](https://github.com/tchegito/zildo/tree/master/zildo/src/zildo/monde/sprites/utils/MouvementPerso.java). _text_ attribute points to an item name in [MouvementZildo](https://github.com/tchegito/zildo/tree/master/zildo/src/zildo/monde/sprites/utils/MouvementZildo.java). |
| sound       | text     | V       | V        | V           | Play a given sound (default location is hero's one.) |
| spawn       | text     | V       | V        | -           | Spawn a new character/sprite. Supports as attributes : who/what, delta, reverse, rotation, shadow, x, y, z, vx, vy, vz, ax, ay, az, fx, fy, fz, alpha, alphaA, pos, angle, type, info, attente, action, weapon, zoom. Can spawn a chained element with "chained" attribute expecting "n, delay" where 'n' is the number of trailing sprites, and 'delay' the number of frames between each one. |
| speak       | text     | V       | V        | V           | Launch a dialog |
| sprite      | text     | -       | -        | -           | Change a sprite's ("what") attribute ("type" or "reverse"). |
| take        | text     | V       | V        | -           | A character (Zildo for example) takes an object or money. Could be negative value, if Zildo buy something. |
| tile        | text     | -       | -        | -           | Change a map tile. Used with attributes 'back', 'back2' and fore. Set to -1 to remove one of these tiles. 'action' attribute is available to set a TileAction. |
| timer       | text     | -       | -        | -           | Do a repetition of actions on a desired rate. |
| var         | text     | -       | -        | -           | Set a variable indicated by 'name' to the given 'value'. |
| visible     | text     | -       | -        | -           | Set a character/element visibility depending on _value_ 
attribute (true or false) |
| wait        | int      | -       | -        | V           | Wait for a given number of frame |
| zikReplace  | text     | -       | V        | -           | Replace music indicated by 'what' by the one named by 'name'  |
| zoom        | text     | -       | -        | -           | Set a character's zoom attribute (0..255) where 255 is full size |

### Multithread ###

Here is some details for particular commands about multithreading actions.

Basically, each command is launched when the previous one is finished.
But, there's two possibilities to avoid this kind of mechanic.

#### actions ####

Into the command 'actions', you can add a sublist of actions. It means that each one should happen in the same time.

It's useful when two people have to walk in the same direction, or when the camera should follow a character and so on.

#### unblock ####

With the attribute 'unblock', the given command is said to be unblocking. It means that next action can start without waiting for the current one.

#### locked ####

By default, a scene is blocking all player movements, because it's a cinematic which player should see like a movie.
But sometimes, we need to execute a script without blocking the player. For example, a moving platform on which hero has to climb to reach a distant area. And for this, we have the "locked" attribute :
```
<quest name="vg5_but1" locked="false">
     (...)
</quest>
```
It means that this quest doesn't lock the player movements. He can move around while this quest is executing its actions, as if no script was going on. Basically, all scripts executed from this quest inherits the locked attribute.

### Misc ###

#### Focusing entities ####

It's possible to focus on character, or element, with the simple command **focus**. It commands the camera to put the given element on the center of attention.

This command, by default, moves instantly the camera, by setting its position to the desired one. But you can go smoothly to it, by using the attribute _delta_. This attribute has another consequence : the action will be achieved when camera will be at its target. So the attribute _unblocking_ could be useful in such case.

A last thing to notice about focusing is that if the element moves, camera will move too. That's pretty natural for a focus feature ! But you can reset camera focusing by calling **pos** command on camera with the same postion. Here is an example :

```
 <focus what="caveDoor" delta="true"/>
 <focus/>
```

This is going to focus on an element named `caveDoor` and once camera is on it, the focus will finish. It means that camera won't move at this time, and stay at its current position. But it won't follow any movement anymore of this element.

#### Sub-execution ####

A script can execute another one. To handle this and the priorities rules coming from this, this is how it works :
  * a queue is storing all executing scripts
  * when a new one is launched, it goes on the top of the queue
  * as long as the first script isn't finished, the second won't run

There's an exception to this priority rules, driven by the "mapscripts". They need to be ran before all, in order to get a correct aspect for the current map. So they have a "top priority" flag, to avoid another script launched before.


# Class modelization #

Here is a class model representation :

http://lh6.googleusercontent.com/-_0tQGqmH-aA/T2iAgkvDvmI/AAAAAAAABpI/qd8MEWq5p5I/w1420-h284-k/scriptclasses.PNG

We can see that [ScriptManagement](https://github.com/tchegito/zildo/tree/master/zildo/src/zildo/server/state/ScriptManagement.java) handle all the stuff related to script (as [PersoManagement](https://github.com/tchegito/zildo/tree/master/zildo/src/zildo/server/PersoManagement.java), [MapManagement](https://github.com/tchegito/zildo/tree/master/zildo/src/zildo/server/MapManagement.java) in its respective fields). It has two main linked objects :

  * **[AdventureElement](https://github.com/tchegito/zildo/tree/master/zildo/src/zildo/fwk/script/xml/element/AdventureElement.java)** : a representation of all game scripts (see quests.xml), containing the model described in the start of this document. Note that this object is dynamic, and is the mirror of the instant game situation (issued from a savegame for example). It's not a finished state.
  * **[ScriptExecutor](https://github.com/tchegito/zildo/tree/master/zildo/src/zildo/fwk/script/command/ScriptExecutor.java)** : an object dealing with current executing scripts (if any).

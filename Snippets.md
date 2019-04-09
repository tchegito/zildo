# Snippets #

Here are some useful examples to edit scripts.

## 1) A two-way platform moving as soon as hero walks on it ##
 
 ```xml
	<quest name="cavef6_platef6_forward" locked="false">
		<trigger>
			<location name="cavef6" mover="platef6"/>
			<questDone name="!cavef6_platef6_up"/>
		</trigger>
		<action>
			<moveTo what="platef6" pos="-56,-56" delta="true"/>
			<wait value="50"/>
			<markQuest name="cavef6_platef6_up" value="true"/>
			<markQuest name="cavef6_platef6_back" value="false"/>
  		</action>
	</quest>

	<quest name="cavef6_platef6_back" locked="false">
		<trigger>
			<location name="cavef6" mover="platef6"/>
			<questDone name="cavef6_platef6_up"/>
		</trigger>
		<action>
			<moveTo what="platef6" pos="56,56" delta="true"/>
			<wait value="50"/>
   			<markQuest name="cavef6_platef6_up" value="false"/>
			<markQuest name="cavef6_platef6_forward" value="false"/>
   		</action>
	</quest>
	
	<quest name="cavef6_platef6_up"/>
  ```
  
  It's also possible to trigger the event "hero leaves platform", by using a "!" notation:
 ```xml
 <location name="cavef6" mover="!platef6"/>
  ```
  
  ## 2) Create characters in tileAction
  
  Consider following script, which has a singularity:
 ```xml
	<tileAction id="regenSpider">
	  <timer each="50">
	    <action>
	      <lookFor info="ENEMY" pos="x*16,y*16" tile="4" negative="true">
	        <spawn type="STONE_SPIDER" pos="x*16,y*16" />
	      </lookFor>
	    </action>
	  </timer>
	</tileAction>
```
Actually, 'tileAction' induces a specific context around a given tile (a [TileLocationContext](https://github.com/tchegito/zildo/blob/master/zildo/src/main/java/zildo/fwk/script/context/TileLocationContext.java)). Indeed, the 'lookFor' action will generate a new context around found entity ONLY if entity has been found. Nested actions are executed only if condition is evaluated to 'true', but in this case, we have the 'negative' attribute. Thus, we execute nested action without any entity found. So in this particular case, we keep the original context.

## 3) Enemy looks in hero's direction

 ```xml
	<perso who="self" reverse="zildo.x &gt; x : 128, 0"/>
```
Condition is "zildo.x < x" (we have to escape the minus operator because we're inside XML (bad choice, I know ...))

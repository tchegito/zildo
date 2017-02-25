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

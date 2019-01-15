# Cursor on script #

When a script is running in Alembrume's engine, it is executed line by line with a cursor iterating over it. But the cursor doesn't go 1 line per frame. It did before Episode 4, but now there are rules, in order to speed up some processing.

Basically script is intended for cutscenes, so we need a cursor followng a timeline. But sometimes, we need to render some actions in one frame. Note that we could already do this with 'actions' tag, to gather several action in the same time. But that didn't work for loop/conditional tags, like 'for', 'loop' or 'if'.

## Var ##

As this function is just altering a variable, this is not necessary to block the cursor. So, following code is running in a single frame:
```
    <var name="truc" value="1"/>
    <var name="machin" value="2"/>
    <var name="chouette" value="3"/>
```

## Specific functions ##

 * tile
 
## Action creating sub-process ##

Any action creating a sub-process will make the cursor go into it.
For example, following code is running in a single frame:
```
   <for var="j" value="3">
      <tile pos="28,6+j" back="256*10 + 34" back2="-1"/>
   </for>
```
This is okay because 'tile' is not blocking the cursor.

Even if 'for' statements are nested, this is still going in a single frame:
```
   <for var="j" value="3">
      <for var="i" value="6">
         <tile pos="28+i,6+j" back="256*10 + 34" back2="-1"/>
      </for>
   </for>
```
This is the same if 'for' statements are nested inside 'loop'.

But if a blocking action is inside the loop, cursor will wait at each iteration:
```
   <for var="a" value="10">
      <perso who="zildo" addSpr="a"/>
   </for>
```
Then complete script execution will take 11 frames (10 + 1 extra to close the 'for' loop).

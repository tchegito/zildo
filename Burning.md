# Burning elements

Starting from episode 4, a new ability has been added: burn things.
Now, hero can grab a bunch of leaves, for example, with his fork, and hold it to a fire. That will ignite the leaves and he could ignite some other things with it.

## Technically

To handle that, we happened to bump against the basic concepts of Zildo's world, especially SpriteEntity and Element.

The world is defined with:
 * **SpriteEntity** being things on the floor, triggering no collision, and unable to burn.
 * **Element** being evoluated things, on the floor or flying, causing collision.

To materialize the ignition, we choose to use a special type of collision. But in order to do that, we had to mix these abilities. In concrete terms, to represent a bunch of leaves on the floor
(which is a SpriteEntity) able to burn, we add an invisible element on it, linked to a permanent collision.

Two new types of collision have been added:
 * **CATCHING_FIRE**: ability to be burnt
 * **LIGHTING_FIRE**: ability to ignite something

## Changes

* on map load: we add on each SpriteEntity marked as "burnable" (see ElementDescription#isBurnable) an invisible Element.
* on collision check: we detect two element with matching collision types (1 catching and 1 lighting fire). On this circumstance, we ignite the first one, and it becomes a lighting one too.

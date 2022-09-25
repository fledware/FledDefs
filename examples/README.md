## Examples

The entry point for all examples are run from the `example-main`
project. 


### runSpacer

`./gradlew :examples:examples-main:runSpacer`

This example randomly generates a small start cluster. You'll
start in the star view where you can click on any of the stars.
When a star is clicked, you will go to the system view where you
can view the given system. Each star system is its own FledECS world.

The point of this example is to show how mods can work. If you look
in the `load-list-spacer.yaml`, you can see `load mods for the game`.
All of them can be commented out and the example ran to show 
just how much can be changed. Adding each of the mods back one
at a time to show the progression.

### runPathingXaguzman

`./gradlew :examples:examples-main:runPathingXaguzman`

This example downloads `com.github.xaguzman:pathfinding:0.2.6` from
maven central and caches the dep in the `local-repo` directory
next to the load list location. It then loads the dep into the
classpath.

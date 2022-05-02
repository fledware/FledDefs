package simplegame

class BlahSystem {
  // this class should not be able to work because Placement
  // is redefined and the classloader will always check the
  // parent classloader first.
  private val placement = Placement(10, 10, 2, false)
}

class HelloHijacker {
  // the constructor is the same, and will instantiate.
  // but the `drrrr` field won't exist
  val placement = Placement(10, 10, 2)

  fun hijacked() {
    // this will fail because the original class doesn't have this field
    println(placement.drrrr)
  }
}

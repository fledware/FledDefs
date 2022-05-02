package fledware.definitions.util

import kotlin.test.Test
import kotlin.test.assertEquals

class CombinesTest {
  @Test
  fun testCombineSet() {
    val set1 = setOf(1, 2, 3)
    val set2 = setOf(2, 3, 4)
    assertEquals(
        Combines.combineSet(set1, set2),
        setOf(1, 2, 3, 4))
    assertEquals(
        Combines.combineSet(set1, null),
        setOf(1, 2, 3))
    assertEquals(
        Combines.combineSet(null, set2),
        setOf(2, 3, 4))
    assertEquals(
        Combines.combineSet<Int>(null, null),
        null)
  }

  @Test
  fun testCombineMap() {
    val map1 = mapOf(1 to 1, 2 to 2, 3 to 3)
    val map2 = mapOf(2 to 3, 3 to 4, 4 to 5)
    assertEquals(
        Combines.combineMap(map1, map2),
        mapOf(1 to 1, 2 to 3, 3 to 4, 4 to 5))
    assertEquals(
        Combines.combineMap(map1, null),
        mapOf(1 to 1, 2 to 2, 3 to 3))
    assertEquals(
        Combines.combineMap(null, map2),
        mapOf(2 to 3, 3 to 4, 4 to 5))
    assertEquals(
        Combines.combineMap<Int, Int>(null, null),
        null)
  }

  @Test
  fun testCombineMapMap() {
    val map1 = mapOf(
        1 to mapOf(1 to 1, 10 to 1),
        2 to mapOf(2 to 2, 20 to 2),
        3 to mapOf(3 to 3, 30 to 3)
    )
    val map2 = mapOf(
        2 to mapOf(2 to 3),
        3 to mapOf(3 to 4),
        4 to mapOf(4 to 4, 40 to 4)
    )
    assertEquals(
        Combines.combineMapMap(map1, map2),
        mapOf(
            1 to mapOf(1 to 1, 10 to 1),
            2 to mapOf(2 to 3, 20 to 2),
            3 to mapOf(3 to 4, 30 to 3),
            4 to mapOf(4 to 4, 40 to 4)
        )
    )
    assertEquals(
        Combines.combineMapMap(map1, null),
        map1)
    assertEquals(
        Combines.combineMapMap(null, map2),
        map2)
    assertEquals(
        Combines.combineMapMap<Int, Int, Int>(null, null),
        null)
  }
}
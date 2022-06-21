package fledware.definitions.loadlist

import fledware.definitions.GatherIterationType
import fledware.definitions.loadlist.maven.GatherArtifactCommand
import fledware.definitions.loadlist.maven.LoadArtifactCommand
import fledware.definitions.loadlist.maven.MavenLoadListProcessor
import fledware.definitions.loadlist.mods.AppendToClasspathCommand
import fledware.definitions.loadlist.mods.GatherCommand
import fledware.definitions.loadlist.mods.ModLoadListProcessor
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class LoadListManagerTest {
  @Test
  fun canCreateLoadManager() = loadListManager { manager ->
    assertEquals(0, manager.commands.size)
    assertEquals(2, manager.processors.size)
    assertNotNull(manager.findProcessor<ModLoadListProcessor>())
    assertNotNull(manager.findProcessor<MavenLoadListProcessor>())
  }

  @Test
  fun canLoadASingleMod() = loadListManager { manager ->
    val loadList = resourceAsFile("/load-list-single.yaml")
    manager.process(loadList)
    assertEquals(1, manager.commands.size)

    val command = assertIs<GatherCommand>(manager.commands[0])
    assertEquals("path", command.name)
    assertEquals(100, command.weight)
    assertEquals(File(loadList.parentFile, "some/path.jar"), command.target)
    assertEquals(GatherIterationType.CONCURRENT, command.iteration)
  }

  @Test
  fun canLoadMultipleModsWithLibraries() = loadListManager { manager ->
    val loadList = resourceAsFile("/load-list-multiple.yaml")
    manager.process(loadList)
    assertEquals(3, manager.commands.size)

    assertIs<LoadArtifactCommand>(manager.commands[0]).also { command ->
      assertEquals("foo:bar:1.2.3", command.name)
      assertEquals(50, command.weight)
      assertEquals("foo:bar:1.2.3", command.target)
    }

    assertIs<AppendToClasspathCommand>(manager.commands[1]).also { command ->
      assertEquals("other", command.name)
      assertEquals(10, command.weight)
      assertEquals(File(loadList.parentFile, "some/other.jar"), command.target)
    }

    assertIs<GatherCommand>(manager.commands[2]).also { command ->
      assertEquals("path", command.name)
      assertEquals(100, command.weight)
      assertEquals(File(loadList.parentFile, "some/path.jar"), command.target)
      assertEquals(GatherIterationType.CONCURRENT, command.iteration)
    }
  }

  @Test
  fun canLoadMavenDeps() = loadListManager { manager ->
    val loadList = resourceAsFile("/load-list-maven.yaml")
    manager.process(loadList)
    assertEquals(3, manager.commands.size)

    assertIs<LoadArtifactCommand>(manager.commands[0]).also { command ->
      assertEquals("foo2:bar2:1.2.3", command.name)
      assertEquals(50, command.weight)
      assertEquals("foo2:bar2:1.2.3", command.target)
    }

    assertIs<LoadArtifactCommand>(manager.commands[1]).also { command ->
      assertEquals("some:maven:2.3.7", command.name)
      assertEquals(50, command.weight)
      assertEquals("some:maven:2.3.7", command.target)
    }

    assertIs<GatherArtifactCommand>(manager.commands[2]).also { command ->
      assertEquals("blah:stuff:2.3.4", command.name)
      assertEquals(100, command.weight)
      assertEquals("blah:stuff:2.3.4", command.target)
      assertEquals(GatherIterationType.CONCURRENT, command.iteration)
    }
  }

  @Test
  fun canProcessMultipleLoadLists() = loadListManager { manager ->
    val loadList1 = resourceAsFile("/load-list-multiple.yaml")
    val loadList2 = resourceAsFile("/load-list-maven.yaml")
    manager.process(loadList1)
    manager.process(loadList2)
    assertEquals(6, manager.commands.size)

    assertIs<LoadArtifactCommand>(manager.commands[0]).also { command ->
      assertEquals("foo:bar:1.2.3", command.target)
    }

    assertIs<AppendToClasspathCommand>(manager.commands[1]).also { command ->
      assertEquals(File(loadList1.parentFile, "some/other.jar"), command.target)
    }

    assertIs<GatherCommand>(manager.commands[2]).also { command ->
      assertEquals(File(loadList1.parentFile, "some/path.jar"), command.target)
    }

    assertIs<LoadArtifactCommand>(manager.commands[3]).also { command ->
      assertEquals("foo2:bar2:1.2.3", command.target)
    }

    assertIs<LoadArtifactCommand>(manager.commands[4]).also { command ->
      assertEquals("some:maven:2.3.7", command.target)
    }

    assertIs<GatherArtifactCommand>(manager.commands[5]).also { command ->
      assertEquals("blah:stuff:2.3.4", command.target)
    }
  }
}
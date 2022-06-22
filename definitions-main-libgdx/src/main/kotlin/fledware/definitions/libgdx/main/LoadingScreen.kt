package fledware.definitions.libgdx.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import fledware.definitions.ex.LoadIterator
import ktx.actors.txt
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.progressBar
import ktx.scene2d.table
import ktx.style.label
import ktx.style.progressBar
import ktx.style.skin
import org.slf4j.LoggerFactory

/**
 * The [LoadingScreen] is tricky because it has to exist before
 * loading starts. This is why the code here loads assets from
 * the classpath.
 */
@Suppress("MemberVisibilityCanBePrivate")
class LoadingScreen(val loadIterator: LoadIterator,
                    batch: Batch)
  : ScreenAdapter() {

  private val logger = LoggerFactory.getLogger(this::class.java)
  private val stage = Stage(ExtendViewport(2000f, 0f, 2000f, 10_000f), batch)

  // we cannot use the asset manager for any assets while its loading
  val progressBarBackground = Texture(Gdx.files.classpath("loader/progress-bar.png"))
  val progressBarKnob = Texture(Gdx.files.classpath("loader/progress-bar-knob.png"))
  val mainMessage: Label
  val loadingWhat: Label
  val fontHeader: BitmapFont
  val fontSmall: BitmapFont
  val fontTiny: BitmapFont
  var isFinished: Boolean = false
    private set

  init {
    val fontGenerator = FreeTypeFontGenerator(Gdx.files.classpath("loader/kenney-future-narrow.ttf"))
    fontHeader = fontGenerator.generateFont(FreeTypeFontGenerator.FreeTypeFontParameter().also {
      it.size = 100
    })
    fontSmall = fontGenerator.generateFont(FreeTypeFontGenerator.FreeTypeFontParameter().also {
      it.size = 75
    })
    fontTiny = fontGenerator.generateFont(FreeTypeFontGenerator.FreeTypeFontParameter().also {
      it.size = 30
    })
    fontGenerator.dispose()
  }

  val loadSkin = skin {
    label("header") {
      font = fontHeader
      fontColor = Color.WHITE
    }
    label("small") {
      font = fontSmall
      fontColor = Color.WHITE
    }
    label("tiny") {
      font = fontTiny
      fontColor = Color.WHITE
    }
    progressBar("default-horizontal") {
      background = TextureRegionDrawable(progressBarBackground)
      knobBefore = TextureRegionDrawable(progressBarKnob).tint(Color.CYAN)
    }
  }

  val progress: ProgressBar

  init {
    stage.actors {
      table(loadSkin) {
        setFillParent(true)
        label("Welcome!", "header", loadSkin) {
          it.padTop(Value.percentHeight(0.1f, this@table))
        }
        row()
        progress = progressBar(skin = loadSkin) {
          it.padTop(Value.percentHeight(0.2f, this@table))
          it.width(Value.percentWidth(0.7f, this@table))
          value = 0f
        }
        row()
        mainMessage = label("loading.. keep calm", "small", loadSkin) {
          it.padTop(Value.percentHeight(0.05f, this@table))
        }
        row()
        loadingWhat = label("", "tiny", loadSkin) {
          it.padTop(Value.percentHeight(0.05f, this@table))
        }
      }
    }
  }

  fun figurePercentageAndSetLoading() {
    when {
      loadIterator.exception != null -> {
        mainMessage.txt = "loading error"
        loadingWhat.txt = loadIterator.exception?.message ?: ""
      }
      !loadIterator.isFinished -> {
        loadIterator.start()
        loadingWhat.txt = loadIterator.commandAtOrNull?.name ?: ""
        progress.value = loadIterator.percentFinished
        loadIterator.update()
      }
      else -> {
        loadingWhat.txt = "done!"
        isFinished = true
        progress.value = 1f
        logger.info("loading finished in ${loadIterator.loadTime} ms")
      }
    }
  }

  override fun show() {
    Gdx.input.inputProcessor = stage
  }

  override fun resize(width: Int, height: Int) {
    stage.viewport.update(width, height, true)
  }

  override fun render(delta: Float) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
        Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
      Gdx.app.exit()
    }
    figurePercentageAndSetLoading()
    stage.act(delta)
    stage.draw()
  }

  override fun dispose() {
    stage.dispose()
    progressBarBackground.dispose()
    progressBarKnob.dispose()
    fontHeader.dispose()
    fontSmall.dispose()
    fontTiny.dispose()
    loadSkin.dispose()
  }
}

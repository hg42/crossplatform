package com.hg42.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hg42.common.Scripting.Companion.runScript
import com.hg42.common.Scripting.Companion.valueOrError
import com.hg42.common.Scripting.Companion.valuesOrError
import javax.script.ScriptEngineFactory
import javax.script.ScriptEngineManager

data class X(val i: Int = 1, val s: String = "test")

open class Plugin(
    val name: String,
) {
    override fun toString(): String =
        "${javaClass.simpleName}(name = $name)"

    open fun getObject(): Any {
        return X()
    }

    open fun doit(): Unit {}
}

open class UiPlugin(
    name: String,
    val ui: @Composable () -> Unit = { /* Text("not initialized") */ },
) : Plugin(name = name) {
    @Composable
    fun compose() {
        ui()
    }
}

var scriptPlugin by mutableStateOf<UiPlugin?>(null)
var nativePlugin by mutableStateOf<UiPlugin?>(null)

class NativePlugin : UiPlugin(
        name = "native",
        ui = {
            println("compose NativePlugin ui")
            //Text("ui from NativePlugin")
        },
)

val testScript = """

package com.hg42.common

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.hg42.common.Plugin

@Composable fun composeFun(): Unit { Text("<test>") }
 
println("test begin")
    
class TestPlugin : Plugin(
    name = "test",
)

val plugin = TestPlugin()

println("test plugin = " + plugin.toString())

plugin

"""

val dummy = run {
    val x = runScript(testScript)
    println("test = " + x.toString())
    //exitProcess(0)
    0
}

val uiScript = """

package com.hg42.common

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.hg42.common.UiPlugin

println("script begin")
    
class ScriptPlugin : UiPlugin(
    name = "script",
)

val plugin = ScriptPlugin()

println("script = " + plugin.toString())

plugin

"""

//ui = @Composable {
//    println("compose ScriptPlugin ui")
//    Text("ui from ScriptPlugin")
//},

//---------------------------------------- UI

val globalPadding = 2.dp

@Composable
fun ScriptRunner(factory: ScriptEngineFactory) {
    val result = remember { mutableStateOf<Any?>(null) }
    var text by remember { mutableStateOf(if ("kotlin" in factory.names) uiScript else "777+111") }
    Card {
        Column {
            TextField(
                    value = text,
                    onValueChange = {
                        text = it
                        //result.value = runScript(text, factory)
                    }
            )
            val composer = currentComposer
            Button(onClick = {
                result.value = runScript(text, factory)
            }) {
                Text("Run")
            }
            Card {
                when (result.value) {
                    is UiPlugin -> (result.value as UiPlugin).compose()
                    is Plugin   -> Text((result.value as Plugin).getObject().toString())
                    else        -> Text(result.value.toString())
                }
            }
        }
    }
}

@Composable
fun EngineDetails(factory: ScriptEngineFactory) {
    Card {
        Column {
            val engineName = valueOrError { factory.engineName }
            val engineVersion = valueOrError { factory.engineVersion }
            val languageName = valueOrError { factory.languageName }
            val languageVersion = valueOrError { factory.languageVersion }
            val extensions = valuesOrError { factory.extensions }
            val names = valuesOrError { factory.names }
            val mimeTypes = valuesOrError { factory.mimeTypes }
            Text("$engineName-$engineVersion $languageName-$languageVersion")
            Text("    ext: $extensions")
            Text("    names: $names")
            Text("    mime: $mimeTypes")
            ScriptRunner(factory)
        }
    }
}

@Composable
fun App() {

    val platformName = getPlatformName()

    val scrollState = remember { ScrollState(0) }

    LaunchedEffect(uiScript) {
        nativePlugin = NativePlugin()
        println("native: ui = " + nativePlugin?.ui.toString())
        scriptPlugin = runScript(uiScript) as UiPlugin
        println("script: ui = " + scriptPlugin?.ui.toString())
    }

    Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(globalPadding)
    ) {
        Card {
            Column {
                Text("platform $platformName")
                Text("kotlin ${KotlinVersion.CURRENT}")
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(globalPadding)) {
            Text("jsr223 scripting engines:")
            var factories: List<ScriptEngineFactory> = listOf()
            try {
                val manager = ScriptEngineManager()
                factories = manager.getEngineFactories()
            } catch (_: Throwable) {
            }
            factories.forEach {
                EngineDetails(it)
            }
        }

        Button(onClick = {
            println("click")
            val x = runScript(testScript) as Plugin
            x.doit()
        }) {
            Text("Test")
        }

        @Composable
        fun compose(plugin: UiPlugin?) {

            val name = plugin?.name

            println("$name: plugin = $plugin")

            plugin?.let {
                val x = it.getObject()
                when (x) {
                    is X -> Text("$name: i = ${x.i} s = ${x.s}")
                    else -> Text("$name: x is of class ${x::class.simpleName}")
                }
            }

            plugin?.ui?.invoke()
            plugin?.compose()
        }

        compose(nativePlugin)
        compose(scriptPlugin)
    }
}

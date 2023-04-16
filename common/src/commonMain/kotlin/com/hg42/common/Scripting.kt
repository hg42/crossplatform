package com.hg42.common

import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory
import javax.script.ScriptEngineManager

class Scripting {

    companion object {
        fun engine(type: Any): ScriptEngine? {
            return when (type) {
                is ScriptEngineFactory -> type.scriptEngine
                is ScriptEngine -> type
                is String -> try {
                    ScriptEngineManager().getEngineByExtension(type)
                        ?: ScriptEngineManager().getEngineByName(type)
                        ?: ScriptEngineManager().getEngineByMimeType(type)
                        ?: null
                } catch (e: Throwable) {
                    null
                }

                else -> null
            }
        }

        fun runScript(script: String = "", type: Any = "kotlin", args: Map<String, Any> = mapOf()): Any {
            try {
                engine(type)?.let {
                    args.forEach { (k, v) -> it.put(k, v) }
                    return it.eval(script)
                } ?: throw Error("??? ERROR: no engine found for $type")
            } catch (e: Throwable) {
                throw Error("??? ${e.message}\n" +
                        "${e.stackTraceToString()}\n" +
                        e.cause?.let {
                            "${it.message}\n${it.stackTraceToString()}"
                        })
            }
        }

        fun runScriptAsString(script: String = "", type: Any = "kotlin", args: Map<String, Any> = mapOf()): String {
            var result = ""
            try {
                engine(type)?.let {
                    args.forEach { (k, v) -> it.put(k, v) }
                    result = it.eval(script)?.toString() ?: "<no result>"
                } ?: "??? ERROR: no engine found for $type"
            } catch (e: Throwable) {
                result = "??? ${e.message}\n" +
                        "${e.stackTraceToString()}\n" +
                        e.cause?.let {
                            "${it.message}\n${it.stackTraceToString()}"
                        }
            }
            return result
        }

        fun valueOrError(value: () -> Any?): String {
            return try {
                value()?.toString() ?: "unknown"
            } catch (e: Throwable) {
                e.message ?: "ERROR"
            }
        }

        fun valuesOrError(values: () -> Any?): String {
            return try {
                values().toString()
            } catch (e: Throwable) {
                e.message ?: "ERROR"
            }
        }
    }
}
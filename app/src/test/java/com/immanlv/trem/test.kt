package com.immanlv.trem

fun main(){

    val cs = "You've [?C] [?C*>1 & ?L*>1 ?(and)] [?L] left"


    val tokens = cs
        .replace("?C*","3")
        .replace("?L*","0")
        .split("\\[|\\]".toRegex())
        .filter { it.isNotBlank() }
        .map { it.trim() }

    tokens.forEach {
        println("\""+it+"\" = "+it.isExpression())
    }

}

fun String.isExpression():Boolean{
    if(this.contains(">|<|&|/+|-".toRegex())) {



        return true
    }
    return false
}

class PlaceholderParser(private val placeholders: Map<String, Any>) {

    fun parse(input: String): String {
        var result = input

        val pattern = Regex("\\{\\?(.*?)\\}")
        val matches = pattern.findAll(input)

        for (match in matches) {
            val condition = match.groupValues[1]
            val replacement = evaluateCondition(condition)
            result = result.replace(match.value, replacement)
        }

        return result
    }

    private fun evaluateCondition(condition: String): String {
        val parts = condition.split(" ")
        val placeholder = parts[0]

        return placeholders[placeholder]?.let {
            val value = it.toString()
            if (parts.size > 2) {
                val operator = parts[1]
                val threshold = parts[2].toInt()

                when (operator) {
                    ">" -> if (value.toInt() > threshold) parts[3] else ""
                    else -> value
                }
            } else {
                value
            }
        } ?: ""
    }
}

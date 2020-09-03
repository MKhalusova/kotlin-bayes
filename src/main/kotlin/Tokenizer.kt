class Tokenizer {
    val emojisRegex = Regex("(?:[<>]?[:;=8][\\-o*']?[)\\](\\[dDpP/:}{@|\\\\]|[)\\](\\[dDpP/:}{@|\\\\][\\-o*']?[:;=8][<>]?|<3)")

    fun tokenize(string:String): List<String> {

        // extract emojis into separate list
        val emojiMatches = emojisRegex.findAll(string)
        val emojisList = emojiMatches.map { it.value }.toList()
        val withoutEmojis = string.replace(regex = emojisRegex, replacement = "")

        // dropping leftover punctuation and numbers, removing extra white spaces
        val withoutPunctuation = withoutEmojis.replace(regex = Regex("[^a-zA-Z_-]"), replacement = " ").replace(regex = Regex("\\s+")," ").trim()

        // splitting the string into tokens
        val tokensWithoutEmojis: List<String> = withoutPunctuation.split(delimiters = arrayOf(" "))

        val lowercaseTokens = tokensWithoutEmojis.map { it.toLowerCase() }

//      Originally Tokenizer returned the list of tokens including emojis but the dataset has been collected and labeled based on emojis,
//      so that skews the results of the classifier.
//      return lowercaseTokens + emojisList

        return lowercaseTokens
    }

}
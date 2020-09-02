import kotlin.math.ln

class NaiveBayesBinaryClassifier {
    var logPrior: Double = 0.0
    var vocabulary = emptyMap<String, Double>()

    private fun buildFrequences(texts: List<List<String>>, targets:List<Int>): Map<String, Pair<Int, Int>>{
        // X - list of tokenized tweets, targets = labels (will need to combine positive and negative tweets)
        // frequency table of word to Pair<negative (0) count , positive (1) count>
        val frequencyTable = mutableMapOf<String, Pair<Int,Int>>()

        for ((tweet, y)  in texts.zip(targets)) {
            for (word in tweet) {
                frequencyTable.putIfAbsent(word, Pair(0,0))
                val counts = frequencyTable.get(word)
                if (y == 0) frequencyTable.put(word, Pair(counts!!.first +1, counts.second))
                if (y == 1) frequencyTable.put(word, Pair(counts!!.first, counts.second + 1))
            }
        }
        return frequencyTable
    }

    private fun computeLogLambdas(freqs: Map<String, Pair<Int, Int>>): Map<String, Double> {
        val allPositiveCounts = freqs.values.sumBy { it.second }
        val allNegativeCounts = freqs.values.sumBy {it.first}
        val vocabLength = freqs.size

        val logLamdas = mutableMapOf<String, Double>()

        for (word in freqs.keys) {
            val posProb = ((freqs.getValue(word).second +1) / (allPositiveCounts + vocabLength)).toDouble()
            val negProb = ((freqs.getValue(word).first + 1) / (allNegativeCounts + vocabLength)).toDouble()
            val logLambda = ln(posProb/negProb)
            logLamdas[word] = logLambda
        }
        return logLamdas
    }

    fun train(X: List<List<String>>, Y:List<Int>) {
        this.vocabulary = computeLogLambdas(buildFrequences(X, Y))
        val probPos = ((Y.count { it == 1 })/Y.size).toDouble()
        val probNeg = ((Y.count { it == 0})/Y.size).toDouble()
        this.logPrior = ln(probPos/probNeg)
    }

    fun predict(x: List<String>): Double {
        var result = this.logPrior
        for (token in x) {
            result = result.plus(this.vocabulary.getOrDefault(token, 0.0))
        }

        return result
    }

}



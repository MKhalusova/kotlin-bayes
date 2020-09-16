import kotlin.math.ln

class NaiveBayesBinaryClassifier {
    var logPrior: Double = 0.0
    var vocabulary = emptyMap<String, Double>()

    private fun buildFrequencies(texts: List<List<String>>, targets: List<Int>): Map<String, Pair<Int, Int>> {
        // texts - list of tokenized tweets, targets = labels (will need to combine positive and negative tweets)
        // frequency table of word to Pair<negative (0) count , positive (1) count>
        val frequencyTable = mutableMapOf<String, Pair<Int, Int>>()
        for ((tweet, y) in texts.zip(targets)) {
            for (word in tweet) {
                val (negativeCount, positiveCount) = frequencyTable.getOrDefault(word, 0 to 0)
                if (y == 0) frequencyTable.put(word, negativeCount + 1 to positiveCount)
                if (y == 1) frequencyTable.put(word, negativeCount to positiveCount + 1)
            }
        }
        return frequencyTable
    }

    private fun computeLogLambdas(freqs: Map<String, Pair<Int, Int>>): Map<String, Double> {
        val allPositiveCounts = freqs.values.sumBy { it.second }
        val allNegativeCounts = freqs.values.sumBy { it.first }
        val vocabLength = freqs.size

        return freqs.keys.associate { word ->
            val (negative, positive) = freqs[word]!!
            val posProb = (positive + 1).toDouble() / (allPositiveCounts + vocabLength)
            val negProb = (negative + 1).toDouble() / (allNegativeCounts + vocabLength)
            val logLambda = ln(posProb / negProb)
            word to logLambda
        }
    }

    fun train(X: List<List<String>>, Y: List<Int>) {
        require(X.size == Y.size) { "Size of X doesn't match size of Y" }
        vocabulary = computeLogLambdas(buildFrequencies(X, Y))
        val probPos = (Y.count { it == 1 }).toDouble() / Y.size
        val probNeg = (Y.count { it == 0 }).toDouble() / Y.size
        logPrior = ln(probPos / probNeg)
    }

    fun predictLikelihood(x: List<String>): Double =
        logPrior + x.sumByDouble { vocabulary.getOrDefault(it, defaultValue = 0.0) }

    fun predictLabel(x: List<String>): Int = if (predictLikelihood(x) >= 0) 1 else 0

    fun score(xTest: List<List<String>>, yTest: List<Int>): Double {
        require(xTest.size == yTest.size) { "Size of X doesn't match size of Y" }
        val yHat = xTest.map(::predictLabel)
        val correctPredictions = yHat.zip(yTest).count { (y1, y2) -> y1 == y2 }
        return correctPredictions.toDouble() / yTest.size
    }
}



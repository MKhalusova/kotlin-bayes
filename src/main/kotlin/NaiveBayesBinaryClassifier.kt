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
                if (y == 0) frequencyTable.put(word, Pair(counts!!.first + 1, counts.second))
                if (y == 1) frequencyTable.put(word, Pair(counts!!.first, counts.second + 1))
            }
        }
//        println(frequencyTable)
        return frequencyTable
    }

    private fun computeLogLambdas(freqs: Map<String, Pair<Int, Int>>): Map<String, Double> {
        val allPositiveCounts = freqs.values.sumBy { it.second }
        val allNegativeCounts = freqs.values.sumBy {it.first}
        val vocabLength = freqs.size

        val logLamdas = mutableMapOf<String, Double>()

        for (word in freqs.keys) {
            val posProb = ((freqs.getValue(word).second + 1).toDouble() / (allPositiveCounts + vocabLength))
            val negProb = ((freqs.getValue(word).first + 1).toDouble() / (allNegativeCounts + vocabLength))
            val logLambda = ln(posProb/negProb)
            logLamdas[word] = logLambda
        }
        return logLamdas
    }

    fun train(X: List<List<String>>, Y:List<Int>) {
        assert(X.size == Y.size)
        this.vocabulary = computeLogLambdas(buildFrequences(X, Y))
        val probPos = ((Y.count { it == 1 }).toDouble()/Y.size)
        val probNeg = ((Y.count { it == 0}).toDouble()/Y.size)
        this.logPrior = ln(probPos/probNeg)
    }

    fun predictLikelihood(x: List<String>): Double {
        var result = this.logPrior
        for (token in x) {
            result += this.vocabulary.getOrDefault(token, defaultValue = 0.0)
        }

        return result
    }

    fun predictLabel(x: List<String>): Int {
        return if (this.predictLikelihood(x) >= 0) 1
        else 0
    }


    fun score(xTest: List<List<String>>, yTest:List<Int>): Double {
        assert(xTest.size == yTest.size)
        val yHat = mutableListOf<Int>()
        for (x in xTest) {
            yHat.add(predictLabel(x))
        }
        var correctPredictions = 0
        for ((y1, y2) in yHat.zip(yTest)) {
            if (y1 == y2) correctPredictions +=1
        }
        return correctPredictions.toDouble()/yTest.size
    }

}



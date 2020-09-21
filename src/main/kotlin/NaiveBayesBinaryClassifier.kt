import com.google.common.collect.*
import kotlin.math.ln

class NaiveBayesBinaryClassifier {
    var logPrior: Double = 0.0
    var vocabulary = emptyMap<String, Double>()

    private fun buildFrequencies(texts: List<List<String>>, targets: List<Int>): Map<String, Pair<Int, Int>> {
        // texts - list of tokenized tweets, targets = labels (will need to combine positive and negative tweets)
        // frequency table of word to Pair<negative (0) count , positive (1) count>
        val (negativeTweets, positiveTweets) = texts.zip(targets).partition { it.second == 0 }
        val negativeSet = negativeTweets.flatMap { it.first }.toMultiset()
        val positiveSet = positiveTweets.flatMap { it.first }.toMultiset()
        return (negativeSet.elementSet() + positiveSet.elementSet()).associateWith { word ->
            Pair(negativeSet.count(word), positiveSet.count(word))
        }
    }

    private fun computeLogLambdas(freqs: Map<String, Pair<Int, Int>>): Map<String, Double> {
        val allPositiveCounts = freqs.values.sumBy { it.second }
        val allNegativeCounts = freqs.values.sumBy { it.first }
        val vocabLength = freqs.size

        return freqs.keys.associateWith { word ->
            val (negative, positive) = freqs.getValue(word)
            val posProb = (positive + 1.0) / (allPositiveCounts + vocabLength)
            val negProb = (negative + 1.0) / (allNegativeCounts + vocabLength)
            ln(posProb / negProb)
        }
    }

    fun train(X: List<List<String>>, Y: List<Int>) {
        require(X.size == Y.size) { "Size of X doesn't match size of Y" }
        vocabulary = computeLogLambdas(buildFrequencies(X, Y))
        val positiveCount = Y.count { it == 1 }
        val negativeCount = Y.count { it == 0 }
        logPrior = ln(positiveCount.toDouble() / negativeCount)
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

    private fun <T> List<T>.toMultiset(): Multiset<T> = HashMultiset.create(this)
}

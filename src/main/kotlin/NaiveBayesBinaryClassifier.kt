

class NaiveBayesBinaryClassifier {
//    val logLambdaLookup => for each word, has log(prob(w | pos) / prob(w | neg)
//    val log prior = log( prob(pos) / prob(neg))
    // idea: use list of data classes for the frequency table

    fun buildFrequences(x: List<List<String>>, targets:List<Boolean>): Map<Pair<String, Int>, Int>{
        // X - list of tokenized tweets, y = labels (will need to combine positive and negative tweets)
        val yInt = targets.map{if (it) 1 else 0}
        val freqs = mutableMapOf<Pair<String, Int>, Int>()
        for ((tweet, y)  in x.zip(yInt)) {
            for (word in tweet) {
                if (Pair(word, y) in freqs) {
                    freqs[Pair(word, y)] = freqs.getValue(Pair(word, y)) + 1
                }
                else freqs[Pair(word,y)] = 1
            }
        }



        return freqs
    }

    fun computeLogLambdas(freqs: Map<Pair<String, Int>, Int>): Map<String, Double> {
        val logLamdas = mutableMapOf<String, Double>()

        return logLamdas
    }

    fun train(X: List<List<String>>, Y:List<Boolean>) {
        // create a frequences table with lambda
    }

    fun predict(x: List<String>): Boolean {
        return true
    }

}



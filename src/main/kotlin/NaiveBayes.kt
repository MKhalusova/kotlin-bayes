import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.io.File
import java.util.*

fun extractTweetsFromJSON (path: String): List<String> {
    val tweets = mutableListOf<String>()
    val parser = Parser()
    File(path).forEachLine { line -> tweets.add((parser.parse(StringBuilder(line)) as JsonObject).string("text").toString()) }
    return Collections.unmodifiableList(tweets)
}

fun trainTestSplit (positiveTweets: List<List<String>>, negativeTweets: List<List<String>>, testSize: Double = 0.2): Pair<List<Pair<List<String>, Int>>,List<Pair<List<String>, Int>>> {
    val data = positiveTweets + negativeTweets

    val ones = IntArray(positiveTweets.size) { 1 }.toList()
    val zeros = IntArray(negativeTweets.size).toList()
    val targets = listOf(ones, zeros).flatten()

    val dataset: List<Pair<List<String>, Int>> = data.zip(targets)
    val shuffledData = dataset.shuffled()

    val trainSetSize: Int = (shuffledData.size*(1-testSize)).toInt()
    val trainSet: List<Pair<List<String>, Int>> = shuffledData.take(trainSetSize)

    val testSetSize: Int = (shuffledData.size*testSize).toInt()
    val testSet = shuffledData.takeLast(testSetSize)

    return Pair(trainSet, testSet)
}

fun main(){
    val positiveTweetsPath = "src/data/twitter_samples/positive_tweets.json"
    val negativeTweetsPath = "src/data/twitter_samples/negative_tweets.json"

//    read the tweets from JSON files
    val positiveTweets = extractTweetsFromJSON(positiveTweetsPath)
    val negativeTweets = extractTweetsFromJSON(negativeTweetsPath)

//    prepare tweets for model training
    val preprocessor = TweetPreprocessor()
    val processedPositiveTweets = positiveTweets.map { preprocessor.preprocessTweet(it) }
    val processedNegativeTweets = negativeTweets.map { preprocessor.preprocessTweet(it) }

//    split the data into the training and test sets randomly with 20% in test by default
    val (trainSet, testSet) = trainTestSplit(processedPositiveTweets, processedNegativeTweets)
    val (trainX, trainY) = trainSet.unzip()
    val (testX, testY) = testSet.unzip()

    val classifier = NaiveBayesBinaryClassifier()
    classifier.train(trainX, trainY)

    println("Naive Bayes Sentiment Classifier Accuracy: ${classifier.score(testX, testY)}")


}
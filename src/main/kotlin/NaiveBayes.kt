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

//    split the data into the training and test sets
    val x = 4000
    val trainX = processedPositiveTweets.take(x) + processedNegativeTweets.take(x)
    val zeros = IntArray(x).toList()
    val ones = IntArray(x) { 1 }.toList()
    val trainY = listOf(ones, zeros).flatten()


    val testX = processedPositiveTweets.takeLast(1000) + processedNegativeTweets.takeLast(1000)
    val z = IntArray(1000).toList()
    val o = IntArray(1000) { 1 }.toList()
    val testY = listOf(o, z).flatten()

    val classifier = NaiveBayesBinaryClassifier()
    classifier.train(trainX, trainY)

    println("Naive Bayes Sentiment Classifier Accuracy: ${classifier.score(testX, testY)}")


}
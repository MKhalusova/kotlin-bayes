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

data class TweetRecord(val tweetTokens: List<String>, val label: Int)

fun trainTestSplit (dataset: List<TweetRecord>, testSize: Double = 0.2): Pair<List<TweetRecord>,List<TweetRecord>>{
    val shuffledDataset = dataset.shuffled()
    val trainSetSize: Int = (shuffledDataset.size*(1 - testSize)).toInt()
    val trainSet = shuffledDataset.take(trainSetSize)

    val testSetSize: Int = (shuffledDataset.size*testSize).toInt()
    val testSet = shuffledDataset.takeLast(testSetSize)

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

//    combining all tweets into a dataset - list of TweetRecords
    val positiveTweetRecords = processedPositiveTweets.map { TweetRecord(it, 1) }
    val negativeTweetRecords = processedNegativeTweets.map {TweetRecord(it, 0)}
    val fullDataSet = positiveTweetRecords + negativeTweetRecords

// Splitting the data into training set and test set
    val (trainData, testData) = trainTestSplit(fullDataSet)

//  Splittin Tweet records into features and targets (X and Y) for the classifier
    val trainX = trainData.map { it.tweetTokens }
    val trainY = trainData.map { it.label }
    val testX = testData.map { it.tweetTokens }
    val testY = testData.map { it.label }

//    training NaiveBayesClassifier
    val classifier = NaiveBayesBinaryClassifier()
    classifier.train(trainX, trainY)

    println("Naive Bayes Sentiment Classifier Accuracy: ${classifier.score(testX, testY)}")


}
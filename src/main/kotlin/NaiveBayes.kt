import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.io.File
import java.util.*

// remove stock market tickers like $GE
fun String.removeTickers() = replace(regex = Regex("\\\$\\w*"), replacement = "")

// remove old style retweet text "RT"
fun String.removeRTs() = replace(regex = Regex("^RT[\\s]+"), replacement = "")

// remove URLs
fun String.removeURLs() = replace(regex = Regex("https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"), "")

// remove hashtags
fun String.removeHashtags() = replace("#", "")

// remove mentions
fun String.removeMentions() = replace(regex = Regex("[@#][\\w_-]+"), replacement ="")

// remove XML character encodings like &amp;
fun String.removeXMLEncodings() = replace(regex = Regex("&[a-z]*;")," ")

// remove extra spaces
fun String.removeExtraSpaces() = replace(regex = Regex("\\s+")," ")

fun String.cleanTweet(): String {
    return this.removeTickers().removeRTs().removeURLs().removeHashtags().removeMentions().removeXMLEncodings().removeExtraSpaces()
}

// Preprocessing a Tweet to be ready for model training: cleaning tweet, tokenizing, removing stop words and stemming

fun preprocessTweet(tweet: String): List<String> {
    val cleanedTweet = tweet.cleanTweet()

    // tokenizing the tweet: splitting into words, lowercasing, dropping punctuation, keeping emojis
    val tokenizer = Tokenizer()
    val words = tokenizer.tokenize(cleanedTweet)

    val stopWords = listOf("i", "m", "t", "re", "ve", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself",
            "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "they",
            "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those",
            "am", "is", "isn",  "are", "aren", "was", "were", "weren", "be", "been", "being", "have", "has", "had", "hadn",
            "having", "do", "does", "did", "didn", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as",
            "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during",
            "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under",
            "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too",
            "very", "s", "can", "will", "won", "just", "don", "should", "shouldn", "now")

    val tokens = mutableListOf<String>()
    for (word in words) {
        if (word in stopWords) continue
        else tokens.add(word)
    }

    // TODO: Stemming

     return tokens
}

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
    val processedPositiveTweets = positiveTweets.map { preprocessTweet(it) }
    val processedNegativeTweets = negativeTweets.map { preprocessTweet(it) }

    val classifier = NaiveBayesBinaryClassifier()


//    split the data into the training and test sets
//    TODO

//    train Naïve Bayes Classifier
//    TODO

//    measure accuracy on the test set
//    TODO

}
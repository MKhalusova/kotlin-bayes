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

// remove XML character encodings
fun String.removeXMLEncodings() = replace(regex = Regex("&[a-z]*;")," ")

// remove extra spaces
fun String.removeExtraSpaces() = replace(regex = Regex("\\s+")," ")

fun String.cleanTweet(): String {
    return this.removeTickers().removeRTs().removeURLs().removeHashtags().removeMentions().removeXMLEncodings().removeExtraSpaces()
}

fun preprocessTweet(tweet: String): List<String> {
    val cleanedTweet = tweet.cleanTweet()

    // tokenizing the tweet: splitting into words, lowercasing, dropiing punctuation, keeping emojis
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

    // stem words with PorterStemmer
    // from nltk.stem import PorterStemmer
    // stemmer = PorterStemmer()
    // stem_word = stemmer.stem(word)  # stemming word

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

//    remove hashtags, mentions, URLs, extra whitespaces and old style RTs from the tweets
    val processedPositiveTweets = positiveTweets.map { preprocessTweet(it) }
    val processedNegativeTweets = negativeTweets.map { preprocessTweet(it) }



}
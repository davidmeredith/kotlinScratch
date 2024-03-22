/*
Type classes Demo
Shows how to graft new behaviour onto existing types using Kotlin's
extension functions declared on a generic type interface.
Type classes are useful if you don't have the src code of the types you need
to add behaviour to e.g. NewsArticle and Tweet. V.similar to Rust's traits.
 */
data class NewsArticle(val headline: String, val author: String)
data class Tweet(val tweet: String, val retweet: String)

/**
 * Generic Type-Class interface used to graft new behaviour onto existing types.
 * Note that we add the new methods to the generic type 'T'.
 * (note that info() has a default implementation).
 */
interface Summary<T> {
  fun T.summarise(): String
  fun T.info(): String = "Default Information"
}

interface Farewell<D> {
  fun D.sayBye(): String
}

/*
Define concrete implementations of interfaces below on concrete parent types
 */

// Graft Farewell behaviour onto Any Kotlin type, sweet.
val anyFarewellScope = object : Farewell<Any> {
  override fun Any.sayBye(): String = "Tat ta"
}

// Graft Summary behaviour onto Tweets
val tweetSummaryScope = object : Summary<Tweet> {
  override fun Tweet.summarise(): String {
    return "Tweet: $tweet Retweet: $retweet"
  }
  // tweet will use the default implementation of 'info()'.
}

// Graft Summary behaviour onto NewsArticles
val newsArticleSummaryScope = object : Summary<NewsArticle> {
  override fun NewsArticle.summarise(): String {
    return "Headline: $headline Author: $author"
  }
  override fun NewsArticle.info(): String {
    return "NewsArticle Information"
  }
}


fun <T> Summary<T>.globalFuncWithSingleContext(toSummarise: T) =
  "Summary is: ${toSummarise.summarise()}, Info: ${toSummarise.info()}"


context(Summary<T>, Farewell<D>)
fun <T, D> globalFuncWithMultipleContexts(toSummarise: T, daveCtx: D) =
  // This global function uses the new context() decorator so that you can specify multiple contexts.
  "Summary is: ${toSummarise.summarise()}, Info: ${toSummarise.info()} Bye: ${daveCtx.sayBye()}"


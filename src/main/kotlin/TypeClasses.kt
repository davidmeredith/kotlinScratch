/*
Type-classes Demo
Shows how to graft new behaviour onto existing types using Kotlin's extension functions added to a generic type-class
interface.
Type classes are useful if you don't have the src code of the types you need to add behaviour to. In this example,
assume you don't have the src for NewsArticle and Tweet. Kotlin type-classes are very similar to Rust's traits.
 */
data class NewsArticle(val headline: String, val author: String)
data class Tweet(val tweet: String, val retweet: String)

/*
 * 2 examples of a 'Generic Type-Class Interfaces' (Summary<T> and Farewell<D>)
 * These are used to graft new behaviour onto unknown existing types T and D.
 * Note that we add the new methods to the generic types 'T' and 'D', not to the interfaces.
 */

interface Summary<T> {
  fun T.summarise(): String
  fun T.info(): String = "Default Information" // default implementation
}

interface Farewell<D> {
  fun D.sayBye(): String
}

/*
  Given these generic type-classes, there are multiple ways to add new default behaviour to our existing types, below.

  The key point in these examples is that you need both the following:
  1. The generic type-class instance (`Summary<T>`)
  2. An actual value of type T to call the extension functions on `T`
  This is because the extension functions are defined on the type parameter T, not on the interface itself `T`

  So, the example below doesn't make sense & won't compile - summarise() isn't defined on summary, it is defined on T
     fun <T> someFunction(summary: Summary<T>): Unit {
       //summary.summarise() // ERROR - unresolved reference 'summarise'
     }
 */

// Method 1.
// Create an 'anonymous scope object' to provide the default behaviour of the generic type-class for the existing type.
// The scope object can then be used as a receiver at the call site using a 'with(receiver)' scope function call e.g.
//   'with(tweetSummaryScope) {
//        existingTweet.sayBye() // existingTweet can use tweetSummaryScope's sayBye()
//    }'

// Provide a scope object to Graft Summary behaviour onto Tweets
val tweetSummaryScope = object : Summary<Tweet> {
  override fun Tweet.summarise(): String {
    return "Tweet: $tweet Retweet: $retweet"
  }
  // tweet will use the default implementation of 'info()'.
}

// Provide a scope object to Graft Summary behaviour onto NewsArticles
val newsArticleSummaryScope = object : Summary<NewsArticle> {
  override fun NewsArticle.summarise(): String {
    return "Headline: $headline Author: $author"
  }
  override fun NewsArticle.info(): String {
    return "NewsArticle Information"
  }
}

// Provide a scope object to Graft Farewell.sayBye() onto the 'Any' Kotlin type, so that we can call sayBye() on any
// Kotlin object within a 'with(anyFarewellScope) { anyRef.sayBye() }' block, nice.
val anyFarewellScope = object : Farewell<Any> {
  override fun Any.sayBye(): String = "Tat ta"
}

// Method 2.
// Add extension functions to our generic type-class interface so that the function body has access to both the generic
// type-class (Summary<T>) and the value T.

fun <T> Summary<T>.globalFuncWithSingleContext(toSummarise: T) =
  "Summary is: ${toSummarise.summarise()}, Info: ${toSummarise.info()}"

// Method 3.
// Add context and fun parameters for both the generic type-class and the value T.
// Note with this method, we can add multiple context params.

context(summary: Summary<T>, farewell: Farewell<D>)
fun <T, D> globalFuncWithMultipleContexts(toSummarise: T, toSayGoodbye: D) =
  with(summary) {  // can access T's methods here
    with(farewell) { // can access D's methods here
      "Summary is: ${toSummarise.summarise()}, Info: ${toSummarise.info()} Bye: ${toSayGoodbye.sayBye()}"
    }
  }

// Method 4.
// Passing both the type-class instance and the value via plain old function arguments.
fun <T> someFunction(summary: Summary<T>, value: T) {
  with(summary) {
    value.summarise() // Can access T's methods here
  }
}
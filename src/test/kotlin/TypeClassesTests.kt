import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TypeClassesTests {
    @Test
    fun `test type-class demo with separate contexts`() {
        with(tweetSummaryScope) {
            val tweet = Tweet("my tweet", "my retweet")
            val myTweetSummary = tweet.summarise()
            val expectedTweetSummary = "Tweet: my tweet Retweet: my retweet"
            assertEquals(expectedTweetSummary, myTweetSummary)
        }
        with(newsArticleSummaryScope) {
            val newsArticle = NewsArticle("My headline", "Homer Simpson")
            val myArticleSummary = newsArticle.summarise()
            val expectedArticleSummary = "Headline: My headline Author: Homer Simpson"
            assertEquals(expectedArticleSummary, myArticleSummary)
        }
    }

    @Test
    fun `test type-class with stacked contexts`() {
        with(newsArticleSummaryScope){
            with(tweetSummaryScope){
                val tweet = Tweet("my tweet", "my retweet")
                assertEquals("Default Information", tweet.info())
                val myTweetSummary = tweet.summarise()
                val expectedTweetSummary = "Tweet: my tweet Retweet: my retweet"
                assertEquals(expectedTweetSummary, myTweetSummary)
                val newsArticle = NewsArticle("My headline", "Homer Simpson")
                val myArticleSummary = newsArticle.summarise()
                assertEquals("NewsArticle Information", newsArticle.info())
                val expectedArticleSummary = "Headline: My headline Author: Homer Simpson"
                assertEquals(expectedArticleSummary, myArticleSummary)
            }
        }
    }

    @Test
    fun `test type-class with global function with context`() {
        val tweet = Tweet("my tweet", "my retweet")
        val expected = "Summary is: Tweet: my tweet Retweet: my retweet, Info: Default Information"
        with(tweetSummaryScope) {
            with(anyFarewellScope) {
                assertEquals("Tat ta", tweet.sayBye())
                assertEquals(expected, globalFuncWithSingleContext(tweet))
                assertEquals("$expected Bye: Tat ta", globalFuncWithMultipleContexts(tweet, "Bye: "))
            }
        }
    }
}
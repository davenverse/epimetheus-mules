package io.chrisdavenport.epimetheus
package mules

import io.chrisdavenport.mules._
import org.specs2._
import cats.effect._

class CacheLookupCounterSpec extends mutable.Specification {

  import cats.effect.unsafe.implicits.global

  "CacheLookupCounter" should {
    "modify a cache" in {
      val test = for {
        cr <- CollectorRegistry.build[IO]
        cache <- MemoryCache.ofSingleImmutableMap[IO, String, String](None)
        modifier <- CacheLookupCounter.register(cr)
        newCache = modifier.meteredMemoryCache(cache, "foo")
        _ <- newCache.insert("yellow", "green")
        _ <- newCache.lookup("yellow")
        _ <- newCache.lookup("green")
        out <- cr.write004
      } yield dropCreatedLines(out, 2)

      val expected = 
      """# HELP mules_cache_lookup_total Cache Lookup Status Counter.
        |# TYPE mules_cache_lookup_total counter
        |mules_cache_lookup_total{cache_name="foo",status="miss",} 1.0
        |mules_cache_lookup_total{cache_name="foo",status="hit",} 1.0
        |# HELP mules_cache_lookup_created Cache Lookup Status Counter.
        |# TYPE mules_cache_lookup_created gauge""".stripMargin

      test.unsafeRunSync() must_=== expected
    }

    "modify multiple caches" in {
      val test = for {
        cr <- CollectorRegistry.build[IO]
        cache <- MemoryCache.ofSingleImmutableMap[IO, String, String](None)
        cache2 <- MemoryCache.ofSingleImmutableMap[IO, Int, Double](None)
        modifier <- CacheLookupCounter.register(cr)
        newCache = modifier.meteredMemoryCache(cache, "foo")
        newCache2 = modifier.meteredMemoryCache(cache2, "bar")
        _ <- newCache.insert("yellow", "green")
        _ <- newCache.lookup("yellow")
        _ <- newCache.lookup("green")
        _ <- cache2.insert(3, 0D)
        _ <- newCache2.lookup(3)
        out <- cr.write004
      } yield dropCreatedLines(out, 3)

      val expected = 
      """# HELP mules_cache_lookup_total Cache Lookup Status Counter.
        |# TYPE mules_cache_lookup_total counter
        |mules_cache_lookup_total{cache_name="foo",status="miss",} 1.0
        |mules_cache_lookup_total{cache_name="bar",status="hit",} 1.0
        |mules_cache_lookup_total{cache_name="foo",status="hit",} 1.0
        |# HELP mules_cache_lookup_created Cache Lookup Status Counter.
        |# TYPE mules_cache_lookup_created gauge""".stripMargin

      test.unsafeRunSync() must_=== expected
    }
  }

  private def dropCreatedLines(s: String, n: Int): String =
    s.linesIterator.toList.dropRight(n).mkString("\n")

}
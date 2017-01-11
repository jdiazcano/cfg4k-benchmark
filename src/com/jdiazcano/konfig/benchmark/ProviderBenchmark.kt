package com.jdiazcano.konfig.benchmark

import com.jdiazcano.konfig.providers.bind
import com.jdiazcano.konfig.bytebuddy.bytebuddy
import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.CachedConfigProvider
import com.jdiazcano.konfig.providers.ConfigProvider
import com.jdiazcano.konfig.providers.Providers
import kt.times.times
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.io.File

@State(Scope.Benchmark)
open class ProviderBenchmark {
    private val bytebuddy = Providers.bytebuddy(PropertyConfigLoader(File("test.properties").toURI().toURL()))
    private val proxy = Providers.proxy(PropertyConfigLoader(File("test.properties").toURI().toURL()))

    private val cachedBytebuddy = CachedConfigProvider(bytebuddy)
    private val cachedProxy = CachedConfigProvider(proxy)

    fun noBind(provider: ConfigProvider) {
        provider.getProperty("stringProperty", String::class.java)
        // provider.getProperty("integerProperty", Int::class.java)
        // provider.getProperty("floatProperty", Float::class.java)
        // provider.getProperty("doubleProperty", Double::class.java)
        // provider.getProperty("booleanProperty", Boolean::class.java)
    }

    @Benchmark
    fun bytebuddy() {
        100000.times {
            noBind(bytebuddy)
        }
    }

    //@Benchmark
    fun proxy() {
        10000.times {
            noBind(proxy)
        }
    }

    //@Benchmark
    fun cachedBytebuddy() {
        10000.times {
            noBind(cachedBytebuddy)
        }
    }

    //@Benchmark
    fun cachedProxy() {
        10000.times {
            noBind(cachedProxy)
        }
    }

    //@Benchmark
    fun bindingBytebuddy() {
        10000.times {
            cachedBytebuddy.bind<Benchmarked>("")
        }
    }

    //@Benchmark
    fun bindingProxy() {
        10000.times {
            cachedProxy.bind<Benchmarked>("")
        }
    }

    //@Benchmark
    fun bindingCachedBytebuddy() {
        10000.times {
            cachedBytebuddy.bind<Benchmarked>("")
        }
    }

    //@Benchmark
    fun bindingCachedProxy() {
        10000.times {
            cachedProxy.bind<Benchmarked>("")
        }
    }

}

fun main(args: Array<String>) {
    val opt = OptionsBuilder()
            .include(".*" + ProviderBenchmark::class.java.simpleName + ".*")
            .forks(1)
            .warmupIterations(5)
            .measurementIterations(5)
            .build()
    Runner(opt).run()
}

interface Benchmarked {
    fun stringProperty(): String
    fun integerProperty(): Int
    fun floatProperty(): Float
    fun doubleProperty(): Double
    fun booleanProperty(): Boolean
}
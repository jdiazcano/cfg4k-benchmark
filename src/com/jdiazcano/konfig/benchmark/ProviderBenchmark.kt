package com.jdiazcano.konfig.benchmark

import com.jdiazcano.konfig.providers.bind
import com.jdiazcano.konfig.bytebuddy.bytebuddy
import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.CachedConfigProvider
import com.jdiazcano.konfig.providers.ConfigProvider
import com.jdiazcano.konfig.providers.Providers
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.io.File

@State(Scope.Benchmark)
open class ProviderBenchmark {

    private lateinit var bytebuddy: ConfigProvider
    private lateinit var proxy: ConfigProvider
    private lateinit var cachedBytebuddy: ConfigProvider
    private lateinit var cachedProxy: ConfigProvider

    @Setup
    fun setUp() {
       bytebuddy = Providers.bytebuddy(PropertyConfigLoader(File("test.properties").toURI().toURL()))
       proxy = Providers.proxy(PropertyConfigLoader(File("test.properties").toURI().toURL()))

       cachedBytebuddy = CachedConfigProvider(bytebuddy)
       cachedProxy = CachedConfigProvider(proxy)
    }

    fun noBind(provider: ConfigProvider) {
        provider.getProperty("stringProperty", String::class.java)
        // provider.getProperty("integerProperty", Int::class.java)
        // provider.getProperty("floatProperty", Float::class.java)
        // provider.getProperty("doubleProperty", Double::class.java)
        // provider.getProperty("booleanProperty", Boolean::class.java)
    }

    @Benchmark
    fun bytebuddy() = bytebuddy.getProperty("integerProperty", Int::class.java)

    @Benchmark
    fun proxy() = proxy.getProperty("integerProperty", Int::class.java)

    @Benchmark
    fun cachedBytebuddy() = cachedBytebuddy.getProperty("integerProperty", Int::class.java)

    @Benchmark
    fun cachedProxy() = cachedProxy.getProperty("integerProperty", Int::class.java)

    @Benchmark
    fun bindingBytebuddy() = cachedBytebuddy.bind<Benchmarked>("")

    @Benchmark
    fun bindingProxy() = cachedProxy.bind<Benchmarked>("")

    @Benchmark
    fun bindingCachedBytebuddy() = cachedBytebuddy.bind<Benchmarked>("")

    @Benchmark
    fun bindingCachedProxy() = cachedProxy.bind<Benchmarked>("")

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
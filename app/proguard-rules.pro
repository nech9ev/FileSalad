-dontwarn io.ktor.client.features.HttpTimeout$Feature
-dontwarn io.ktor.client.features.HttpTimeout$HttpTimeoutCapabilityConfiguration
-dontwarn io.ktor.client.features.HttpTimeout
-dontwarn io.ktor.client.features.HttpTimeoutKt
-dontwarn io.ktor.network.sockets.ConnectTimeoutException
-dontwarn io.ktor.network.sockets.TimeoutExceptionsCommonKt
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.gradle.api.Plugin

-keepnames class <1>$$serializer { # -keepnames suffices; class is kept when serializer() is kept.
    static <1>$$serializer INSTANCE;
}

java_plugin(
    name = "SimpleUntrustedDataPassPlugin",
    srcs = ["SimpleUntrustedDataPassPlugin.java"],
    deps = [
        "@error_prone//jar",
            "@guava//jar",
            "//third_party/java/auto_service",
    ],
)

java_library(
    name = "SQLinjectionTest",
    srcs = ["SQLInjectionTest.java"],
    plugins = [":SimpleUntrustedDataPassPlugin"],
)

java_library(
    name = "XMLinjectionTest",
    srcs = ["XMLInjectionTest.java"],
    plugins = [":SimpleUntrustedDataPassPlugin"],
)

java_library(
    name = "RuntimeExecInjectionTest",
    srcs = ["RuntimeCommandInjectionTest.java"],
    plugins = [":SimpleUntrustedDataPassPlugin"],
)

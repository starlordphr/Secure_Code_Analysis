java_plugin(
    name = "XMLUnsanitizedDataCheckPlugin",
    srcs = ["AbstractUnsanitizedUntrustedDataPassedCheck.java","XMLUnsanitizedDataCheck.java"],
    deps = [
        "@error_prone//jar",
            "@guava//jar",
            "//third_party/java/auto_service",
    ],
)

java_plugin(
    name = "SQLUnsanitizedDataCheckPlugin",
    srcs = ["AbstractUnsanitizedUntrustedDataPassedCheck.java", "SQLUnsanitizedDataCheck.java"],
    deps = [
        "@error_prone//jar",
            "@guava//jar",
            "//third_party/java/auto_service",
    ],
)

java_library(
    name = "SQLinjectionTest",
    srcs = ["SQLInjectionTest.java"],
    plugins = [":SQLUnsanitizedDataCheckPlugin"],
)

java_library(
    name = "XMLinjectionTest",
    srcs = ["XMLInjectionTest.java"],
    plugins = [":XMLUnsanitizedDataCheckPlugin"],
)

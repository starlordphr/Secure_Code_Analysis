java_plugin(
    name = "XMLUnsanitizedDataCheckPlugin",
    srcs = ["XMLUnsanitizedDataCheck.java"],
    deps = [
        "@error_prone//jar",
            "@guava//jar",
            "//third_party/java/auto_service",
    ],
)

java_plugin(
    name = "SQLUnsanitizedDataCheckPlugin",
    srcs = ["SQLUnsanitizedDataCheck.java"],
    deps = [
        "@error_prone//jar",
            "@guava//jar",
            "//third_party/java/auto_service",
    ],
)

java_library(
    name = "injectionLanguageTests",
    srcs = ["SQLInjectionTest.java", "XMLInjectionTest.java"],
    plugins = [":SQLUnsanitizedDataCheckPlugin", ":XMLUnsanitizedDataCheckPlugin"],
)

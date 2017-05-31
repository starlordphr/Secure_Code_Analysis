java_plugin(
    name = "ExtendedReturnValueIgnoredCheck",
    srcs = ["ExtendedReturnValueIgnoredCheck.java"],
    deps = ["@error_prone//jar",
            "@guava//jar",
            "//third_party/java/auto_service",],
)

java_library(
    name = "ignoredReturnValue",
    srcs = ["ValuesReturnedByMethodIgnoredTest.java"],
    plugins = [":ExtendedReturnValueIgnoredCheck"],
)

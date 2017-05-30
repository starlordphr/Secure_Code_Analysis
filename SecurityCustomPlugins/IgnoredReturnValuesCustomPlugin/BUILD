java_plugin(
    name = "ExtendedReturnValueIgnoredCheck",
    srcs = ["ExtendedReturnValueIgnoredCheck.java"],
    deps = ["@error_prone//jar",
             "@auto_service//jar"],
)

java_library(
    name = "ignoredReturnValue",
    srcs = ["ValuesReturnedByMethodIgnoredTest.java"],
    plugins = [":ExtendedReturnValueIgnoredCheck"],
)

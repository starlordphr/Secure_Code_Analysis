java_plugin(
    name = "Insecure_Randomness_Check",
    srcs = ["Insecure_Randomness_Check.java"],
    deps = ["@error_prone//jar",
            "@guava//jar",
            "//third_party/java/auto_service",],
)

java_library(
    name = "NonCompliantTest",
    srcs = ["NonCompliantTest.java",],
    plugins = [":Insecure_Randomness_Check"],
)

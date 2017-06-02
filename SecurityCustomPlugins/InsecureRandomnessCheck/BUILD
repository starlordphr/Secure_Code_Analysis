java_plugin(
    name = "InsecureRandomCheck",
    srcs = ["InsecureRandomCheck.java"],
    deps = ["@error_prone//jar",
            "//third_party/java/auto_service",],
)

java_library(
    name = "NonCompliantTest",
    srcs = ["NonCompliantTest.java",],
    plugins = [":InsecureRandomCheck"],
)

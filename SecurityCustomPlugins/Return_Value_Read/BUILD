java_plugin(
    name = "CaptureReturnValueofRead",
    srcs = ["CaptureReturnValueofRead.java"],
    deps = ["@error_prone//jar",
            "@guava//jar",
            "//third_party/java/auto_service",],
)

java_library(
    name = "NonCompliantTest1",
    srcs = ["NonCompliantTest1.java",],
    plugins = [":CaptureReturnValueofRead"],
)

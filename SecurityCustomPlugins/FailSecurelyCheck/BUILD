java_plugin(
    name = "FailSecurelyCheck",
    srcs = ["FailSecurelyCheck.java"],
    deps = ["@error_prone//jar",
            "//third_party/java/auto_service",],
)

java_library(
    name = "NonCompliant",
    srcs = ["NonCompliant.java",],
    plugins = [":FailSecurelyCheck"],
)

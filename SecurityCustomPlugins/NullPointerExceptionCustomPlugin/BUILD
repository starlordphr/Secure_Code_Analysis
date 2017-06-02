java_plugin(
    name = "NPEAndParentsCaughtCheck",
    srcs = ["NPEAndParentsCaughtCheck.java"],
    deps = ["@error_prone//jar",
            "@guava//jar",
            "//third_party/java/auto_service",],
)

java_library(
    name = "nullPointerExceptionCaughtTests",
    srcs = ["NullPointerExceptionCaughtTest.java"],
    plugins = [":NPEAndParentsCaughtCheck"],
)

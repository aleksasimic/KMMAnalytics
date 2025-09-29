// swift-tools-version:5.9
import PackageDescription

let package = Package(
  name: "KMMAnalytics",
  platforms: [
   .iOS(.v14),
  ],
  products: [
   .library(name: "KMMAnalytics", targets: ["KMMAnalytics"])
  ],
  targets: [
   .binaryTarget(
     name: "KMMAnalytics",
     url: "https://github.com/aleksa-aetherius/KMMAnalytics/releases/download/1.0.4/KMMAnalytics.xcframework.zip",
checksum:"c8d21d43490f94ecd442bad114e7684f2898d9624f757a78d13d09dc06fca885"
)
  ]
)
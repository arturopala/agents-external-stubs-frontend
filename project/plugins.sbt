resolvers += Resolver.url("HMRC Sbt Plugin Releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(
  Resolver.ivyStylePatterns)
resolvers += "HMRC Releases" at "https://dl.bintray.com/hmrc/releases"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.5")

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "2.9.0")
addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "2.0.0")
addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "2.1.0")
addSbtPlugin("uk.gov.hmrc" % "sbt-artifactory" % "1.3.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")

addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.16")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")  // provides sbt command "dependencyUpdates"
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")  // provides sbt command "dependencyTree"

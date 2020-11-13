
#
# On azure, outgoing idle connection are dropped after 4 minutes.
# Usually, you can configure wagon with ttl. But these settings are
# ignored, as soon as dokka-maven-plugin is loaded.
# dokka-maven-plugin tries to load additional dependencies at runtime
# and injects a different http client, which is not configured correctly
# and thus maven fails if it tries to download later in the build process
# further dependencies.
#
# The workaround applied here is: first resolve all dependencies,
# then explicitly get dokka-maven-plugin and then resolve all plugins
# execpt for dokka-maven-plugin, as it does not play well with dependency-plugin.
#
function maven_dependencies_resolve() {
    dokka_version=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${dokka.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:3.0.0:exec)

    ./mvnw dependency:resolve
    ./mvnw dependency:get -DgroupId=org.jetbrains.dokka \
           -DartifactId=dokka-maven-plugin \
           -Dversion=${dokka_version} \
           -Dpackaging=jar \
           -DremoteRepositories=jcenter::default::https://jcenter.bintray.com/
    ./mvnw dependency:resolve-plugins -DexcludeGroupIds=org.jetbrains.dokka
}

# hotwire-samples

A Kotlin/Java SpringBoot sample of https://hotwire.dev/ tooling

See accompanying blog post at https://delitescere.medium.com/hotwire-html-over-the-wire-2c733487268c?source=friends_link&sk=8bc17367a27ea7248491ccc84e58b6dd

## Running locally

macOS: `((while ! nc -z localhost 8080; do sleep 1; done; open http://localhost:8080) &); ./gradlew bootRun`

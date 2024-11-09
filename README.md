# Kotlin—Ktor Application using Http with Websocket

## Build

```shell
./gradlew clean build
```

## Test
```shell
./gradlew clean test
```

## Run
```shell
./gradlew clean run
```

## Run on Docker

```shell
./gradlew clean buildImage
```

```shell
docker load -i build/jib-image.tar 
```

```shell
docker run -p 8080:8080 julianocervelin/mad-chat-api:latest
```

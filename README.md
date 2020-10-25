## Java app
```
./gradlew :app:run -Djava.library.path=$PWD/native/build/lib/main/debug
```

## Native app
```
./gradlew :native_app:assemble && LD_PRELOAD=$PWD/native/build/lib/main/debug/libnative.so ./native_app/build/exe/main/debug/native_app
```
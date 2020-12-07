build_dir=build
if [ ! -d "$build_dir" ]; then
  mkdir "$build_dir"
fi
go build -buildmode=c-shared -o build/libelvb58.dylib libelvb58/main.go

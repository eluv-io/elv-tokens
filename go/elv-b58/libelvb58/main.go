package main

import (
	"github.com/mr-tron/base58"
	"unsafe"
)

/*
#include <stdlib.h>

*/
import "C"

//export Base58
func Base58(cBytes unsafe.Pointer, length C.int) *C.char {
	result := base58.Encode(C.GoBytes(cBytes, length))
	return C.CString(result)
}

//export FreeCString
func FreeCString(a *C.char) {
	C.free(unsafe.Pointer(a))
}

/*
go build -buildmode=c-shared -o ../bin/libelvb58.so libelvb58/main.go
*/
func main() {}

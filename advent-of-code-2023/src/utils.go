package main

import (
	"fmt"
	"time"
)

func check[T comparable](part string, expected T, retrieveValue func() T) {
	fmt.Print(part, ": ")
	start := time.Now()
	result := retrieveValue()
	elapsedTime := time.Since(start).Milliseconds()
	fmt.Print(result, fmt.Sprintf(" (elapsed: %d ms) -- ", elapsedTime))
	if result == expected {
		fmt.Println("OK")
	} else {
		fmt.Println("ERROR! Expected: ", expected, " Actual: ", result)
	}
}

func simulate[T any](part string, retrieveValue func() T) {
	fmt.Print(part, ": ")
	start := time.Now()
	result := retrieveValue()
	elapsedTime := time.Since(start).Milliseconds()
	fmt.Print(result, fmt.Sprintf(" (elapsed: %d ms)", elapsedTime))
}

// reverse returns its argument string reversed rune-wise left to right.
func reverse(s string) string {
	r := []rune(s)
	for i, j := 0, len(r)-1; i < len(r)/2; i, j = i+1, j-1 {
		r[i], r[j] = r[j], r[i]
	}
	return string(r)
}

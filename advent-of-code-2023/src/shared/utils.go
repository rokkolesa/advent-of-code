package shared

import (
	"fmt"
	"strconv"
	"strings"
	"time"
)

func Check[T comparable](part string, expected T, retrieveValue func() T) {
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

func Simulate[T any](part string, retrieveValue func() T) {
	fmt.Print(part, ": ")
	start := time.Now()
	result := retrieveValue()
	elapsedTime := time.Since(start).Milliseconds()
	fmt.Print(result, fmt.Sprintf(" (elapsed: %d ms)", elapsedTime))
}

// Reverse returns its argument string reversed rune-wise left to right.
func Reverse(s string) string {
	r := []rune(s)
	for i, j := 0, len(r)-1; i < len(r)/2; i, j = i+1, j-1 {
		r[i], r[j] = r[j], r[i]
	}
	return string(r)
}

func Range(from, to int) (slice []int) {
	slice = make([]int, to-from)
	for i := range slice {
		slice[i] = i + from
	}
	return
}

type Point struct {
	X int
	Y int
}

func (p Point) Adjacent() []Point {
	return []Point{
		// left-right
		{X: p.X - 1, Y: p.Y},
		{X: p.X + 1, Y: p.Y},
		// top-bottom
		{X: p.X, Y: p.Y + 1},
		{X: p.X, Y: p.Y - 1},
		// diagonals
		{X: p.X - 1, Y: p.Y - 1},
		{X: p.X + 1, Y: p.Y - 1},
		{X: p.X + 1, Y: p.Y + 1},
		{X: p.X - 1, Y: p.Y + 1},
	}
}

func Reduce[T any](slice []T, initialState T, reducer func(T, T) T) T {
	state := initialState
	for _, element := range slice {
		state = reducer(state, element)
	}
	return state
}

func Sum[T int | float64](slice []T) T {
	return Reduce(slice, 0, func(state T, element T) T {
		return state + element
	})
}

func Map[T any, R any](slice []T, mapper func(T) R) []R {
	newSlice := make([]R, len(slice))
	for i, element := range slice {
		newSlice[i] = mapper(element)
	}
	return newSlice
}

func Filter[T any](slice []T, filter func(T) bool) []T {
	var newSlice []T
	for _, element := range slice {
		if filter(element) {
			newSlice = append(newSlice, element)
		}
	}
	return newSlice
}

func ParseIntsAfter(input string, after string) []int {
	return ParseInts(input[strings.Index(input, after)+1:])
}
func ParseInts(input string) []int {
	return Map(
		strings.Fields(strings.TrimSpace(input)),
		func(str string) int {
			parsed, _ := strconv.Atoi(str)
			return parsed
		},
	)
}

//func ParseIntsSep(input string, sep rune) []int {
//	return Map(
//		strings.FieldsFunc(
//			strings.TrimSpace(input),
//			func(r rune) bool {
//				return r == sep
//			},
//		),
//		func(str string) int {
//			parsed, _ := strconv.Atoi(str)
//			return parsed
//		},
//	)
//}
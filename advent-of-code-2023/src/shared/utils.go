package shared

import (
	"cmp"
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
	if from > to {
		from, to = to, from
	}
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

func Reduce[T, U any](slice []T, initialState U, reducer func(U, T) U) U {
	return ReduceIndexed(slice, initialState, func(state U, element T, _ int) U {
		return reducer(state, element)
	})
}

func ReduceIndexed[T, U any](slice []T, initialState U, reducer func(U, T, int) U) U {
	state := initialState
	for i, element := range slice {
		state = reducer(state, element, i)
	}
	return state
}

func Sum[T int | int64 | float64](slice []T) T {
	return Reduce(slice, 0, func(state T, element T) T {
		return state + element
	})
}

func Product[T int | int64 | float64](slice []T) T {
	return Reduce(slice, 1, func(state T, element T) T {
		return state * element
	})
}

func Map[T any, R any](slice []T, mapper func(T) R) []R {
	return MapIndexed(slice, func(t T, _ int) R { return mapper(t) })
}

func MapIndexed[T any, R any](slice []T, mapper func(T, int) R) []R {
	newSlice := make([]R, len(slice))
	for i, element := range slice {
		newSlice[i] = mapper(element, i)
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

func AnyMatch[T any](slice []T, test func(T) bool) bool {
	for _, element := range slice {
		if test(element) {
			return true
		}
	}
	return false
}

func AllMatch[T any](slice []T, test func(T) bool) bool {
	return !AnyMatch(slice, func(element T) bool {
		return !test(element)
	})
}

func MaxEntryByValue[K comparable, V cmp.Ordered](m map[K]V) (K, V) {
	var maxKey K
	var maxValue V
	for key, value := range m {
		if value > maxValue {
			maxKey = key
			maxValue = value
		}
	}
	return maxKey, maxValue
}

func ParseIntSafe(str string) int {
	parsed, _ := strconv.Atoi(str)
	return parsed
}
func ParseInt64Safe(str string) int64 {
	parsed, _ := strconv.ParseInt(str, 10, 64)
	return parsed
}
func ParseFloatSafe(str string) float64 {
	parsed, _ := strconv.ParseFloat(str, 64)
	return parsed
}

func ParseIntsAfter(input string, after string) []int {
	return ParseFuncAfter(input, after, ParseIntSafe)
}
func ParseInts(input string) []int {
	return ParseFunc(input, ParseIntSafe)
}

func ParseFunc[T any](input string, parseFunc func(string) T) []T {
	return Map(
		strings.Fields(strings.TrimSpace(input)),
		parseFunc,
	)
}

func ParseFuncAfter[T any](input string, after string, parseFunc func(string) T) []T {
	return ParseFunc(input[strings.Index(input, after)+1:], parseFunc)
}

func DeleteSpaces(input string) string {
	return strings.ReplaceAll(input, " ", "")
}

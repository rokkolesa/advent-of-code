package shared

import (
	"cmp"
	"fmt"
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

func DeleteSpaces(input string) string {
	return strings.ReplaceAll(input, " ", "")
}

func Transpose(a []string) (b []string) {
	b = make([]string, len(a[0]))
	for _, s := range a {
		for j, c := range s {
			b[j] += string(c)
		}
	}
	return
}

func Mod(a, b int) int {
	return (a%b + b) % b
}

func Gcd(a, b int) int {
	for b != 0 {
		b, a = a%b, b
	}
	return a
}

func Lcm(a, b int) int {
	return a * b / Gcd(a, b)
}

type Layout[T any] [][]T

func (layout Layout[T]) InBounds(p Point) bool {
	yBound := len(layout)
	xBound := len(layout[0])
	return 0 <= p.Y && p.Y < yBound && 0 <= p.X && p.X < xBound
}

func ParseLayout(input string) Layout[string] {
	return ParseLayoutFunc(input, func(x, y int, s string) string { return s })
}
func ParseLayoutFunc[T any](input string, transform func(x, y int, s string) T) Layout[T] {
	lines := strings.Split(input, "\n")
	layout := make(Layout[T], len(lines))
	for y, line := range lines {
		layout[y] = make([]T, len(line))
		for x, char := range line {
			layout[y][x] = transform(x, y, string(char))
		}
	}
	return layout
}

package shared

import (
	"strconv"
	"strings"
)

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

func ParseIntsSep(input string, sep rune) []int {
	return ParseFuncSep(input, sep, ParseIntSafe)
}

func ParseFunc[T any](input string, parseFunc func(string) T) []T {
	return Map(
		strings.Fields(strings.TrimSpace(input)),
		parseFunc,
	)
}

func ParseFuncSep[T any](input string, sep rune, parseFunc func(string) T) []T {
	return Map(
		strings.FieldsFunc(strings.TrimSpace(input), func(r rune) bool { return r == sep }),
		parseFunc,
	)
}

func ParseFuncAfter[T any](input string, after string, parseFunc func(string) T) []T {
	return ParseFunc(input[strings.Index(input, after)+1:], parseFunc)
}

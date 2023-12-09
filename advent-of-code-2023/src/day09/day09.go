package main

import (
	"../shared"
	_ "embed"
	"slices"
	"strings"
)

//go:embed day09_sample.txt
var sample string

//go:embed day09.txt
var input string

func part1(input string) int {
	history := shared.Map(strings.Split(input, "\n"), shared.ParseInts)
	return shared.Sum(shared.Map(history, extrapolate))
}

func part2(input string) int {
	history := shared.Map(strings.Split(input, "\n"), shared.ParseInts)
	for _, h := range history {
		slices.Reverse(h)
	}
	return shared.Sum(shared.Map(history, extrapolate))
}

func extrapolate(history []int) int {
	j := len(history) - 1
	for shared.AnyMatch(history[:j], func(i int) bool { return i != 0 }) {
		for i := 0; i < j; i++ {
			history[i] = history[i+1] - history[i]
		}
		j--
	}
	return shared.Sum(history)
}

func main() {
	shared.Check("Part 1", 114, func() int {
		return part1(sample)
	})
	shared.Check("Part 1", 1972648895, func() int {
		return part1(input)
	})
	shared.Check("Part 2", 2, func() int {
		return part2(sample)
	})
	shared.Check("Part 2", 919, func() int {
		return part2(input)
	})
}

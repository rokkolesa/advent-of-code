package main

import (
	. "../shared"
	_ "embed"
	"slices"
	"strings"
)

//go:embed day09_sample.txt
var sample string

//go:embed day09.txt
var input string

func part1(input string) int {
	history := Map(strings.Split(input, "\n"), ParseInts)
	return Sum(Map(history, extrapolate))
}

func part2(input string) int {
	history := Map(strings.Split(input, "\n"), ParseInts)
	for _, h := range history {
		slices.Reverse(h)
	}
	return Sum(Map(history, extrapolate))
}

func extrapolate(history []int) int {
	//for j := len(history) - 1; shared.AnyMatch(history[:j], func(i int) bool { return i != 0 }); j-- {
	for j := len(history) - 1; j > 0; j-- {
		for i := 0; i < j; i++ {
			history[i] = history[i+1] - history[i]
		}
	}
	return Sum(history)
}

func main() {
	Check("Part 1", 114, func() int {
		return part1(sample)
	})
	Check("Part 1", 1972648895, func() int {
		return part1(input)
	})
	Check("Part 2", 2, func() int {
		return part2(sample)
	})
	Check("Part 2", 919, func() int {
		return part2(input)
	})
}

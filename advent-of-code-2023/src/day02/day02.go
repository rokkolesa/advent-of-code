package main

import (
	"../shared"
	_ "embed"
	"strconv"
	"strings"
)

//go:embed day02_sample.txt
var sample string

//go:embed day02.txt
var input string

func part1(input string) int {
	cubes := map[string]int{
		"red":   12,
		"green": 13,
		"blue":  14,
	}
	sum := 0
	for gameId, game := range strings.Split(input, "\n") {
		allGamesPossible := parseAndCheck(game, func(color string, number int) bool {
			return number <= cubes[color]
		})
		if allGamesPossible {
			sum += gameId + 1
		}
	}
	return sum
}

func part2(input string) int {
	sum := 0
	for _, game := range strings.Split(input, "\n") {
		cubes := map[string]int{
			"red":   0,
			"green": 0,
			"blue":  0,
		}

		parseAndCheck(game, func(color string, number int) bool {
			if number > cubes[color] {
				cubes[color] = number
			}
			return true
		})

		gamePower := 1
		for _, cubeValue := range cubes {
			gamePower *= cubeValue
		}
		sum += gamePower
	}
	return sum
}

func parseAndCheck(game string, action func(string, int) bool) bool {
	colonIndex := strings.Index(game, ":")
	revealed := strings.Split(game[colonIndex+1:], ";")

	for _, configuration := range revealed {
		for _, numberAndColor := range strings.Split(configuration, ",") {
			numberAndColorSplit := strings.Split(strings.TrimSpace(numberAndColor), " ")
			number, _ := strconv.Atoi(numberAndColorSplit[0])
			color := numberAndColorSplit[1]
			if !action(color, number) {
				return false
			}
		}
	}
	return true
}

func main() {
	shared.Check("Part 1", 8, func() int {
		return part1(sample)
	})
	shared.Check("Part 1", 2810, func() int {
		return part1(input)
	})
	shared.Check("Part 2", 2286, func() int {
		return part2(sample)
	})
	shared.Check("Part 2", 69110, func() int {
		return part2(input)
	})
}

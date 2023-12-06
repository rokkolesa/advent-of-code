package main

import (
	"../shared"
	_ "embed"
	"math"
	"strings"
)

//go:embed day06_sample.txt
var sample string

//go:embed day06.txt
var input string

func part1(input string) int {
	split := strings.Split(input, "\n")

	// parse as floats as it will come in handy later
	times := shared.ParseFuncAfter(split[0], ":", shared.ParseFloatSafe)
	distances := shared.ParseFuncAfter(split[1], ":", shared.ParseFloatSafe)

	return winScenarios(times, distances)
}

func part2(input string) int {
	return part1(shared.DeleteSpaces(input))
}

func winScenarios(times []float64, distances []float64) int {
	var scenarios = make([]int, len(times))
	for i := range times {
		time := times[i]
		distance := distances[i]
		// (time - button) * button > distance
		// -button^2 + time*button - distance > 0
		// (-b +- sqrt(b^2 - 4ac) ) / 2a
		// (-time +- sqrt(time^2 - 4 (-1) (-distance)) / -2
		first := (-time + math.Sqrt(time*time-4*distance)) / -2
		second := (-time - math.Sqrt(time*time-4*distance)) / -2

		// strict inequality!
		// the result of 2,3 gives the floor of 3,3, which is 3
		// the result of 2,0 gives the floor of 3,0, which is 3
		// the result of 1,7 gives the ceil of 0,7, which is 1
		// the result of 2,0 gives the ceil of 1,0 which is 1
		firstPossibleWin := int(math.Floor(first + 1))
		lastPossibleWin := int(math.Ceil(second - 1))

		scenarios[i] = lastPossibleWin - firstPossibleWin + 1
	}

	return shared.Product(scenarios)
}

func main() {
	shared.Check("Part 1", 288, func() int {
		return part1(sample)
	})
	shared.Check("Part 1", 281600, func() int {
		return part1(input)
	})
	shared.Check("Part 2", 71503, func() int {
		return part2(sample)
	})
	shared.Check("Part 2", 33875953, func() int {
		return part2(input)
	})
}

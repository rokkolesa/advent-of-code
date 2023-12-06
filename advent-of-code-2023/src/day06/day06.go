package main

import (
	"../shared"
	_ "embed"
	"math"
	"strconv"
	"strings"
)

//go:embed day06_sample.txt
var sample string

//go:embed day06.txt
var input string

func part1(input string) int {
	split := strings.Split(input, "\n")
	times := shared.ParseIntsAfter(split[0], ":")
	distances := shared.ParseIntsAfter(split[1], ":")

	return winScenarios(times, distances)
}

func winScenarios(times []int, distances []int) int {
	var scenarios []int
	for i := range times {
		time := float64(times[i])
		distance := float64(distances[i])
		// -button^2 + time*button - distance > 0
		// (-b +- sqrt(b^2 - 4ac) ) / 2a
		// (-time +- sqrt(time^2 - 4 (-1) (-distance)) / -2
		first := (-time + math.Sqrt(time*time-4*distance)) / -2
		second := (-time - math.Sqrt(time*time-4*distance)) / -2

		// strict non-equals!
		firstPossibleWin := int(math.Floor(first + 1))
		lastPossibleWin := int(math.Ceil(second - 1))

		//fmt.Println("First", first, "Second", second)
		//fmt.Println("First possible", firstPossibleWin, "Second possible", lastPossibleWin, "All scenarios:", lastPossibleWin-firstPossibleWin+1)

		scenarios = append(scenarios, lastPossibleWin-firstPossibleWin+1)
	}

	return shared.Reduce(scenarios, 1,
		func(acc, scenario int) int {
			return acc * scenario
		})
}

func part2(input string) int {
	split := strings.Split(input, "\n")
	timeString := strings.Join(strings.Fields(strings.Split(split[0], ":")[1]), "")
	time, _ := strconv.Atoi(timeString)
	distanceString := strings.Join(strings.Fields(strings.Split(split[1], ":")[1]), "")
	distance, _ := strconv.Atoi(distanceString)

	return winScenarios([]int{time}, []int{distance})
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

package main

import (
	. "../shared"
	_ "embed"
	"fmt"
	"slices"
	"strings"
)

//go:embed day12_sample.txt
var sample string

//go:embed day12.txt
var input string

func memoKey(configuration string, numberConfiguration []int, operational bool, damaged bool) string {
	sprintf := fmt.Sprintf("%v%v%v%v", configuration, numberConfiguration, operational, damaged)
	return sprintf
}

func part1(input string) int {
	return Sum(Map(strings.Split(input, "\n"), possibleSprings))
}

func possibleSprings(line string) int {
	springDefinition := strings.Fields(line)
	configuration := springDefinition[0]
	numberConfiguration := ParseIntsSep(springDefinition[1], ',')
	previousResults := make(map[string]int)
	combinations := possibleCombinations(configuration, numberConfiguration, false, false, previousResults)
	return combinations
}

func possibleCombinations(configuration string, numberConfiguration []int, operational bool, damaged bool, results map[string]int) int {
	resultKey := memoKey(configuration, numberConfiguration, operational, damaged)
	if result, exists := results[resultKey]; exists {
		return result
	}
	if operational && configuration[0] == '#' || damaged && configuration[0] == '.' {
		results[resultKey] = 0
		return results[resultKey]
	}

	if !strings.Contains(configuration, "?") {
		if isValid(configuration, numberConfiguration) {
			results[resultKey] = 1
		} else {
			results[resultKey] = 0
		}
		return results[resultKey]
	}

	if configuration[0] == '.' {
		results[resultKey] = possibleCombinations(configuration[1:], numberConfiguration, false, false, results)
		return results[resultKey]
	}

	if configuration[0] == '#' {
		// copy values as we mutate this slice
		numberConfiguration = slices.Clone(numberConfiguration)

		if len(numberConfiguration) <= 0 {
			results[resultKey] = 0
			return results[resultKey]
		}
		number := numberConfiguration[0]
		number--
		operational = false
		damaged = false
		if number <= 0 {
			numberConfiguration = slices.Delete(numberConfiguration, 0, 1)
			operational = true
		} else {
			numberConfiguration[0] = number
			damaged = true
		}
		results[resultKey] = possibleCombinations(configuration[1:], numberConfiguration, operational, damaged, results)
		return results[resultKey]
	}

	combinations := possibleCombinations(strings.Replace(configuration, "?", "#", 1), numberConfiguration, operational, damaged, results) +
		possibleCombinations(strings.Replace(configuration, "?", ".", 1), numberConfiguration, operational, damaged, results)

	results[resultKey] = combinations
	return results[resultKey]
}

func isValid(configuration string, numberConfiguration []int) bool {
	n := 0
	damagedNumber := 0
	if len(numberConfiguration) > 0 {
		damagedNumber = numberConfiguration[n]
	}
	for springIndex, spring := range configuration {
		if spring == '.' {
			if n < len(numberConfiguration) && damagedNumber != numberConfiguration[n] {
				return false
			}
			continue
		}
		damagedNumber--
		if damagedNumber < 0 {
			return false
		}
		if damagedNumber == 0 {
			if nextIndex := springIndex + 1; nextIndex < len(configuration) && configuration[nextIndex] != '.' {
				return false
			}
			n++
			if n < len(numberConfiguration) {
				damagedNumber = numberConfiguration[n]
			}
		}

	}
	return damagedNumber == 0
}

func part2(input string) int {
	return Sum(
		Map(
			Map(
				strings.Split(input, "\n"),
				func(line string) string {
					springDefinition := strings.Fields(line)
					configuration := strings.Repeat(springDefinition[0]+"?", 5)
					configuration = configuration[:len(configuration)-1]

					numberConfiguration := strings.Repeat(springDefinition[1]+",", 5)
					numberConfiguration = numberConfiguration[:len(numberConfiguration)-1]

					return configuration + " " + numberConfiguration
				},
			),
			possibleSprings,
		),
	)
}

func main() {
	Check("Part 1", 21, func() int {
		return part1(sample)
	})
	Check("Part 1", 7857, func() int {
		return part1(input)
	})
	Check("Part 2", 525152, func() int {
		return part2(sample)
	})
	Check("Part 2", 28606137449920, func() int {
		return part2(input)
	})
}

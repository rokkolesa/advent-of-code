package main

import (
	"../shared"
	_ "embed"
	"strconv"
	"strings"
	"unicode"
)

//go:embed day01_1_sample.txt
var sample1 string

//go:embed day01_2_sample.txt
var sample2 string

//go:embed day01_1.txt
var input string

func part1(input string) int {
	var sum = 0
	for _, l := range strings.Split(input, "\n") {
		firstValue, lastValue := findFirstAndLastDigit(l)
		sum += firstValue*10 + lastValue
	}
	return sum
}

func part2(input string) int {
	var sum = 0
	for _, l := range strings.Split(input, "\n") {

		firstValue := getFirstValue(l, false)
		lastValue := getFirstValue(l, true)
		sum += firstValue*10 + lastValue
		//fmt.Println(fmt.Sprintf("First: %d, Last: %d", firstValue, lastValue) + " -- " + l)
	}
	return sum
}

var numbers = map[string]string{
	"one":   "1",
	"two":   "2",
	"three": "3",
	"four":  "4",
	"five":  "5",
	"six":   "6",
	"seven": "7",
	"eight": "8",
	"nine":  "9",
}
var reverseNumbers = map[string]string{
	"eno":   "1",
	"owt":   "2",
	"eerht": "3",
	"ruof":  "4",
	"evif":  "5",
	"xis":   "6",
	"neves": "7",
	"thgie": "8",
	"enin":  "9",
}

func getFirstValue(line string, shouldReverse bool) int {
	numbersMap := numbers
	if shouldReverse {
		numbersMap = reverseNumbers
	}
	if shouldReverse {
		line = shared.Reverse(line)
	}
	var leftIndex = len(line) + 1
	var leftVerboseNumber string
	for verboseNumber, _ := range numbersMap {
		index := strings.Index(line, verboseNumber)
		if index >= 0 && index < leftIndex {
			leftIndex = index
			leftVerboseNumber = verboseNumber
		}
	}
	line = strings.Replace(line, leftVerboseNumber, numbersMap[leftVerboseNumber], 1)
	firstValue, _ := findFirstAndLastDigit(line)
	return firstValue
}

func findFirstAndLastDigit(line string) (int, int) {
	var first = -1
	var last = -1
	for i, c := range []rune(line) {
		if unicode.IsDigit(c) {
			if first == -1 {
				first = i
			}
			last = i
		}
	}

	firstValue, _ := strconv.Atoi(string(line[first]))
	lastValue, _ := strconv.Atoi(string(line[last]))

	return firstValue, lastValue
}

func main() {
	shared.Check("Part 1", 142, func() int {
		return part1(sample1)
	})
	shared.Check("Part 1", 54667, func() int {
		return part1(input)
	})
	shared.Check("Part 2", 281, func() int {
		return part2(sample2)
	})
	shared.Check("Part 2", 54203, func() int {
		return part2(input)
	})
}

package main

import (
	_ "embed"
	"flag"
	"fmt"
	"strconv"
	"strings"
	"time"
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

		firstValue := getFirstVerboseNumber(l, false)
		lastValue := getFirstVerboseNumber(l, true)
		sum += firstValue*10 + lastValue
		//fmt.Println(fmt.Sprintf("First: %d, Last: %d", firstValue, lastValue) + " -- " + l)
	}
	return sum
}

func getFirstVerboseNumber(line string, shouldReverse bool) int {
	numbers := map[string]string{
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

	if shouldReverse {
		numbers = map[string]string{
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
	}
	var search = line
	if shouldReverse {
		search = reverse(search)
	}
	var leftIndex = len(search) + 1
	var leftNumber string
	for verbose, _ := range numbers {
		index := strings.Index(search, verbose)
		if index >= 0 && index < leftIndex {
			leftIndex = index
			leftNumber = verbose
		}
	}
	if leftNumber != "" {
		search = strings.Replace(search, leftNumber, numbers[leftNumber], 1)
	}
	firstValue, _ := findFirstAndLastDigit(search)
	return firstValue
}

// reverse returns its argument string reversed rune-wise left to right.
func reverse(s string) string {
	r := []rune(s)
	for i, j := 0, len(r)-1; i < len(r)/2; i, j = i+1, j-1 {
		r[i], r[j] = r[j], r[i]
	}
	return string(r)
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

	inputPtr := flag.Bool("input", false, "sample or input")

	var part int
	flag.IntVar(&part, "part", 1, "part 1 or 2")

	flag.Parse()

	var inputText string
	if *inputPtr {
		inputText = strings.TrimSpace(input)
		fmt.Println("Running part", part, "on input.")
	} else {
		if part == 1 {
			inputText = strings.TrimSpace(sample1)
		} else {
			inputText = strings.TrimSpace(sample2)
		}
		fmt.Println("Running part", part, "on sample.")
	}

	start := time.Now()
	if part == 1 {
		fmt.Println("Result:", part1(inputText))
		//	54667
	} else {
		fmt.Println("Result:", part2(inputText))
		//	54203
	}
	fmt.Println("Time:", fmt.Sprintf("%d ms", time.Since(start).Milliseconds()))
}

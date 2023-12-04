package main

import (
	"../shared"
	_ "embed"
	"strconv"
	"strings"
	"unicode"
)

//go:embed day03_sample.txt
var sample string

//go:embed day03.txt
var input string

type machinePart struct {
	value int
	xs    []int
	y     int
}

func (part machinePart) adjacent() (points []shared.Point) {
	// left-right
	points = append(points, shared.Point{X: part.xs[0] - 1, Y: part.y}, shared.Point{X: part.xs[len(part.xs)-1] + 1, Y: part.y})
	// top-bottom
	topBottom := make([]shared.Point, 2*len(part.xs))
	for i := 0; i < len(part.xs); i++ {
		topBottom[i] = shared.Point{X: part.xs[i], Y: part.y + 1}
		topBottom[i+len(part.xs)] = shared.Point{X: part.xs[i], Y: part.y - 1}
	}
	points = append(points, topBottom...)
	// diagonals
	points = append(points,
		shared.Point{X: part.xs[0] - 1, Y: part.y - 1},
		shared.Point{X: part.xs[len(part.xs)-1] + 1, Y: part.y - 1},
		shared.Point{X: part.xs[len(part.xs)-1] + 1, Y: part.y + 1},
		shared.Point{X: part.xs[0] - 1, Y: part.y + 1},
	)
	return points
}

func part1(input string) int {
	symbols, machineParts := parseSchematic(input)
	sum := 0
	for _, part := range machineParts {
		for _, potentialSymbol := range part.adjacent() {
			if symbols[potentialSymbol.X][potentialSymbol.Y] != 0 {
				sum += part.value
				break
			}
		}
	}
	return sum
}

func part2(input string) int {
	symbols, machineParts := parseSchematic(input)
	adjacentToStars := make(map[shared.Point][]machinePart)
	for _, part := range machineParts {
		for _, potentialSymbol := range part.adjacent() {
			if symbols[potentialSymbol.X][potentialSymbol.Y] == '*' {
				adjacentToStars[potentialSymbol] = append(adjacentToStars[potentialSymbol], part)
			}
		}
	}
	sum := 0
	for _, gear := range adjacentToStars {
		if len(gear) == 2 {
			sum += gear[0].value * gear[1].value
		}
	}
	return sum
}

func parseSchematic(input string) (symbols map[int]map[int]rune, partNumbers []machinePart) {
	symbols = make(map[int]map[int]rune)
	for y, line := range strings.Split(input, "\n") {
		partialNumber := ""
		startX := -1
		for x, character := range line {
			if symbols[x] == nil {
				symbols[x] = make(map[int]rune)
			}
			if unicode.IsDigit(character) {
				// we get a digit => just append the rune and set the start x coordinate if not yet set
				partialNumber += string(character)
				if startX < 0 {
					startX = x
				}
			} else if partialNumber != "" {
				// we do not have a digit => save the possibly parsed number and reset the parsing
				value, _ := strconv.Atoi(partialNumber)
				partNumbers = append(partNumbers, machinePart{value, shared.Range(startX, x), y})
				partialNumber = ""
				startX = -1
			}
			// skip the dots and write other characters as symbols
			// important that there is no else-if here!
			if character != '.' {
				symbols[x][y] = character
			}
		}
		// the number can be at the end of the line
		if partialNumber != "" {
			value, _ := strconv.Atoi(partialNumber)
			partNumbers = append(partNumbers, machinePart{value, shared.Range(startX, len(line)), y})
		}
	}
	return
}

func main() {
	shared.Check("Part 1", 4361, func() int {
		return part1(sample)
	})
	shared.Check("Part 1", 525911, func() int {
		return part1(input)
	})
	shared.Check("Part 2", 467835, func() int {
		return part2(sample)
	})
	shared.Check("Part 2", 75805607, func() int {
		return part2(input)
	})
}

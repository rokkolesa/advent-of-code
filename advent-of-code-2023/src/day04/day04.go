package main

import (
	"../shared"
	_ "embed"
	"math"
	"slices"
	"strings"
)

//go:embed day04_sample.txt
var sample string

//go:embed day04.txt
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
	sum := 0
	for _, line := range strings.Split(input, "\n") {
		hits := hitCount(line)
		if hits > 0 {
			sum += int(math.Pow(2, float64(hits-1)))
		}
	}
	return sum
}

func hitCount(line string) int {
	colonIndex := strings.Index(line, ":") + 1
	combinations := strings.Split(line[colonIndex:], "|")
	winningCombination := strings.Split(strings.TrimSpace(combinations[0]), " ")
	myCombination := strings.Split(strings.TrimSpace(combinations[1]), " ")

	hits := 0
	for _, number := range myCombination {
		if number != "" && slices.Contains(winningCombination, number) {
			hits++
		}
	}
	return hits
}

func part2(input string) int {

	lines := strings.Split(input, "\n")
	cards := make([]int, len(lines))

	for cardId, line := range lines {
		hits := hitCount(line)

		for i := 0; i < hits; i++ {
			cards[cardId+1+i] += cards[cardId] + 1
		}
	}
	return shared.Sum(cards) + len(cards)
}

func main() {
	shared.Check("Part 1", 13, func() int {
		return part1(sample)
	})
	shared.Check("Part 1", 15205, func() int {
		return part1(input)
	})
	shared.Check("Part 2", 30, func() int {
		return part2(sample)
	})
	shared.Check("Part 2", 6189740, func() int {
		return part2(input)
	})
}

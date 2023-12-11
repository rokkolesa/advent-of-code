package main

import (
	"../shared"
	_ "embed"
	"fmt"
	"slices"
	"strings"
)

//go:embed day10_1_sample.txt
var sample11 string

//go:embed day10_1_sample_2.txt
var sample12 string

//go:embed day10_2_sample.txt
var sample21 string

//go:embed day10_2_sample_2.txt
var sample22 string

//go:embed day10_2_sample_3.txt
var sample23 string

//go:embed day10_2_sample_4.txt
var sample24 string

//go:embed day10.txt
var input string

type Symbol struct {
	id string
	shared.Point
}

func (receiver Symbol) nextFrom(previous Symbol) (shared.Point, bool) {
	switch receiver.id {
	case "-":
		if receiver.Y != previous.Y {
			return previous.Point, false
		}
		// add or remove X
		return shared.Point{X: receiver.X + (receiver.X - previous.X), Y: receiver.Y}, true
	case "|":
		if receiver.X != previous.X {
			return previous.Point, false
		}
		// add or remove Y
		return shared.Point{X: receiver.X, Y: receiver.Y + (receiver.Y - previous.Y)}, true
	case "L":
		if previous.X < receiver.X && previous.Y == receiver.Y || previous.Y > receiver.Y && previous.X == receiver.X {
			return previous.Point, false
		}
		if receiver.Y != previous.Y {
			return shared.Point{X: receiver.X + 1, Y: receiver.Y}, true
		} else {
			return shared.Point{X: receiver.X, Y: receiver.Y - 1}, true
		}
	case "7":
		if previous.X > receiver.X && previous.Y == receiver.Y || previous.Y < receiver.Y && previous.X == receiver.X {
			return previous.Point, false
		}
		if receiver.Y != previous.Y {
			return shared.Point{X: receiver.X - 1, Y: receiver.Y}, true
		} else {
			return shared.Point{X: receiver.X, Y: receiver.Y + 1}, true
		}
	case "F":
		if previous.X < receiver.X && previous.Y == receiver.Y || previous.Y < receiver.Y && previous.X == receiver.X {
			return previous.Point, false
		}
		if receiver.Y != previous.Y {
			return shared.Point{X: receiver.X + 1, Y: receiver.Y}, true
		} else {
			return shared.Point{X: receiver.X, Y: receiver.Y + 1}, true
		}
	case "J":
		if previous.X > receiver.X && previous.Y == receiver.Y || previous.Y > receiver.Y && previous.X == receiver.X {
			return previous.Point, false
		}
		if receiver.Y != previous.Y {
			return shared.Point{X: receiver.X - 1, Y: receiver.Y}, true
		} else {
			return shared.Point{X: receiver.X, Y: receiver.Y - 1}, true
		}
	case "S":
		return receiver.Point, true
	default:
		return previous.Point, false
	}
}

func part1(input string) int {
	pipeMap, previous, current := analyzeMap(input)

	// we already made 1 step
	l := 1
	for current.id != "S" {
		next, _ := current.nextFrom(previous)
		previous = current
		current = pipeMap[next.Y][next.X]
		l++
	}

	return l / 2
}

func part2(input string) int {
	pipeMap, previous, current := analyzeMap(input)

	loop := []Symbol{current}
	for current.id != "S" {
		next, _ := current.nextFrom(previous)
		previous = current
		current = pipeMap[next.Y][next.X]
		loop = append(loop, current)
	}

	insideCount := 0
	var insideArea []Symbol
	for y, line := range strings.Split(input, "\n") {
		inside := false
		var previousSymbol Symbol
		for x, symbolId := range line {
			currentSymbol := pipeMap[y][x]
			symbolOnLoop := slices.Contains(loop, currentSymbol)
			// the first one is either on the loop or outside
			if x == 0 {
				inside = symbolOnLoop
				if symbolOnLoop {
					previousSymbol = currentSymbol
				}
			} else if !symbolOnLoop && inside {
				insideArea = append(insideArea, currentSymbol)
				insideCount++
			} else if symbolOnLoop && symbolId != '-' {
				// ignore dashes as they do not contribute to anything - they retain the same state

				// when we cross |, L, F, we cross the loop boundary
				// we have to correct the boundary when we just make a U turn (in both directions) - LJ and F7

				if symbolId == '|' || symbolId == 'L' || symbolId == 'F' {
					inside = !inside
				} else if turn := previousSymbol.id + string(symbolId); turn == "LJ" || turn == "F7" {
					inside = !inside
				}

				previousSymbol = currentSymbol
			}
		}
	}
	//printMap(pipeMap, loop, insideArea)
	return insideCount
}

func printMap(pipeMap [][]Symbol, loop []Symbol, area []Symbol) {
	fmt.Println()

	for _, line := range pipeMap {
		for _, symbol := range line {
			if slices.Contains(loop, symbol) {
				colored := fmt.Sprintf("\x1b[%dm%s\x1b[0m", 95, symbol.id)
				fmt.Print(colored)
			} else if slices.Contains(area, symbol) {
				colored := fmt.Sprintf("\x1b[%dm%s\x1b[0m", 96, "I")
				fmt.Print(colored)
			} else {
				colored := fmt.Sprintf("\x1b[%dm%s\x1b[0m", 94, "O")
				fmt.Print(colored)
			}
		}
		fmt.Println()
	}
}

func analyzeMap(input string) (pipeMap [][]Symbol, start Symbol, next Symbol) {
	inputLines := strings.Split(input, "\n")
	pipeMap = make([][]Symbol, len(inputLines))
	for y, line := range inputLines {
		pipeMap[y] = make([]Symbol, len(line))
		for x, symbolId := range line {
			symbol := Symbol{id: string(symbolId), Point: shared.Point{X: x, Y: y}}
			pipeMap[y][x] = symbol
			if symbolId == 'S' {
				start = symbol
			}
		}
	}

	possibleNexts := shared.Map(
		shared.Filter([]shared.Point{
			{start.X + 1, start.Y},
			{start.X - 1, start.Y},
			{start.X, start.Y + 1},
			{start.X, start.Y - 1},
		}, func(point shared.Point) bool { return point.X >= 0 && point.Y >= 0 }),
		func(p shared.Point) Symbol { return pipeMap[p.Y][p.X] })

	// it doesn't matter which step we take, as long as it is the correct one
	next = shared.Filter(possibleNexts, func(point Symbol) bool {
		_, possible := point.nextFrom(start)
		return possible
	})[0]
	return
}

func main() {
	shared.Check("Part 1", 4, func() int {
		return part1(sample11)
	})
	shared.Check("Part 1", 8, func() int {
		return part1(sample12)
	})
	shared.Check("Part 1", 7173, func() int {
		return part1(input)
	})
	shared.Check("Part 2", 4, func() int {
		return part2(sample21)
	})
	shared.Check("Part 2", 4, func() int {
		return part2(sample22)
	})
	shared.Check("Part 2", 8, func() int {
		return part2(sample23)
	})
	shared.Check("Part 2", 10, func() int {
		return part2(sample24)
	})
	shared.Check("Part 2", 291, func() int {
		return part2(input)
	})
}

package main

import (
	"../shared"
	_ "embed"
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

func (receiver Symbol) nextFrom(previous shared.Point) (shared.Point, bool) {
	switch receiver.id {
	case "-":
		if receiver.Y != previous.Y {
			return previous, false
		}
		// add or remove X
		return shared.Point{X: receiver.X + (receiver.X - previous.X), Y: receiver.Y}, true
	case "|":
		if receiver.X != previous.X {
			return previous, false
		}
		// add or remove Y
		return shared.Point{X: receiver.X, Y: receiver.Y + (receiver.Y - previous.Y)}, true
	case "L":
		if previous.X < receiver.X && previous.Y == receiver.Y || previous.Y > receiver.Y && previous.X == receiver.X {
			return previous, false
		}
		if receiver.Y != previous.Y {
			return shared.Point{X: receiver.X + 1, Y: receiver.Y}, true
		} else {
			return shared.Point{X: receiver.X, Y: receiver.Y - 1}, true
		}
	case "7":
		if previous.X > receiver.X && previous.Y == receiver.Y || previous.Y < receiver.Y && previous.X == receiver.X {
			return previous, false
		}
		if receiver.Y != previous.Y {
			return shared.Point{X: receiver.X - 1, Y: receiver.Y}, true
		} else {
			return shared.Point{X: receiver.X, Y: receiver.Y + 1}, true
		}
	case "F":
		if previous.X < receiver.X && previous.Y == receiver.Y || previous.Y < receiver.Y && previous.X == receiver.X {
			return previous, false
		}
		if receiver.Y != previous.Y {
			return shared.Point{X: receiver.X + 1, Y: receiver.Y}, true
		} else {
			return shared.Point{X: receiver.X, Y: receiver.Y + 1}, true
		}
	case "J":
		if previous.X > receiver.X && previous.Y == receiver.Y || previous.Y > receiver.Y && previous.X == receiver.X {
			return previous, false
		}
		if receiver.Y != previous.Y {
			return shared.Point{X: receiver.X - 1, Y: receiver.Y}, true
		} else {
			return shared.Point{X: receiver.X, Y: receiver.Y - 1}, true
		}
	case "S":
		return receiver.Point, true
	default:
		return previous, false
	}
}

func part1(input string) int {
	pipeMap, previous, current := analyzeMap(input)

	// we already made 1 step
	l := 1
	for current.id != "S" {
		next, _ := current.nextFrom(previous)
		previous = current.Point
		current = pipeMap[next.Y][next.X]
		l++
	}

	return l / 2
}

func part2(input string) int {
	pipeMap, previous, current := analyzeMap(input)

	loop := []Symbol{pipeMap[current.Y][current.X]}
	for current.id != "S" {
		next, _ := current.nextFrom(previous)
		previous = current.Point
		current = pipeMap[next.Y][next.X]
		loop = append(loop, current)
	}

	insideCount := 0
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
	return insideCount
}

func analyzeMap(input string) ([][]Symbol, shared.Point, Symbol) {
	inputLines := strings.Split(input, "\n")
	pipeMap := make([][]Symbol, len(inputLines))
	var start shared.Point
	for y, line := range inputLines {
		pipeMap[y] = make([]Symbol, len(line))
		for x, symbol := range line {
			point := shared.Point{X: x, Y: y}
			pipeMap[y][x] = Symbol{id: string(symbol), Point: point}
			if symbol == 'S' {
				start = point
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
	next := shared.Filter(possibleNexts, func(point Symbol) bool {
		_, possible := point.nextFrom(start)
		return possible
	})[0]
	return pipeMap, start, next
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

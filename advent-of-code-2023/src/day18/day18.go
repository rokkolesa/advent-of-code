package main

import (
	. "../shared"
	_ "embed"
	"strconv"
	"strings"
)

//go:embed day18_sample.txt
var sample string

//go:embed day18.txt
var input string

func part1(input string) int {
	return lagoonSize(input, func(position Point, edgeDefinition string) (Point, int) {
		edgeConfiguration := strings.Fields(edgeDefinition)
		direction := edgeConfiguration[0]
		repeat := ParseIntSafe(edgeConfiguration[1])
		return position.Plus(Unit(direction).Times(repeat)), repeat
	})
}

func part2(input string) int {
	return lagoonSize(input, func(position Point, edgeDefinition string) (Point, int) {
		edgeConfiguration := strings.Fields(edgeDefinition)
		rgb := edgeConfiguration[2]
		direction := ""
		switch rgb[len(rgb)-2 : len(rgb)-1] {
		case "0":
			direction = "R"
		case "1":
			direction = "D"
		case "2":
			direction = "L"
		case "3":
			direction = "U"
		}
		repeat, _ := strconv.ParseInt(rgb[2:len(rgb)-2], 16, 32)
		return position.Plus(Unit(direction).Times(int(repeat))), int(repeat)
	})
}

func lagoonSize(input string, parseEdge func(Point, string) (Point, int)) int {
	position := Point{}
	splitInput := strings.Split(input, "\n")
	areaSize := 0
	for i := 0; i < len(splitInput); i++ {
		newPosition, length := parseEdge(position, splitInput[i])
		areaSize += position.X*newPosition.Y - newPosition.X*position.Y + length
		position = newPosition
	}
	return areaSize/2 + 1
}

func main() {
	Check("Part 1", 62, func() int {
		return part1(sample)
	})
	Check("Part 1", 74074, func() int {
		return part1(input)
	})
	Check("Part 2", 952408144115, func() int {
		return part2(sample)
	})
	Check("Part 2", 112074045986829, func() int {
		return part2(input)
	})
}

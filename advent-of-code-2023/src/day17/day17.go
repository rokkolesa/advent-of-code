package main

import (
	. "../shared"
	_ "embed"
	"fmt"
	"slices"
)

//go:embed day17_sample.txt
var sample string

//go:embed day17.txt
var input string

type Block struct {
	heatLoss int
	Point
}
type Path struct {
	dir string
	Point
}

func memoKey(previous Point, direction string, straight int, chain []Path) string {
	sprintf := fmt.Sprintf("%v%v%v%v", previous, direction, straight, chain)
	return sprintf
}

func findHeatLoss(results map[string]int, layout Layout[int], end Point, previous Point, direction string, straight int, chain []Path) int {
	key := memoKey(previous, direction, straight, chain)
	if result, exists := results[key]; exists {
		return result
	}

	leftTurn, leftDirection := previous.Turn(direction, "L")
	rightTurn, rightDirection := previous.Turn(direction, "R")
	var straightTurn Point
	nextPoints := []Point{leftTurn, rightTurn}
	if straight < 2 {
		straightTurn, _ = previous.Turn(direction, "S")
		nextPoints = append(nextPoints, straightTurn)
	}

	if previous == end {
		results[key] = layout[end.Y][end.X] + Sum(Map(chain, func(point Path) int { return layout[point.Y][point.X] }))
		//printLayout(layout, chain)
		return results[key]
	}

	var heatLossTurn []int
	if layout.InBounds(leftTurn) && !slices.ContainsFunc(chain, func(p Path) bool { return leftTurn == p.Point }) {
		leftHeatLoss := findHeatLoss(results, layout, end, leftTurn, leftDirection, 0, append(chain, Path{dir: leftDirection, Point: leftTurn}))
		heatLossTurn = append(heatLossTurn, leftHeatLoss)
	}
	if layout.InBounds(rightTurn) && !slices.ContainsFunc(chain, func(p Path) bool { return rightTurn == p.Point }) {
		rightHeatLoss := findHeatLoss(results, layout, end, rightTurn, rightDirection, 0, append(chain, Path{dir: rightDirection, Point: rightTurn}))
		heatLossTurn = append(heatLossTurn, rightHeatLoss)
	}
	if layout.InBounds(straightTurn) && straight < 2 && !slices.ContainsFunc(chain, func(p Path) bool { return straightTurn == p.Point }) {
		straightHeatLoss := findHeatLoss(results, layout, end, straightTurn, direction, straight+1, append(chain, Path{dir: direction, Point: straightTurn}))
		heatLossTurn = append(heatLossTurn, straightHeatLoss)
	}
	chainHeatLoss := Sum(Map(chain, func(point Path) int { return layout[point.Y][point.X] }))
	if len(heatLossTurn) == 0 {
		results[key] = 999999999999999
	} else {
		results[key] = chainHeatLoss + layout[previous.Y][previous.X] + slices.Min(heatLossTurn)
	}
	return results[key]
}

func printLayout(layout Layout[int], chain []Path) {
	fmt.Println()
	for y := range layout {
		for x := range layout[y] {
			idx := slices.IndexFunc(chain, func(p Path) bool { return Point{X: x, Y: y} == p.Point })
			if idx >= 0 {
				fmt.Print(chain[idx].dir)
			} else {
				fmt.Print(".")
			}
		}
		fmt.Println()
	}
}

func part1(input string) int {
	layout := ParseLayoutFunc(input, func(_, _ int, s string) int { return ParseIntSafe(s) })
	return findHeatLoss(make(map[string]int), layout, Point{X: len(layout[0]) - 1, Y: len(layout) - 1}, Point{}, "U", 0, []Path{})
}

func part2(input string) int {
	return -2
}

func main() {
	Check("Part 1", 102, func() int {
		return part1(sample)
	})
	//Check("Part 1", 0, func() int {
	//	return part1(input)
	//})
	//Check("Part 2", 0, func() int {
	//	return part2(sample)
	//})
	//Check("Part 2", 0, func() int {
	//	return part2(input)
	//})
}

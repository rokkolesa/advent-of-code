package main

import (
	. "../shared"
	_ "embed"
)

//go:embed day21_sample.txt
var sample string

//go:embed day21.txt
var input string

func part1(input string, steps int) int {
	var start Point
	var rocks = Set[Point]()
	layout := ParseLayoutFunc(input, func(x, y int, s string) string {
		if s == "S" {
			start = Point{X: x, Y: y}
		}
		if s == "#" {
			rocks.Add(Point{X: x, Y: y})
		}
		return s
	})

	positions := []Point{start}
	for step := 0; step < steps; step++ {
		newPositions := Set[Point]()
		for _, position := range positions {
			newPositions.Add(
				Filter(position.Adjacent(), func(point Point) bool {
					return !rocks.Contains(Point{
						X: Mod(point.X, len(layout)),
						Y: Mod(point.Y, len(layout)),
					})
				})...,
			)
		}
		positions = newPositions.ToSlice()
	}
	return len(positions)
}

func part2(input string, steps int) int {
	// TODO
	return part1(input, steps)
}

func main() {
	Check("Part 1", 16, func() int {
		return part1(sample, 6)
	})
	Check("Part 1", 3615, func() int {
		return part1(input, 64)
	})
	Check("Part 2", 16, func() int {
		return part2(sample, 6)
	})
	Check("Part 2", 50, func() int {
		return part2(sample, 10)
	})
	Check("Part 2", 1594, func() int {
		return part2(sample, 50)
	})
	Check("Part 2", 6536, func() int {
		return part2(sample, 100)
	})
	Check("Part 2", 167004, func() int {
		return part2(sample, 700)
	})
	Check("Part 2", 668697, func() int {
		return part2(input, 1000)
	})
	Check("Part 2", 16733044, func() int {
		return part2(sample, 5000)
	})
	Check("Part 2", 0, func() int {
		return part2(input, 26501365)
	})
}

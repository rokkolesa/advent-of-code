package main

import (
	. "../shared"
	_ "embed"
	"math"
	"strings"
)

//go:embed day11_sample.txt
var sample string

//go:embed day11.txt
var input string

func distance(p, q Point) int {
	return int(math.Abs(float64(p.X-q.X))) + int(math.Abs(float64(p.Y-q.Y)))
}

func galaxyDistances(input string, expansion int) int {
	inputLines := strings.Split(input, "\n")
	var galaxyMap []Point
	yGalaxies := Set[int]()
	xGalaxies := Set[int]()

	for y, line := range inputLines {
		for x, r := range line {
			if r == '#' {
				yGalaxies.Add(y)
				xGalaxies.Add(x)
				galaxyMap = append(galaxyMap, Point{X: x, Y: y})
			}
		}
	}
	sum := 0
	for _, g := range galaxyMap {
		for _, h := range galaxyMap {
			sum += distance(g, h)
			sum += len(Filter(Range(g.X, h.X), func(i int) bool { return !xGalaxies.Contains(i) })) * (expansion - 1)
			sum += len(Filter(Range(g.Y, h.Y), func(i int) bool { return !yGalaxies.Contains(i) })) * (expansion - 1)
		}
	}
	return sum / 2
}

func main() {
	Check("Part 1", 374, func() int {
		return galaxyDistances(sample, 2)
	})
	Check("Part 1", 9647174, func() int {
		return galaxyDistances(input, 2)
	})
	Check("Part 2", 1030, func() int {
		return galaxyDistances(sample, 10)
	})
	Check("Part 2", 8410, func() int {
		return galaxyDistances(sample, 100)
	})
	Check("Part 2", 377318892554, func() int {
		return galaxyDistances(input, 1000000)
	})
}

package main

import (
	. "../shared"
	_ "embed"
	"slices"
)

//go:embed day16_sample.txt
var sample string

//go:embed day16.txt
var input string

type Beam struct {
	direction string
	Point
}

func (thisBeam Beam) next(symbol string) []Beam {
	switch symbol {
	case ".":
		return thisBeam.move(thisBeam.direction)
	case "/":
		switch thisBeam.direction {
		case "R":
			return thisBeam.move("U")
		case "L":
			return thisBeam.move("D")
		case "U":
			return thisBeam.move("R")
		case "D":
			return thisBeam.move("L")
		}
	case "\\":
		switch thisBeam.direction {
		case "R":
			return thisBeam.move("D")
		case "L":
			return thisBeam.move("U")
		case "U":
			return thisBeam.move("L")
		case "D":
			return thisBeam.move("R")
		}
	case "-":
		if thisBeam.direction == "R" || thisBeam.direction == "L" {
			return thisBeam.move(thisBeam.direction)
		} else {
			return thisBeam.move("R", "L")

		}
	case "|":
		if thisBeam.direction == "U" || thisBeam.direction == "D" {
			return thisBeam.move(thisBeam.direction)
		} else {
			return thisBeam.move("U", "D")
		}
	}
	return []Beam{}
}

func (thisBeam Beam) move(directions ...string) []Beam {
	return Map(directions, func(direction string) Beam {
		return Beam{
			direction: direction,
			Point:     thisBeam.Move(direction),
		}
	})
}

func energyFrom(layout Layout, start Beam) int {
	beamHistory := Set[Beam](start)
	var queue = []Beam{start}

	for len(queue) > 0 {
		beam := queue[0]
		queue = queue[1:]

		nextBeams := beam.next(layout[beam.Y][beam.X])
		nextBeams = Filter(nextBeams, func(b Beam) bool { return !beamHistory.Contains(b) && layout.InBounds(b.Point) })
		beamHistory.Add(nextBeams...)

		queue = append(queue, nextBeams...)
	}
	// unique points from beam history
	return Set(
		Map(
			beamHistory.ToSlice(),
			func(b Beam) Point { return b.Point },
		)...,
	).Len()
}

func part1(input string) int {
	layout := ParseLayout(input)
	return energyFrom(layout, Beam{direction: "R", Point: Point{}})
}
func part2(input string) (sum int) {
	layout := ParseLayout(input)
	var energies []int
	for i := 0; i < len(layout); i++ {
		energies = append(energies, energyFrom(layout, Beam{direction: "R", Point: Point{X: 0, Y: i}}))
		energies = append(energies, energyFrom(layout, Beam{direction: "L", Point: Point{X: 0, Y: len(layout[0]) - 1}}))
	}
	for i := 0; i < len(layout[0]); i++ {
		energies = append(energies, energyFrom(layout, Beam{direction: "D", Point: Point{X: i, Y: 0}}))
		energies = append(energies, energyFrom(layout, Beam{direction: "U", Point: Point{X: len(layout[0]) - 1, Y: 0}}))
	}
	return slices.Max(energies)
}

func main() {
	Check("Part 1", 46, func() int {
		return part1(sample)
	})
	Check("Part 1", 7046, func() int {
		return part1(input)
	})
	Check("Part 2", 51, func() int {
		return part2(sample)
	})
	Check("Part 2", 7313, func() int {
		return part2(input)
	})
}

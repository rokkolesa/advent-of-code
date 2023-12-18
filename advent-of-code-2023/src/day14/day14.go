package main

import (
	. "../shared"
	_ "embed"
	"fmt"
)

//go:embed day14_sample.txt
var sample string

//go:embed day14.txt
var input string

func northLoad(platform Layout[string]) int {
	return Sum(
		MapIndexed(platform, func(line []string, y int) int {
			return (len(platform) - y) * CountSample(line, "O")
		}),
	)
}

func tilt(platform Layout[string], directions ...string) {
	for _, direction := range directions {
		// normal for-loop parameters
		xInitial := 0
		xBoundCondition := func(x int) bool { return x < len(platform[0]) }
		xIncrement := func(x int) int { return x + 1 }
		yInitial := 0
		yBoundCondition := func(y int) bool { return y < len(platform) }
		yIncrement := func(y int) int { return y + 1 }

		// scan Y from behind
		if direction == "D" {
			yInitial = len(platform) - 1
			yBoundCondition = func(y int) bool { return y >= 0 }
			yIncrement = func(y int) int { return y - 1 }
		}
		// scan X from behind
		if direction == "R" {
			xInitial = len(platform[0]) - 1
			xBoundCondition = func(x int) bool { return x >= 0 }
			xIncrement = func(x int) int { return x - 1 }
		}

		for y := yInitial; yBoundCondition(y); y = yIncrement(y) {
			for x := xInitial; xBoundCondition(x); x = xIncrement(x) {
				if platform[y][x] == "O" {
					rock := Point{X: x, Y: y}
					canMove := true
					for canMove {
						nextRock := rock.Move(direction)
						canMove = platform.InBounds(nextRock) && platform[nextRock.Y][nextRock.X] == "."
						if canMove {
							platform[nextRock.Y][nextRock.X] = "O"
							platform[rock.Y][rock.X] = "."
							rock = nextRock
						}
					}
				}
			}
		}
	}
}

func part1(input string) int {
	platform := ParseLayout(input)

	tilt(platform, "U")

	return northLoad(platform)
}

func part2(input string) int {
	platform := ParseLayout(input)
	var platformStates = make(map[string]int)

	tiltCycle := 0
	var cycleStart, cycleLength int
	for tiltCycle < 1000000000 {
		tilt(platform, "U", "L", "D", "R")
		tiltCycle++

		platformState := state(platform)
		if stateIndex, exists := platformStates[platformState]; exists {
			cycleStart = stateIndex
			cycleLength = tiltCycle - stateIndex
			break
		}
		platformStates[platformState] = tiltCycle
	}

	fmt.Print("Cycle found! Started at ", cycleStart, " with length of ", cycleLength, ": ")
	tiltCycle = 0
	for tiltCycle < (1000000000-cycleStart)%cycleLength {
		tilt(platform, "U", "L", "D", "R")
		tiltCycle++
	}

	return northLoad(platform)
}

func state(platform Layout[string]) (s string) {
	for y := range platform {
		for x := range platform[y] {
			s += platform[y][x]
		}
	}
	return
}

func main() {
	Check("Part 1", 136, func() int {
		return part1(sample)
	})
	Check("Part 1", 109638, func() int {
		return part1(input)
	})
	Check("Part 2", 64, func() int {
		return part2(sample)
	})
	Check("Part 2", 102657, func() int {
		return part2(input)
	})
}

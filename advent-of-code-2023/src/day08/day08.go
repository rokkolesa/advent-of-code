package main

import (
	"../shared"
	_ "embed"
	"golang.org/x/exp/maps"
	"regexp"
	"strings"
)

//go:embed day08_1_sample.txt
var sample11 string

//go:embed day08_1_sample_2.txt
var sample12 string

//go:embed day08_2_sample.txt
var sample2 string

//go:embed day08.txt
var input string

func part1(input string) int {
	directions, nodes := findDirections(input)

	currentNode := "AAA"
	i := 0
	for ; currentNode != "ZZZ"; i++ {
		direction := directions[i%len(directions)]
		currentNode = nodes[currentNode][direction]
	}
	return i
}
func part2(input string) int {
	directions, nodes := findDirections(input)

	currentNodes := shared.Filter(maps.Keys(nodes), func(s string) bool { return lastRune(s) == 'A' })
	pathToZ := make([]int, len(currentNodes))
	for i := 0; shared.AnyMatch(pathToZ, func(i int) bool { return i == 0 }); i++ {
		direction := directions[i%len(directions)]
		currentNodes = shared.Map(currentNodes, func(node string) string { return nodes[node][direction] })
		for n, currentNode := range currentNodes {
			if lastRune(currentNode) == 'Z' {
				pathToZ[n] = i + 1
			}
		}
	}
	return shared.Reduce(pathToZ, pathToZ[0], lcm)
}

func lastRune(s string) rune {
	return rune(s[len(s)-1])
}

func gcd(a, b int) int {
	for b != 0 {
		b, a = a%b, b
	}
	return a
}

func lcm(a, b int) int {
	return a * b / gcd(a, b)
}

func findDirections(input string) (directions []rune, nodes map[string]map[rune]string) {
	rules := strings.Split(input, "\n\n")
	directions = []rune(rules[0])
	nodes = make(map[string]map[rune]string)

	nodeRegex, _ := regexp.Compile("(.*) = \\((.*), (.*)\\)")
	for _, nodeString := range strings.Split(rules[1], "\n") {
		matches := nodeRegex.FindStringSubmatch(nodeString)
		nodes[matches[1]] = make(map[rune]string)
		nodes[matches[1]]['L'] = matches[2]
		nodes[matches[1]]['R'] = matches[3]
	}
	return
}

func main() {
	shared.Check("Part 1, 1", 2, func() int {
		return part1(sample11)
	})
	shared.Check("Part 1, 2", 6, func() int {
		return part1(sample12)
	})
	shared.Check("Part 1", 12643, func() int {
		return part1(input)
	})
	shared.Check("Part 2", 6, func() int {
		return part2(sample2)
	})
	shared.Check("Part 2", 13133452426987, func() int {
		return part2(input)
	})
}

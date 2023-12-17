package main

import (
	. "../shared"
	_ "embed"
	"slices"
	"strings"
)

//go:embed day15_sample.txt
var sample string

//go:embed day15.txt
var input string

func part1(input string) int {
	return Reduce(
		strings.Split(input, ","),
		0,
		func(sum int, step string) int { return sum + hash(step) },
	)
}

func hash(step string) int {
	hash := 0
	for _, character := range step {
		hash += int(character)
		hash *= 17
		hash = hash % 256
	}
	return hash
}

type Lens struct {
	label string
	value int
}

func part2(input string) (sum int) {
	boxes := make(map[int][]*Lens)
	for _, step := range strings.Split(input, ",") {
		if strings.Contains(step, "=") {
			stepSplit := strings.Split(step, "=")
			label := stepSplit[0]
			value := ParseIntSafe(stepSplit[1])
			labelHash := hash(label)

			// if the box exists and the label does not => append the Lens
			// if the box exists and the label exists => update the value of the existing Lens
			// if the box does not exist => add the Lens to the box
			if box, exists := boxes[labelHash]; exists {
				if lensIndex := slices.IndexFunc(box, func(lens *Lens) bool { return lens.label == label }); lensIndex >= 0 {
					box[lensIndex].value = value
				} else {
					boxes[labelHash] = append(box, &Lens{label: label, value: value})
				}
			} else {
				boxes[labelHash] = []*Lens{{label: label, value: value}}
			}
		} else {
			label := step[:len(step)-1]
			labelHash := hash(label)
			if box, exists := boxes[labelHash]; exists {
				boxes[labelHash] = slices.DeleteFunc(box, func(lens *Lens) bool { return lens.label == label })
			}
		}
	}
	for i, box := range boxes {
		for j, lens := range box {
			sum += (i + 1) * (j + 1) * lens.value
		}
	}
	return
}

func main() {
	Check("Part 1", 1320, func() int {
		return part1(sample)
	})
	Check("Part 1", 510792, func() int {
		return part1(input)
	})
	Check("Part 2", 145, func() int {
		return part2(sample)
	})
	Check("Part 2", 269410, func() int {
		return part2(input)
	})
}

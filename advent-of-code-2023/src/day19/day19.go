package main

import (
	. "../shared"
	_ "embed"
	"golang.org/x/exp/maps"
	"strings"
)

//go:embed day19_sample.txt
var sample string

//go:embed day19.txt
var input string

type Interval struct {
	from int
	to   int
}

func (interval Interval) len() int {
	return interval.to - interval.from + 1
}

type Part struct {
	x int
	m int
	a int
	s int
}

type WorkflowStep struct {
	category string
	compare  string
	bound    int
	fallback bool
	next     string
}

func (step WorkflowStep) accepts(part Part) bool {
	if step.fallback {
		return true
	}
	compareValue := partCategory(step.category)(part)
	if step.compare == ">" {
		return compareValue > step.bound
	}
	return compareValue < step.bound
}

func (step WorkflowStep) evaluateBounds(interval Interval, inverse bool) Interval {
	newFromBound := interval.from
	newToBound := interval.to
	bound := step.bound

	if !inverse && step.compare == "<" || inverse && step.compare == ">" {
		if !inverse {
			bound--
		}
		newToBound = min(interval.to, bound)
	} else if !inverse && step.compare == ">" || inverse && step.compare == "<" {
		if !inverse {
			bound++
		}
		newFromBound = max(interval.from, bound)
	}

	return Interval{
		from: newFromBound,
		to:   newToBound,
	}
}

func parseWorkflowsAndParts(input string) (map[string][]WorkflowStep, []Part) {
	inputSplit := strings.Split(input, "\n\n")
	workflows := make(map[string][]WorkflowStep)
	for _, workflow := range strings.Split(inputSplit[0], "\n") {
		curlyBracketIndex := strings.Index(workflow, "{")
		name := workflow[:curlyBracketIndex]

		var workflowSteps []WorkflowStep
		for _, workflowStep := range strings.Split(workflow[curlyBracketIndex+1:len(workflow)-1], ",") {
			if strings.Contains(workflowStep, ":") {
				stepSplit := strings.Split(workflowStep, ":")
				conditionString := stepSplit[0]
				compare := "<"
				if strings.Contains(conditionString, ">") {
					compare = ">"
				}
				conditionDefinition := strings.Split(conditionString, compare)

				workflowSteps = append(workflowSteps, WorkflowStep{
					category: conditionDefinition[0],
					bound:    ParseIntSafe(conditionDefinition[1]),
					compare:  compare,
					fallback: false,
					next:     stepSplit[1],
				})
			} else {
				workflowSteps = append(workflowSteps, WorkflowStep{
					fallback: true,
					next:     workflowStep,
				})
			}
		}

		workflows[name] = workflowSteps
	}

	var parts []Part
	for _, partDefinition := range strings.Split(inputSplit[1], "\n") {
		part := Part{}
		for _, category := range strings.Split(partDefinition[1:len(partDefinition)-1], ",") {
			categorySplit := strings.Split(category, "=")
			switch categorySplit[0] {
			case "x":
				part.x = ParseIntSafe(categorySplit[1])
			case "m":
				part.m = ParseIntSafe(categorySplit[1])
			case "a":
				part.a = ParseIntSafe(categorySplit[1])
			case "s":
				part.s = ParseIntSafe(categorySplit[1])
			}
		}
		parts = append(parts, part)
	}

	return workflows, parts
}

func partCategory(category string) func(Part) int {
	switch category {
	case "x":
		return func(part Part) int { return part.x }
	case "m":
		return func(part Part) int { return part.m }
	case "a":
		return func(part Part) int { return part.a }
	case "s":
		return func(part Part) int { return part.s }
	}
	panic("Unknown category!")
}

func part1(input string) int {
	workflows, parts := parseWorkflowsAndParts(input)

	acceptedSum := 0
	for _, part := range parts {
		currentWorkflow := "in"
		for currentWorkflow != "A" && currentWorkflow != "R" {
			for _, step := range workflows[currentWorkflow] {
				if step.accepts(part) {
					currentWorkflow = step.next
					break
				}
			}
		}
		if currentWorkflow == "A" {
			acceptedSum += part.x + part.m + part.a + part.s
		}
	}

	return acceptedSum
}

func part2(input string) int {
	workflows, _ := parseWorkflowsAndParts(input)

	categoryIntervals := make(map[string]Interval)
	categoryIntervals["x"] = Interval{from: 1, to: 4000}
	categoryIntervals["m"] = Interval{from: 1, to: 4000}
	categoryIntervals["a"] = Interval{from: 1, to: 4000}
	categoryIntervals["s"] = Interval{from: 1, to: 4000}

	return combinations(workflows, "in", categoryIntervals)
}

func combinations(workflows map[string][]WorkflowStep, workflow string, categoryIntervals map[string]Interval) int {
	if workflow == "R" {
		return 0
	}
	if workflow == "A" {
		return Product(Map(maps.Values(categoryIntervals), func(interval Interval) int {
			return interval.len()
		}))
	}

	allCombinations := 0
	newIntervals := maps.Clone(categoryIntervals)
	for _, step := range workflows[workflow] {
		categoryInterval := newIntervals[step.category]
		newIntervals[step.category] = step.evaluateBounds(categoryInterval, false)
		allCombinations += combinations(workflows, step.next, newIntervals)
		newIntervals[step.category] = step.evaluateBounds(categoryInterval, true)
	}
	return allCombinations
}

func main() {
	Check("Part 1", 19114, func() int {
		return part1(sample)
	})
	Check("Part 1", 432427, func() int {
		return part1(input)
	})
	Check("Part 2", 167409079868000, func() int {
		return part2(sample)
	})
	Check("Part 2", 143760172569135, func() int {
		return part2(input)
	})
}

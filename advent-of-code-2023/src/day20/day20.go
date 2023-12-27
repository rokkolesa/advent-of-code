package main

import (
	. "../shared"
	_ "embed"
	"golang.org/x/exp/maps"
	"strings"
)

//go:embed day20_sample.txt
var sample string

//go:embed day20_sample_2.txt
var sample2 string

//go:embed day20.txt
var input string

type Switch interface {
	next() []string
	receivePulse(swtch string, level string) string
}
type FlipFlop struct {
	state        bool
	nextSwitches []string
}

type Conjuction struct {
	state        map[string]string
	nextSwitches []string
}

type Broadcaster struct {
	nextSwitches []string
}

func (flipFlop *FlipFlop) next() []string {
	return flipFlop.nextSwitches
}
func (conjuction *Conjuction) next() []string {
	return conjuction.nextSwitches
}
func (broadcaster *Broadcaster) next() []string {
	return broadcaster.nextSwitches
}

func (flipFlop *FlipFlop) receivePulse(swtch string, level string) string {
	if level == "high" {
		return ""
	}
	flipFlop.state = !flipFlop.state
	if flipFlop.state {
		return "high"
	}
	return "low"
}
func (conjuction *Conjuction) receivePulse(swtch string, level string) string {
	conjuction.state[swtch] = level
	if AllMatch(maps.Values(conjuction.state), func(s string) bool { return s == "high" }) {
		return "low"
	}
	return "high"
}

func (broadcaster *Broadcaster) receivePulse(swtch string, level string) string {
	return level
}

type Signal struct {
	switchFrom string
	level      string
	switchTo   string
}

func parseSwitches(input string) (map[string]*Switch, map[string][]string) {
	seenSwitches := make(map[string]*Switch)
	possibleInputs := make(map[string][]string)
	for _, line := range strings.Split(input, "\n") {
		splitLine := strings.Split(line, "->")
		switchName := strings.TrimSpace(splitLine[0])
		next := ParseFuncSep(splitLine[1], ',', func(s string) string { return strings.TrimSpace(s) })

		if switchName == "broadcaster" {
			var broadcaster Switch = &Broadcaster{nextSwitches: next}
			seenSwitches[switchName] = &broadcaster
			continue
		}
		switchId := switchName[0]
		switchName = switchName[1:]
		for _, swtch := range next {
			if seenSwitch, seen := seenSwitches[swtch]; !seen {
				possibleInputs[swtch] = append(possibleInputs[swtch], switchName)
			} else if conjuction, isConjunction := (*seenSwitch).(*Conjuction); isConjunction {
				conjuction.state[switchName] = "low"
			}
		}

		if switchId == '%' {
			var flipFlop Switch = &FlipFlop{state: false, nextSwitches: next}
			seenSwitches[switchName] = &flipFlop
			delete(possibleInputs, switchName)
		} else if switchId == '&' {
			conjuctionState := make(map[string]string)
			if inputs, exists := possibleInputs[switchName]; exists {
				for _, nextSwitch := range inputs {
					conjuctionState[nextSwitch] = "low"
				}
			}
			var conjuction Switch = &Conjuction{state: conjuctionState, nextSwitches: next}
			seenSwitches[switchName] = &conjuction
			delete(possibleInputs, switchName)
		}
	}
	return seenSwitches, possibleInputs
}

func part1(input string, repeat int) int {
	switches, _ := parseSwitches(input)
	lowSent := 0
	highSent := 0
	for i := 0; i < repeat; i++ {
		queue := []Signal{{"button", "low", "broadcaster"}}
		for len(queue) > 0 {
			signal := queue[0]
			queue = queue[1:]
			if signal.level == "low" {
				lowSent++
			} else if signal.level == "high" {
				highSent++
			}
			currentSwitch, exists := switches[signal.switchTo]
			if !exists {
				continue
			}
			newLevel := (*currentSwitch).receivePulse(signal.switchFrom, signal.level)
			if newLevel == "" {
				continue
			}

			queue = append(queue, Map((*currentSwitch).next(), func(swtch string) Signal {
				return Signal{switchFrom: signal.switchTo, level: newLevel, switchTo: swtch}
			})...)
		}
	}
	return lowSent * highSent
}

func part2(input string) int {
	switches, possibleInputs := parseSwitches(input)
	cycles := make(map[string]int)
	for _, criticalSwitch := range maps.Keys((*switches[possibleInputs["rx"][0]]).(*Conjuction).state) {
		cycles[criticalSwitch] = -1
	}

	i := 1
	for AnyMatch(maps.Values(cycles), func(cycleLength int) bool { return cycleLength < 0 }) {
		queue := []Signal{{"button", "low", "broadcaster"}}
		for len(queue) > 0 {
			signal := queue[0]
			queue = queue[1:]
			currentSwitch, exists := switches[signal.switchTo]
			if !exists {
				continue
			}
			newLevel := (*currentSwitch).receivePulse(signal.switchFrom, signal.level)
			if newLevel == "" {
				continue
			}
			if cycleLength, isCritical := cycles[signal.switchFrom]; signal.level == "high" && isCritical && cycleLength < 0 {
				cycles[signal.switchFrom] = i
			}

			queue = append(queue, Map((*currentSwitch).next(), func(swtch string) Signal {
				return Signal{switchFrom: signal.switchTo, level: newLevel, switchTo: swtch}
			})...)
		}
		i++
	}
	return Reduce(maps.Values(cycles), cycles[maps.Keys(cycles)[0]], Lcm)
}

func main() {
	Check("Part 1", 32000000, func() int {
		return part1(sample, 1000)
	})
	Check("Part 1", 11687500, func() int {
		return part1(sample2, 1000)
	})
	Check("Part 1", 791120136, func() int {
		return part1(input, 1000)
	})
	Check("Part 2", 215252378794009, func() int {
		return part2(input)
	})
}

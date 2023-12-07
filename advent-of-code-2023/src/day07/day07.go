package main

import (
	"../shared"
	_ "embed"
	"golang.org/x/exp/maps"
	"slices"
	"strings"
	"unicode"
)

//go:embed day07_sample.txt
var sample string

//go:embed day07.txt
var input string

type Hand struct {
	strengths []int
	handType  int
	bid       int
}

func part1(input string) int {
	return findWinnings(input, false)
}

func part2(input string) int {
	return findWinnings(input, true)
}

func findWinnings(input string, containsJoker bool) int {
	hands := shared.Map(
		strings.Split(input, "\n"),
		func(str string) Hand {
			fields := strings.Fields(str)
			handType := findHandType(fields[0], containsJoker)
			bid := shared.ParseIntSafe(fields[1])
			return Hand{
				strengths: findStrengths(fields[0], containsJoker),
				handType:  handType,
				bid:       bid,
			}
		},
	)

	slices.SortFunc(hands, func(a, b Hand) int {
		if a.handType != b.handType {
			return a.handType - b.handType
		}
		for i := range a.strengths {
			if a.strengths[i] != b.strengths[i] {
				return a.strengths[i] - b.strengths[i]
			}
		}
		return 0
	})

	return shared.ReduceIndexed(hands, 0, func(state int, hand Hand, i int) int {
		return state + hand.bid*(i+1)
	})
}

func findStrengths(handString string, containsJoker bool) (strengths []int) {
	for _, card := range handString {
		var cardStrength int
		if unicode.IsDigit(card) {
			cardStrength = shared.ParseIntSafe(string(card))
		} else {
			switch card {
			case 'T':
				cardStrength = 10
			case 'J':
				cardStrength = 11
				if containsJoker {
					cardStrength = 1
				}
			case 'Q':
				cardStrength = 12
			case 'K':
				cardStrength = 13
			case 'A':
				cardStrength = 14
			}
		}
		strengths = append(strengths, cardStrength)
	}
	return
}

func findHandType(handString string, containsJokers bool) int {
	var buckets = make(map[rune]int)
	for _, card := range handString {
		_, exists := buckets[card]
		if exists {
			buckets[card]++
		} else {
			buckets[card] = 1
		}
	}
	if jokers, hasJoker := buckets['J']; hasJoker && containsJokers {
		delete(buckets, 'J')
		maxCard, _ := shared.MaxEntryByValue(buckets)
		buckets[maxCard] += jokers
	}

	// all distinct - high card
	if len(buckets) == 5 {
		return 1
	}
	// all the same - five of a kind
	if len(buckets) == 1 {
		return 7
	}
	// one card is duplicated - one pair
	if len(buckets) == 4 {
		return 2
	}
	// two pairs, three of a kind
	if len(buckets) == 3 {
		// three of a kind
		if shared.AnyMatch(maps.Values(buckets), func(count int) bool { return count == 3 }) {
			return 4
		}
		// two pairs
		return 3
	}
	// four of a kind
	if shared.AnyMatch(maps.Values(buckets), func(count int) bool { return count == 4 }) {
		return 6
	}
	// full house
	return 5
}

func main() {
	shared.Check("Part 1", 6440, func() int {
		return part1(sample)
	})
	shared.Check("Part 1", 255048101, func() int {
		return part1(input)
	})
	shared.Check("Part 2", 5905, func() int {
		return part2(sample)
	})
	shared.Check("Part 2", 253718286, func() int {
		return part2(input)
	})
}

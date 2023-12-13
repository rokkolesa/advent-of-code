package main

import (
	"../shared"
	_ "embed"
	"strings"
)

//go:embed day13_sample.txt
var sample string

//go:embed day13.txt
var input string

func splitOffset(mirrorLines []string, smudgeable bool) (int, bool) {
	for i := 0; i < len(mirrorLines)-1; i++ {
		smudged := false
		candidateFound := true
		for k := 0; i-k >= 0 && i+k < len(mirrorLines)-1; k++ {
			first := mirrorLines[i-k]
			second := mirrorLines[i+k+1]
			if first != second {
				if smudgeable && !smudged && canSmudge(first, second) {
					smudged = true
					continue
				}
				candidateFound = false
				break
			}
		}
		if candidateFound && (!smudgeable || smudged) {
			return i + 1, smudged
		}
	}
	return 0, false
}

func canSmudge(a, b string) bool {
	smudge := 0
	for i := range a {
		if a[i] != b[i] {
			smudge++
		}
		if smudge > 1 {
			return false
		}
	}
	return smudge == 1
}

func findMirrorSplits(input string, smudgeable bool) int {
	sum := 0
	for _, mirror := range strings.Split(input, "\n\n") {
		mirrorLines := strings.Split(mirror, "\n")

		horizontalOffset, horizontalSmudged := splitOffset(mirrorLines, smudgeable)
		if horizontalSmudged {
			sum += 100 * horizontalOffset
		} else if verticalOffset, verticalSmudged := splitOffset(shared.Transpose(mirrorLines), smudgeable); verticalSmudged {
			sum += verticalOffset
		} else {
			sum += 100*horizontalOffset + verticalOffset
		}
	}
	return sum
}

func part1(input string) int {
	return findMirrorSplits(input, false)
}

func part2(input string) int {
	return findMirrorSplits(input, true)
}

func main() {
	shared.Check("Part 1", 405, func() int {
		return part1(sample)
	})
	shared.Check("Part 1", 34821, func() int {
		return part1(input)
	})
	shared.Check("Part 2", 400, func() int {
		return part2(sample)
	})
	shared.Check("Part 2", 36919, func() int {
		return part2(input)
	})
}

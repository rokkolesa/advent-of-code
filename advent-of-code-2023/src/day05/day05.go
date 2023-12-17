package main

import (
	. "../shared"
	_ "embed"
	"fmt"
	"regexp"
	"slices"
	"strings"
)

//go:embed day05_sample.txt
var sample string

//go:embed day05.txt
var input string

type AlmanacMap struct {
	source  string
	target  string
	entries []AlmanacMapEntry
}

func (receiver AlmanacMap) String() string {
	return fmt.Sprintf("%v-to-%v: %v", receiver.source, receiver.target, receiver.entries)
}

type AlmanacMapEntry struct {
	destinationStart int64
	sourceStart      int64
	rangeLength      int64
}

func (receiver AlmanacMapEntry) destinationInterval() Interval {
	return IntervalFromLength(receiver.destinationStart, receiver.rangeLength)
}
func (receiver AlmanacMapEntry) sourceInterval() Interval {
	return IntervalFromLength(receiver.sourceStart, receiver.rangeLength)
}

type Interval struct {
	from int64
	to   int64
}

func IntervalFromLength(from, length int64) Interval {
	return Interval{
		from: from,
		to:   from + length - 1,
	}
}

func part1(input string) int64 {
	seeds, almanacMaps := getAlmanac(input)

	var lowestLocations []int64
	for _, seed := range seeds {
		currentValue := seed
		step := "seed"
		for step != "location" {
			almanacMap := almanacMaps[step]
			step = almanacMap.target
			for _, almanacMapEntry := range almanacMap.entries {
				if sourceOffset := currentValue - almanacMapEntry.sourceStart; sourceOffset >= 0 && sourceOffset < almanacMapEntry.rangeLength {
					currentValue = sourceOffset + almanacMapEntry.destinationStart
					break
				}
			}
		}
		lowestLocations = append(lowestLocations, currentValue)
	}
	return slices.Min(lowestLocations)
}

func part2(input string) int64 {
	seeds, almanacMaps := getAlmanac(input)
	var seedsIntervals []Interval
	for i := 0; i < len(seeds); i += 2 {
		seedsIntervals = append(seedsIntervals, IntervalFromLength(seeds[i], seeds[i+1]))
	}

	var lowestLocations []int64
	for _, seedsInterval := range seedsIntervals {
		currentIntervals := []Interval{seedsInterval}
		step := "seed"
		for step != "location" {
			almanacMap := almanacMaps[step]
			step = almanacMap.target

			currentIntervals = splitIntervals(currentIntervals, almanacMap)
		}

		lowestLocations = append(
			lowestLocations,
			slices.Min(Map(currentIntervals, func(interval Interval) int64 { return interval.from })),
		)
	}
	return slices.Min(lowestLocations)
}

type ProcessedInterval struct {
	interval  Interval
	processed bool
}

func splitIntervals(currentIntervals []Interval, almanacMap *AlmanacMap) []Interval {
	newIntervals := Map(currentIntervals, func(interval Interval) ProcessedInterval { return ProcessedInterval{interval, false} })
	for _, almanacEntry := range almanacMap.entries {
		for i, currentInterval := range newIntervals {
			if !currentInterval.processed {
				newIntervals = slices.Replace(newIntervals, i, i+1, splitInterval(currentInterval, almanacEntry)...)
			}
		}
	}
	return Map(newIntervals, func(processed ProcessedInterval) Interval { return processed.interval })
}

func splitInterval(processedInterval ProcessedInterval, almanacEntry AlmanacMapEntry) []ProcessedInterval {
	interval := processedInterval.interval
	almanacMapSourceInterval := almanacEntry.sourceInterval()
	almanacMapDestinationInterval := almanacEntry.destinationInterval()
	// no common numbers
	if almanacMapSourceInterval.to < interval.from || almanacMapSourceInterval.from > interval.to {
		return []ProcessedInterval{processedInterval}
	}
	// intersects at the start of sourceInterval
	if interval.from < almanacMapSourceInterval.from && interval.to <= almanacMapSourceInterval.to {
		offset := interval.to - almanacMapSourceInterval.from
		return []ProcessedInterval{
			{Interval{interval.from, almanacMapSourceInterval.from - 1}, false},
			{Interval{almanacMapDestinationInterval.from, almanacMapDestinationInterval.from + offset}, true},
		}
	}
	// interval is completely contained
	if interval.from >= almanacMapSourceInterval.from && interval.to <= almanacMapSourceInterval.to {
		offsetStart := interval.from - almanacMapSourceInterval.from
		offsetEnd := almanacMapSourceInterval.to - interval.to
		return []ProcessedInterval{{Interval{almanacMapDestinationInterval.from + offsetStart, almanacMapDestinationInterval.to - offsetEnd}, true}}
	}
	// intersects at the end of sourceInterval
	if interval.from >= almanacMapSourceInterval.from && interval.to > almanacMapSourceInterval.to {
		offset := almanacMapSourceInterval.to - interval.from
		return []ProcessedInterval{
			{Interval{almanacMapDestinationInterval.to - offset, almanacMapDestinationInterval.to}, true},
			{Interval{almanacMapSourceInterval.to + 1, interval.to}, false},
		}
	}
	// interval is bigger than sourceInterval
	return []ProcessedInterval{
		{Interval{interval.from, almanacMapSourceInterval.from - 1}, false},
		{Interval{almanacMapDestinationInterval.from, almanacMapDestinationInterval.to}, true},
		{Interval{almanacMapSourceInterval.to + 1, interval.to}, false},
	}
}

func getAlmanac(input string) (seeds []int64, almanacMaps map[string]*AlmanacMap) {
	seedsAndMaps := strings.SplitN(input, "\n\n", 2)

	seeds = ParseFuncAfter(seedsAndMaps[0], ":", ParseInt64Safe)

	almanacMapIdRegex, _ := regexp.Compile("(.*)-to-(.*) map:")
	almanacMaps = make(map[string]*AlmanacMap)
	for _, almanacMapString := range strings.Split(seedsAndMaps[1], "\n\n") {
		almanacMap := AlmanacMap{}
		for j, almanacMapDef := range strings.Split(almanacMapString, "\n") {
			// the first line is the source-to-target
			if j == 0 {
				almanacMapIds := almanacMapIdRegex.FindStringSubmatch(almanacMapDef)
				almanacMap.source = almanacMapIds[1]
				almanacMap.target = almanacMapIds[2]
			} else {
				almanacMapEntryDef := ParseFunc(almanacMapDef, ParseInt64Safe)

				almanacMap.entries = append(almanacMap.entries, AlmanacMapEntry{
					destinationStart: almanacMapEntryDef[0],
					sourceStart:      almanacMapEntryDef[1],
					rangeLength:      almanacMapEntryDef[2],
				})
			}
		}
		almanacMaps[almanacMap.source] = &almanacMap
	}

	return
}

func main() {
	Check("Part 1", 35, func() int64 {
		return part1(sample)
	})
	Check("Part 1", 379811651, func() int64 {
		return part1(input)
	})
	Check("Part 2", 46, func() int64 {
		return part2(sample)
	})
	Check("Part 2", 27992443, func() int64 {
		return part2(input)
	})
}

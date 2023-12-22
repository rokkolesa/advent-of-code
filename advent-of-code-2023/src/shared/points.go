package shared

import "slices"

type Point struct {
	X int
	Y int
}

func (thisPoint Point) Adjacent() []Point {
	return []Point{
		thisPoint.Move("L"),
		thisPoint.Move("R"),
		thisPoint.Move("U"),
		thisPoint.Move("D"),
	}
}

func (thisPoint Point) Plus(other Point) Point {
	return Point{
		X: thisPoint.X + other.X,
		Y: thisPoint.Y + other.Y,
	}
}

func (thisPoint Point) Times(scalar int) Point {
	return Point{
		X: thisPoint.X * scalar,
		Y: thisPoint.Y * scalar,
	}
}

func (thisPoint Point) Negative() Point {
	return Point{X: -thisPoint.X, Y: -thisPoint.Y}
}

func (thisPoint Point) Move(direction string) Point {
	return thisPoint.Plus(Unit(direction))
}

func (thisPoint Point) Turn(direction, turn string) (Point, string) {
	directions := []string{"U", "R", "D", "L"}
	directionIndex := slices.Index(directions, direction)
	if directionIndex < 0 {
		panic("Unknown direction!")
	}
	var newDirection string
	switch turn {
	case "S":
		newDirection = direction
	case "B":
		newDirection = directions[(directionIndex+2)%4]
	case "R":
		newDirection = directions[(directionIndex+1)%4]
	case "L":
		newDirection = directions[(directionIndex+3)%4]
	default:
		panic("Unknown turn!")

	}
	return thisPoint.Move(newDirection), newDirection
}

func Unit(direction string) Point {
	switch direction {
	case "R":
		return Point{X: 1, Y: 0}
	case "L":
		return Point{X: -1, Y: 0}
	case "U":
		return Point{X: 0, Y: -1}
	case "D":
		return Point{X: 0, Y: 1}
	}
	panic("Unknown direction!")
}

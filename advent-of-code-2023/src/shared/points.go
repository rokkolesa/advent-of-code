package shared

type Point struct {
	X int
	Y int
}

func (thisPoint Point) Adjacent() []Point {
	return []Point{
		// left-right
		{X: thisPoint.X - 1, Y: thisPoint.Y},
		{X: thisPoint.X + 1, Y: thisPoint.Y},
		// top-bottom
		{X: thisPoint.X, Y: thisPoint.Y + 1},
		{X: thisPoint.X, Y: thisPoint.Y - 1},
		// diagonals
		{X: thisPoint.X - 1, Y: thisPoint.Y - 1},
		{X: thisPoint.X + 1, Y: thisPoint.Y - 1},
		{X: thisPoint.X + 1, Y: thisPoint.Y + 1},
		{X: thisPoint.X - 1, Y: thisPoint.Y + 1},
	}
}
func (thisPoint Point) Plus(other Point) Point {
	return Point{
		X: thisPoint.X + other.X,
		Y: thisPoint.Y + other.Y,
	}
}

func (thisPoint Point) Negative() Point {
	return Point{X: -thisPoint.X, Y: -thisPoint.Y}
}

func (thisPoint Point) Move(direction string) Point {
	return thisPoint.Plus(Unit(direction))
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

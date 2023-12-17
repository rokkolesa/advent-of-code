package shared

func Reduce[T, U any](slice []T, initialState U, reducer func(U, T) U) U {
	return ReduceIndexed(slice, initialState, func(state U, element T, _ int) U {
		return reducer(state, element)
	})
}

func ReduceIndexed[T, U any](slice []T, initialState U, reducer func(U, T, int) U) U {
	state := initialState
	for i, element := range slice {
		state = reducer(state, element, i)
	}
	return state
}

func Sum[T int | int64 | float64](slice []T) T {
	return Reduce(slice, 0, func(state T, element T) T {
		return state + element
	})
}

func Product[T int | int64 | float64](slice []T) T {
	return Reduce(slice, 1, func(state T, element T) T {
		return state * element
	})
}

func CountFunc[T any](slice []T, criteria func(element T) bool) int {
	return Reduce(slice, 0, func(state int, element T) int {
		if criteria(element) {
			return state + 1
		}
		return state
	})
}

func CountSample[T comparable](slice []T, sample T) int {
	return CountFunc(slice, func(element T) bool { return element == sample })
}

func Map[T any, R any](slice []T, mapper func(T) R) []R {
	return MapIndexed(slice, func(t T, _ int) R { return mapper(t) })
}

func MapIndexed[T any, R any](slice []T, mapper func(T, int) R) []R {
	newSlice := make([]R, len(slice))
	for i, element := range slice {
		newSlice[i] = mapper(element, i)
	}
	return newSlice
}

func Filter[T any](slice []T, filter func(T) bool) []T {
	var newSlice []T
	for _, element := range slice {
		if filter(element) {
			newSlice = append(newSlice, element)
		}
	}
	return newSlice
}

func AnyMatch[T any](slice []T, test func(T) bool) bool {
	for _, element := range slice {
		if test(element) {
			return true
		}
	}
	return false
}

func AllMatch[T any](slice []T, test func(T) bool) bool {
	return !AnyMatch(slice, func(element T) bool {
		return !test(element)
	})
}

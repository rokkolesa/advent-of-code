package shared

import (
	"fmt"
	maps "golang.org/x/exp/maps"
)

type SetType[T comparable] interface {
	internalData() map[T]bool

	Len() int

	Add(elements ...T)
	Remove(elements ...T)
	Contains(element T) bool
	IsEmpty() bool
	Clear()
	Intersection(other SetType[T]) SetType[T]
	Union(other SetType[T]) SetType[T]

	ToSlice() []T
}

type set[T comparable] struct {
	data *map[T]bool
}

func Set[T comparable](elements ...T) SetType[T] {
	internalData := make(map[T]bool)
	internalSet := set[T]{
		data: &internalData,
	}
	internalSet.Add(elements...)
	return internalSet
}

func (receiver set[T]) Len() int {
	return len(receiver.internalData())
}

func (receiver set[T]) internalData() map[T]bool {
	return *receiver.data
}

func (receiver set[T]) Add(elements ...T) {
	for _, element := range elements {
		(*receiver.data)[element] = true
	}
}

func (receiver set[T]) Remove(elements ...T) {
	for _, element := range elements {
		delete(*receiver.data, element)
	}
}

func (receiver set[T]) Contains(element T) bool {
	return (*receiver.data)[element]
}

func (receiver set[T]) IsEmpty() bool {
	return receiver.Len() == 0
}

func (receiver set[T]) Clear() {
	fmt.Println("Before clear", receiver.data)
	*receiver.data = make(map[T]bool)
	fmt.Println("After clear", receiver.data)
}

func (receiver set[T]) Intersection(other SetType[T]) SetType[T] {
	baseData := receiver.internalData()
	otherData := other.internalData()
	if other.Len() < receiver.Len() {
		baseData, otherData = otherData, baseData
	}
	clone := maps.Clone(baseData)
	for element := range clone {
		if !otherData[element] {
			delete(clone, element)
		}
	}
	return set[T]{data: &clone}
}

func (receiver set[T]) Union(other SetType[T]) SetType[T] {
	baseData := receiver.internalData()
	otherData := other.internalData()
	if other.Len() > receiver.Len() {
		baseData, otherData = otherData, baseData
	}
	clone := maps.Clone(baseData)
	for element := range otherData {
		clone[element] = true
	}
	return set[T]{data: &clone}
}

func (receiver set[T]) ToSlice() []T {
	return maps.Keys(receiver.internalData())
}

func (receiver set[T]) String() string {
	str := ""
	i := 0
	for k := range *receiver.data {
		str += fmt.Sprintf("%v", k)
		i++
		if i < len(*receiver.data) {
			str += " "
		}
	}
	return fmt.Sprintf("set{%s}", str)
}

func (receiver set[T]) GoString() string {
	return receiver.String()
}

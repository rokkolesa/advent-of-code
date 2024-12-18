use crate::utils;

pub fn run() {
    // Read the input file for Day 1
    let input = utils::read_file("inputs/day01.txt");
    let sample = utils::read_file("inputs/day01_sample.txt");

    utils::check("Part 1", 11, || solve_part1(&sample));
    utils::check("Part 1", 1341714, || solve_part1(&input));
    utils::check("Part 2", 31, || solve_part2(&sample));
    utils::check("Part 2", 27384707, || solve_part2(&input));

}

fn solve_part1(input: &str) -> i32 {
    let (mut left, mut right) = parse_input(input);

    left.sort();
    right.sort();

    left.into_iter()
        .zip(right.into_iter())
        .map(|(a, b)| a - b)
        .map(|result| result.abs())
        .sum()
}

fn solve_part2(input: &str) -> i32 {
    let (left,right) = parse_input(input);

    left.iter()
        .map(|l| right.iter().filter(|&r| r == l).count() as i32 * l)
        .sum()
}

fn parse_input(input: &str) -> (Vec<i32>, Vec<i32>) {
    let mut left: Vec<i32> = Vec::new();
    let mut right: Vec<i32> = Vec::new();
    for line in input.lines() {
        let split_line: Vec<i32> = line.split_whitespace().map(|x| x.parse::<i32>().unwrap()).collect();
        left.push(split_line[0]);
        right.push(split_line[1]);
    }

    (left, right)
}

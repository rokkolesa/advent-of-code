use crate::utils;
use std::collections::HashMap;

pub fn run() {
    let input = utils::read_file("inputs/day19.txt");
    let sample = utils::read_file("inputs/day19_sample.txt");

    utils::check("Part 1", 6, || solve_part1(&sample));
    utils::check("Part 1", 338, || solve_part1(&input));
    utils::check("Part 2", 16, || solve_part2(&sample));
    utils::check("Part 2", 841533074412361, || solve_part2(&input));
}

fn solve_part1(input: &str) -> i64 {
    let (patterns, designs) = parse_input(input);

    let mut solutions: HashMap<String, i64> = HashMap::new();

    designs
        .iter()
        .map(|design| find_combinations(design, &patterns, &mut solutions))
        .filter(|x| *x > 0)
        .count() as i64
}

fn solve_part2(input: &str) -> i64 {
    let (patterns, designs) = parse_input(input);

    let mut solutions: HashMap<String, i64> = HashMap::new();

    designs
        .iter()
        .map(|design| find_combinations(design, &patterns, &mut solutions))
        .sum()
}

fn find_combinations(
    design: &String,
    patterns: &Vec<String>,
    solutions: &mut HashMap<String, i64>,
) -> i64 {
    if design.len() == 0 {
        return 1;
    }
    if solutions.contains_key(design) {
        return solutions[design];
    }
    let combinations = patterns
        .iter()
        .filter(|pattern| design.starts_with(*pattern))
        .map(|pattern| design[pattern.len()..].to_string())
        .map(|remaining_design| find_combinations(&remaining_design, patterns, solutions))
        .sum();

    solutions.insert(design.clone(), combinations);

    combinations
}

fn parse_input(input: &str) -> (Vec<String>, Vec<String>) {
    let mut lines = input.lines();
    let patterns: Vec<String> = lines
        .next()
        .unwrap()
        .split(",")
        .map(|x| x.trim().to_string())
        .collect();

    let designs = lines
        .filter(|x| !x.is_empty())
        .map(|x| x.to_string())
        .collect();

    (patterns, designs)
}
